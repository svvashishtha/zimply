package com.application.zimply.objects;

import com.google.gson.Gson;
import com.application.zimply.baseobjects.ArticleDetailObject;
import com.application.zimply.baseobjects.ArticleListObject;

import java.io.Serializable;

public class AllArticles implements Serializable {

	static AllArticles sInstance;

	public static AllArticles getInstance() {
		if (sInstance == null) {
			sInstance = new AllArticles();
		}
		return sInstance;
	}

	public Object parseData(String responseString) {
		Object object = new Gson().fromJson(responseString, ArticleListObject.class);
		return object;
	}

	public Object parseArticleData(String responseString) {

		return new Gson().fromJson(responseString, ArticleDetailObject.class);
	}
	/*public Object parseHomeArticleObject(String data)
	{
		return new Gson().fromJson(data, HomeArticleObj.class);
	}*/
}
