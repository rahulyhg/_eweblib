package com.eweblib.bean;

import java.util.List;

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
