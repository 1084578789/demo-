package com.yc.ctroller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yc.bean.Columns;
import com.yc.dao.ly.BeanUtils;

import biz.BizException;
import biz.ColumnsBiz;

/**
 * Servlet implementation class ColumnsServlet
 */
@WebServlet("/columns.s")
public class ColumnsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String op = request.getParameter("op");
		if("query".equals(op)){
			query(request,response);
		}else if("add".equals(op)){
			add(request,response);
		}else if("delete".equals(op)){
			delete(request,response);
		}
	}

	
	private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		   
		
	}


	private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String parentId2 = request.getParameter("parentId");
		
		System.out.println(parentId2);
		long parentId = 1;
		if("≥Ã–Ú»À…˙".equals(parentId2)){
			parentId=10;		
		}
		ColumnsBiz cbiz = new ColumnsBiz();
		Columns columns = BeanUtils.asBean(request, Columns.class);
		try {
			cbiz.add(columns,parentId);
		} catch (BizException e) {
			e.printStackTrace();
			request.setAttribute("msg", e.getMessage());
		}finally{
			query(request,response);
		}
		
	}


	private void query(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
			ColumnsBiz cbiz = new ColumnsBiz();
			Columns columns = BeanUtils.asBean(request, Columns.class);
			request.setAttribute("columnsList", cbiz.find(columns));
			request.getRequestDispatcher("category.jsp").forward(request, response);;
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		doGet(request, response);
	}

}
