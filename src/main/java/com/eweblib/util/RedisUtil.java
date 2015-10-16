package com.eweblib.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.eweblib.cfg.ConfigManager;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

public class RedisUtil {
	public static Logger logger = LogManager.getLogger(RedisUtil.class);

	private static RedisConnection<String, String> connection = null;

	public static void init() {

		if (connection == null) {
			RedisClient redisClient = new RedisClient(ConfigManager.getProperty("redis_server"));
			connection = redisClient.connect();
		}
	}

	public static String loadValue(String key) {
		init();
		String value = connection.get(key);
		logger.info("load cache from redis:  " + key + " = " + value);
		return value;
	}

	public static Integer loadInt(String key) {
		String value = loadValue(key);

		if (EweblibUtil.isValid(value)) {

			return EweblibUtil.getInteger(value, 0);
		}
		return null;
	}

	public static void set(String key, Object value) {
		init();
		if (value != null) {
			logger.info("save cache into redis:  " + key + " = " + value);
			connection.set(key, value.toString());
		}
	}

}
