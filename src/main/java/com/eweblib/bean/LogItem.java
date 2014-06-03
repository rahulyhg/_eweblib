package com.eweblib.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.eweblib.bean.BaseEntity;
import com.google.gson.annotations.Expose;

@Table(name = LogItem.TABLE_NAME)
public class LogItem extends BaseEntity {

	public static final String NEW_VALUE = "newValue";

	public static final String OLD_VALUE = "oldValue";

	public static final String FIELD = "field";

	public static final String LOG_ID = "logId";
	
	public static final String _TABLE_NAME = "tableName";

	public static final String TABLE_NAME = "LogItem";

	@Column(name = LOG_ID)
	@Expose
	public String logId;

	@Column(name = FIELD)
	@Expose
	public String field;

	@Column(name = OLD_VALUE)
	public String oldValue;

	@Column(name = NEW_VALUE)
	public String newValue;
	
	@Column(name = _TABLE_NAME)
	@Expose
	public String tableName;

	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

}
