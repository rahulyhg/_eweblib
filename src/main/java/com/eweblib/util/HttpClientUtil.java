package com.eweblib.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class HttpClientUtil {

	private static Logger logger = LogManager.getLogger(HttpClientUtil.class);

	public static String doGet(String url, Map<String, Object> parameters, String urlEncoding)  {

		// 
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = null;
			URIBuilder builder = new URIBuilder(url);
			if (parameters != null) {
				Set<String> keys = parameters.keySet();
				for (String key : keys) {
					
					if (parameters.get(key) != null) {
						builder.setParameter(key, parameters.get(key).toString());
					}
				}
			}
			URI uri = builder.build();
			
			if(EweblibUtil.isValid(urlEncoding)){
				url = URLEncoder.encode(url, urlEncoding);
			}
			// builder.
			HttpGet httpget = new HttpGet(uri);

			response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity, "UTF-8");
			}

		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException when try to get data from ".concat(url), e);
		} catch (IOException e) {
			logger.error("IOException when try to get data from ".concat(url), e);
		} catch (URISyntaxException e) {
			logger.error("URISyntaxException when try to get data from ".concat(url), e);
		}
		return null;
	
		
	}

	/**
	 * 
	 * Request a get request with data paramter
	 * 
	 * @param url
	 * @param parameters
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String doGet(String url, Map<String, Object> parameters) {
		return doGet(url, parameters, null);

	}
	
	
	public static String doPost(String url, Map<String, Object> parameters, String urlEncoding) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		HttpPost method = new HttpPost(url);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		Set<String> keys = parameters.keySet();
		for (String key : keys) {

			if (parameters.get(key) != null) {
				nameValuePairs.add(new BasicNameValuePair(key, parameters.get(key).toString()));
			}

		}
		UrlEncodedFormEntity rentity = null;
		try {
			rentity = new UrlEncodedFormEntity(nameValuePairs, urlEncoding);
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException when try to encode data for ".concat(url), e);
		}
		try {
			method.setEntity(rentity);
			response = httpClient.execute(method);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity,  "UTF-8");
			}

		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException when try to post data to ".concat(url), e);
		} catch (IOException e) {
			logger.error("IOException when try to post data to ".concat(url), e);
		}
		return null;
	}

	public static String doPost(String url, Map<String, Object> parameters) {
		return doPost(url, parameters, "UTF-8");
	}


	
	public static String doBodyPost(String url, String data) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		HttpPost method = new HttpPost(url);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		if (data == null) {
			data = "";
		}

		try {
			StringEntity stringEntity = new StringEntity(data, "UTF-8");

			method.setEntity(stringEntity);
			response = httpClient.execute(method);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity, "UTF-8");
			}

		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException when try to post data to ".concat(url), e);
		} catch (IOException e) {
			logger.error("IOException when try to post data to ".concat(url), e);
		}
		return null;
	}	
	

}
