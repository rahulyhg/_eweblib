package com.eweblib.bean;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = MenuItem.TABLE_NAME)
public class MenuItem extends BaseEntity {

	public static final String CSS_NAME = "cssName";
	public static final String MENU_ID = "menuId";
	public static final String TITLE = "title";
	public static final String HREF = "href";
	public static final String DISPLAY_ORDER = "displayOrder";
	public static final String TABLE_NAME = "MenuItem";

	@Column(name = TITLE)
	public String title;

	@Column(name = HREF)
	public String href;

	@Column(name = DISPLAY_ORDER)
	public Integer displayOrder;

	@Column(name = MENU_ID)
	public String menuId;

	@Column(name = CSS_NAME)
	public String cssName;

	public String getCssName() {
		return cssName;
	}

	public void setCssName(String cssName) {
		this.cssName = cssName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

}
