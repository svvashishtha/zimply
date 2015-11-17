package com.application.zimplyshop.baseobjects;

import java.util.ArrayList;

public class ArticleListObject {

	private String next_url;

	private ArrayList<HomeArticleObj> output;

	public String getNext_url() {
		return next_url;
	}

	public void setNext_url(String next_url) {
		this.next_url = next_url;
	}

	public ArrayList<HomeArticleObj> getOutput() {
		return output;
	}

	public void setOutput(ArrayList<HomeArticleObj> output) {
		this.output = output;
	}
}
