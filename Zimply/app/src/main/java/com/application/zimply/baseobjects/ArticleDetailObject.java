package com.application.zimply.baseobjects;

public class ArticleDetailObject {

	private String title;

	private String subtitle;

	private String content_url;

	private String image_featured;

	private String publisher_profile_photo;

	private String publisher_name;

	private int image_width;

	private int image_height;

	private int is_favourite;
	
	public int getIsFavourite()
	{
		return is_favourite;
	}
	public int getImage_width() {
		return image_width;
	}

	public void setImage_width(int image_width) {
		this.image_width = image_width;
	}

	public int getImage_height() {
		return image_height;
	}

	public void setImage_height(int image_height) {
		this.image_height = image_height;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getContent_url() {
		return content_url;
	}

	public void setContent_url(String content_url) {
		this.content_url = content_url;
	}

	public String getImage_featured() {
		return image_featured;
	}

	public void setImage_featured(String image_featured) {
		this.image_featured = image_featured;
	}

	public String getPublisher_profile_photo() {
		return publisher_profile_photo;
	}

	public void setPublisher_profile_photo(String publisher_profile_photo) {
		this.publisher_profile_photo = publisher_profile_photo;
	}

	public String getPublisher_name() {
		return publisher_name;
	}

	public void setPublisher_name(String publisher_name) {
		this.publisher_name = publisher_name;
	}
	public boolean getIs_favourite() {
		return is_favourite == 1;
	}
}
