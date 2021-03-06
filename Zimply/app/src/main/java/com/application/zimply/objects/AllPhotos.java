package com.application.zimply.objects;

import com.google.gson.Gson;
import com.application.zimply.baseobjects.HomePhotoObj;
import com.application.zimply.baseobjects.PhotoListObject;

import java.io.Serializable;

public class AllPhotos implements Serializable {

	static AllPhotos sInstance;

	public static AllPhotos getInstance() {
		if (sInstance == null) {
			sInstance = new AllPhotos();

		}
		return sInstance;
	}

	public Object parseData(String responseString) {
		return new Gson().fromJson(responseString, PhotoListObject.class);
	}

	public Object parseDataObject(String responseString)
	{
		return new Gson().fromJson(responseString, HomePhotoObj.class);
	}

}
