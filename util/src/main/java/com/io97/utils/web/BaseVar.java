package com.io97.utils.web;

public interface BaseVar {
	// json返回用
	public static final String RESULT_KEY = "res";
	public static final String RESULT_SUCCESS = "success";
	public static final String RESULT_FAIL = "fail";
	public static final String RESULT_NO_AUTH = "noauth";
	public static final String RESULT_NO_LOGIN = "nologin";
	public static final String RESULT_INFO = "info";
	public static final String RESULT_DATA = "data";

	// session&&context
	public static final String SESSION_LOGIN_MANAGER = "loginManger";
	public static final String SESSION_TOP_MENU = "topMenu";
	public static final String SESSION_SECOND_MENU = "secondMenu";
	public static final String CONTEXT_ALL_MENU = "allMenu";
	public static final String SESSION_USER_RIGHT = "userRight";
	public static final String SESSION_USER_ROLE = "userRole";
}
