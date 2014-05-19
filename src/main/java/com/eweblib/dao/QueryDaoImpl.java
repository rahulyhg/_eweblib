package com.eweblib.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Table;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.eweblib.annotation.column.FloatColumn;
import com.eweblib.annotation.column.IntegerColumn;
import com.eweblib.bean.BaseEntity;
import com.eweblib.bean.EntityResults;
import com.eweblib.bean.OrderBy;
import com.eweblib.bean.Pagination;
import com.eweblib.constants.EWebLibConstants;
import com.eweblib.dbhelper.DataBaseQueryBuilder;
import com.eweblib.exception.ResponseException;
import com.eweblib.log.LogJDBCAppender;
import com.eweblib.util.EWeblibThreadLocal;
import com.eweblib.util.EweblibUtil;

@Repository(value = "commonDao")
public class QueryDaoImpl implements IQueryDao {
	private static Logger logger = LogManager.getLogger(QueryDaoImpl.class);

	@Autowired
	public IMyBatisDao dao;

	public IMyBatisDao getDao() {
		return dao;
	}

	public void setDao(IMyBatisDao dao) {
		this.dao = dao;
	}

	@Override
	public BaseEntity insert(BaseEntity entity) {

		if (EweblibUtil.isEmpty(entity.getId())) {
			entity.setId(UUID.randomUUID().toString());
		}
		entity.setCreatedOn(new Date());
		entity.setUpdatedOn(new Date());
		entity.setCreatorId(EWeblibThreadLocal.getCurrentUserId());
		int result = dao.insert(entity);

		if (result == 1) {

		} else {
			throw new ResponseException("保存失败");
		}
	
		LogJDBCAppender.appendLog(entity, false, entity.getClass());
		return entity;

	}
	

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BaseEntity> List<T> listByQuery(DataBaseQueryBuilder builder, Class<T> classzz) {

		builder = mergeQueryBuilder(builder, classzz);

		List<Map<String, Object>> results = dao.listByQuery(builder);

		return mergeListValue(classzz, results, builder.getLimitColumnNames());

	}
	
	public <T extends BaseEntity> List<T> listBySql(String sql, Class<T> classzz){
		List<Map<String, Object>> results = dao.listBySql(sql);

		List<T> entityList = mergeListValue(classzz, results, null);
		return entityList;
	}

	public <T extends BaseEntity> List<T> mergeListValue(Class<T> classzz, List<Map<String, Object>> results, Set<String> keys) {
		List<T> entityList = new ArrayList<T>();


		for (Map<String, Object> result : results) {
			if (result == null) {
				result = new HashMap<String, Object>();
			}

			mergeEntityValue(classzz, keys, result);
			entityList.add((T) EweblibUtil.toEntity(result, classzz));
		}
		return entityList;
	}

	public <T extends BaseEntity> void mergeEntityValue(Class<T> classzz, Set<String> keys, Map<String, Object> result) {
	    if (keys != null) {
	    	for (String key : keys) {

	    		try {
	    			String className = classzz.getField(key).getType().getName();
	    			if (className.equalsIgnoreCase("java.lang.String") || className.equalsIgnoreCase("java.util.Date")) {
	    				if (result.get(key) == null) {
	    					result.put(key, "");
	    				}else{
	    					
	    					if (className.equalsIgnoreCase("java.util.Date")) {
	    						//FIXME other solution for date field not datetime field?
	    						if (result.get(key).toString().contains(" 00:00:00")) {
	    							result.put(key, result.get(key).toString().replace(" 00:00:00", ""));
	    						}
	    					}
	    				}
	    			}
	    		} catch (NoSuchFieldException e) {
	    			// do nothing
	    		} catch (SecurityException e) {
	    			// do nothing
	    		}

	    	}
	    }
    }
	

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BaseEntity> EntityResults<T> listByQueryWithPagnation(DataBaseQueryBuilder builder, Class<T> classzz) {

		builder = mergeQueryBuilder(builder, classzz);

		List<Map<String, Object>> results = dao.listByQueryWithPagination(builder);

		List<T> entityList = mergeListValue(classzz, results, builder.getLimitColumnNames());

		return toListBean(builder, entityList);

	}

	private <T extends BaseEntity> DataBaseQueryBuilder mergeQueryBuilder(DataBaseQueryBuilder builder, Class<T> classzz) {
	
		
		if (builder == null) {

			Table table = classzz.getAnnotation(Table.class);
			builder = new DataBaseQueryBuilder(table.name());
		}
		if (EWeblibThreadLocal.get(EWebLibConstants.PAGENATION) != null) {
			builder.pagination((Pagination) EWeblibThreadLocal.get(EWebLibConstants.PAGENATION));
		}
		if (EWeblibThreadLocal.get(EWebLibConstants.DB_QUERY_ORDER_BY) != null) {
			OrderBy order = (OrderBy) EWeblibThreadLocal.get(EWebLibConstants.DB_QUERY_ORDER_BY);
			if(order.getOrder().equalsIgnoreCase("asc")){
				builder.orderBy(order.getSort(), true);
			}else{
				builder.orderBy(order.getSort(), false);
			}

		}
		if (EweblibUtil.isEmpty(builder.getOrderBy())) {
			builder.orderBy(BaseEntity.CREATED_ON, false);			
		}
	    return builder;
    }

	@Override
	public void updateById(BaseEntity entity) {
		
		if (EweblibUtil.isEmpty(entity.getId())) {
			throw new IllegalArgumentException("Must have id value when call updateById method");
		}
		//not need update created on field
		entity.setUpdatedOn(new Date());
		LogJDBCAppender.appendLog(entity, true, entity.getClass());
		dao.updateById(entity);
		
	}
	


	@Override
	public void updateByQuery(DataBaseQueryBuilder builder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteById(BaseEntity entity) {
		dao.deleteById(entity);
	}

	public void deleteAllByTableName(String table) {
		dao.deleteAllByTableName(table);
	}
	
	public void deleteByQuery(DataBaseQueryBuilder builder){
		dao.deleteByQuery(builder);
	}

	@Override
	public <T extends BaseEntity> BaseEntity findOneByQuery(DataBaseQueryBuilder builder, Class<T> classzz) {
		Map<String, Object> result = dao.findOneByQuery(builder);
		return EweblibUtil.toEntity(result, classzz);
	}
	
	public <T extends BaseEntity> BaseEntity findById(String id, String table, Class<T> classzz) {
		DataBaseQueryBuilder builder = new DataBaseQueryBuilder(table);
		builder.and(EWebLibConstants.ID, id);
		
		Map<String, Object> result = dao.findOneByQuery(builder);

		if (EweblibUtil.isEmpty(result)) {
			return null;
		}
		
		return EweblibUtil.toEntity(result, classzz);
	}
	
	
	public <T extends BaseEntity> BaseEntity findByKeyValue(String key, String value, String table, Class<T> classzz) {
		DataBaseQueryBuilder builder = new DataBaseQueryBuilder(table);
		builder.and(key, value);
		Map<String, Object> result = dao.findOneByQuery(builder);

		if (EweblibUtil.isEmpty(result)) {
			return null;
		}
		return EweblibUtil.toEntity(result, classzz);
	}

	public int count(DataBaseQueryBuilder builder) {
		return dao.count(builder);
	}

	private <T extends BaseEntity> EntityResults<T> toListBean(DataBaseQueryBuilder builder, List<T> entityList) {

		EntityResults<T> listBean = new EntityResults<T>();
		listBean.setEntityList(entityList);

		int total = dao.count(builder);

		Pagination pagnation = new Pagination();
		pagnation.setTotal(total);
		listBean.setPagnation(pagnation);
		return listBean;

	}

	public boolean exists(DataBaseQueryBuilder builder) {

		if (this.count(builder) > 0) {
			return true;
		}
		return false;
	}
	
	public boolean exists(String key, String value, String table){
		DataBaseQueryBuilder builder = new DataBaseQueryBuilder(table);
		builder.and(key, value);
		return this.exists(builder);
	}

	
	public <T extends BaseEntity> List<T> distinctQuery(DataBaseQueryBuilder builder, Class<T> classzz) {
		return listByQuery(builder, classzz);
	}

}
