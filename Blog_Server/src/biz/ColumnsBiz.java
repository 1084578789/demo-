package biz;

import java.util.ArrayList;

import com.yc.bean.Columns;
import com.yc.bean.User;
import com.yc.dao.ly.DBHelper;

public class ColumnsBiz {

	public Object find(Columns columns) {
		String sql = "select * from columns where 1=1";
		ArrayList<Object> params = new ArrayList<Object>();
		/*if( columns.getColumnName()!=null && columns.getColumnName().trim().isEmpty()==false ){
			sql += " and columnName like ?";
			params.add("%"+columns.getColumnName()+"%");
		}*/
		return DBHelper.select(sql, params);
	}

	public void add(Columns columns,long parentId) throws BizException{
		if( columns.getColumnName()== null || columns.getColumnName().trim().isEmpty() ){
			throw new BizException("����д��Ŀ����");
		}
		if( columns.getAliasName() == null || columns.getAliasName().trim().isEmpty() ){
			throw new BizException("����д��Ŀ����");
		}
		if( columns.getKeyWords() == null || columns.getKeyWords().trim().isEmpty() ){
			throw new BizException("����д�ؼ���");
		}		
		String sql = "insert into columns(columnName,aliasName,parentId,keyWords,description) value (?,?,?,?,?)";
		DBHelper.insert(sql, columns.getColumnName(),columns.getAliasName(),
				1,columns.getKeyWords(),columns.getDescription());
	}
	
}
