package com.fh.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fh.controller.base.BaseController;
import com.fh.util.FileUtil;

/**
 * 登录验证过滤器
 */
public class LoginFilter extends BaseController implements Filter {

	/**
	 * 初始化
	 */
	@Override
	public void init(FilterConfig fc) throws ServletException {
		//FileUtil.createDir("d:/FH/topic/");
	}
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
						 FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		// 调用下一过滤器
		chain.doFilter(req, res);
	}
}
