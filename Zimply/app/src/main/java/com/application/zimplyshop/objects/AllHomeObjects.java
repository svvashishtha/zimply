package com.application.zimplyshop.objects;

import com.application.zimplyshop.baseobjects.HomeObject;
import com.google.gson.Gson;

import java.io.Serializable;

public class AllHomeObjects implements Serializable {

	static AllHomeObjects sInstance;

	public static AllHomeObjects getInstance() {
		if (sInstance == null) {
			sInstance = new AllHomeObjects();
		}
		return sInstance;
	}

	

	public Object parseData(String responseString) {
		return new Gson().fromJson(responseString, HomeObject.class);
	}

}
