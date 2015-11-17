package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 10/9/2015.
 */
public class IndividualOrderItemObj implements Serializable{

    private int id;

    private String status;

    private String slug;

    private String name;

    private int qty;

    private int item_price;

    private int total_orderitem_price;

    private String image;

    private boolean cancel_orderitem;

    private boolean return_orderitem;

    private String track;

    private String color;

    private String estimated_delivery;

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

    public String getEstimated_delivery() {
        return estimated_delivery;
    }

    public void setEstimated_delivery(String estimated_delivery) {
        this.estimated_delivery = estimated_delivery;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isCancel_orderitem() {
        return cancel_orderitem;
    }

    public void setCancel_orderitem(boolean cancel_orderitem) {
        this.cancel_orderitem = cancel_orderitem;
    }

    public boolean isReturn_orderitem() {
        return return_orderitem;
    }

    public void setReturn_orderitem(boolean return_orderitem) {
        this.return_orderitem = return_orderitem;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getItem_price() {
        return item_price;
    }

    public void setItem_price(int item_price) {
        this.item_price = item_price;
    }

    public int getTotal_orderitem_price() {
        return total_orderitem_price;
    }

    public void setTotal_orderitem_price(int total_orderitem_price) {
        this.total_orderitem_price = total_orderitem_price;
    }
}
