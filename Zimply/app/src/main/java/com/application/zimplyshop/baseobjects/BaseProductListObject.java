package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 12/19/2015.
 */
public class BaseProductListObject implements Serializable{

    private String image;

    private boolean is_o2o;

    private String name;

    private int price;

    private int mrp;

    private String slug;

    private int id;

    private int height;

    private int width;

    private int discount;

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getDiscount() {
        return discount;
    }

    private boolean is_cod;

    public int getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }

    public boolean is_cod() {
        return is_cod;
    }

    public void setIs_cod(boolean is_cod) {
        this.is_cod = is_cod;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean is_o2o() {
        return is_o2o;
    }

    public void setIs_o2o(boolean is_o2o) {
        this.is_o2o = is_o2o;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
