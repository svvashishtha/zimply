package com.application.zimply.objects;

import com.google.gson.Gson;
import com.application.zimply.baseobjects.SignupObject;

import java.io.Serializable;

public class AllUsers implements Serializable {

	public static AllUsers sInstance;

	public static AllUsers getInstance() {
		if (sInstance == null) {
			sInstance = new AllUsers();
		}
		return sInstance;
	}

	public Object parseUserSignup(String responseString) {
		return new Gson().fromJson(responseString, SignupObject.class);
	}
}
