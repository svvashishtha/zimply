package com.application.zimply.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 10/9/2015.
 */
public class OrderItemObj implements Serializable{
    private String order_id;

    private String payment_method;

    private String ordered_on;

    private ShippingAddressObj shipping_address;

    private ArrayList<IndividualOrderItemObj> orderitem;

    private int total_price;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getOrdered_on() {
        return ordered_on;
    }

    public void setOrdered_on(String ordered_on) {
        this.ordered_on = ordered_on;
    }

    public ShippingAddressObj getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(ShippingAddressObj shipping_address) {
        this.shipping_address = shipping_address;
    }

    public ArrayList<IndividualOrderItemObj> getOrderitem() {
        return orderitem;
    }

    public void setOrderitem(ArrayList<IndividualOrderItemObj> orderitem) {
        this.orderitem = orderitem;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public class ShippingAddressObj{
        String phone;
        int id;
        String line1;
        String name;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getLine1() {
            return line1;
        }

        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {

            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
