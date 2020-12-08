package com.fh.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fh.util.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.fh.entity.system.User;

import java.io.IOException;
import java.io.PrintWriter;
import com.alibaba.fastjson.JSON;

/**
 * 
* 类名称：LoginHandlerInterceptor.java
* 类描述： 
* @author FH
* 作者单位： 
* 联系方式：
* 创建时间：2015年1月1日
* @version 1.6
 */
public class LoginHandlerInterceptor extends HandlerInterceptorAdapter{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		String path = request.getServletPath();
		if(path.matches(Const.NO_INTERCEPTOR_PATH)){
			if (path.contains("/api/")) {
				//排除登录接口以外的其他接口均需要先登录才能访问
				if (!path.equals("/api/v1/login") && !path.equals("/api/v1/getSmsCaptcha") && !path.equals("/api/v1/excel")&& !path.equals("/api/v1/downExcel")&& !path.equals("/api/v1/readExcel") && !path.equals("/api/v1/downunitExcel") && !path.equals("/api/v1/readunitExcel") && !path.equals("/api/v1/downloadTemplate") && !path.equals("/api/v1/uploadExcel")  && !path.equals("/api/v1/downloaddatabase") && !path.equals("/api/v1/uploadImage") &&!path.equals("/api/v1/uploadImageReport") && !path.equals("/api/v1/downloadcheckpicture")  && !path.equals("/api/v1/downloaddatabasecheck")&& !path.equals("/api/v1/downloaddatabasecheckone")) {
					ResultModel resultModel = null;
					String uid = request.getParameter("uid");
					if (uid == null || "".equals(uid)) {
						resultModel = ResultModel.failure("请先登录才能调用接口");
					}


					boolean b = checkKey(request.getParameter("FKEYNAME"), request.getParameter("FKEY"));
					if(!b){
						resultModel = ResultModel.failure("加密参数有问题");
					}

					if (null != resultModel) {
						wirteJsonToResponse(response, resultModel);
						return false;
					}
				}
			}
			return true;
		}else{
			//shiro管理的session
			Subject currentUser = SecurityUtils.getSubject();  
			Session session = currentUser.getSession();
			User user = (User)session.getAttribute(Const.SESSION_USER);
			if(user!=null){
				path = path.substring(1, path.length());
				boolean b = Jurisdiction.hasJurisdiction(path);
				if(!b){
					response.sendRedirect(request.getContextPath() + Const.LOGIN);
				}
				return b;
			}else{
				//登陆过滤
				response.sendRedirect(request.getContextPath() + Const.LOGIN);
				return false;		
				//return true;
			}
		}

	}


	private void wirteJsonToResponse(HttpServletResponse resp, Object obj) throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("application/json; charset=utf-8");
		PrintWriter writer = resp.getWriter();
		writer.write(JSON.toJSONString(obj));
	}



	public  boolean checkKey(String paraname, String FKEY){
		paraname = (null == paraname)? "":paraname;

		String a = DateUtil.getDays();

		return MD5.md5(paraname+DateUtil.getDays()+",fh,").equals(FKEY);
	}


}

