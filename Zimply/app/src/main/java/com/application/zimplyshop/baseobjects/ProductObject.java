package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 12/19/2015.
 *
 * Basic Product Object
 *
 */
public class ProductObject implements Serializable{

    private String sku;

    private boolean is_cod;

    private String image;

    private int height;

    private ArrayList<String> thumbs;

    int max_shipping_days;

    private ArrayList<String> images;

    private String return_damage;

    private int id;

    private String category;

    private int width;

    private int shipping_charges;

    private String description;

    private int price;

    private String faq;

    private int available_quantity;

    private String slug;

    private String care;

    private int min_shipping_days;

    private String name;

    private ArrayList<ProductAttribute> attribute;

    private boolean is_favourite;

    private int favourite_item_id;

    private boolean is_o2o;

    public boolean is_o2o() {
        return is_o2o;
    }

    public void setIs_o2o(boolean is_o2o) {
        this.is_o2o = is_o2o;
    }

    public int getFavourite_item_id() {
        return favourite_item_id;
    }

    public boolean is_favourite() {
        return is_favourite;
    }

    public void setFavourite_item_id(int favourite_item_id) {
        this.favourite_item_id = favourite_item_id;
    }

    public void setIs_favourite(boolean is_favourite) {
        this.is_favourite = is_favourite;
    }

    public ArrayList<ProductAttribute> getAttribute() {
        return attribute;
    }

    public void setAttribute(ArrayList<ProductAttribute> attribute) {
        this.attribute = attribute;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public boolean is_cod() {
        return is_cod;
    }

    public void setIs_cod(boolean is_cod) {
        this.is_cod = is_cod;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ArrayList<String> getThumbs() {
        return thumbs;
    }

    public void setThumbs(ArrayList<String> thumbs) {
        this.thumbs = thumbs;
    }

    public int getMax_shipping_days() {
        return max_shipping_days;
    }

    public void setMax_shipping_days(int max_shipping_days) {
        this.max_shipping_days = max_shipping_days;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getReturn_damage() {
        return return_damage;
    }

    public void setReturn_damage(String return_damage) {
        this.return_damage = return_damage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getShipping_charges() {
        return shipping_charges;
    }

    public void setShipping_charges(int shipping_charges) {
        this.shipping_charges = shipping_charges;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getFaq() {
        return faq;
    }

    public void setFaq(String faq) {
        this.faq = faq;
    }

    public int getAvailable_quantity() {
        return available_quantity;
    }

    public void setAvailable_quantity(int available_quantity) {
        this.available_quantity = available_quantity;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCare() {
        return care;
    }

    public void setCare(String care) {
        this.care = care;
    }

    public int getMin_shipping_days() {
        return min_shipping_days;
    }

    public void setMin_shipping_days(int min_shipping_days) {
        this.min_shipping_days = min_shipping_days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
