package com.eweblib.bean;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Table(name = Menu.TABLE_NAME)
public class Menu extends BaseEntity {
	public static final String MENU_GROUP_ID = "menuGroupId";

	public static final String STYLE = "style";

	public static final String CSS_NAME = "cssName";

	public static final String STYLE_NAME = "styleName";

	public static final String TITLE = "title";

	public static final String DISPLAY_ORDER = "displayOrder";

	public static final String GROUP_ID = "groupId";

	public static final String TABLE_NAME = "Menu";


	@Column(name = DISPLAY_ORDER)
	public Integer displayOrder;

	@Column(name = TITLE)
	public String title;

	@Column(name = STYLE)
	public String style;

	@Column(name = CSS_NAME)
	public String cssName;

	
	@Column(name = MENU_GROUP_ID)
	public String menuGroupId;
	

	
	public String getMenuGroupId() {
		return menuGroupId;
	}

	public void setMenuGroupId(String menuGroupId) {
		this.menuGroupId = menuGroupId;
	}

	public List<MenuItem> list;
	
	
	public List<String> items;
	

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getCssName() {
		return cssName;
	}

	public void setCssName(String dataOptions) {
		this.cssName = dataOptions;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public static String getTableName() {
		return TABLE_NAME;
	}

	public List<MenuItem> getList() {
		return list;
	}

	public void setList(List<MenuItem> list) {
		this.list = list;
	}

}
