package com.eweblib.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.eweblib.annotation.column.BooleanColumn;
import com.eweblib.annotation.column.CopyColum;
import com.eweblib.annotation.column.DateColumn;
import com.eweblib.annotation.column.DoubleColumn;
import com.eweblib.annotation.column.FloatColumn;
import com.eweblib.annotation.column.IntegerColumn;
import com.eweblib.annotation.column.ObjectColumn;
import com.eweblib.bean.BaseEntity;
import com.eweblib.cfg.ConfigManager;
import com.eweblib.exception.ResponseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.thoughtworks.xstream.XStream;

public class EweblibUtil {

	private static Logger logger = LogManager.getLogger(EweblibUtil.class);

	public static String concat(String symbole, String[] concats) {

		StringBuffer sb = new StringBuffer();

		int i = 0;
		for (String concat : concats) {
			if (i == concats.length - 1) {
				sb.append(concat);
			} else {
				sb.append(concat).append(symbole);
			}
		}
		return sb.toString();
	}

	public static boolean isEmpty(Object param) {

		if (param == null) {
			return true;
		}

		if (param instanceof Map) {
			return ((Map) param).isEmpty();
		}
		if (param instanceof List) {
			return ((List) param).isEmpty();
		}
		String parameter = param.toString();
		if (parameter.trim().length() == 0) {
			return true;
		}

		if ("null".equalsIgnoreCase(parameter)) {
			return true;
		}

		return false;
	}

	public static Integer getInteger(Object value, int defaultValue) {
		Integer result = null;

		if (isEmpty(value)) {
			result = defaultValue;
		} else {
			try {
				result = (int) Float.parseFloat(String.valueOf(value));
			} catch (NumberFormatException e) {
				try {
					result = Integer.parseInt(String.valueOf(value));
				} catch (NumberFormatException e1) {
					logger.error(String.format("Integer parameter illegal [%s]", value), e);

				}
			}

		}
		if (result == null)
			result = defaultValue;
		return result;
	}

	public static Float getFloat(Object value, Float defaultValue) {
		Float result = null;

		if (isEmpty(value)) {
			result = defaultValue;
		} else {
			try {
				result = Float.parseFloat(String.valueOf(value));
			} catch (NumberFormatException e) {

				logger.error(String.format("Integer parameter illegal [%s]", value), e);
				throw new ResponseException("ILEGAL_PARAMTERS");

			}

		}
		if (result == null)
			result = defaultValue;
		return result;
	}

	public static Double getDouble(Object value, Double defaultValue) {
		Double result = null;

		if (isEmpty(value)) {
			result = defaultValue;
		} else {
			try {
				result = Double.parseDouble(String.valueOf(value));

			} catch (NumberFormatException e) {

				logger.error(String.format("Integer parameter illegal [%s]", value), e);

			}

		}
		if (result == null)
			result = defaultValue;
		return result;
	}

	public static String getRandomNum(int num) {
		String[] digits = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
		Random rnum = new Random(new Date().getTime());

		for (int i = 0; i < digits.length; i++) {
			int index = Math.abs(rnum.nextInt()) % 10;
			String tmpDigit = digits[index];
			digits[index] = digits[i];
			digits[i] = tmpDigit;
		}

		String returnStr = digits[0];
		for (int i = 1; i < num; i++) {
			returnStr = digits[i] + returnStr;
		}
		return returnStr;
	}

	public static String join(String[] array) {
		return join(array, ",");
	}

	public static String join(List<String> array) {
		return join((String[]) array.toArray(), ",");
	}

	public static String join(Set<String> array) {
		return join((String[]) array.toArray(), ",");
	}

	/**
	 * Join all the elements of a string array into a single String.
	 * 
	 * If the given array empty an empty string will be returned. Null elements
	 * of the array are allowed and will be treated like empty Strings.
	 * 
	 * @param array
	 *            Array to be joined into a string.
	 * @param delimiter
	 *            String to place between array elements.
	 * @return Concatenation of all the elements of the given array with the the
	 *         delimiter in between.
	 * @throws NullPointerException
	 *             if array or delimiter is null.
	 * 
	 * @since ostermillerutils 1.05.00
	 */
	public static String join(String[] array, String delimiter) {
		// Cache the length of the delimiter
		// has the side effect of throwing a NullPointerException if
		// the delimiter is null.
		int delimiterLength = delimiter.length();
		// Nothing in the array return empty string
		// has the side effect of throwing a NullPointerException if
		// the array is null.
		if (array.length == 0)
			return "";
		// Only one thing in the array, return it.
		if (array.length == 1) {
			if (array[0] == null)
				return "";
			return array[0];
		}
		// Make a pass through and determine the size
		// of the resulting string.
		int length = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null)
				length += array[i].length();
			if (i < array.length - 1)
				length += delimiterLength;
		}
		// Make a second pass through and concatenate everything
		// into a string buffer.
		StringBuffer result = new StringBuffer(length);
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null)
				result.append(array[i]);
			if (i < array.length - 1)
				result.append(delimiter);
		}
		return result.toString();
	}

	static JsonSerializer<Date> ser = new JsonSerializer<Date>() {
		@Override
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			return src == null ? new JsonPrimitive("") : new JsonPrimitive(DateUtil.getDateStringByLong(src.getTime()));
		}
	};

	static JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return DateUtil.getDateTime(json.getAsString());
		}
	};

	public static <T extends BaseEntity> BaseEntity toEntity(Map<String, Object> data, Class<T> classzz) {
		EweblibUtil.updateJsonFieldWithType(data, classzz);

		String json = new GsonBuilder().registerTypeAdapter(Date.class, ser).setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(data);
		return new GsonBuilder().registerTypeAdapter(Date.class, deser).create().fromJson(json, classzz);

	}

	public static <T extends BaseEntity> BaseEntity toEntity(String data, Class<T> classzz) {

		return toEntity(new GsonBuilder().registerTypeAdapter(Date.class, deser).create().fromJson(data, Map.class), classzz);

	}

	public static <T extends BaseEntity> List<T> toJsonList(Map<String, Object> params, Class<T> clz, String key) {
		List<T> results = new ArrayList<T>();

		if (!EweblibUtil.isEmpty(params.get(key))) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (params.get(key) instanceof String) {

				list = (List<Map<String, Object>>) new Gson().fromJson((String) params.get(key), List.class);
			} else if (params.get(key) instanceof List) {
				list = (List<Map<String, Object>>) params.get(key);
			}

			for (Map<String, Object> obj : list) {
				updateJsonFieldWithType(obj, clz);
				results.add((T) EweblibUtil.toEntity(obj, clz));
			}

		}
		return results;

	}

	public static <T extends BaseEntity> List<T> toJsonList(Map<String, Object> params, Class<T> clz) {

		return toJsonList(params, clz, "rows");

	}

	public static String toJson(BaseEntity entity) {
		return new GsonBuilder().registerTypeAdapter(Date.class, ser).create().toJson(entity);
	}

	public static String toJson(Object data) {
		return new GsonBuilder().registerTypeAdapter(Date.class, ser).create().toJson(data);
	}

	public static Map<String, Object> toMap(BaseEntity entity) {
		return new Gson().fromJson(entity.toString(), HashMap.class);
	}

	public static Map<String, Object> toMap(String jsonStr) {
		return new Gson().fromJson(jsonStr, HashMap.class);
	}

	public static String toString(Map<String, Object> data) {
		return new Gson().toJson(data);
	}

	public static <T extends BaseEntity> Object fromXml(String txt, Class<T> claszz) {
		XStream xs = new XStream();
		xs.alias("xml", claszz);
		return xs.fromXML(txt);
	}

	public static <T extends BaseEntity> String toXml(BaseEntity entity, Map<String, Class> map) {

		XStream xs = new XStream();
		for (String key : map.keySet()) {
			xs.alias(key, map.get(key));

		}
		return xs.toXML(entity);

	}

	public static <T extends BaseEntity> void updateJsonFieldWithType(Map<String, Object> params, Class<?> clz) {

		if (params != null) {
			Field[] fields = clz.getFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(IntegerColumn.class)) {
					if (params.get(field.getName()) != null) {
						params.put(field.getName(), getInteger(params.get(field.getName()), 0));
					}
				}
				
				if (field.isAnnotationPresent(DateColumn.class)) {
					if (params.get(field.getName()) != null) {
						params.put(field.getName(), DateUtil.getDateTime(params.get(field.getName()).toString()));
					}
				}

				if (field.isAnnotationPresent(FloatColumn.class)) {
					if (params.get(field.getName()) != null) {
						params.put(field.getName(), getFloat(params.get(field.getName()), 0.0f));
					}
				}

				if (field.isAnnotationPresent(DoubleColumn.class)) {
					if (params.get(field.getName()) != null) {
						params.put(field.getName(), getDouble(params.get(field.getName()), 0d));
					}
				}

				if (field.isAnnotationPresent(BooleanColumn.class)) {

					if (params != null && field != null) {
						if (EweblibUtil.isValid(params.get(field.getName()))) {
							String text = params.get(field.getName()).toString();
							if (text.equalsIgnoreCase("1") || text.equalsIgnoreCase("true")) {
								params.put(field.getName(), true);
							} else {
								params.put(field.getName(), false);
							}

						}
					}
				}

				if (field.isAnnotationPresent(ObjectColumn.class)) {
					Object v = params.get(field.getName());

					if (EweblibUtil.isEmpty(v)) {
						params.remove(field.getName());
					} else {
						if (v instanceof Map) {

							try {
								updateJsonFieldWithType((Map<String, Object>) v, Class.forName(field.getGenericType().toString().replaceAll("class", "").trim()));
							} catch (ClassNotFoundException e) {
								// do nothing
							}
						} else if (v instanceof List) {
							List<Map<String, Object>> list = (List<Map<String, Object>>) v;
							String className = field.getGenericType().toString().replaceAll("class", "").trim();
							className = className.replaceAll("java.util.List", "");

							className = className.replaceAll("<", "");
							className = className.replaceAll(">", "");
							for (Map<String, Object> data : list) {

								try {
									updateJsonFieldWithType(data, Class.forName(className.trim()));
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
									// do nothing
								}
							}

						}

					}
				}
				
				if (field.isAnnotationPresent(CopyColum.class)) {
					if (params.get(field.getName()) != null) {
						CopyColum cc = field.getAnnotation(CopyColum.class);
						params.put(cc.name(), params.get(field.getName()));
					}
				}
				
				

			}
		}

	}

	public static Long getLongParam(Map<String, Object> params, String key) {
		if (params == null) {
			return null;
		}
		return getLong(params.get(key));
	}

	public static Long getLong(Object value) {
		Long result = null;
		if (isValid(value)) {
			try {
				if (value instanceof Number) {
					result = ((Number) value).longValue();
				} else {
					result = Long.parseLong(String.valueOf(value));
				}
			} catch (NumberFormatException e) {
				// throw new
				// ApiResponseCodeException(String.format("Long parameter illegal [%s]",
				// value),
				// ResponseCodeConstants.NUMBER_PARAMETER_ILLEGAL);
			}
		}
		return result;
	}

	public static boolean isValid(Object param) {
		return !isEmpty(param);
	}

	private static final double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
	 * 
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return
	 */
	public static double GetDistance(double lng1, double lat1, double lng2, double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;

	}

	/** 判断字符串是否为数字和字母组成 */
	public static boolean isCharNum(String str) {
		if (str != null) {
			return str.matches("^[A-Za-z0-9]+$");
		}
		return false;
	}

	public static String getUrl(String path) {

		return "http://".concat(ConfigManager.getProperty("WEI_XIN_HOST").toString()).concat("/").concat(path);
	}
	
	
	public static int generateRandom(int a, int b) {
		int temp = 0;

		if (a > b) {
			temp = new Random().nextInt(a - b);
			return temp + b;
		} else {
			temp = new Random().nextInt(b - a);
			return temp + a;
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double distance = GetDistance(121.38254370023, 31.239157976918, 121.41725835734, 31.248301067351);
		System.out.println(distance);
	}

}
