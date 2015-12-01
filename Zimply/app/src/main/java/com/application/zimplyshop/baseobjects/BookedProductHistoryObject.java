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

    String line1;

    String line2;

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    String vendor;

    String pincode;

    String city;

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

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
