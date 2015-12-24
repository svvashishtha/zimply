package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 12/19/2015.
 */
public class FavouriteObject implements Serializable{

    private BaseProductListObject product;

    private boolean is_favourite;

    private int favourite_item_id;

    private int type;

    public BaseProductListObject getProduct() {
        return product;
    }

    public void setProduct(BaseProductListObject product) {
        this.product = product;
    }

    public boolean is_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(boolean is_favourite) {
        this.is_favourite = is_favourite;
    }

    public int getFavourite_item_id() {
        return favourite_item_id;
    }

    public void setFavourite_item_id(int favourite_item_id) {
        this.favourite_item_id = favourite_item_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
