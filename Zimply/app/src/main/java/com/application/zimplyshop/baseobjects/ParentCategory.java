package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class ParentCategory implements Serializable {

    ArrayList<CategoryObject> subCategories;
    ArrayList<CategoryObject> categories;

    public ArrayList<CategoryObject> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<CategoryObject> subCategories) {
        this.subCategories = subCategories;
    }

    public ArrayList<CategoryObject> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<CategoryObject> categories) {
        this.categories = categories;
    }
}
