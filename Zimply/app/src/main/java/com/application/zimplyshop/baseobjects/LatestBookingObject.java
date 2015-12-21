package com.application.zimplyshop.baseobjects;

/**
 * Created by Umesh Lohani on 11/27/2015.
 */
public class LatestBookingObject {

    private BaseProductListObject product;

    private NewVendorObject vendor;

    private NewBookObject book;

    public NewBookObject getBook() {
        return book;
    }

    public void setBook(NewBookObject book) {
        this.book = book;
    }

    public BaseProductListObject getProduct() {
        return product;
    }

    public void setProduct(BaseProductListObject product) {
        this.product = product;
    }

    public NewVendorObject getVendor() {
        return vendor;
    }

    public void setVendor(NewVendorObject vendor) {
        this.vendor = vendor;
    }
}
