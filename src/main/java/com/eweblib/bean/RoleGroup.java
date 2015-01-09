package com.eweblib.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Table(name = RoleGroup.TABLE_NAME)
public class RoleGroup extends BaseEntity {
	public static final String MENU_IDS = "menuIds";

	public static final String GROUP_NAME = "groupName";

	public static final String PERMISSIONS = "permissions";

	public static final String TABLE_NAME = "RoleGroup";

	@Column(name = GROUP_NAME)
	@Expose
	public String groupName;

	@Column(name = "description")
	@Expose
	public String description;
	
	@Column(name = MENU_IDS)
	public String menuIds;
	
	
	public String getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(String menuIds) {
		this.menuIds = menuIds;
	}
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}