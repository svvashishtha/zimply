package com.application.zimplyshop.baseobjects;

/**
 * Created by Umesh Lohani on 12/19/2015.
 *
 * New Booking object
 *
 */
public class NewBookObject {

    private String created_on;

    private String booking_status;

    int id;

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
