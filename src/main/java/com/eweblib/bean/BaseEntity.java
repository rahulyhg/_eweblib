package com.eweblib.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.eweblib.exception.BeanStructureException;
import com.eweblib.util.EweblibUtil;
import com.google.gson.annotations.Expose;

public class BaseEntity {

	public static final String UPDATED_ON = "updatedOn";

	public static final String CREATOR_ID = "creatorId";

	public static final String CREATED_ON = "createdOn";

	public static final String ID = "id";

	@Id
	@Column(unique = true, name = ID)
	@Expose
	public String id;

	@Column(name = CREATED_ON)
	@Expose
	public Date createdOn;

	@Column(name = UPDATED_ON)
	@Expose
	public Date updatedOn;

	@Column(name = CREATOR_ID)
	@Expose
	public String creatorId;
	

	public BaseEntity() {

	}


	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String ownerId) {
		this.creatorId = ownerId;
	}

	public String getId() {

		return id;
	}

	public void setId(String uuid) {
		this.id = uuid;
	}
	

	/**
	 * for Mybatis invoke insert columns EXP
	 * 
	 * @return  ${columnA},${columnB}
	 */
	public String getInsertColumnsExp() {
		this.caculationColumnList();

		return this.returnInsertColumnsDefine();
	}

	/**
	 * for Mybatis invoke columns
	 * @return
	 */
	public String getInsertColumns() {
		this.caculationColumnList();
		return this.returnInsertColumnsName();
	}

	/**
	 * for Mybatis invoke table name
	 * @return
	 */
	public String getTable() {
		this.caculationColumnList();
		Table table = this.getClass().getAnnotation(Table.class);
		if (table != null)
			return table.name();
		else
			throw new BeanStructureException("undefine POJO @Table, need Tablename(@Table(name))");
	}
	
	
	/**
	 * for Mybatis invoke table name
	 * @return
	 */
	public String getUniquLogColumn() {

		Field[] fields = this.getClass().getFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(Column.class)) {
				Column column = field.getAnnotation(Column.class);
				if (column.unique() && !field.getName().equalsIgnoreCase("id")) {

					return field.getName();

				}

			}
		}

		return null;
	}
	
	/**
	 * for Mybatis invoke columns
	 * @return
	 */
	public String getUpdateColumns() {
		this.caculationColumnList();
		return this.returnUpdateSet();
	}


	/**
	 * 用于存放POJO的列信息
	 */
	private transient static Map<Class<? extends BaseEntity>, List<String>> columnMap = new HashMap<Class<? extends BaseEntity>, List<String>>();

	private boolean isNull(String fieldname) {
		try {
			Field field = this.getClass().getField(fieldname);
			return isNull(field);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return false;
	}

	private boolean isNull(Field field) {
		try {
			field.setAccessible(true);
			return field.get(this) == null;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 用于计算类定义 需要POJO中的属性定义@Column(name)
	 */
	public void caculationColumnList() {
		if (columnMap.containsKey(this.getClass()))
			return;

		Field[] fields = this.getClass().getFields();
		List<String> columnList = new ArrayList<String>(fields.length);

		for (Field field : fields) {
			if (field.isAnnotationPresent(Column.class))
				columnList.add(field.getName());
		}

		columnMap.put(this.getClass(), columnList);
	}
	
	
	/**
	 * 用于计算类定义 需要POJO中的属性定义@Column(name)
	 */
	public List<String> getColumnList() {
		if (!columnMap.containsKey(this.getClass())) {
			this.caculationColumnList();
		}
		return columnMap.get(this.getClass());

	}

	/**
	 * 获取用于WHERE的 有值字段表
	 * 
	 * @return
	 */
	public List<WhereColumn> returnWhereColumnsName() {
		Field[] fields = this.getClass().getFields();
		List<WhereColumn> columnList = new ArrayList<WhereColumn>(fields.length);

		for (Field field : fields) {
			if (field.isAnnotationPresent(Column.class) && !isNull(field))
				columnList.add(new WhereColumn(field.getName(), field.getGenericType().equals(String.class)));
		}

		return columnList;
	}

	/**
	 * Where条件信息
	 * 
	 * @author HUYAO
	 * 
	 */
	public class WhereColumn {
		public String name;
		public boolean isString;

		public WhereColumn(String name, boolean isString) {
			this.name = name;
			this.isString = isString;
		}
	}

	/**
	 * 用于获取Insert的字段累加
	 * 
	 * @return
	 */
	public String returnInsertColumnsName() {
		StringBuilder sb = new StringBuilder();

		List<String> list = columnMap.get(this.getClass());
		int i = 0;
		for (String column : list) {
			// if (isNull(column))
			// continue;

			if (i++ != 0)
				sb.append(',');
			sb.append(column);
		}
		return sb.toString();
	}

	/**
	 * 用于获取Insert的字段映射累加
	 * 
	 * @return
	 */
	public String returnInsertColumnsDefine() {
		StringBuilder sb = new StringBuilder();

		List<String> list = columnMap.get(this.getClass());
		int i = 0;
		if (list != null) {
			for (String column : list) {
				// if (isNull(column))
				// continue;

				if (i++ != 0)
					sb.append(',');
				sb.append("#{").append(column).append('}');
			}
		}
		return sb.toString();
	}

	/**
	 * 用于获取Update Set的字段累加
	 * 
	 * @return
	 */
	public String returnUpdateSet() {
		this.caculationColumnList();
		StringBuilder sb = new StringBuilder();

		List<String> list = columnMap.get(this.getClass());
		int i = 0;
		for (String column : list) {
			if (isNull(column) || column.equalsIgnoreCase(ID))
				continue;

			if (i++ != 0)
				sb.append(',');
			sb.append(column).append("=#{").append(column).append('}');
		}
		return sb.toString();
	}

	/**
	 * 打印类字段信息
	 */
	@Override
	public String toString() {
		
		return EweblibUtil.toJson(this);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> toMap() {
		return EweblibUtil.toMap(this);
	}


}
