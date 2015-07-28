package com.eweblib.dbhelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.eweblib.bean.vo.Pagination;
import com.eweblib.cfg.ConfigManager;
import com.eweblib.exception.BeanStructureException;
import com.eweblib.util.EweblibUtil;

public class DataBaseQueryBuilder {

	private String queryStr = null;

	private int lastOpType = -1;

	private String table;

	private String limitColumns = null;

	private Integer limitStart = null;

	private Integer limitRows = null;

	private String orderBy = null;

	private String onQuery = null;

	private String distinctColumn = null;

	private String groupBy = null;

	public String updateColumns = null;

	private boolean disableOrder = false;

	private Set<String> limitColumnNames = new HashSet<String>();

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public boolean isDisableOrder() {
		return disableOrder;
	}

	public void setDisableOrder(boolean removeOrder) {
		this.disableOrder = removeOrder;
	}

	public Integer getLimitStart() {
		return limitStart;
	}

	public void setLimitStart(Integer limitStart) {
		this.limitStart = limitStart;
	}

	public Integer getLimitRows() {
		return limitRows;
	}

	public void setLimitRows(Integer limitEnd) {
		this.limitRows = limitEnd;
	}

	public String getLimitColumns() {
		return limitColumns;
	}

	public void setLimitColumns(String limitColumns) {
		this.limitColumns = limitColumns;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public DataBaseQueryBuilder(String table) {

		this.table = table;
	}

	public DataBaseQueryBuilder(String table, String queryStr) {

		this.table = table;
		this.queryStr = queryStr;
	}

	public Set<String> getLimitColumnNames() {
		return limitColumnNames;
	}

	public void setLimitColumnNames(Set<String> limitColumnNames) {
		this.limitColumnNames = limitColumnNames;
	}

	public DataBaseQueryBuilder and(String key, Object value) {
		return and(DataBaseQueryOpertion.EQUAILS, key, value);
	}

	public DataBaseQueryBuilder or(String key, Object value) {
		return or(DataBaseQueryOpertion.EQUAILS, key, value);
	}

	public DataBaseQueryBuilder and(DataBaseQueryOpertion op, String key) {
		return and(op, key, null);
	}

	public DataBaseQueryBuilder or(DataBaseQueryOpertion op, String key) {
		return or(op, key, null);
	}

	public DataBaseQueryBuilder and(DataBaseQueryOpertion op, String key, Object value) {

		if (this.onQuery != null && !key.contains(".")) {
			key = this.table + "." + key;
		}

		if (this.onQuery == null && !key.contains(".")) {
			key = this.table + "." + key;
		}

		DataBaseQuery query = new DataBaseQuery(op, key, value);
		if (this.queryStr == null) {
			queryStr = "";
		}
		if (lastOpType == 0) {
			queryStr = "(" + queryStr + ") and " + query.toString();

		} else {

			if (EweblibUtil.isEmpty(queryStr)) {
				queryStr = query.toString();
			} else {
				queryStr = queryStr + " and " + query.toString();

			}

		}

		lastOpType = 1;
		return this;
	}

	public DataBaseQueryBuilder or(DataBaseQueryOpertion op, String key, Object value) {
		if (this.onQuery != null && !key.contains(".")) {
			key = this.table + "." + key;
		}
		DataBaseQuery query = new DataBaseQuery(op, key, value);

		if (this.queryStr == null) {
			queryStr = "";
		}
		if (lastOpType == 1) {
			queryStr = "(" + queryStr + ") or " + query.toString();
		} else {

			if (EweblibUtil.isEmpty(queryStr)) {
				queryStr = query.toString();
			} else {
				queryStr = queryStr + " or " + query.toString();

			}

		}
		lastOpType = 0;

		return this;
	}

	public DataBaseQueryBuilder leftJoin(String leftTable, String leftAlias, String rightTable, String rightAlias, String leftKey, String rightKey) {

		if (EweblibUtil.isEmpty(leftAlias)) {
			leftAlias = leftTable;
		}

		if (EweblibUtil.isEmpty(rightAlias)) {
			rightAlias = rightTable;
		}

		if (this.queryStr != null) {
			throw new RuntimeException("Must set join table first before set query operation");
		}
		if (this.onQuery == null) {
			this.onQuery = " left join " + rightTable + " as " + rightAlias + " on " + leftAlias + "." + leftKey + "=" + rightAlias + "." + rightKey;
		} else {
			this.onQuery = this.onQuery + " left join " + rightTable + " as " + rightAlias + " on " + leftAlias + "." + leftKey + "=" + rightAlias + "." + rightKey;
		}
		return this;
	}

	public DataBaseQueryBuilder innerJoin(String leftTable, String leftAlias, String rightTable, String rightAlias, String leftKey, String rightKey) {
		if (EweblibUtil.isEmpty(leftAlias)) {
			leftAlias = leftTable;
		}

		if (EweblibUtil.isEmpty(rightAlias)) {
			rightAlias = rightTable;
		}

		if (this.queryStr != null) {
			throw new RuntimeException("Must set join table first before set query operation");
		}
		if (this.onQuery == null) {
			this.onQuery = " inner join " + rightTable + " as " + rightAlias + " on " + leftAlias + "." + leftKey + "=" + rightAlias + "." + rightKey;
		} else {
			this.onQuery = this.onQuery + " inner join " + rightTable + " as " + rightAlias + " on " + leftAlias + "." + leftKey + "=" + rightAlias + "." + rightKey;
		}
		return this;
	}

	public DataBaseQueryBuilder leftJoin(String leftTable, String rightTable, String leftKey, String rightKey) {

		return this.leftJoin(leftTable, leftTable, rightTable, rightTable, leftKey, rightKey);
	}

	public DataBaseQueryBuilder innerJoin(String leftTable, String rightTable, String leftKey, String rightKey) {
		return this.innerJoin(leftTable, null, rightTable, null, leftKey, rightKey);
	}

	public DataBaseQueryBuilder joinColumns(String tableAlias, String[] joinColumns) {

		if (this.queryStr != null) {
			throw new RuntimeException("Must set join columns first before set query");
		}

		if (this.onQuery == null) {
			throw new RuntimeException("Must set join table first before set join columns ");
		}

		if (joinColumns != null && joinColumns.length > 0) {

			for (String column : joinColumns) {
				appednLimitColumn(tableAlias, column);
			}
		}
		return this;

	}

	public DataBaseQueryBuilder sum(String tableAlias, String fieldName, String asFieldName) {

		if (EweblibUtil.isEmpty(asFieldName)) {
			asFieldName = fieldName;
		}
		String column = tableAlias + "." + fieldName + " as " + asFieldName + " ";

		if (this.limitColumns == null) {
			this.limitColumns = tableAlias + "." + column;
		} else {
			this.limitColumns = this.limitColumns + "," + tableAlias + "." + column;
		}
		return this;
	}

	private void setColumnNames(String column) {

		if (EweblibUtil.isValid(column)) {
			String[] splitColumns = column.split(",");
			if (splitColumns.length > 1) {
				column = splitColumns[0] + " as " + splitColumns[1] + " ";
				this.limitColumnNames.add(splitColumns[1]);
			} else {
				this.limitColumnNames.add(column);
			}
		}
	}

	public DataBaseQueryBuilder joinColumns(String tableAlias, List<String> joinColumns) {

		if (this.queryStr != null) {
			throw new RuntimeException("Must set join columns first before set query");
		}

		if (this.onQuery == null) {
			throw new RuntimeException("Must set join table first before set join columns ");
		}

		if (joinColumns != null && joinColumns.size() > 0) {

			for (String column : joinColumns) {
				appednLimitColumn(tableAlias, column);
			}
		}
		return this;

	}

	public DataBaseQueryBuilder and(DataBaseQueryBuilder builder) {

		if (!EweblibUtil.isEmpty(builder.getQueryStr())) {
			if (this.queryStr == null) {
				queryStr = "";
			}

			String andQuryStr = builder.getQueryStr() == null ? "" : builder.getQueryStr();
			if (EweblibUtil.isEmpty(queryStr)) {
				queryStr = andQuryStr;
			} else {
				if (!EweblibUtil.isEmpty(andQuryStr)) {
					queryStr = "(" + queryStr + ") and (" + andQuryStr + ")";
				}
			}
		}
		return this;
	}

	public DataBaseQueryBuilder or(DataBaseQueryBuilder builder) {
		if (!EweblibUtil.isEmpty(builder.getQueryStr())) {
			if (this.queryStr == null) {
				queryStr = "";
			}

			String orQueryStr = builder.getQueryStr() == null ? "" : builder.getQueryStr();
			if (EweblibUtil.isEmpty(queryStr)) {

				queryStr = orQueryStr;
			} else {
				if (!EweblibUtil.isEmpty(orQueryStr)) {
					queryStr = "(" + queryStr + ") or (" + orQueryStr + ")";
				}
			}

		}
		return this;

	}

	public DataBaseQueryBuilder limitColumns(String[] columns) {

		if (this.distinctColumn != null && columns.length > 0) {
			throw new RuntimeException("distinctColumn and limitColumns can not exists at same time");
		}

		if (columns != null && columns.length > 0) {

			for (String column : columns) {

				appednLimitColumn(column);
			}
		}
		return this;
	}

	public DataBaseQueryBuilder limitColumns(List<String> columns) {

		if (columns != null && columns.size() > 0) {

			for (String column : columns) {

				appednLimitColumn(column);
			}
		}
		return this;
	}
	private DataBaseQueryBuilder appednLimitColumn(String column) {
		
		 appednLimitColumn(null, column);
		 return this;
	}

	private DataBaseQueryBuilder appednLimitColumn(String tableName, String column) {

		column = column.replaceAll(" as ", ",");
		setColumnNames(column);

		if (tableName == null) {
			tableName = this.table;
		}

		String[] splitColumns = column.split(",");
		if (splitColumns.length > 1) {
			column = splitColumns[0] + " as " + splitColumns[1] + " ";
		}

		if (!column.contains("count(*)") && !column.contains("sum(")) {
			if (this.limitColumns == null) {
				this.limitColumns = tableName + "." + column;
			} else {
				this.limitColumns = this.limitColumns + "," + tableName + "." + column;

			}
		} else {
			if (this.limitColumns == null) {
				this.limitColumns = column;
			} else {
				this.limitColumns = this.limitColumns + "," + column;

			}
		}

		return this;

	}

	public DataBaseQueryBuilder orderBy(String column, boolean asc, boolean alias) {

		if (this.onQuery != null && alias) {
			column = this.table + "." + column;
		}

		if (asc) {
			if (this.orderBy == null) {
				this.orderBy = "  " + column + " ASC ";
			} else {
				this.orderBy = this.orderBy + " ,  " + column + " ASC ";
			}

		} else {
			if (this.orderBy == null) {
				this.orderBy = "  " + column + " DESC ";
			} else {
				this.orderBy = this.orderBy + " ,  " + column + " DESC ";
			}
		}
		return this;
	}

	public DataBaseQueryBuilder clearOderBy() {
		this.orderBy = null;

		return this;
	}

	public DataBaseQueryBuilder orderBy(String column, boolean asc) {

		return orderBy(column, asc, true);
	}

	public DataBaseQueryBuilder orderBy(String table, String column, boolean asc) {

		if (this.onQuery != null) {
			column = table + "." + column;
		}
		if (asc) {
			if (this.orderBy == null) {
				this.orderBy = "  " + column + " ASC ";
			} else {
				this.orderBy = this.orderBy + " ,  " + column + " ASC ";
			}

		} else {
			if (this.orderBy == null) {
				this.orderBy = "  " + column + " DESC ";
			} else {
				this.orderBy = this.orderBy + " ,  " + column + " DESC ";
			}
		}
		return this;
	}

	public DataBaseQueryBuilder disableOrderBy() {

		this.disableOrder = true;

		return this;
	}

	public DataBaseQueryBuilder groupBy(String table, String column) {

		if (this.onQuery != null) {
			column = table + "." + column;
		}

		if (this.groupBy == null) {
			this.groupBy = "  " + column + "  ";
		} else {
			this.groupBy = this.groupBy + " ,  " + column;
		}

		return this;
	}

	public DataBaseQueryBuilder pagination(Pagination pagination) {

		if (pagination != null && pagination.getRows() > 0) {
			int currentPage = pagination.getPage();
			if (currentPage < 1) {
				currentPage = 1;
			}
			this.limitStart = (currentPage - 1) * pagination.getRows();
			this.limitRows = pagination.getRows();

		}

		return this;
	}

	public DataBaseQueryBuilder distinct(String columnName) {

		if (columnName != null && this.limitColumns != null) {
			throw new RuntimeException("distinctColumn and limitColumns can not exists at same time");
		}
		
		if(this.distinctColumn != null ){
			this.distinctColumn = this.distinctColumn + ", " + columnName;
		}
		this.distinctColumn = columnName;

		return this;
	}

	public DataBaseQueryBuilder update(String column, Object value) {

		if (value == null) {
			value = "null";
		}

		String updateStr = "";
		if (value instanceof String) {
			updateStr = column + "=\"" + value + "\"";
		} else {
			updateStr = column + "=" + value;
		}

		if (this.updateColumns == null) {
			this.updateColumns = updateStr;
		} else {
			this.updateColumns = this.updateColumns + ", " + updateStr;
		}

		return this;
	}

	public String getQueryStr() {

		if (this.queryStr != null) {
			this.queryStr = this.queryStr.replace("( and", "(");
			this.queryStr = this.queryStr.replace("( or", "(");
		}

		return this.queryStr;
	}

	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

	public String getTable() {
		if (ConfigManager.isPQ()) {
			return "\"" + table + "\"";
		}
		
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public int getLastOpType() {
		return lastOpType;
	}

	public void setLastOpType(int lastOpType) {
		this.lastOpType = lastOpType;
	}

	public String getOnQuery() {
		return onQuery;
	}

	public void setOnQuery(String onQuery) {
		this.onQuery = onQuery;
	}

	public String getDistinctColumn() {
		return distinctColumn;
	}

	public void setDistinctColumn(String distinctColumn) {
		this.distinctColumn = distinctColumn;
	}

	public String getUpdateColumns() {
		return updateColumns;
	}

	public void setUpdateColumns(String updateColumns) {
		this.updateColumns = updateColumns;
	}
	
	
	public String getTableName() {

		if (ConfigManager.isPQ()) {
			return "\"" + table + "\"";
		}
		
		return table;
	}

}
