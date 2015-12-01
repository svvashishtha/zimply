package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Object class for Home Activity
 * 
 * @author Umesh
 *
 */
public class HomeObject implements Serializable{

	private ArrayList<ShopCategoryObject> category;

	private ArrayList<HomeProductObj> products;

	private ArrayList<HomeProductObj> deals;

	public ArrayList<ShopCategoryObject> getCategory() {
		return category;
	}

	public void setCategory(ArrayList<ShopCategoryObject> category) {
		this.category = category;
	}

	public ArrayList<HomeProductObj> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<HomeProductObj> products) {
		this.products = products;
	}

	public ArrayList<HomeProductObj> getDeals() {
		return deals;
	}

	public void setDeals(ArrayList<HomeProductObj> deals) {
		this.deals = deals;
	}

}
