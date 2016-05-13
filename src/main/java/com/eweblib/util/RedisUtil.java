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
    public static boolean SERVER_CRASHED = false;
    private static void initialPool() {

        if (jedisPool == null) {
            // 池基本配置
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(2000);
            config.setMaxIdle(200);
            config.setMaxWaitMillis(30 * 1000);
            config.setTestOnBorrow(false);

            jedisPool = new JedisPool(config, ConfigManager.getProperty("redis_server"), 6379);
        }
    }

    public static String get(String key) {
        initialPool();

        try {
            Jedis jedis = jedisPool.getResource();

            String value = jedis.get(key);
            jedis.close();
            return value;
        } catch (Exception e) {
            logger.error("get value from redis failed with key: " + key, e);
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
            jedis.close();
        } catch (Exception e) {
            logger.error("remove redis with key: " + key + " failed", e);
        }
    }

    public static void set(String key, Object value, int seconds) {
        initialPool();
        if (value != null) {

            try {
                Jedis jedis = jedisPool.getResource();
                jedis.set(key, value.toString());
                jedis.expire(key, seconds);
                jedis.close();
            } catch (Exception e) {
                logger.error("save data to  redis  failed", e);
            }
        }
    }

}
