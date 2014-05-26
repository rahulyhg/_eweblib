package com.eweblib.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.eweblib.bean.BaseEntity;
import com.eweblib.dao.IQueryDao;
import com.eweblib.dbhelper.DataBaseQueryBuilder;
import com.eweblib.dbhelper.DataBaseQueryOpertion;
import com.eweblib.exception.ResponseException;
import com.eweblib.util.EWeblibThreadLocal;

public abstract class AbstractService {

	@Autowired
	public IQueryDao dao;

	public IQueryDao getDao() {
		return dao;
	}

	public void setDao(IQueryDao dao) {
		this.dao = dao;
	}

	protected void checkOwner(BaseEntity entity) {
		checkOwner(entity, BaseEntity.CREATOR_ID, null, null);
	}
	
	protected void checkOwner(BaseEntity entity, String checkKey, Object checkValue) {
		checkOwner(entity, BaseEntity.CREATOR_ID, checkKey, checkValue);
	}

	protected void checkOwner(BaseEntity entity, String ownerKey, String checkKey, Object checkValue) {
		DataBaseQueryBuilder query = new DataBaseQueryBuilder(entity.getTable());

		if (ownerKey != null) {

			query.and(ownerKey, EWeblibThreadLocal.getCurrentUserId());

		}
		if (checkKey != null && checkValue != null) {
			query.and(checkKey, checkValue);
		}

		if (!this.dao.exists(query)) {
			throw new ResponseException("非法数据");
		}

	}
	
	protected void checkOwner(BaseEntity entity, String ownerKey, DataBaseQueryBuilder dbQuery) {
		if (ownerKey != null) {

			DataBaseQueryBuilder query = new DataBaseQueryBuilder(entity.getTable());
			query.and(ownerKey, EWeblibThreadLocal.getCurrentUserId());

			dbQuery.and(query);
		}

		if (!this.dao.exists(dbQuery)) {
			throw new ResponseException("非法数据");
		}

	}
	
	
	protected void mergeKeywordQuery(DataBaseQueryBuilder builder, String keyword, String table, String[] queryKeys) {

		if (keyword != null && queryKeys != null) {
			DataBaseQueryBuilder keywordQuery = new DataBaseQueryBuilder(table);

			for (String key : queryKeys) {
				keywordQuery.or(DataBaseQueryOpertion.LIKE, key, keyword);
			}

			builder.and(keywordQuery);
		}

	}

	protected void checkIdsStatus(DataBaseQueryBuilder query, List<String> ids) {
		checkIdsStatus(query, ids, BaseEntity.CREATOR_ID);

	}
	
	protected void checkIdsStatus(DataBaseQueryBuilder query, List<String> ids, String ownerKey) {
		if (ids.size() > 0) {
			DataBaseQueryBuilder idsQuery = new DataBaseQueryBuilder(query.getTable());
			if (ownerKey != null) {
				idsQuery.and(ownerKey, EWeblibThreadLocal.getCurrentUserId());

			}
			idsQuery.and(DataBaseQueryOpertion.IN, BaseEntity.ID, ids);
			idsQuery.and(query);
			if (this.dao.count(idsQuery) != ids.size()) {
				throw new ResponseException("非法数据");
			}

		}

	}
	
	protected String genDownloadRandomRelativePath(String userId) {
		StringBuffer sb = new StringBuffer("");
		sb.append("download/").append(userId).append("/");
		return sb.toString();
	}

}
