package com.eweblib.dbhelper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.eweblib.util.DateUtil;
import com.eweblib.util.EweblibUtil;

public class DataBaseQuery {

	private String column;

	private DataBaseQueryOpertion operation;

	private Object value;

	private String sql;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public DataBaseQuery(String sql) {
		this.sql = sql;
	}

	public DataBaseQuery(String column, Object value) {
		this(DataBaseQueryOpertion.EQUAILS, column, value);
	}

	public DataBaseQuery(DataBaseQueryOpertion operation, String column, Object value) {

		if (value != null && value instanceof String) {
			String v = value.toString();
			if (v.contains("\"")) {
				v = v.replaceAll("\"", "\\\\\"");
			}

			this.value = v;
		} else {
			this.value = value;

		}
		this.operation = operation;
		this.column = column;
	}

	public DataBaseQueryOpertion getOperation() {
		return operation;
	}

	public void setOperation(DataBaseQueryOpertion operation) {
		this.operation = operation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String toString() {

		if (!EweblibUtil.isEmpty(this.sql)) {
			return this.sql;
		}
		String sqlStr = null;

		switch (this.operation.toString()) {

		case "=":
			sqlStr = commonOperation();
			break;
		case ">":
			sqlStr = commonOperation();
			break;
		case "<":
			sqlStr = commonOperation();
			break;
		case ">=":
			sqlStr = commonOperation();
			break;
		case "<=":
			sqlStr = commonOperation();
			break;
		case "!=":
			sqlStr = commonOperation();
			break;
		case "like":
			sqlStr = likeOperation();
			break;
		case "not like":
			sqlStr = likeOperation();
			break;
		case "in":
			sqlStr = inOperation();
			break;
		case "not in":
			sqlStr = inOperation();
			break;
		case "is not null":
			sqlStr = notValueOperation();
			break;
		case "is null":
			sqlStr = nullValueOperation();
			break;
		case "is true":
			sqlStr = notValueOperation();
			break;
		case "is false":
			sqlStr = notValueOperation();
			break;
		default:

		}

		return sqlStr;

	}

	private String nullValueOperation() {
		return "(" + this.column + " " + this.operation.toString() + " or " + this.column + "=\"\")";
	}

	private String notValueOperation() {
		return this.column + " " + this.operation.toString();
    }


	private String inOperation() {
		if (this.value instanceof String[]) {
			String v[] = (String[]) this.value;
			return this.column + " " + this.operation.toString() + " (" + sqlJoin(v) + ")";

		} else if (this.value instanceof List) {
			List<String> v = (List<String>) this.value;
			return this.column + " " + this.operation.toString() + " (" + sqlJoin(v) + ")";
		} else if (this.value instanceof Set) {
			Set<String> v = (Set<String>) this.value;
			return this.column + " " + this.operation.toString() + " (" + sqlJoin(v) + ")";
		}

		return this.column + " " + this.operation.toString() + " (\"" + this.value + "\")";
	}

	private String commonOperation() {
		if (this.value instanceof Integer || this.value instanceof Double || this.value instanceof Float) {
			return this.column + this.operation.toString() + "" + this.value;

		} else if (this.value instanceof Date) {
			return this.column + this.operation.toString() + "\"" + DateUtil.getDateStringTime((Date) this.value) + "\"";

		}
		return this.column + this.operation.toString() + "\"" + this.value + "\"";

	}

	private String likeOperation() {

		return this.column + " " + this.operation.toString() + " \"%" + this.value + "%\"";

	}
	
	


	public  String sqlJoin(List<String> array) {
		String[] values = new String[array.size()];
				
		int i=0;
		for(String value: array){
			values[i] = value;
			i++;
		}
		return sqlJoin((String[]) values);
	}
	
	public  String sqlJoin(Set<String> array) {
		String[] values = new String[array.size()];
		
		int i=0;
		for(String value: array){
			values[i] = value;
			i++;
		}
		return sqlJoin((String[]) values);
	}
	/**
	 * Join all the elements of a string array into a single String.
	 * 
	 * If the given array empty an empty string will be returned. Null elements
	 * of the array are allowed and will be treated like empty Strings.
	 * 
	 * @param array
	 *            Array to be joined into a string.
	 * @param delimiter
	 *            String to place between array elements.
	 * @return Concatenation of all the elements of the given array with the the
	 *         delimiter in between.
	 * @throws NullPointerException
	 *             if array or delimiter is null.
	 * 
	 * @since ostermillerutils 1.05.00
	 */
	public String sqlJoin(String[] array) {

		StringBuffer result = new StringBuffer();

		for (int i = 0; i < array.length; i++) {
			result.append("\"");
			result.append(array[i]);
			result.append("\",");
		}

		if(array.length > 0){
			result.append(")");
		}else{
			result.append("\"");
			result.append("\",");
			result.append(")");
		}
		return result.toString().replace(",)", "");
	}

}
