package com.eweblib.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.eweblib.bean.BaseEntity;
import com.eweblib.bean.Log;
import com.eweblib.cfg.ConfigManager;
import com.eweblib.controller.interceptor.ControllerFilter;
import com.eweblib.dbhelper.DataBaseQueryBuilder;
import com.eweblib.util.EWeblibThreadLocal;
import com.eweblib.util.EweblibUtil;

public class LogJDBCAppender {
	private static Logger logger = LogManager.getLogger(LogJDBCAppender.class);

	public static <T extends BaseEntity> void appendLog(BaseEntity entity, boolean update, Class<T> classzz) {

		List<String> logClassNames = new ArrayList<String>();
		logClassNames.add("Manufacturer");
		logClassNames.add("ServiceProvider");
		logClassNames.add("User");
		logClassNames.add("Worker");
		logClassNames.add("ServiceOrder");
		logClassNames.add("ProductOrder");
		logClassNames.add("SystemConfig");

		if (!(entity instanceof Log) && logClassNames.contains(entity.getClass().getSimpleName()) && EWeblibThreadLocal.get(ControllerFilter.URL_PATH)!=null) {
			try {
				Log log = new Log();
				log.setOperatorId(EWeblibThreadLocal.getCurrentUserId());
				log.setUrlPath((String) EWeblibThreadLocal.get(ControllerFilter.URL_PATH));
				log.setThread(Thread.currentThread().getName());
				log.setTableName(entity.getTable());
				log.setDataId(entity.getId());
				if (update) {
					log.setMessage("update");
				} else {
					log.setMessage("insert");
				}
				List<String> columns = entity.getColumnList();
				Map<String, Object> savedMap = new HashMap<String, Object>();

				if (update) {
					BaseEntity oldObject = ConfigManager.dao.findById(entity.getId(), entity.getTable(), classzz);
					Map<String, Object> oldMap = oldObject.toMap();
					Map<String, Object> updateMap = entity.toMap();
					for (String key : updateMap.keySet()) {

						if (columns.contains(key)) {
							if (oldMap.get(key) != null) {

								String oldValue = (String) oldMap.get(key);
								String newValue = (String) updateMap.get(key);
								if (!newValue.equalsIgnoreCase(oldValue)) {
									savedMap.put(key, updateMap.get(key));
								}
							} else {
								savedMap.put(key, updateMap.get(key));
							}
						}
					}
					savedMap.remove(BaseEntity.CREATED_ON);
					savedMap.remove(BaseEntity.UPDATED_ON);
					
					String column = entity.getUniquLogColumn();
					if (column != null) {
						if (oldMap.get(column) != null) {
							log.setDisplayValue(oldMap.get(column).toString());
						}
					}
					
					log.setSearchValue(oldObject.toString());
					if (EweblibUtil.isValid(savedMap)) {


						DataBaseQueryBuilder logQuery = new DataBaseQueryBuilder(Log.TABLE_NAME);
						logQuery.and(Log.OPERATOR_ID, EWeblibThreadLocal.getCurrentUserId());
						logQuery.and(Log.URL_PATH, EWeblibThreadLocal.get(ControllerFilter.URL_PATH));
						logQuery.and(Log.THREAD, Thread.currentThread().getName());
						logQuery.and(Log.DATA_ID, entity.getId());

						Log oldLog = (Log) ConfigManager.dao.findOneByQuery(logQuery, Log.class);

						if (oldLog != null) {
							String data = oldLog.getData();
							Map<String, Object> dataMap = EweblibUtil.toMap(data);

							dataMap.putAll(savedMap);
							log.setData(EweblibUtil.toString(dataMap));
							log.setId(oldLog.getId());
							
							log.setMessage(oldLog.getMessage());

							if (EweblibUtil.isValid(log.getData())) {
								ConfigManager.dao.updateById(log);
							}
						} else {
							log.setData(EweblibUtil.toJson(savedMap));

							if (EweblibUtil.isValid(log.getData())) {

							
								ConfigManager.dao.insert(log);
							}
						}

					}

				} else {
					log.setData(entity.toString());
					if (EweblibUtil.isValid(log.getData())) {
						String column = entity.getUniquLogColumn();
						if (column != null) {
							if (entity.toMap().get(column) != null) {
								log.setDisplayValue(entity.toMap().get(column).toString());
							}
						}
						ConfigManager.dao.insert(log);
					}
				}

			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

}
