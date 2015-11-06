package com.application.zimply.objects;

import com.google.gson.Gson;
import com.application.zimply.baseobjects.HomeObject;

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
