package com.application.zimply.baseobjects;

import java.util.ArrayList;

public class PhotoListObject {
	private ArrayList<HomePhotoObj> output;

	private String next_url;

	public ArrayList<HomePhotoObj> getOutput() {
		return output;
	}

	public void setOutput(ArrayList<HomePhotoObj> output) {
		this.output = output;
	}

	public String getNext_url() {
		return next_url;
	}

	public void setNext_url(String next_url) {
		this.next_url = next_url;
	}

}
