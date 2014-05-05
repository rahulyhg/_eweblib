package com.eweblib.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Table(name = SystemConfig.TABLE_NAME)
public class SystemConfig extends BaseEntity {


	public static final String ORDER_IMPORT_IGNORE_KEYWORD = "order_import_ignore_keyword";
	public static final String ORDER_TO_SP_AFTER_CONFIRMED_HOURS = "orderToSpAfterConfirmedHours";
	public static final String ORDER_EVALUATION_DEFAULT_DAYS = "orderEvaluationDefaultDays";
	
	public static final String BAIDU_MAP_KEY = "baidu_map_key";
	public static final String BAIDU_MAP_KEY_ERROR = "baidu_map_key_error";
	public static final String SMS_ACCOUNT_ID_ERROR = "sms_account_userid_error";
	public static final String SMS_ACCOUNT_ID = "sms_account_userid";
	public static final String SMS_ACCOUNT_NAME = "sms_account_name";
	public static final String SMS_ACCOUNT_PASSWORD = "sms_account_password";
	
	public static final String CONFIG_VALUE = "cfgValue";

	public static final String CONFIG_ID = "configId";

	public static final String TABLE_NAME = "SystemConfig";

	@Column(name = CONFIG_ID)
	@Expose
	public String configId;

	@Column(name = CONFIG_VALUE)
	@Expose
	public String cfgValue;

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getCfgValue() {
		return cfgValue;
	}

	public void setCfgValue(String value) {
		this.cfgValue = value;
	}

}
