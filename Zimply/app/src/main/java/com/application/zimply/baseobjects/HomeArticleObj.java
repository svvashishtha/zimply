package com.application.zimply.baseobjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class HomeArticleObj implements Parcelable,Serializable {
	private String image;

	private String created_date;

	private String subtitle;

	private String title;

	private String slug;

	private String content_url;

	private String article_publisher_logo;

	private String article_publisher_name;

	private String favourite_item_id;

    private boolean is_favourite;

	private int width;

	private int height;

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

	public String getArticle_publisher_logo() {
		return article_publisher_logo;
	}

	public void setArticle_publisher_logo(String article_publisher_logo) {
		this.article_publisher_logo = article_publisher_logo;
	}

	public String getArticle_publisher_name() {
		return article_publisher_name;
	}

	public void setArticle_publisher_name(String article_publisher_name) {
		this.article_publisher_name = article_publisher_name;
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

	public static Creator<HomeArticleObj> getCreator() {
		return CREATOR;
	}

	public HomeArticleObj(Parcel in) {
		this.image = in.readString();
		this.subtitle = in.readString();
		this.title = in.readString();
		this.created_date = in.readString();
		this.slug = in.readString();
		this.content_url = in.readString();
		this.article_publisher_logo = in.readString();
		this.article_publisher_name = in.readString();
		this.favourite_item_id = in.readString();
		this.is_favourite = in.readInt() == 0 ? false : true;
		this.width = in.readInt();
		this.height = in.readInt();

	}

	public String getContent_url() {
		return content_url;
	}

	public void setContent_url(String content_url) {
		this.content_url = content_url;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCreated_date() {
		return created_date;
	}

	public String getPublisher_name() {
		return article_publisher_name;
	}

	public void setPublisher_name(String publisher_name) {
		this.article_publisher_name = publisher_name;
	}

	public String getPublisher_profile_photo() {
		return article_publisher_logo;
	}

	public void setPublisher_profile_photo(String publisher_profile_photo) {
		this.article_publisher_logo = publisher_profile_photo;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(image);
		dest.writeString(subtitle);
		dest.writeString(title);
		dest.writeString(created_date);
		dest.writeString(slug);
		dest.writeString(content_url);
		dest.writeString(article_publisher_logo);
		dest.writeString(article_publisher_name);
		dest.writeString(favourite_item_id);
		dest.writeInt(is_favourite ? 1 : 0);
		dest.writeInt(width);
		dest.writeInt(height);
	}

	public static final Creator<HomeArticleObj> CREATOR = new Creator<HomeArticleObj>() {

		public HomeArticleObj createFromParcel(Parcel in) {
			return new HomeArticleObj(in);
		}

		public HomeArticleObj[] newArray(int size) {
			return new HomeArticleObj[size];
		}
	};

}
