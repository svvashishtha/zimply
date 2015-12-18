package com.application.zimplyshop.baseobjects;

import java.util.ArrayList;

public class ProductListObject {
	private String next_url;

	private ArrayList<HomeProductObj> products;

	private ArrayList<ShopSubCategoryObj> subcategory;

	public ArrayList<ShopSubCategoryObj> getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(ArrayList<ShopSubCategoryObj> subcategory) {
		this.subcategory = subcategory;
	}

	public String getNext_url() {
		return next_url;
	}

	public void setNext_url(String next_url) {
		this.next_url = next_url;
	}

	public ArrayList<HomeProductObj> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<HomeProductObj> products) {
		this.products = products;
	}
	
}
