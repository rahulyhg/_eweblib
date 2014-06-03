package com.eweblib.log;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.eweblib.bean.BaseEntity;

public class LogJDBCAppender {
	private static Logger logger = LogManager.getLogger(LogJDBCAppender.class);

	public static <T extends BaseEntity> void appendLog(BaseEntity entity, boolean update, Class<T> classzz) {
		
		
	}
	
	
	

}
