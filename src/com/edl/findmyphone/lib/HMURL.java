package com.edl.findmyphone.lib;

public class HMURL {

	 public static String BASE_HTTP = "http://192.168.1.4:8088/findMyPhone";
	 
	 //mina
	 public static String BASE_HM_HOST = "192.168.1.4";
	 public static int BASE_HM_PORT = 9080;


	/**
	 * 登录部分的url地址
	 */
	public final static String URL_HTTP_LOGIN = BASE_HTTP + "/phone/login.do";
	/**
	 * 注册url
	 */	public final static String URL_HTTP_REGISTER = BASE_HTTP + "/phone/register.do";

	/**
	 * 经纬度上传
	 */
	public final static String URL_HTTP_LOCUPLOAD = BASE_HTTP + "/location/upload.do";
	/**
	 * 定位开关
	 */
	public final static String URL_HTTP_SWITCH = BASE_HTTP + "/location/switch.do";



}
