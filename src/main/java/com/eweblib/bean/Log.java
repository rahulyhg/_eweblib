package com.eweblib.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.eweblib.bean.BaseEntity;
import com.google.gson.annotations.Expose;

@Table(name = Log.TABLE_NAME)
public class Log extends BaseEntity {

	public static final String URL_PATH = "urlPath";

	public static final String DATA_ID = "dataId";

	public static final String _TABLE_NAME = "tableName";

	public static final String USER_ID = "userId";

	public static final String LOG_TYPE = "logType";

	public static final String USER_NAME = "userName";

	public static final String MESSAGE = "message";

	public static final String TABLE_NAME = "Log";

	@Column(name = USER_ID)
	@Expose
	public String userId;

	@Expose
	public String userName;

	@Column(name = _TABLE_NAME)
	@Expose
	public String tableName;

	@Column(name = MESSAGE)
	@Expose
	public String message;

	@Column(name = LOG_TYPE)
	public String logType;

	@Column(name = DATA_ID)
	public String dataId;
	
	@Column(name = URL_PATH)
	public String urlPath;

	public String getDataId() {
		return dataId;
	}

	
	
	public String getUrlPath() {
		return urlPath;
	}



	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}



	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
