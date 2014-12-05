package com.eweblib.cfg;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.eweblib.dao.IQueryDao;
import com.eweblib.exception.ConfigException;
import com.eweblib.util.EweblibUtil;

public class ConfigManager {
	private static Logger logger = LogManager.getLogger(ConfigManager.class);

	private static Properties properties = new Properties();

	public static IQueryDao dao;

	public static void setConfiguraion(String configFiles, IQueryDao dao) {
		ConfigManager.dao = dao;

		String files[] = configFiles.split(",");

		for (String file : files) {
			try {
				// load resource from class root path
				properties.load(ConfigManager.class.getResourceAsStream("/".concat(file)));
			} catch (IOException e) {
				logger.fatal("Load property file failed: ".concat(file), e);
			}

		}

	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static void setProperties(String key, String value) {
		properties.setProperty(key, value);
	}

	public static void setProperties(String key, Object value) {
		properties.put(key, value);

	}

	public static Object getPropertyObject(String key) {

		return properties.get(key);

	}

	public static void remove(String key) {
		properties.remove(key);
	}

	public static String getLuceneIndexDir() {

		String dir = ConfigManager.getProperty("lucene_index_dir");

		if (EweblibUtil.isEmpty(dir)) {
			throw new ConfigException("lucene_index_dir must be set into config.properties");
		}

		return dir;

	}

	public static boolean isProductEnviroment() {
		String env = ConfigManager.getProperty("enviroment");

		if (EweblibUtil.isEmpty(env)) {
			return false;
		}

		if (ConfigManager.getProperty("enviroment").equalsIgnoreCase("product")) {
			return true;
		}

		return false;
	}

	public static boolean isDevEnviroment() {
		return !isProductEnviroment();
	}
}
