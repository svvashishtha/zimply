package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

public class VendorAddressObj implements Serializable{


    int id;

    String line1;

    String city;

    String pincode;

    String phone;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    VendorLocationObj location;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public VendorLocationObj getLocation() {
        return location;
    }

    public void setLocation(VendorLocationObj location) {
        this.location = location;
    }
}
