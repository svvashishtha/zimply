package com.application.zimplyshop.baseobjects;

import java.util.ArrayList;

/**
 * Created by Ashish Goel on 1/8/2016.
 */
public class SimilarProductsListObject {

    String next_url;

    ArrayList<BaseProductListObject> products;

    public ArrayList<BaseProductListObject> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<BaseProductListObject> products) {
        this.products = products;
    }

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String next_url) {
        this.next_url = next_url;
    }
}
