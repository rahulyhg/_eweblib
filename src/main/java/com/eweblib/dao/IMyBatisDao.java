package com.eweblib.dao;

import java.util.List;
import java.util.Map;

import com.eweblib.bean.BaseEntity;
import com.eweblib.dbhelper.DataBaseQueryBuilder;

public interface IMyBatisDao {

	public int insert(BaseEntity entity);

	public <T extends BaseEntity> int batchInsert(Map<String, Object> entityList);

	public List<Map<String, Object>> listByQuery(DataBaseQueryBuilder builder);

	public List<Map<String, Object>> listByQueryWithPagination(DataBaseQueryBuilder builder);

	public void updateById(BaseEntity entity);

	public void executeSql(String sql);

	public void updateByQuery(DataBaseQueryBuilder builder);

	public void deleteById(BaseEntity entity);

	public void deleteByQuery(DataBaseQueryBuilder builder);

	public Map<String, Object> findOneByQuery(DataBaseQueryBuilder builder);

	public void deleteAllByTableName(String table);

	public int count(DataBaseQueryBuilder builder);

	public List<Map<String, Object>> listBySql(String sql);

	public void callListProcedure(Map<String, Object> parameters);

	public void callEntityProcedure(Map<String, Object> parameters);

}
