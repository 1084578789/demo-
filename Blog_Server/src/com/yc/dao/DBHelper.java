package com.yc.dao;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;


public class DBHelper {
	/* 鍒濆鍖朿ontext瀵硅薄 */
	static Context ctx = null;
	
	//闈欐�佸揩锛岀敤鏉ュ姞杞介┍鍔�
	static{
		try {
			Class.forName(Env.getInstance().getProperty("driverClassName"));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		try {
			ctx = new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 鑾峰彇杩炴帴
	 * @throws Exception 
	 */
	public Connection getConnection(){
		Connection con=null;
		try {
			DataSource ds = BasicDataSourceFactory.createDataSource(Env.getInstance());
			con = ds.getConnection();
			
			//閫氳繃鏁版嵁杩炴帴姹犲幓鍙�
//			DataSource dataSource = (DataSource)ctx.lookup("java:comp/env/jdbc/cludtags");
//			//out.print(hello);
//			//鍙栧嚭涓�涓繛鎺�
//		   con = dataSource.getConnection();
		   
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}


	/**
	 * 鍏抽棴鐨勬柟娉�
	 */
	public void closeAll(Connection con,PreparedStatement pstmt,ResultSet rs,CallableStatement cs){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		if(cs!=null){
			try {
				cs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		if(pstmt!=null){
			try {
				pstmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}


	private void setValues(PreparedStatement st,List<Object> objs) {
		// 鍒ゆ柇鏄惁鏈夊弬鏁�
		if (objs == null || objs.size() == 0) {
			return;
		}
		try {
			for (int i = 0,len=objs.size(); i < len; i++) {
				if(objs.get(i)!=null){
					String paramType = objs.get(i).getClass().getName(); // 鑾峰緱鍙傛暟鐨勭被鍨�
					if (Integer.class.getName().equals(paramType)) { // 鍒ゆ柇鏄惁鏄痠nt绫诲瀷
						st.setInt(i + 1, (Integer) objs.get(i));
					} else if (Double.class.getName().equals(paramType)) { // 鍒ゆ柇鏄惁鏄痙obule绫诲瀷
						st.setDouble(i + 1, (Double) objs.get(i));
					} else if (String.class.getName().equals(paramType)) { // 鍒ゆ柇鏄惁鏄痵tring绫诲瀷
						st.setString(i + 1, (String) objs.get(i));
					} else {
						st.setObject(i + 1, objs.get(i));
					}
				}else{
					st.setObject(i + 1,objs.get(i));
				}

			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 鑾峰彇缁撴灉闆嗕腑姣忎釜鍒楃殑绫诲悕
	 * @param rs锛氱粨鏋滈泦
	 * @return
	 */
	private String[] getColumnNames(ResultSet rs){
		String[] colNames=null;
		try {
			ResultSetMetaData md=rs.getMetaData(); //鑾峰彇缁撴灉闆嗙殑鍏冩暟鎹紝瀹冨弽鏄犱簡缁撴灉闆嗙殑淇℃伅
			colNames=new String[md.getColumnCount()];//鍒涘缓涓�涓暟鎹甤olnames锛岀敤鏉ュ瓨鏀惧垪鐨勫悕瀛�
			for(int i=0;i<colNames.length;i++){  //灏嗗垪鍚嶄繚瀛樺埌colname鏁扮粍涓�
				colNames[i]=md.getColumnName(i+1).toLowerCase();
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return colNames;
	}


	/**
	 * 澧炲垹鏀�
	 * @param sql锛歴ql璇彞闆嗗悎锛岄噷闈㈠彲浠ュ姞锛�
	 * @param params锛氳〃绀�?瀵瑰簲鐨勫弬鏁板�肩殑闆嗗悎
	 * @return int:杩斿洖鐨勫�笺�傛垚鍔�>0锛屽け璐�<=0
	 */
	public int update(List<String> sql,List<List<Object>> params){
		int result=0;
		Connection con=getConnection();
		PreparedStatement pstmt=null;
		try {
			con.setAutoCommit(false);  //浜嬪姟澶勭悊
			for(int i=0;i<sql.size();i++){
				List<Object> param=params.get(i);
				pstmt=con.prepareStatement(sql.get(i));  //棰勭紪璇戝璞�
				setValues(pstmt,param);    //璁剧疆鍙傛暟
				result=pstmt.executeUpdate();
			}
			con.commit(); //娌℃湁閿欏鎵ц
		} catch (SQLException e) {
			try {
				con.rollback();  //鍑洪敊鍥炴粴
			} catch (SQLException e1) {
				throw new RuntimeException(e);
			}
			throw new RuntimeException(e);
		}finally{
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			closeAll(con,pstmt,null,null);
		}
		return result;
	}

	/**
	 * 澧炲垹鏀规壒澶勭悊
	 * @param sql锛歴ql璇彞闆嗗悎锛岄噷闈㈠彲浠ュ姞锛�
	 * @param params锛氳〃绀�?瀵瑰簲鐨勫弬鏁板�肩殑闆嗗悎
	 * @return int:杩斿洖鐨勫�笺�傛垚鍔�>0锛屽け璐�<=0
	 */
	public boolean updates(List<String> sqls,List<List<Object>> params){
		Connection con=getConnection();
		PreparedStatement pstmt=null;
		//java涓槸鑷姩鎻愪氦浜嬪姟锛屾墍浠ユ垜浠繀椤诲厛鍏抽棴鑷姩鎻愪氦
		try {
			con.setAutoCommit(false);
			//寰幆鎵цsql璇彞
			for(int i=0;i<sqls.size();i++){
				pstmt=con.prepareStatement(sqls.get(i)); //鍙栧嚭绗琲鏉ql璇彞
				setValues(pstmt, params.get(i)); //鍙栧嚭绗琲鏉ql璇彞瀵瑰簲鐨勫弬鏁板垪琛�
				pstmt.addBatch();
			}
			//濡傛灉鎵�鏈夎鍙ユ墽琛屽悗閮芥病鏈夊嚭閿欙紝鍒欐彁浜�
			pstmt.executeBatch();
			con.commit();
		} catch (SQLException e) {
			//濡傛灉鎵ц杩囩▼涓嚭閿欎簡锛屽垯鍥炴粴
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException(e);
			}
			throw new RuntimeException(e);
		} finally{
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			this.closeAll(con, pstmt,null,null);
		}

		return true;
	}

	/**
	 * 鍗曡〃澧炲垹鏀�
	 * @param sql锛歴ql璇彞闆嗗悎锛岄噷闈㈠彲浠ュ姞锛�
	 * @param params锛氳〃绀�?瀵瑰簲鐨勫弬鏁板�肩殑闆嗗悎
	 * @return int:杩斿洖鐨勫�笺�傛垚鍔�>0锛屽け璐�<=0
	 */
	public int update(String sql,List<Object> params){
		int result=0;
		Connection con=getConnection();
		PreparedStatement pstmt=null;	
		try {
			pstmt=con.prepareStatement(sql);  //棰勭紪璇戝璞�
			setValues(pstmt,params);    //璁剧疆鍙傛暟
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}finally{
			closeAll(con,pstmt,null,null);
		}
		return result;
	}


	/**
	 * 鑱氬悎鏌ヨ
	 * @param sql锛氳仛鍚堟煡璇㈣鍙�
	 * @param params锛氬弬鏁板垪琛紝鐢ㄦ潵鏇挎崲sql涓殑?锛堝崰浣嶇锛�
	 * @return list:缁撴灉闆�
	 */

	public List<String> uniqueResult(String sql,List<Object> params){
		List<String> list=new ArrayList<String>();
		Connection con=getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			pstmt=con.prepareStatement(sql);  //棰勭紪璇戝璞�
			setValues(pstmt,params);   //璁剧疆鍙傛暟
			rs=pstmt.executeQuery();  //鎵ц鏌ヨ

			ResultSetMetaData md=rs.getMetaData();  //缁撴灉闆嗙殑鍏冩暟鎹紝瀹冨弽鏄犱簡缁撴灉闆嗙殑淇℃伅
			int count=md.getColumnCount();    //鍙栧嚭缁撴灉闆嗕腑鍒楃殑鏁伴噺

			if(rs.next()){
				for(int i=1;i<=count;i++){
					list.add(rs.getString(i));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}finally{
			closeAll(con,pstmt,rs,null);
		}
		return list;
	}


	/**
	 * 鏌ヨ
	 * @param <T> 娉涘瀷锛氬嵆浣犺寰楀埌鐨勯泦鍚堜腑瀛樼殑瀵硅薄鐨勭被鍨�
	 * @param sql: 鏌ヨ璇彞锛屽彲浠ュ惈鏈�?
	 * @param params: ?鎵�瀵瑰簲鐨勫弬鏁板�肩殑闆嗗悎
	 * @param c锛� 娉涘瀷绫诲瀷鎵�瀵瑰簲鐨勫弽灏勫璞�
	 * @return 锛氬瓨鍌ㄤ簡瀵硅薄鐨勯泦鍚�
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public <T> List<T> find(String sql,Class<T> c,List<Object> params) {
		List<T> list=new ArrayList<T>(); //瑕佽繑鍥炵殑缁撴灉鐨勯泦鍚�
		Connection con=getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			pstmt=con.prepareStatement(sql); //棰勭紪璇戝璞�
			setValues(pstmt, params); //璁剧疆鍗犱綅绗�
			rs=pstmt.executeQuery();  //鎵ц鏌ヨ璇彞锛屽緱鍒扮粨鏋滈泦

			Method[] ms=c.getMethods(); //鍙栧嚭杩欎釜鍙嶅皠瀹炰緥鐨勬墍鏈夋柟娉�
			
			String[] colnames=getColumnNames(rs); //鑾峰彇缁撴灉闆嗕腑鎵�鏈夊垪鐨勫垪鍚�
			T t;
			String mname=null;  //鏂规硶鍚�
			String cname=null;  //鍒楀悕
			String ctypename=null; //绫诲瀷鍚�

			while(rs.next()){
				t=(T)c.newInstance(); //鍒涘缓鍙嶅皠绫荤殑瀹炰緥鍖栧璞�    Product t=(Product)c.newInstance();
				for(int i=0;i<colnames.length;i++){//寰幆鏂规硶鍚� ,鏍煎紡涓簊etXXXX鎴杇etXXX
					cname=colnames[i]; //鍙栧嚭鍒楀悕骞跺湪鍓嶉潰鍔犱笂set  setXXX
					cname="set"+cname.substring(0,1).toUpperCase()+cname.substring(1).toLowerCase();
					if(ms!=null&&ms.length>0){
						for(Method m:ms){//寰幆鍒楀悕
							mname=m.getName(); //鍙栧嚭鏂规硶鍚�

							if(cname.equals(mname)&&rs.getObject(colnames[i])!=null){//鍒ゆ柇鏂规硶鍚嶅拰鍒楀悕鏄惁涓�鏍凤紝鐩稿悓鍒欐縺娲绘柟娉曪紝娉ㄥ叆鏁版嵁      //鍙"set"+鏁版嵁鍒楀悕.equalsIgnoreCase锛堟柟娉曞悕锛夛紝鍒欐縺娲昏繖涓柟娉�
								//setXXX(String str); setXXX(int num); 婵�娲诲搴旂殑鏂规硶杩樺繀椤荤煡閬撳畠鐨勬暟鎹被鍨�
								ctypename=rs.getObject(colnames[i]).getClass().getName();//鑾峰彇褰撳墠鍒楃殑绫诲瀷鍚�

								if("java.lang.Integer".equals(ctypename)){
									m.invoke(t,rs.getInt(colnames[i])); //obj.setXX(xx);
								}else if("java.lang.String".equals(ctypename)){
									m.invoke(t, rs.getString(colnames[i]));
								}else if("java.math.BigInteger".equals(ctypename)){
									m.invoke(t, rs.getDouble(colnames[i]));
								}else if("java.math.BigDecimal".equals(ctypename)){
									try{
										m.invoke(t, rs.getInt(colnames[i]));
									}catch(Exception e1){
										m.invoke(t, rs.getDouble(colnames[i]));
									}
								}else if("java.sql.Timestamp".equals(ctypename)){
									m.invoke(t, rs.getString(colnames[i]));
								}else if("java.sql.Date".equals(ctypename)){
									m.invoke(t, rs.getString(colnames[i]));
								}else if("java.sql.Time".equals(ctypename)){
									m.invoke(t, rs.getString(colnames[i]));
								}else if("image".equals(ctypename)) {
									m.invoke(t,rs.getBlob(colnames[i]));
								}else{
									m.invoke(t, rs.getString(colnames[i]));
								}
								break;
							}
						}
					}
				}
				list.add(t);
			}
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}finally{
			closeAll(con, pstmt, rs,null);
		}
		return list;
	}

	/**
	 * 鏌ヨ
	 * @param <T> 娉涘瀷锛氬嵆浣犺寰楀埌鐨勯泦鍚堜腑瀛樼殑瀵硅薄鐨勭被鍨�
	 * @param sql: 鏌ヨ璇彞锛屽彲浠ュ惈鏈�?
	 * @param params: ?鎵�瀵瑰簲鐨勫弬鏁板�肩殑闆嗗悎
	 * @param c锛� 娉涘瀷绫诲瀷鎵�瀵瑰簲鐨勫弽灏勫璞�
	 * @return 锛氬瓨鍌ㄤ簡瀵硅薄鐨勯泦鍚�
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public <T> T findByOne(String sql,Class<T> c,List<Object> params) {
		Connection con=getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		T t = null;
		try {
			pstmt=con.prepareStatement(sql); //棰勭紪璇戝璞�
			setValues(pstmt, params); //璁剧疆鍗犱綅绗�
			rs=pstmt.executeQuery();  //鎵ц鏌ヨ璇彞锛屽緱鍒扮粨鏋滈泦

			Method[] ms=c.getMethods(); //鍙栧嚭杩欎釜鍙嶅皠瀹炰緥鐨勬墍鏈夋柟娉�
			
			String[] colnames=getColumnNames(rs); //鑾峰彇缁撴灉闆嗕腑鎵�鏈夊垪鐨勫垪鍚�
			String mname=null;  //鏂规硶鍚�
			String cname=null;  //鍒楀悕
			String ctypename=null; //绫诲瀷鍚�

			while(rs.next()){
				t=(T)c.newInstance(); //鍒涘缓鍙嶅皠绫荤殑瀹炰緥鍖栧璞�    Product t=(Product)c.newInstance();
				for(int i=0;i<colnames.length;i++){//寰幆鏂规硶鍚� ,鏍煎紡涓簊etXXXX鎴杇etXXX
					cname=colnames[i]; //鍙栧嚭鍒楀悕骞跺湪鍓嶉潰鍔犱笂set  setXXX
					cname="set"+cname.substring(0,1).toUpperCase()+cname.substring(1).toLowerCase();
					if(ms!=null&&ms.length>0){
						for(Method m:ms){//寰幆鍒楀悕
							mname=m.getName(); //鍙栧嚭鏂规硶鍚�

							if(cname.equals(mname)&&rs.getObject(colnames[i])!=null){//鍒ゆ柇鏂规硶鍚嶅拰鍒楀悕鏄惁涓�鏍凤紝鐩稿悓鍒欐縺娲绘柟娉曪紝娉ㄥ叆鏁版嵁      //鍙"set"+鏁版嵁鍒楀悕.equalsIgnoreCase锛堟柟娉曞悕锛夛紝鍒欐縺娲昏繖涓柟娉�
								//setXXX(String str); setXXX(int num); 婵�娲诲搴旂殑鏂规硶杩樺繀椤荤煡閬撳畠鐨勬暟鎹被鍨�
								ctypename=rs.getObject(colnames[i]).getClass().getName();//鑾峰彇褰撳墠鍒楃殑绫诲瀷鍚�

								if("java.lang.Integer".equals(ctypename)){
									m.invoke(t,rs.getInt(colnames[i])); //obj.setXX(xx);
								}else if("java.lang.String".equals(ctypename)){
									m.invoke(t, rs.getString(colnames[i]));
								}else if("java.math.BigInteger".equals(ctypename)){
									m.invoke(t, rs.getDouble(colnames[i]));
								}else if("java.math.BigDecimal".equals(ctypename)){
									try{
										m.invoke(t, rs.getInt(colnames[i]));
									}catch(Exception e1){
										m.invoke(t, rs.getDouble(colnames[i]));
									}
								}else if("java.sql.Timestamp".equals(ctypename)){
									m.invoke(t, rs.getString(colnames[i]));
								}else if("java.sql.Date".equals(ctypename)){
									m.invoke(t, rs.getString(colnames[i]));
								}else if("java.sql.Time".equals(ctypename)){
									m.invoke(t, rs.getString(colnames[i]));
								}else if("image".equals(ctypename)) {
									m.invoke(t,rs.getBlob(colnames[i]));
								}else{
									m.invoke(t, rs.getString(colnames[i]));
								}
								break;
							}
						}
					}
				}
			}
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}finally{
			closeAll(con, pstmt, rs,null);
		}
		return t;
	}
	/**
	 * 鏌ヨ鏁版嵁鐨勬柟娉�
	 * @param sql锛氳鎵ц鐨勬煡璇㈣鍙�
	 * @param params锛氬搴旂殑sql璇彞涓殑闂彿鐨勫��
	 * @return锛氭墍鏈夋弧瓒虫潯浠剁殑鏁版嵁鐨勯泦鍚� Map<String,String> key涓哄垪鍚�
	 */
	public List<Map<String,String>> find(String sql,List<Object> params){
		List<Map<String,String>> result=new ArrayList<Map<String,String>>();
		Connection con=getConnection();//鑾峰彇杩炴帴
		PreparedStatement pstmt=null;
		ResultSet rs=null;  
		try {
			//鑾峰彇杩炴帴
			con=this.getConnection();
			//棰勭紪璇憇ql璇彞
			pstmt=con.prepareStatement(sql);

			//缁欏崰浣嶇璧嬪��
			setValues(pstmt, params);

			//鎵ц璇彞骞惰幏鍙栬繑鍥炵殑缁撴灉闆�
			rs=pstmt.executeQuery();

			//鑾峰彇杩斿洖鐨勭粨鏋滈泦涓垪鐨勪俊鎭�
			String[] cols=getColumnNames(rs); //鑾峰彇缁撴灉闆嗕腑鎵�鏈夊垪鐨勫垪鍚�

			Map<String,String> map; //鐢ㄦ潵瀛樻斁涓�鏉¤褰曪紝浠ュ垪鍚嶄负key,瀵瑰簲鍒楃殑鍊间负value

			//鍥犱负灏佽鍦∕ap涓紝鑰宮ap鎴戜滑閫夌敤鍒楀悕涓簁ey,瀵瑰簲鍒楃殑鍊间负value,鍒欐垜浠渶瑕佽幏鍙栨墍鏈夊垪鐨勫垪鍚�
			while(rs.next()){
				map=new HashMap<String,String>();
				for(String col:cols){
					map.put(col,rs.getString(col));
				}
				result.add(map);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally{
			this.closeAll(con, pstmt, rs,null);
		}
		return result;
	}


	/**
	 * 鏌ヨ鏁版嵁鐨勬柟娉�
	 * @param sql锛氳鎵ц鐨勬煡璇㈣鍙�
	 * @param params锛氬搴旂殑sql璇彞涓殑闂彿鐨勫��
	 * @return锛氭墍鏈夋弧瓒虫潯浠剁殑鏁版嵁鐨勯泦鍚� Map<String,String> key涓哄垪鍚�
	 */
	public List<List<String>> finds(String sql,List<Object> params){
		List<List<String>> results=new ArrayList<List<String>>(); //瀛樻斁鎵�鏈夌殑璁板綍
		List<String> result; //瀛樻斁涓�鏉¤褰�
		Connection con=getConnection();//鑾峰彇杩炴帴
		PreparedStatement pstmt=null;
		ResultSet rs=null;  
		try {
			//鑾峰彇杩炴帴
			con=this.getConnection();
			//棰勭紪璇憇ql璇彞
			pstmt=con.prepareStatement(sql);

			//缁欏崰浣嶇璧嬪��
			setValues(pstmt, params);

			//鎵ц璇彞骞惰幏鍙栬繑鍥炵殑缁撴灉闆�
			rs=pstmt.executeQuery();

			//鑾峰彇杩斿洖鐨勭粨鏋滈泦涓垪鐨勪俊鎭�
			ResultSetMetaData rsmd=rs.getMetaData();

			//鍥犱负灏佽鍦∕ap涓紝鑰宮ap鎴戜滑閫夌敤鍒楀悕涓簁ey,瀵瑰簲鍒楃殑鍊间负value,鍒欐垜浠渶瑕佽幏鍙栨墍鏈夊垪鐨勫垪鍚�
			while(rs.next()){
				result=new ArrayList<String>();
				for(int i=0,len=rsmd.getColumnCount();i<len;i++){
					result.add(rs.getString(i+1));
				}
				results.add(result);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally{
			this.closeAll(con, pstmt, rs, null);
		}
		return results;
	}


	/**
	 * 澶氳〃鏌ヨ
	 * @param sql锛氭煡璇㈣鍙�
	 * @param params锛� 鏌ヨ璇彞涓�?鎵�瀵瑰簲鐨勫��
	 * @return锛氱粨鏋滈泦锛屽瓨鍦ㄤ竴涓狶ist琛ㄤ腑锛岀敤Map涓�瀵逛竴鐨勫瓨鏀�
	 * @throws SQLException
	 */
	public List<String> findList(String sql,List<Object> params){
		List<String> result=new ArrayList<String>(); //灏嗙粨鏋滀竴娆″瓨鍦╨ist涓繑鍥�
		Connection con=getConnection();//鑾峰彇杩炴帴
		PreparedStatement pstmt=null;
		ResultSet rs=null;  
		try {
			pstmt=con.prepareStatement(sql);
			setValues(pstmt, params);
			rs=pstmt.executeQuery();
			String[] colnames=getColumnNames(rs); //鑾峰彇缁撴灉闆嗕腑鎵�鏈夊垪鐨勫垪鍚�
			
			while(rs.next()){
				for(int i=0,len=colnames.length;i<len;i++){
					result.add(rs.getString(i+1));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}finally{
			this.closeAll(con, pstmt, rs ,null);
		}
		return result;
	}



	/**
	 * 
	 * @param sql  瑕佹墽琛岀殑sql璇彞
	 * @param objs 鎵цsql璇彞闇�瑕佺殑鍙傛暟
	 * @return  鍙栧嚭鏁版嵁搴撶殑鏁版嵁, key鏄瓧娈靛悕鎴栧瓧娈靛埆鍚�(灏忓啓瀛楁瘝), value搴斿瀛楁鐨勫��
	 */
	public Map<String,String> findMap(String sql,List<Object> objs){
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<String,String> results = null;
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql); // 3.sql鎵ц宸ュ叿
			setValues(pstmt, objs);
			rs = pstmt.executeQuery(); // 4.鎵цsql鍙栧埌杩斿洖鏁版嵁鐧界粨鏋滈泦
			ResultSetMetaData rsmd = rs.getMetaData(); // 鍏冩暟鎹�; 瀵硅薄鍙栧彇鍒扮殑缁撴灉闆嗘暟鎹殑鎻忚堪
			int cloumCount = rsmd.getColumnCount();
			if (rs.next()) { // 鍒ゆ柇缁撴灉闆嗘槸鍚﹁繕鏈夋暟鎹� (鏁版嵁鏄竴鏉¤褰曠殑鏂瑰紡鍙栧嚭)
				results = new HashMap<String,String>();
				for (int i = 1; i <= cloumCount; i++) {
					results.put(rsmd.getColumnName(i).toLowerCase(), rs.getString(i));
				}
			}
		}  catch (SQLException e) {
			throw new RuntimeException(e);
		}finally{
			this.closeAll(con, pstmt, rs ,null);
		}
		return results;
	}


	/**
	 * 瀛樿繃杩囩▼鍙傛暟璁剧疆
	 * @param cst
	 * @param params
	 */
	@SuppressWarnings("unchecked")
	public void setParams(CallableStatement cs,Map<Integer,Object> paramsIn,Map<Integer,String> paramsOut){
		int key=0; //瀵瑰簲鐨勯棶鍙风殑搴忓彿
		Object value=null;
		String typename=null;

		String attrType;
		Set keys;  //鎵�鏈夌殑閿�
		if(paramsIn!=null&&paramsIn.size()>0){
			keys=paramsIn.keySet();  //鍙栧嚭鎵�鏈夊叆鍙傜殑閿紝鍗冲叆鍙傚搴旂殑闂彿鐨勫簭鍙�
			if(keys!=null){
				Iterator iterator=keys.iterator();
				while(iterator.hasNext()){
					key=(Integer) iterator.next();
					value=paramsIn.get(key);      //1,88
					attrType=value.getClass().getName();

					//鍒ゆ柇鍊肩殑鏁版嵁绫诲瀷
					try {
						if("java.lang.Integer".equals(attrType)){
							cs.setInt(key,(Integer)value);
						}else if("java.lang.String".equals(attrType)){
							cs.setString(key,(String)value);
						}

					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		int typeId=0;

		if(paramsOut!=null&&paramsOut.size()>0){
			keys=paramsOut.keySet();  //鍙栧嚭鎵�鏈夊叆鍙傜殑閿紝鍗冲叆鍙傚搴旂殑闂彿鐨勫簭鍙�
			if(keys!=null){
				Iterator iterator=keys.iterator();
				while(iterator.hasNext()){
					key=(Integer) iterator.next();
					typename=(String) paramsOut.get(key);      //3,varchar  4, cursor

					//鍒ゆ柇鍊肩殑鏁版嵁绫诲瀷
					try {
						 if("int".equals(typename)){
							typeId=Types.INTEGER;
						}else if("double".equals(typename)){
							typeId=Types.NUMERIC;
						}else{
							typeId=Types.VARCHAR;
						}
						cs.registerOutParameter(key,typeId);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

}

