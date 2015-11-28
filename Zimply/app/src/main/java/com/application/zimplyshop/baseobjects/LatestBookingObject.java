package com.application.zimplyshop.baseobjects;

/**
 * Created by Umesh Lohani on 11/27/2015.
 */
public class LatestBookingObject {

    private HomeProductObj product;

    private VendorObj vendor;

    private String visit_date;


    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public HomeProductObj getProduct() {
        return product;
    }

    public void setProduct(HomeProductObj product) {
        this.product = product;
    }

    public VendorObj getVendor() {
        return vendor;
    }

    public void setVendor(VendorObj vendor) {
        this.vendor = vendor;
    }
}
