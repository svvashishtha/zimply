package com.application.zimply.baseobjects;

import java.util.ArrayList;

public class ProductListObject {
	private String next_url;

	private ArrayList<HomeProductObj> products;

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
