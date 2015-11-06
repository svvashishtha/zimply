package com.application.zimply.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 11/4/2015.
 */
public class BannerObject implements Serializable{

    private int type;

    private String banner;

    private String slug;

    private String name;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
