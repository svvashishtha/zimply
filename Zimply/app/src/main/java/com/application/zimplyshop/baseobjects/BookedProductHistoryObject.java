package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 11/15/2015.
 */
public class BookedProductHistoryObject implements Serializable{

    String productImg;

    String name;

    int price;

    String status;

    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    ProductVendorTimeObj vendorTimeObj;

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductVendorTimeObj getVendorTimeObj() {
        return vendorTimeObj;
    }

    public void setVendorTimeObj(ProductVendorTimeObj vendorTimeObj) {
        this.vendorTimeObj = vendorTimeObj;
    }
}
