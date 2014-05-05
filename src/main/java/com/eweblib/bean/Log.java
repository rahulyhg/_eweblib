package com.eweblib.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Table(name = Log.TABLE_NAME)
public class Log extends BaseEntity {

	public static final String DISPLAY_VALUE = "displayValue";

	public static final String SEARCH_VALUE = "searchValue";

	public static final String THREAD = "thread";

	public static final String DATA_ID = "dataId";

	public static final String LOG_TABLE_NAME = "tableName";

	public static final String DATA = "data";

	public static final String MESSAGE = "message";

	public static final String URL_PATH = "urlPath";

	public static final String OPERATOR_ID = "operatorId";

	public static final String OPERATOR = "operator";

	public static final String TABLE_NAME = "Log";

	@Column(name = OPERATOR_ID)
	@Expose
	public String operatorId;

	@Column(name = URL_PATH)
	@Expose
	public String urlPath;

	@Column(name = MESSAGE)
	@Expose
	public String message;

	@Column(name = THREAD)
	@Expose
	public String thread;
	
	@Column(name = DATA)
	@Expose
	public String data;
	
	@Column(name = LOG_TABLE_NAME)
	@Expose
	public String tableName;

	@Column(name = DATA_ID)
	@Expose
	public String dataId;
	
	@Column(name = SEARCH_VALUE)
	@Expose
	public String searchValue;

	
	
	@Column(name = DISPLAY_VALUE)
	@Expose
	public String displayValue;
	
	
	@Expose
	public String userName;
	


	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getData() {
    	return data;
    }

	public void setData(String data) {
    	this.data = data;
    }

	public String getTableName() {
    	return tableName;
    }

	public void setTableName(String tableName) {
    	this.tableName = tableName;
    }

	public String getDataId() {
    	return dataId;
    }

	public void setDataId(String dataId) {
    	this.dataId = dataId;
    }

	public String getSearchValue() {
    	return searchValue;
    }

	public void setSearchValue(String searchValue) {
    	this.searchValue = searchValue;
    }

	public String getDisplayValue() {
    	return displayValue;
    }

	public void setDisplayValue(String displayValue) {
    	this.displayValue = displayValue;
    }
	
	
	

}
