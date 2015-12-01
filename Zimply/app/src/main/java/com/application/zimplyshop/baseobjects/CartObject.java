package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Saurabh on 12-10-2015.
 */
public class CartObject implements Serializable {
    cartObj cart;

    public cartObj getCart() {
        return cart;
    }

    public class cartObj implements  Serializable{

        String price, total_price, total_shipping;
        List<ProdctDetail> detail;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getTotal_price() {
            return total_price;
        }

        public void setTotal_price(String total_price) {
            this.total_price = total_price;
        }

        public String getTotal_shipping() {
            return total_shipping;
        }

        public void setTotal_shipping(String total_shipping) {
            this.total_shipping = total_shipping;
        }

        public List<ProdctDetail> getDetail() {
            return detail;
        }

        public void setDetail(List<ProdctDetail> detail) {
            this.detail = detail;
        }
    }

    public class ProdctDetail implements Serializable{
        String product_id, price, cart_item_id, quantity, image, name, available_quantity, slug;
        float shipping_charge, total_price, shipping_charges;
        boolean is_o2o;

        public boolean is_o2o() {
            return is_o2o;
        }

        public void setIs_o2o(boolean is_o2o) {
            this.is_o2o = is_o2o;
        }

        public float getIndividualTotal_price() {
            return total_price;
        }

        public void setIndividualTotal_price(float total_price) {
            this.total_price = total_price;
        }

        public float getShipping_charges() {
            return shipping_charges;
        }

        public void setShipping_charges(float shipping_charges) {
            this.shipping_charges = shipping_charges;
        }

        public float getIndividualShipping_charge() {
            return shipping_charge;
        }

        public String getSlug() {
            return slug;
        }

        public int getAvailable_quantity() {
            return Integer.parseInt(available_quantity);
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCart_item_id() {
            return cart_item_id;
        }

        public void setCart_item_id(String cart_item_id) {
            this.cart_item_id = cart_item_id;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
