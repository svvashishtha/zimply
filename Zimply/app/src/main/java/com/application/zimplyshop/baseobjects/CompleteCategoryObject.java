package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class CompleteCategoryObject implements Serializable {

	private String name;

	private ArrayList<CompleteSubCategoryObject> subcategory;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<CompleteSubCategoryObject> getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(ArrayList<CompleteSubCategoryObject> subcategory) {
		this.subcategory = subcategory;
	}
}
