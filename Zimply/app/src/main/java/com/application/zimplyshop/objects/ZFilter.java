package com.application.zimplyshop.objects;

import java.io.Serializable;

public class ZFilter implements Serializable {

	private String name;
	private String phone;
	private int userid;
	private String location;
	private String query;
	private String photo_slug;
	private String pro_slug;
	private String category;
	private String area;
	private int area_unit;
	private int budget;
	private int type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getPhoto_slug() {
		return photo_slug;
	}

	public void setPhoto_slug(String photo_slug) {
		this.photo_slug = photo_slug;
	}

	public String getPro_slug() {
		return pro_slug;
	}

	public void setPro_slug(String pro_slug) {
		this.pro_slug = pro_slug;
	}


	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public int getArea_unit() {
		return area_unit;
	}

	public void setArea_unit(int area_unit) {
		this.area_unit = area_unit;
	}

	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
