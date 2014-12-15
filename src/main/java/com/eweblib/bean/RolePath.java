package com.eweblib.bean;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = RolePath.TABLE_NAME)
public class RolePath extends BaseEntity {

	public static final String GROUP_NAME = "groupName";

	public static final String DESCRIPTION = "description";

	public static final String PATH = "path";

	public static final String TABLE_NAME = "RolePath";

	@Column(name = PATH)
	public String path;

	@Column(name = DESCRIPTION)
	public String description;

	@Column(name = GROUP_NAME)
	public String groupName;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
