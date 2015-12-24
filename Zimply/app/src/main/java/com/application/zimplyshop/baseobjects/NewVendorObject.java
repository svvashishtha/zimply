package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 12/19/2015.
 *
 * New Vendor Object
 *
 */
public class NewVendorObject implements Serializable{

    private int id;

    private String map;

    private String name;

    private NewVendorAddressObject address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NewVendorAddressObject getAddress() {
        return address;
    }

    public void setAddress(NewVendorAddressObject address) {
        this.address = address;
    }
}
