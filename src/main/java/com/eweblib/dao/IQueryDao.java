package com.eweblib.dao;

import java.util.List;

import com.eweblib.bean.BaseEntity;
import com.eweblib.bean.vo.EntityResults;
import com.eweblib.dbhelper.DataBaseQueryBuilder;

public interface IQueryDao {
	
	public IMyBatisDao getDao();
	
	public void setDao(IMyBatisDao dao);
	/**
	 * 
	 * 传人entity对象，table自动获取
	 * 
	 * @param entity
	 * @return 1 成功， 其它错误
	 */
	public BaseEntity insert(BaseEntity entity);
	
	
	public  <T extends BaseEntity> void batchInsert(List<T> entityList);
	
	public  <T extends BaseEntity> void batchInsert(List<T> entityList, String table);

	
	public void executeSql(String sql);

	/**
	 * 传人entity对象，数据操作取entity里面的table和id，其它属性和字段忽略
	 * 
	 * 
	 * @param entity
	 */
	public void updateById(BaseEntity entity);

	/**
	 * 传人DataBaseQueryBuilder对象，
	 * 
	 * 
	 * @param builder
	 */
	public void updateByQuery(DataBaseQueryBuilder builder);

	/**
	 * 传人entity对象，数据操作取entity里面的table和id，其它属性和字段忽略
	 * 
	 * 
	 * @param entity
	 */
	public void deleteById(BaseEntity entity);

	
	public void deleteByQuery(DataBaseQueryBuilder builder);

	/**
	 * 
	 * 根据table name一次性删除所有数据
	 * 
	 * @param table
	 */
	public void deleteAllByTableName(String table);

	
	
	public <T extends BaseEntity> List<T> listByQuery(DataBaseQueryBuilder builder, Class<T> classzz);
	
	public <T extends BaseEntity> List<T> listBySql(String sql, Class<T> classzz);


	public <T extends BaseEntity> EntityResults<T> listByQueryWithPagnation(DataBaseQueryBuilder builder, Class<T> classzz);

	public <T extends BaseEntity> BaseEntity findOneByQuery(DataBaseQueryBuilder builder, Class<T> classzz);
	
	public <T extends BaseEntity> BaseEntity findById(String id, String table, Class<T> classzz);
	
	public <T extends BaseEntity> BaseEntity findByKeyValue(String key, Object value, String table, Class<T> classzz);

	public int count(DataBaseQueryBuilder builder);
	
	public boolean exists(DataBaseQueryBuilder builder);
	
	public boolean exists(String key, String value, String table);
	
	public <T extends BaseEntity> List<T> distinctQuery(DataBaseQueryBuilder builder, Class<T> classzz);

	public <T extends BaseEntity, T1 extends BaseEntity> List<T> callListProcedure(BaseEntity queryEntity, Class<T> targetClasszz, Class<T1> tempClasszz, String procedure);

	public <T extends BaseEntity, T1 extends BaseEntity> BaseEntity callEntityProcedure(BaseEntity queryEntity, Class<T> targetClasszz,  Class<T1> tempClasszz, String procedure);

}
