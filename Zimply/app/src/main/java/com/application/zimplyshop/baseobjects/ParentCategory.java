package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class ParentCategory implements Serializable {

    ArrayList<CategoryObject> subCategories;
    ArrayList<CategoryObject> categories;
    ArrayList<BaseProductListObject> products;
    public ArrayList<CategoryObject> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<CategoryObject> subCategories) {
        this.subCategories = subCategories;
    }

    public ArrayList<BaseProductListObject> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<BaseProductListObject> products) {
        this.products = products;
    }

    public ArrayList<CategoryObject> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<CategoryObject> categories) {
        this.categories = categories;
    }
}
