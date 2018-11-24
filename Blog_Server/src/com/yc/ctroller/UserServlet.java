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
 * �û�Servlet���� ��¼.ע��.��ѯ���˳����������룬ʹ��op �ֶα�ʶҵ���������     1������input  2.ת����ʱ�򴫵�
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
		response.getWriter().append("ɾ���ɹ�");
	}


	private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		User user = BeanUtils.asBean(request, User.class);
		String msg = null;
		
		try {
			ubiz.save(user);
			msg = "�û���Ϣ����ɹ�";
			
		} catch (BizException e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
			
		response.getWriter().append(msg);
		
	}


	private void find(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String id = request.getParameter("id");
		User user = ubiz.findById(id);
		//��user���ظ�����
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
		//����ҳ�洫�صĲ���
		//���������ص� user������
		User user = BeanUtils.asBean(request, User.class);
		//String username = user.getName();
		//���� ubiz.add ���������û���ӵ����ݿ�
		try {
			ubiz.add(user,repwd);
			//��ʽ1��
			//request.getRequestDispatcher("user.s?op=query").forward(request, response);
			//��ʽ2��
			//query(request, response);
		} catch (BizException e) {
			e.printStackTrace();
			request.setAttribute("msg", e.getMessage());
		}finally{
			//����ɹ�����ת�� �û���ѯ����
			//����ɹ���Ҳ��ת�� �û���ѯ����
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
			//ʧ��
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}
		
		if(user == null){
			request.setAttribute("msg", "�û������������");
			//ʧ��
			//request.setAttribute("error", "yes");
			request.getRequestDispatcher("login.jsp").forward(request, response);
			
		}else{
			//�ɹ�
			//���û���Ϣ���浽�Ự��
			request.getSession().setAttribute("loginedUser",user);
			//�ض���      ��ֹҳ��ˢ�µ�ʱ�򣬱������ظ��ύ
			response.sendRedirect("index.jsp");
			//request.getRequestDispatcher("index.jsp").forward(request, response);
			//System.out.println("�ɹ�");
		}
	}
	
	
	
}
