package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class BaseArticlePhotoCategoryObj implements Serializable {

	private ArrayList<CategoryObject> photo_category;
	private ArrayList<CategoryObject> article_category;
	private ArrayList<CategoryObject> expert_category;
	private ArrayList<CategoryObject> expert_base_category;


	public ArrayList<CategoryObject> getExpert_category() {
		return expert_category;
	}

	public void setExpert_category(ArrayList<CategoryObject> expert_category) {
		this.expert_category = expert_category;
	}

	public ArrayList<CategoryObject> getArticle_category() {
		return article_category;
	}

	public void setArticle_category(ArrayList<CategoryObject> article_category) {
		this.article_category = article_category;
	}

	public ArrayList<CategoryObject> getPhoto_category() {
		return photo_category;
	}

	public void setPhoto_category(ArrayList<CategoryObject> photo_category) {
		this.photo_category = photo_category;
	}

    public void setExpert_base_category(ArrayList<CategoryObject> expert_base_category) {
        this.expert_base_category = expert_base_category;
    }

    public ArrayList<CategoryObject> getExpert_base_category() {
        return expert_base_category;
    }


}
