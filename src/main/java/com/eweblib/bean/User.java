package com.eweblib.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Table(name = User.TABLE_NAME)
public class User extends BaseEntity {

	public static final String STATUS = "status";
	public static final String TABLE_NAME = "User";
	public static final String NAME = "name";
	public static final String PASSWORD = "password";
	public static final String USER_NAME = "userName";

	@Column(name = USER_NAME, unique = true)
	@Expose
	public String userName;

	@Column(name = NAME)
	@Expose
	public String name;

	@Column(name = PASSWORD)
	@Expose
	public String password;

	@Column(name = STATUS)
	@Expose
	public Integer status;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
