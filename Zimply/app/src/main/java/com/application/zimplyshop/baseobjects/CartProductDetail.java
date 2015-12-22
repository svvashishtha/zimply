package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 12/21/2015.
 *
 * Detail of every item added in cart
 */
public class CartProductDetail implements Serializable{
    int shipping_charge;

    int cart_item_id;

    int available_qty;

    boolean isShowingPaymentDesc;

    BaseProductListObject product;

    int qty;

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getShipping_charge() {
        return shipping_charge;
    }

    public void setShipping_charge(int shipping_charge) {
        this.shipping_charge = shipping_charge;
    }

    public int getCart_item_id() {
        return cart_item_id;
    }

    public void setCart_item_id(int cart_item_id) {
        this.cart_item_id = cart_item_id;
    }

    public int getAvailable_qty() {
        return available_qty;
    }

    public void setAvailable_qty(int available_qty) {
        this.available_qty = available_qty;
    }

    public BaseProductListObject getProduct() {
        return product;
    }

    public void setProduct(BaseProductListObject product) {
        this.product = product;
    }

    public boolean isShowingPaymentDesc() {
        return isShowingPaymentDesc;
    }

    public void setIsShowingPaymentDesc(boolean isShowingPaymentDesc) {
        this.isShowingPaymentDesc = isShowingPaymentDesc;
    }
}
