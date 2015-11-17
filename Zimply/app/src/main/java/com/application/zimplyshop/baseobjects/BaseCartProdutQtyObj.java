package com.application.zimplyshop.baseobjects;

/**
 * Created by Umesh Lohani on 10/23/2015.
 */
public class BaseCartProdutQtyObj {

    private int id;

    private int qty;

    public BaseCartProdutQtyObj(int id, int qty){
        this.id = id;
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
