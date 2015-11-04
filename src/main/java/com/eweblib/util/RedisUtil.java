package com.eweblib.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eweblib.cfg.ConfigManager;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

public class RedisUtil {
	public static Logger logger = LogManager.getLogger(RedisUtil.class);

	private static RedisConnection<String, String> connection = null;

	public static void init() {

		try {
			if (connection == null) {
				RedisClient redisClient = new RedisClient(ConfigManager.getProperty("redis_server"));
				connection = redisClient.connect();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static String get(String key) {
		init();

		try {
			String value = connection.get(key);
			return value;
		} catch (Exception e) {
			logger.error(e);
			init();
		}

		return null;

	}

	public static Integer getInt(String key) {
		String value = get(key);

		if (EweblibUtil.isValid(value)) {

			return EweblibUtil.getInteger(value, 0);
		}
		return null;
	}
	
	public static Long getLong(String key) {
		String value = get(key);

		if (EweblibUtil.isValid(value)) {

			return EweblibUtil.getLong(value);
		}
		return null;
	}

	public static void set(String key, Object value) {
		set(key, value, 5 * 60);
	}

	public static void remove(String key) {
		init();
		try {
			connection.del(key);
		} catch (Exception e) {
			logger.error(e);
			init();
		}
	}

	public static void set(String key, Object value, int seconds) {
		init();
		if (value != null) {

			try {
				connection.set(key, value.toString());
				connection.expire(key, seconds);
			} catch (Exception e) {
				logger.error(e);
				init();
			}
		}
	}

}
