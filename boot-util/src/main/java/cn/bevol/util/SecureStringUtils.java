package cn.bevol.util;

import org.apache.commons.lang3.StringUtils;

public class SecureStringUtils {
	
	public static String transferInput(String input){
		if(StringUtils.isNotBlank(input)){
			return input.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("\'", "&#39;");
		}
		
		return input;
	}
	
	public static String filterInput(String input){
		String regex = "[;\'\"{}\\[\\]\\(\\)<>]";
		return input.replaceAll(regex, "");
	}

}
