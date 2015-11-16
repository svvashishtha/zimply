package com.application.zimply.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeProductObj implements Serializable{

	private String sku;
	private int minShippingDays;
	private String slug;
	private double shippingCharges;
	private String name;
	private boolean isCod;
    private ArrayList<ProductAttribute> attribute;
	private double price;
	private double tax;
	private int quantity;
	private ArrayList<String> imageUrls;
	private ArrayList<String> thumbs;
	private int maxShippingDays;
	private int score;
	private String vpc;
	private boolean isActive;
	private String vendor;
	private String category;
	private long id;
	private String description;
	private String returnPolicy;
    private String favourite_item_id;
    private boolean is_favourite;

	private double discounted_price;
	private String discountFactor;
	private String image;

	private boolean is_o2o;

	public boolean is_o2o() {
		return is_o2o;
	}

	public void setIs_o2o(boolean is_o2o) {
		this.is_o2o = is_o2o;
	}

	public ArrayList<String> getThumbs() {
		return thumbs;
	}

	public void setThumbs(ArrayList<String> thumbs) {
		this.thumbs = thumbs;
	}

	public String getFavourite_item_id() {
        return favourite_item_id;
    }

    public void setFavourite_item_id(String favourite_item_id) {
        this.favourite_item_id = favourite_item_id;
    }

    public boolean is_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(boolean is_favourite) {
        this.is_favourite = is_favourite;
    }

    public double getDiscounted_price() {
		return discounted_price;
	}

    public void setDiscounted_price(double discounted_price) {
        this.discounted_price = discounted_price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public int getMinShippingDays() {
		return minShippingDays;
	}

	public void setMinShippingDays(int minShippingDays) {
		this.minShippingDays = minShippingDays;
	}

	public double getShippingCharges() {
		return shippingCharges;
	}

	public void setShippingCharges(double shippingCharges) {
		this.shippingCharges = shippingCharges;
	}

	public boolean isCod() {
		return isCod;
	}

	public void setIsCod(boolean isCod) {
		this.isCod = isCod;
	}

    public ArrayList<ProductAttribute> getAttributes() {
		return attribute;
	}

    public void setAttributes(ArrayList<ProductAttribute> attribute) {
        this.attribute = attribute;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public ArrayList<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(ArrayList<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public int getMaxShippingDays() {
		return maxShippingDays;
	}

	public void setMaxShippingDays(int maxShippingDays) {
		this.maxShippingDays = maxShippingDays;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getVpc() {
		return vpc;
	}

	public void setVpc(String vpc) {
		this.vpc = vpc;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public String getDiscountFactor() {
        return discountFactor;
    }

    public void setDiscountFactor(String discountFactor) {
        this.discountFactor = discountFactor;
    }

	public String getReturnPolicy() {
		return returnPolicy;
	}

	public void setReturnPolicy(String returnPolicy) {
		this.returnPolicy = returnPolicy;
	}
}
