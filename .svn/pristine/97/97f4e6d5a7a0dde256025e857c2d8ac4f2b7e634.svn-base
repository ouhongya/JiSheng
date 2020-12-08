package com.fh.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import com.fh.service.app.SettingService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;



import org.dom4j.Document;   
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;   
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 通过短信接口发送短信
 */
@Component
public class SmsUtil {


/*	public static void main(String [] args) {
		
		sendSms2("13511111111","您的验证码是：1111。请不要把验证码泄露给其他人。");
		//sendSmsAll(List<PageData> list)
		
		//sendSms1();
	}*/

	@Autowired
	private  SettingService settingInfoService;
	private static SettingService settingService;

	@PostConstruct
	public void init() {
		settingService = settingInfoService;
	}

	//短信商 一  http://www.dxton.com/ =====================================================================================
	/**
	 * 给一个人发送单条短信
	 * @param mobile 手机号
	 * @param code  短信内容
	 */
 	public static  String sendSms1(String mobile,String code){

	    /*String account = "", password = "";
	    String strSMS1 = Tools.readTxtFile(Const.SMS1);			//读取短信1配置
		if(null != strSMS1 && !"".equals(strSMS1)){
			String strS1[] = strSMS1.split(",fh,");
			if(strS1.length == 2){
				account = strS1[0];
				password = strS1[1];
			}
		}*/
 		String PostData = "";
		List<PageData> settings = null;

		try {
			settings = settingService.findAllSystemSettings();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String templateCode = null;
			String password = null;
			String username = null;
			for (PageData setting : settings) {
				if((Integer) setting.get("type")==3){
					 username =(String) setting.get("username");
					 password =(String) setting.get("password");
					 templateCode =(String) setting.get("ip");
				}
			}
			String phoneCode = templateCode.replace("【变量】", code);
			PostData = "account="+username+"&password="+password+"&mobile="+mobile+"&content="+URLEncoder.encode(phoneCode,"utf-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("短信提交失败");
		}
		 //System.out.println(PostData);
 	     String ret = SMS(PostData, "http://sms.106jiekou.com/utf8/sms.aspx");
 	     return ret;
 	     /*
		   100			发送成功
		   101			验证失败
		   102			手机号码格式不正确
		   103			会员级别不够
		   104			内容未审核
		   105			内容过多
		   106			账户余额不足
		   107			Ip受限
		   108			手机号码发送太频繁，请换号或隔天再发
		   109			帐号被锁定
		   110			发送通道不正确
		   111			当前时间段禁止短信发送
		   120			系统升级
			*/



	}

//	public static  String SMS(String postData, String postUrl) {
//		try {
//
//			//发送POST请求
//			URL url = new URL(postUrl);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestMethod("POST");
//			conn.setConnectTimeout(5000);//设置连接超时:500ms
//			conn.setReadTimeout(5000);//设置读取超时:500ms
//			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//			conn.setRequestProperty("Connection", "Keep-Alive");
//			conn.setUseCaches(false);
//			conn.setDoOutput(true);
//
//			conn.setRequestProperty("Content-Length", "" + postData.length());
//			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
//			out.write(postData);
//			out.flush();
//			out.close();
//
//			//获取响应状态
//			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//				System.out.println("connect failed!");
//				return "";
//			}
//			//获取响应内容体
//			String line, result = "";
//			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
//			while ((line = in.readLine()) != null) {
//				result += line + "\n";
//			}
//			in.close();
//			return result;
//		} catch (IOException e) {
//			e.printStackTrace(System.out);
//		}
//		return "";
//	}
	 //===================================================================================================================





	public static  String SMS(String postData, String postUrl) {
		HttpURLConnection conn = null;
		InputStream is = null;
		InputStreamReader reader = null;
		BufferedReader br = null;
		String str = "";
		try {
			//发送POST请求
			URL url = new URL(postUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-agent","Mozilla/4.0");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);//默觉得false的，所以须要设置
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();
			OutputStream outStream = conn.getOutputStream();
			DataOutputStream out = new DataOutputStream(outStream);
			out.writeBytes(postData);
			out.close();

			is = conn.getInputStream();
			reader = new InputStreamReader(is, "UTF-8");
			br = new BufferedReader(reader);
			String readLine = "";
			while((readLine=br.readLine())!=null){
				str+=readLine+"\n";
			}
			return str;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		return "";
	}




	/**
	 *
	 * 短信商 二  http://www.ihuyi.com/ =====================================================================================
	 *
	 */

	private static  String Url = "http://106.ihuyi.com/webservice/sms.php?method=Submit";
	
	
	
	/**
	 * 给一个人发送单条短信
	 * @param mobile 手机号
	 * @param code  短信内容
	 */
	public static  void sendSms2(String mobile,String code){
		HttpClient client = new HttpClient(); 
		PostMethod method = new PostMethod(Url); 
			
		client.getParams().setContentCharset("UTF-8");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");

	    String content = new String(code);  
	    
	    String account = "", password = "";
	    String strSMS2 = Tools.readTxtFile(Const.SMS2);			//读取短信2配置
		if(null != strSMS2 && !"".equals(strSMS2)){
			String strS2[] = strSMS2.split(",fh,");
			if(strS2.length == 2){
				account = strS2[0];
				password = strS2[1];
			}
		}
	    
		NameValuePair[] data = {//提交短信
		    new NameValuePair("account", account), 
		    new NameValuePair("password", password), 			//密码可以使用明文密码或使用32位MD5加密
		    new NameValuePair("mobile", mobile), 
		    new NameValuePair("content", content),
		};
		
		method.setRequestBody(data);
		
		try {
			client.executeMethod(method);
			
			String SubmitResult =method.getResponseBodyAsString();
					
			Document doc = DocumentHelper.parseText(SubmitResult); 
			Element root = doc.getRootElement();


			code = root.elementText("code");
			String msg = root.elementText("msg");
			String smsid = root.elementText("smsid");
			
			
			System.out.println(code);
			System.out.println(msg);
			System.out.println(smsid);
			
			if(code == "2"){
				System.out.println("短信提交成功");
			}
			
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}	
		
	}
	
	/**
	 * 给多个人发送单条短信
	 * @param list 手机号验证码
	 */
	public static  void sendSmsAll(List<PageData> list){
		String code;
		String mobile;
		for(int i=0;i<list.size();i++){
			code=list.get(i).get("code").toString();
			mobile=list.get(i).get("mobile").toString();
			sendSms2(mobile,code);
		}
	}
	// =================================================================================================
	
	
	
}

