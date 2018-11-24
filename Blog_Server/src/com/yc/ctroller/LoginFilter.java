package com.yc.ctroller;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;


@WebFilter(urlPatterns={"*.jsp","*.s"})
public class LoginFilter implements Filter {


	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		//向下转型
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		
		/**
		 * 实现访问权限控制
		 */
		//获取当前访问资源名
		String path = httpRequest.getServletPath();
		//判断资源名是否要被拦截
		if(path.endsWith("login.jsp") || path.endsWith("user.s")){
			chain.doFilter(request, response);
			return;
		}
		
		if(httpRequest.getSession().getAttribute("loginedUser")!=null){
			//已经登录
			//正常业务必须执行 过滤器链的doFilter
			chain.doFilter(request, response);
		}else{
			//未登录，跳转登录页
			request.setAttribute("msg", "请先登录系统");
			httpRequest.getRequestDispatcher("login.jsp").forward(request, response);
		}
		
		
	}

	
	public void init(FilterConfig fConfig) throws ServletException {
		
	}

}
