package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 11/27/2015.
 */
public class VendorObj implements Serializable{

    String company_name;

    VendorAddressObj reg_add;

    int vendor_id;

    private int book_product_id;

    String map;

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public int getBook_product_id() {
        return book_product_id;
    }

    public void setBook_product_id(int book_product_id) {
        this.book_product_id = book_product_id;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public VendorAddressObj getReg_add() {
        return reg_add;
    }

    public void setReg_add(VendorAddressObj reg_add) {
        this.reg_add = reg_add;
    }


}
