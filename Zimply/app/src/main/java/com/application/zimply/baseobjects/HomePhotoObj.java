package com.application.zimply.baseobjects;

import java.io.Serializable;

public class HomePhotoObj implements Serializable {

	private String image;

	private String slug;
	private String image2;

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    private int width;

	private int height;

	private String favourite_item_id;

	private boolean is_favourite;

	private String cat;

	private String style;

	private HomeExpertObj expert;

	public String getFavourite_item_id() {
		return favourite_item_id;
	}

	public void setFavourite_item_id(String favourite_item_id) {
		this.favourite_item_id = favourite_item_id;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getFavorite_item_id() {
		return favourite_item_id;
	}

	public void setFavorite_item_id(String favorite_item_id) {
		this.favourite_item_id = favorite_item_id;
	}

	public boolean getIs_favourite() {
		return is_favourite;
	}

	public void setIs_favourite(boolean is_favourite) {
		this.is_favourite = is_favourite;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}


    public HomeExpertObj getExpert() {
        return expert;
    }

    public void setExpert(HomeExpertObj expert) {
        this.expert = expert;
    }
}
