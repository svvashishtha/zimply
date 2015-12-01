package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 11/27/2015.
 */
public class VendorObj implements Serializable{

    String company_name;

    VendorAddressObj reg_add;

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public VendorAddressObj getReg_add() {
        return reg_add;
    }

    public void setReg_add(VendorAddressObj reg_add) {
        this.reg_add = reg_add;
    }

    public class VendorAddressObj{
        String phone;

        int id;

        String line1;

        LocationObj location;

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

        public LocationObj getLocation() {
            return location;
        }

        public void setLocation(LocationObj location) {
            this.location = location;
        }
    }

    public class LocationObj{
        Double latitude;

        Double longitude;

        int id;

        String name;

        boolean serve;

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
    }

}
