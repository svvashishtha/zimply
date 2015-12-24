package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

public class HomeProductObj implements Serializable{

	private ProductObject product;

	private NewVendorObject vendor;

    public ProductObject getProduct() {
        return product;
    }

    public void setProduct(ProductObject product) {
        this.product = product;
    }

    public NewVendorObject getVendor() {
        return vendor;
    }

    public void setVendor(NewVendorObject vendor) {
        this.vendor = vendor;
    }
}
