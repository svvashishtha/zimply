package com.application.zimply.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 10/17/2015.
 */
public class NonLoggedInCartObj implements Serializable{

    String productId;

    int quantity;

    public NonLoggedInCartObj(String productId ,int quantity){
        this.productId = productId;
        this.quantity =quantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object obj) {
        return ((NonLoggedInCartObj) obj).getProductId().equalsIgnoreCase(productId);

    }
}
