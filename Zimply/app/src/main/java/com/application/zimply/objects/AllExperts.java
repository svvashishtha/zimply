package com.application.zimply.objects;

import com.google.gson.Gson;
import com.application.zimply.baseobjects.ExpertListObject;

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
