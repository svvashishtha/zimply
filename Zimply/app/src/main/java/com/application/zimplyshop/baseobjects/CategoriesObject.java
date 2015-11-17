package com.application.zimplyshop.baseobjects;

import java.util.ArrayList;

public class CategoriesObject {
	String name;
	ArrayList<String> subCategories;

	public CategoriesObject(String name, ArrayList<String> subCategories) {
		this.name = name;
		this.subCategories = subCategories;
	}

	public ArrayList<String> getSubCategories() {
		return subCategories;
	}

	public String getName() {
		return name;
	}
}
