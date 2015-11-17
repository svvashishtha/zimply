package com.application.zimplyshop.baseobjects;

import java.util.ArrayList;

public class CompleteSubCategoryObject {

	private String name;

	private ArrayList<String> subsubcategory;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getSubsubcategory() {
		return subsubcategory;
	}

	public void setSubsubcategory(ArrayList<String> subsubcategory) {
		this.subsubcategory = subsubcategory;
	}
}
