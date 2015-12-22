package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 12/21/2015.
 *
 * Detail of the cart that contains products of the cart also as a Arraylist
 */
public class CartDetailObject implements Serializable{

    private int total_shipping;

    private int price;

    private int total_price;

    private ArrayList<CartProductDetail> detail;

    private int cart_id;

    public int getTotal_shipping() {
        return total_shipping;
    }

    public void setTotal_shipping(int total_shipping) {
        this.total_shipping = total_shipping;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public ArrayList<CartProductDetail> getDetail() {
        return detail;
    }

    public void setDetail(ArrayList<CartProductDetail> detail) {
        this.detail = detail;
    }

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }
}
