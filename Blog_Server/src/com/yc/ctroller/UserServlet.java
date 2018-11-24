package com.yc.ctroller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.yc.bean.User;
import com.yc.dao.ly.BeanUtils;

import biz.BizException;
import biz.UserBiz;

/**
 * 用户Servlet包含 登录.注册.查询，退出，忘记密码，使用op 字段标识业务操作类型     1：隐藏input  2.转发的时候传的
 */
@WebServlet("/user.s")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserBiz ubiz = new UserBiz();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String op = request.getParameter("op");
		if("login".equals(op)){
			login(request,response);
		}else if("query".equals(op)){
			query(request,response);
		}else if("add".equals(op)){
			add(request,response);
		}else if("find".equals(op)){
			find(request,response);
		}else if("save".equals(op)){
			save(request,response);
		}else if("delete".equals(op)){
			delete(request,response);
		}
	}
	

	private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String id = request.getParameter("id");
		
		ubiz.delete(id);
		response.getWriter().append("删除成功");
	}


	private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		User user = BeanUtils.asBean(request, User.class);
		String msg = null;
		
		try {
			ubiz.save(user);
			msg = "用户信息保存成功";
			
		} catch (BizException e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
			
		response.getWriter().append(msg);
		
	}


	private void find(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String id = request.getParameter("id");
		User user = ubiz.findById(id);
		//将user返回给界面
		String userString = JSON.toJSONString(user);
		response.getWriter().append(userString);
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		doGet(request, response);
	}
	
	private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		
		String repwd = request.getParameter("repwd");
		//接收页面传回的参数
		//将参数加载到 user对象中
		User user = BeanUtils.asBean(request, User.class);
		//String username = user.getName();
		//调用 ubiz.add 方法，将用户添加到数据库
		try {
			ubiz.add(user,repwd);
			//方式1：
			//request.getRequestDispatcher("user.s?op=query").forward(request, response);
			//方式2：
			//query(request, response);
		} catch (BizException e) {
			e.printStackTrace();
			request.setAttribute("msg", e.getMessage());
		}finally{
			//如果成功则跳转到 用户查询界面
			//如果成功，也跳转到 用户查询界面
			query(request, response);
		}
	}

	private void query(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		User user = BeanUtils.asBean(request, User.class);
		request.setAttribute("userList", ubiz.find(user));
		request.getRequestDispatcher("manage-user.jsp").forward(request, response);
	}
	
	private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String username = request.getParameter("username");
		String userpwd = request.getParameter("userpwd");
		
		
		//Map<String, String> user = null;
		User user = null;
		try {
			user = ubiz.login(username, userpwd);
		} catch (BizException e) {
			e.printStackTrace();
			request.setAttribute("msg", e.getMessage());
			//失败
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}
		
		if(user == null){
			request.setAttribute("msg", "用户名或密码错误");
			//失败
			//request.setAttribute("error", "yes");
			request.getRequestDispatcher("login.jsp").forward(request, response);
			
		}else{
			//成功
			//将用户信息保存到会话中
			request.getSession().setAttribute("loginedUser",user);
			//重定向      防止页面刷新的时候，表单数据重复提交
			response.sendRedirect("index.jsp");
			//request.getRequestDispatcher("index.jsp").forward(request, response);
			//System.out.println("成功");
		}
	}
	
	
	
}
