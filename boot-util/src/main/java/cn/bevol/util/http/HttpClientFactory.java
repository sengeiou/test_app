package cn.bevol.util.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class HttpClientFactory {

	private static final int SO_TIMEOUT = 3000;
	
	public static HttpClient get(){
		HttpClient instance = new DefaultHttpClient();
		instance.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
		return instance;
	}


	public static HttpClient get(int timeoutMS){
		HttpClient instance = new DefaultHttpClient();
		instance.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeoutMS);
		return instance;
	}
}
