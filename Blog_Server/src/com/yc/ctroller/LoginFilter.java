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
		
		//����ת��
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		
		/**
		 * ʵ�ַ���Ȩ�޿���
		 */
		//��ȡ��ǰ������Դ��
		String path = httpRequest.getServletPath();
		//�ж���Դ���Ƿ�Ҫ������
		if(path.endsWith("login.jsp") || path.endsWith("user.s")){
			chain.doFilter(request, response);
			return;
		}
		
		if(httpRequest.getSession().getAttribute("loginedUser")!=null){
			//�Ѿ���¼
			//����ҵ�����ִ�� ����������doFilter
			chain.doFilter(request, response);
		}else{
			//δ��¼����ת��¼ҳ
			request.setAttribute("msg", "���ȵ�¼ϵͳ");
			httpRequest.getRequestDispatcher("login.jsp").forward(request, response);
		}
		
		
	}

	
	public void init(FilterConfig fConfig) throws ServletException {
		
	}

}
