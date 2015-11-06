package com.application.zimply.baseobjects;

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

	private ArrayList<HomePhotoObj> photos;

	private ArrayList<HomeArticleObj> articles;

	private ArrayList<HomeProductObj> products;

	private ArrayList<HomeProductObj> deals;

	private ArrayList<HomeExpertObj> experts;

	public ArrayList<ShopCategoryObject> getCategory() {
		return category;
	}

	public void setCategory(ArrayList<ShopCategoryObject> category) {
		this.category = category;
	}

	public ArrayList<HomePhotoObj> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<HomePhotoObj> photos) {
		this.photos = photos;
	}

	public ArrayList<HomeArticleObj> getArticles() {
		return articles;
	}

	public void setArticles(ArrayList<HomeArticleObj> articles) {
		this.articles = articles;
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

	public ArrayList<HomeExpertObj> getExperts() {
		return experts;
	}

	public void setExperts(ArrayList<HomeExpertObj> experts) {
		this.experts = experts;
	}

}
