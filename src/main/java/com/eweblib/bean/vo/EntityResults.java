package com.eweblib.bean.vo;

import java.util.List;

import com.eweblib.bean.BaseEntity;

public class EntityResults<T extends BaseEntity> {

	public Pagination pagnation;

	public List<T> entityList;

	public Pagination getPagnation() {
		return pagnation;
	}

	public void setPagnation(Pagination pagnation) {
		this.pagnation = pagnation;
	}

	public List<T> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<T> entityList) {
		this.entityList = entityList;
	}

}
