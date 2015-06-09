package com.eweblib.bean.vo;

import java.util.List;

import com.eweblib.bean.BaseEntity;
import com.google.gson.annotations.Expose;

/**
 * 从前端传递过来的数组ID
 * 
 * @author
 * 
 */
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
