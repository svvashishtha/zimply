package com.application.zimplyshop.objects;

import com.application.zimplyshop.baseobjects.SignupObject;
import com.google.gson.Gson;

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
