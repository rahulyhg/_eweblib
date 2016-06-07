package com.eweblib.cfg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eweblib.dao.IQueryDao;
import com.eweblib.exception.ConfigException;
import com.eweblib.util.EweblibUtil;

/**
 * 配置管理,
 * 
 * 
 * @author
 * 
 */
public class ConfigManager {
    private static final String ENVIROMENT_PRODUCT = "product";

    private static final String ENVIROMENT = "enviroment";

    // 索引路径
    private static final String LUCENE_INDEX_DIR = "lucene_index_dir";

    private static Logger logger = LogManager.getLogger(ConfigManager.class);

    private static Properties properties = new Properties();

    public static IQueryDao dao;

    public static void setConfiguraion(String configFiles, IQueryDao dao) {
        ConfigManager.dao = dao;

        String files[] = configFiles.split(",");

        for (String file : files) {
            InputStreamReader reader = null;
            InputStream resourceAsStream = null;
            try {

                // load resource from class root path
                resourceAsStream = ConfigManager.class.getResourceAsStream("/".concat(file));

                reader = new InputStreamReader(resourceAsStream, "UTF-8");
                if (reader != null) {
                    logger.info("Load config from file :::: " + file);
                    properties.load(reader);
                }

            } catch (IOException e) {
                logger.fatal("Load property file failed: ".concat(file), e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        logger.fatal("close reader of file {} failed: ", file);
                    }

                }

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

    public static String getLuceneIndexDir(String folder) {

        String dir = ConfigManager.getProperty(LUCENE_INDEX_DIR);

        if (EweblibUtil.isEmpty(dir)) {
            throw new ConfigException("lucene_index_dir must be set into config.properties");
        }

        if (EweblibUtil.isValid(folder)) {

            return dir + folder + File.separator;
        }

        return dir + File.separator;

    }

    public static boolean isProductEnviroment() {
        String env = ConfigManager.getProperty(ENVIROMENT);

        if (EweblibUtil.isEmpty(env)) {
            return false;
        }

        if (ConfigManager.getProperty(ENVIROMENT).equalsIgnoreCase(ENVIROMENT_PRODUCT)) {
            return true;
        }

        return false;
    }

    public static boolean isDevEnviroment() {
        return !isProductEnviroment();
    }

    public static boolean isPQ() {
        String db = ConfigManager.getProperty("DB_NAME");

        if (db != null && db.equalsIgnoreCase("pq")) {
            return true;
        }

        return false;

    }

    /**
     * 是否记录日志
     * 
     * @return
     */
    public static boolean enableLog() {
        String enableLog = ConfigManager.getProperty("ENABLE_LOG");

        if (enableLog != null && enableLog.equalsIgnoreCase("true")) {
            return true;
        }

        return false;

    }

}
