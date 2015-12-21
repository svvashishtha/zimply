package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Saurabh on 12-10-2015.
 * Plain cart object
 *
 */
public class CartObject implements Serializable {

    CartDetailObject cart;

    public CartDetailObject getCart() {
        return cart;
    }

    public void setCart(CartDetailObject cart) {
        this.cart = cart;
    }
}
