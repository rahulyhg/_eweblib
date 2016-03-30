package com.eweblib.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eweblib.cfg.ConfigManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
	public static Logger logger = LogManager.getLogger(RedisUtil.class);

	private static JedisPool jedisPool;// 非切片连接池
	private static Jedis jedis;
	private static void initialPool() {

		if (jedisPool == null) {
			// 池基本配置
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(2000);
			config.setMaxIdle(50);
			config.setTestOnBorrow(false);

			jedisPool = new JedisPool(config, ConfigManager.getProperty("redis_server"), 6379);
			jedis = jedisPool.getResource();
		}
	}

	public static String getApMac(String key) {
		String upKey = key.toUpperCase();
		try {
			initialPool();
			String value = jedis.hget(upKey, "advertise");
			return value;

		} catch (Exception e) {
			logger.fatal("query ap mac from redis server failed with user mac[{}]", upKey, e);
			return null;
		}
	}

	public static String getApGroupInfo(String key) {
		String lowerKey = key.toLowerCase();
		try {
			initialPool();
			String value = jedis.hget(lowerKey, "group_id_name");
			return value;

		} catch (Exception e) {
			logger.fatal("query ap group from redis server failed with user mac[{}]", lowerKey, e);
			return null;
		}
	}


	public static String get(String key) {
		initialPool();

		try {
			String value = jedis.get(key);
			return value;
		} catch (Exception e) {
			logger.error(e);
			initialPool();
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
		initialPool();
		try {
			Jedis jedis = jedisPool.getResource();
			jedis.del(key);
		} catch (Exception e) {
			logger.error(e);
			initialPool();
		}
	}

	public static void set(String key, Object value, int seconds) {
		initialPool();
		if (value != null) {

			try {
				Jedis jedis = jedisPool.getResource();
				jedis.set(key, value.toString());
				jedis.expire(key, seconds);
			} catch (Exception e) {
				logger.error(e);
				initialPool();
			}
		}
	}

}
