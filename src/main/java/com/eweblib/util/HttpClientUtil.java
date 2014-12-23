package com.eweblib.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.eweblib.bean.vo.LBS;
import com.eweblib.cfg.ConfigManager;
import com.eweblib.exception.ResponseException;

public class HttpClientUtil {

	private static Logger logger = LogManager.getLogger(HttpClientUtil.class);

	public static String doGet(String url, Map<String, Object> parameters, String urlEncoding) {

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

			if (EweblibUtil.isValid(urlEncoding)) {
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

		if (parameters != null) {
			Set<String> keys = parameters.keySet();
			for (String key : keys) {

				if (parameters.get(key) != null) {
					nameValuePairs.add(new BasicNameValuePair(key, parameters.get(key).toString()));
				}

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
				return EntityUtils.toString(entity, "UTF-8");
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

	public static void downloadFile(String url, Map<String, Object> parameters, String savePath) {

		downloadFile(url, parameters, savePath, "UTF-8");
	}

	public static void downloadFile(String url, Map<String, Object> parameters, String savePath, String urlEncoding) {

		//
		new File(savePath).getParentFile().mkdirs();
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

			if (EweblibUtil.isValid(urlEncoding)) {
				url = URLEncoder.encode(url, urlEncoding);
			}
			// builder.
			HttpGet httpget = new HttpGet(uri);

			response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				entity.writeTo(new FileOutputStream(new String(savePath.getBytes(), "gbk")));

			}

		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException when try to get data from ".concat(url), e);
		} catch (IOException e) {
			logger.error("IOException when try to get data from ".concat(url), e);
		} catch (URISyntaxException e) {
			logger.error("URISyntaxException when try to get data from ".concat(url), e);
		}

	}

	public static LBS getLngAndLat(String address) {

		return getLngAndLat(address, ConfigManager.getProperty("baidu_map_key"));

	}

	public static LBS getLngAndLat(String address, String key) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("address", address);
		params.put("output", "json");
		if (key == null) {
			params.put("ak", ConfigManager.getProperty("baidu_map_key"));
		} else {
			params.put("ak", key);
		}
		Map<String, Object> location = new HashMap<String, Object>();
		Map<String, Object> result = null;
		try {
			String res = HttpClientUtil.doPost("http://api.map.baidu.com/geocoder/v2/", params);
			result = EweblibUtil.toMap(res);

			if (result != null && !EweblibUtil.isEmpty(result.get("result"))) {
				Map<String, Object> locationResult = (Map<String, Object>) result.get("result");
				if (!EweblibUtil.isEmpty(locationResult.get("location"))) {
					location = (Map<String, Object>) locationResult.get("location");

				}

			}
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseException(e.getMessage());
		}

		if (result != null) {
			Object status = result.get("status");
			if (status == null || !(status.toString().equalsIgnoreCase("0.0") || status.toString().equalsIgnoreCase("0"))) {
				throw new ResponseException("百度地图异常，返回状态码: " + status + " , 请查阅http://developer.baidu.com/map/webservice-geocoding.htm, 8.返回码状态表");
			}
		}

		LBS lbs = new LBS();
		lbs.setAddress(address);

		if (!EweblibUtil.isEmpty(location)) {
			lbs.setLng(EweblibUtil.getDouble(location.get("lng"), null));
			lbs.setLat(EweblibUtil.getDouble(location.get("lat"), null));
		}

		return lbs;
	}

	public static Map<String, Object> convertLngAndLatToBaidu(Double lng, Double lat, String key, String from) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("from", from);
		params.put("output", "json");
		if (key == null) {
			params.put("ak", ConfigManager.getProperty("baidu_map_key"));
		} else {
			params.put("ak", key);
		}

		params.put("coords", lng + "," + lat);

		Map<String, Object> location = new HashMap<String, Object>();
		Map<String, Object> result = null;
		try {
			String res = HttpClientUtil.doPost("http://api.map.baidu.com/geoconv/v1/", params);
			result = EweblibUtil.toMap(res);

			if (result != null && !EweblibUtil.isEmpty(result.get("result"))) {
				List<Map<String, Object>> locationResult = (List<Map<String, Object>>) result.get("result");
				if (!EweblibUtil.isEmpty(locationResult)) {
					location = (Map<String, Object>) locationResult.get(0);

				}

			}
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseException(e.getMessage());
		}

		if (result != null) {
			Object status = result.get("status");
			if (status == null || !(status.toString().equalsIgnoreCase("0.0") || status.toString().equalsIgnoreCase("0"))) {
				throw new ResponseException("百度地图异常，返回状态码: " + status + " , 请查阅http://developer.baidu.com/map/webservice-geocoding.htm, 8.返回码状态表");
			}
		}
		return location;
	}

	public static Map<String, Object> getAddressByLngAndLat(String lng, String lat, String key) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("location", lng + "," + lat);
		params.put("output", "json");
		params.put("coord_type", "bd09ll");
		if (key == null) {
			params.put("ak", ConfigManager.getProperty("baidu_map_key"));
		} else {
			params.put("ak", key);
		}
		String res = null;
		try {
			res = HttpClientUtil.doGet("http://api.map.baidu.com/telematics/v3/reverseGeocoding", params);
		} catch (Exception e) {
			logger.error(e);
		}

		Map<String, Object> result = EweblibUtil.toMap(res);

		return result;
	}

	public static Map<String, Object> getAddressByLngAndLatV2(String lng, String lat, String key) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("location", lat + "," + lng);
		params.put("output", "json");
		params.put("coord_type", "bd09ll");
		params.put("pois", "0");
		if (key == null) {
			params.put("ak", ConfigManager.getProperty("baidu_map_key"));
		} else {
			params.put("ak", key);
		}
		String res = null;
		try {
			res = HttpClientUtil.doGet("http://api.map.baidu.com/geocoder/v2/", params);
		} catch (Exception e) {
			logger.error(e);
		}

		Map<String, Object> result = EweblibUtil.toMap(res);
		System.out.println(result.get("result"));

		return result;
	}

}
