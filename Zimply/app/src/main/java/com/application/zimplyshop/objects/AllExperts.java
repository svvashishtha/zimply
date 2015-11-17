package com.application.zimplyshop.objects;

import com.application.zimplyshop.baseobjects.ExpertListObject;
import com.google.gson.Gson;

import java.io.Serializable;

public class AllExperts implements Serializable {

	static AllExperts sInstance;

	public static AllExperts getInstance() {
		if (sInstance == null) {
			sInstance = new AllExperts();
		}
		return sInstance;
	}

	

	public Object parseData(String responseString) {
		return new Gson().fromJson(responseString, ExpertListObject.class);
	}
}
