package com.application.zimplyshop.baseobjects;

import java.util.ArrayList;

public class ExpertListObject {
	private String next_url;

	private int count;

	private ArrayList<HomeExpertObj> output;


	private SubCategories selected;

	public SubCategories getSelected() {
		return selected;
	}

    public void setSelected(SubCategories selected) {
		this.selected = selected;
	}

	public String getNext_url() {
		return next_url;
	}

	public void setNext_url(String next_url) {
		this.next_url = next_url;
	}

	public ArrayList<HomeExpertObj> getOutput() {
		return output;
	}

	public void setOutput(ArrayList<HomeExpertObj> output) {
		this.output = output;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public class SubCategories{
		private ArrayList<CategoryObject> subcategories;

        public ArrayList<CategoryObject> getSubcategories() {
            return subcategories;
        }

        public void setSubcategories(ArrayList<CategoryObject> subcategories) {
            this.subcategories = subcategories;
        }
    }
}
