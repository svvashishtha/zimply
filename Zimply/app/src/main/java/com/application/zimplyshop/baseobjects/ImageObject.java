package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 11/17/2015.
 */
public class ImageObject implements Serializable{

    private int width;

    private int height;

    private String image;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
