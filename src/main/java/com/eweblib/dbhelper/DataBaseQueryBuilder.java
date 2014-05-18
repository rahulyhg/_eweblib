package com.eweblib.dbhelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eweblib.bean.Pagination;
import com.eweblib.util.EWeblibThreadLocal;
import com.eweblib.util.EweblibUtil;

public class DataBaseQueryBuilder {

	private String queryStr = null;
	
	private int lastOpType = -1;

	private String table;

	private String limitColumns = null;
	
	private String joinColumns = null;

	private Integer limitStart = null;

	private Integer limitRows = null;
	
	private String orderBy  = null;

	private String onQuery = null;
	
	private String distinctColumn = null;
	
	
	private Set<String> limitColumnNames = new HashSet<String>();

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
		
		if(this.onQuery !=null && !key.contains(".")){
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
		if(this.onQuery !=null && !key.contains(".")){
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

	public DataBaseQueryBuilder join(String leftTable, String rightTable, String leftKey, String rightKey) {
	
		if (this.queryStr != null) {
			throw new RuntimeException("Must set join table first before set query operation");
		}
		if (this.onQuery == null) {
			this.onQuery = " left join " + rightTable + " as " + rightTable + " on "  +  leftTable + "." + leftKey + "=" + rightTable + "." + rightKey;
		}else{
			this.onQuery = this.onQuery + " left join " + rightTable + " as " + rightTable  + " on "  +  leftTable + "." + leftKey + "=" + rightTable + "." + rightKey;
		}
		return this;
	}

	public DataBaseQueryBuilder on(String leftTable, String rightTable, String leftKey, String rightKey) {

		if (this.queryStr != null) {
			throw new RuntimeException("Must set join table first before set query");
		}
		
		if (this.onQuery == null) {
			throw new RuntimeException("Must set join table first before set on query ");
		}

		this.onQuery = this.onQuery + " and " + leftTable + "." + leftKey + "=" + rightTable + "." + rightKey;

		return this;
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
				setColumnNames(column);
				String[] splitColumns = column.split(",");
				if(splitColumns.length > 1){
					column = splitColumns[0] + " as " + splitColumns[1] + " "; 
				}
				if (this.limitColumns == null) {
					this.limitColumns = tableAlias+ "." + column;
				} else {
					this.limitColumns = this.limitColumns + "," + tableAlias + "." + column;

				}
			}
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
				setColumnNames(column);
				String[] splitColumns = column.split(",");
				if(splitColumns.length > 1){
					column = splitColumns[0] + " as " + splitColumns[1] + " "; 
				}
				if (this.limitColumns == null) {
					this.limitColumns = tableAlias+ "." + column;
				} else {
					this.limitColumns = this.limitColumns + "," + tableAlias + "." + column;

				}
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

	private void appednLimitColumn(String column) {

		setColumnNames(column);
	    String[] splitColumns = column.split(",");
		if(splitColumns.length > 1){
			column = splitColumns[0] + " as " + splitColumns[1] + " "; 
		}
		if (this.limitColumns == null) {
			this.limitColumns = this.table+ "." + column;
		} else {
			this.limitColumns = this.limitColumns + "," + this.table + "." + column;

		}
    }


	public DataBaseQueryBuilder orderBy(String column, boolean asc) {
		
		if(this.onQuery !=null){
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
	
public DataBaseQueryBuilder orderBy(String table, String column, boolean asc) {
		
		if(this.onQuery !=null){
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

		
		if(columnName !=null && this.limitColumns !=null){
			throw new RuntimeException("distinctColumn and limitColumns can not exists at same time");
		}
		this.distinctColumn = columnName;

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

	public String getJoinColumns() {
    	return joinColumns;
    }

	public void setJoinColumns(String joinColumns) {
    	this.joinColumns = joinColumns;
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


}
