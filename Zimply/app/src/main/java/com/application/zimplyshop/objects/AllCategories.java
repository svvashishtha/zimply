package com.application.zimplyshop.objects;

import com.application.zimplyshop.baseobjects.BaseArticlePhotoCategoryObj;
import com.application.zimplyshop.baseobjects.BaseCategoryObject;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.ShopCategoryListObject;
import com.application.zimplyshop.baseobjects.ShopSubCategoryObjectList;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class AllCategories implements Serializable {


	static AllCategories sInstance;

	private BaseCategoryObject categories;

	private BaseArticlePhotoCategoryObj photoCateogryObjs;

	private ArrayList<CategoryObject> cities;

	public BaseArticlePhotoCategoryObj getPhotoCateogryObjs() {
		if(photoCateogryObjs == null ){
			photoCateogryObjs = new BaseArticlePhotoCategoryObj();
		}
		return photoCateogryObjs;
	}

	public BaseCategoryObject getCategories() {
		return categories;
	}

	public static AllCategories getInstance() {
		if (sInstance == null) {
			sInstance = new AllCategories();
		}
		return sInstance;
	}

	public Object parseData(String responseString) {
		return new Gson()
				.fromJson(responseString, ShopCategoryListObject.class);
	}

	public Object parseSubCategoryData(String responseString) {
		return new Gson().fromJson(responseString,
				ShopSubCategoryObjectList.class);
	}

	public Object parseCompleteCategoryData(String responseString) {
		categories = new Gson().fromJson(responseString,
				BaseCategoryObject.class);
		return categories;
	}

	public Object parseArticlePhotoCategoryData(String responseString) {
		photoCateogryObjs = new Gson().fromJson(responseString,
				BaseArticlePhotoCategoryObj.class);
		return photoCateogryObjs;
	}

	public void setPhotoCateogryObjs(BaseArticlePhotoCategoryObj photoCateogryObjs) {
		this.photoCateogryObjs = photoCateogryObjs;
	}

	public ArrayList<CategoryObject> getCities() {
		return cities;
	}

	public void setCities(ArrayList<CategoryObject> cities) {
		if(this.cities == null){
			this.cities = new ArrayList<CategoryObject>();
		}
		this.cities.addAll(cities);
	}
}
