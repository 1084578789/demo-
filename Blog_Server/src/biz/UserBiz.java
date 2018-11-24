package biz;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yc.bean.User;
import com.yc.dao.ly.DBHelper;



public class UserBiz {
	
	
	/**
	 * ��¼����
	 */
	public User login(String username,String userpwd) throws BizException{
		
		if(username == null || username.trim().isEmpty()){
			throw new BizException("����д�û���");
		}
		if(userpwd == null || userpwd.trim().isEmpty()){
			throw new BizException("����д����");
		}
		
		//DBHelper db = new DBHelper();
		//List<Object> params = new ArrayList<Object>();
		//params.add(username);
		//params.add(userpwd);
		String sql = "select * from user where account = ? and pwd = ?";
		return DBHelper.unique(sql, User.class, username,userpwd);
	}

	public List<User> findAll() {
		return DBHelper.select("select * from user", User.class); 
	}

	public void add(User user,String repwd) throws BizException{
		/*
		 * ������û�������֤
		 */
		if(user.getName() == null || user.getName().trim().isEmpty()){
			throw new BizException("����д����");
		}
		if(user.getAccount() == null || user.getAccount().trim().isEmpty()){
			throw new BizException("����д�û���");
		}
		if(user.getTel() == null || user.getTel().trim().isEmpty()){
			throw new BizException("����д�ֻ�����");
		}
		if(user.getPwd() == null || user.getPwd().trim().isEmpty()){
			throw new BizException("����д����");
		}	
		if(!user.getTel().matches("1[34578][0-9]{9}")){
			throw new BizException("��������Ч�ĵ绰����");
		}
		
		String regex = "[A-Za-z\\w]{5,18}";
		if(!user.getPwd().matches(regex)){
			throw new BizException("�벻Ҫ������Ƿ��ַ������룬�ҳ�����5~18");
		}
		
		if(!user.getPwd().equals(repwd)){
			throw new BizException("������������벻һ��");
		}
		
		
		
		//��ǰʱ��
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp time = Timestamp.valueOf(sdf.format(date));
		
		String sql = "insert into user(name,account,tel,pwd,createDate) value(?,?,?,?,?)";
		DBHelper.insert(sql, user.getName(),user.getAccount(),user.getTel(),user.getPwd(),time);
	}

	public Object find(User user) {
		//�ǵ�1=1 ����ո�
		String sql = "select * from user where 1=1";
		ArrayList<Object> params = new ArrayList<Object>();
		if(user.getAccount()!=null && user.getAccount().trim().isEmpty() == false){
			sql += " and account like concat('%',?,'%')";
			params.add(user.getAccount());
		}
		if(user.getName()!=null && user.getName().trim().isEmpty() == false){
			sql += " and name like ?";
			params.add("%"+user.getName()+"%");
		}
		if(user.getTel()!=null && user.getTel().trim().isEmpty() == false){
			sql += " and tel like ?";
			params.add("%"+user.getTel()+"%");
		}
		return DBHelper.select(sql, params);
	}

	public User findById(String id) {
		String sql = "select * from user where id=?";
		return DBHelper.unique(sql, User.class,id);
	}

	public void save(User user) throws BizException {
		if(user.getAccount() == null || user.getAccount().trim().isEmpty()){
			throw new BizException("����д�û���");
		}
		if(user.getName() == null || user.getName().trim().isEmpty()){
			throw new BizException("����д����");
		}
		
		String sql = "update user set name=?,account=?,tel=? where id=?";
		DBHelper.update(sql,
			user.getName(),user.getAccount(),user.getTel(),user.getId());
		
	}

	public void delete(String id) {
		
		String sql = "delete from user where id=?";
		DBHelper.update(sql, id);
		
	}

	
}
