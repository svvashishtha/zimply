package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 11/14/2015.
 */
public class ProductVendorTimeObj implements Serializable{

    private String vendor;
    private String line1;
    private String line2;
    private String city;
    private String pincode;
    private String created_on;
    private int book_product_id;

    public int getBook_product_id() {
        return book_product_id;
    }

    public void setBook_product_id(int book_product_id) {
        this.book_product_id = book_product_id;
    }

    public String getCreated_on() {
        return created_on;
    }
    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
