package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class BaseCategoryObject implements Serializable {
	private ArrayList<CompleteCategoryObject> category;

	public ArrayList<CompleteCategoryObject> getCategory() {
		return category;
	}

	public void setCategory(ArrayList<CompleteCategoryObject> category) {
		this.category = category;
	}
}
