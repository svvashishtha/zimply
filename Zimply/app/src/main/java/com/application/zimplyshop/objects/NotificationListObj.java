package com.application.zimplyshop.objects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 11/19/2015.
 */
public class NotificationListObj implements Serializable{

    private String title;

    private String image;

    private String created_on;

    private int type;

    private String slug;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
