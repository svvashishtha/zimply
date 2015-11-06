package com.application.zimply.baseobjects;

import java.util.ArrayList;

public class FavListObject {

	private String next_url;

	private ArrayList<FavListItemObject> objects;

	public String getNext_url() {
		return next_url;
	}

	public void setNext_url(String next_url) {
		this.next_url = next_url;
	}

	public ArrayList<FavListItemObject> getObjects() {
		if (objects == null) {
			objects = new ArrayList<FavListItemObject>();
		}
		return objects;
	}

	public void setObjects(ArrayList<FavListItemObject> objects) {
		this.objects = objects;
	}

}
