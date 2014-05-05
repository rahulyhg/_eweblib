package com.eweblib.bean;

import java.util.List;

import com.google.gson.annotations.Expose;

public class IDS extends BaseEntity {

	@Expose
	public List<String> ids;



	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}


}
