package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by apoorvarora on 08/10/15.
 */
public class ProductAttribute implements Serializable{

    private String unit;
    private String key;
    private String value;


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
