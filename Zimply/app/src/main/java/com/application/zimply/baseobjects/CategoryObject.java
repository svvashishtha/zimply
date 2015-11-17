package com.application.zimply.baseobjects;

import java.io.Serializable;

public class CategoryObject implements Serializable {

    private String name;
    private String id;
    private String image;
    private int type;
    private int dupType;
    private boolean serve;

    private ImageObject img;

    public CategoryObject() {

    }

    public CategoryObject(CategoryObject object) {
        this.name = object.name;
        this.id = object.getId();
        this.image = object.getImage();
        this.type = object.getType();
        this.dupType = object.getType();
        this.img = object.getImg();
    }


    public ImageObject getImg() {
        return img;
    }

    public void setImg(ImageObject img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isServe() {
        return serve;
    }

    public void setServe(boolean serve) {
        this.serve = serve;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getDupType() {
        return dupType;
    }

    public void setDupType(int dupType) {
        this.dupType = dupType;
    }
}
