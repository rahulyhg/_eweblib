package com.eweblib.util;

import com.eweblib.cfg.ConfigManager;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

public class RedisUtil {

	private static RedisConnection<String, String> connection = null;

	public static void init() {

		if (connection == null) {
			RedisClient redisClient = new RedisClient(ConfigManager.getProperty("redis_server"));
			connection = redisClient.connect();

		}
	}

	public static String loadValue(String key) {
		init();
		return connection.get(key);
	}

	public static Integer loadInt(String key) {
		init();
		String value = connection.get(key);

		if (EweblibUtil.isValid(value)) {

			return EweblibUtil.getInteger(value, 0);
		}
		return null;
	}

	public static void set(String key, Object value) {
		init();
		if (value != null) {
			connection.set(key, value.toString());
		}
	}

}
