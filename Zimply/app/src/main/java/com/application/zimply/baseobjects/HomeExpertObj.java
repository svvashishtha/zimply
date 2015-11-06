package com.application.zimply.baseobjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeExpertObj implements Parcelable,Serializable {

	private String logo;

	private String title;

	private String cover;

	private ArrayList<String> category;

	private double rating;

	private String desc;

	private String slug;

	private int id;

	public HomeExpertObj(){

	}

	public HomeExpertObj(Parcel in) {

		this.logo = in.readString();
		this.title = in.readString();
		this.cover = in.readString();
		this.desc = in.readString();
		this.slug = in.readString();
		this.rating = in.readDouble();
		this.category = new ArrayList<String>();
		this.id = in.readInt();
		in.readList(category, String.class.getClassLoader());
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public ArrayList<String> getCategory() {
		return category;
	}

	public void setCategory(ArrayList<String> category) {
		this.category = category;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(logo);
		dest.writeString(title);
		dest.writeString(cover);
		dest.writeString(desc);
		dest.writeString(slug);
		dest.writeDouble(rating);
		dest.writeList(category);
		dest.writeInt(id);

	}

	public static final Creator<HomeExpertObj> CREATOR = new Creator<HomeExpertObj>() {

		public HomeExpertObj createFromParcel(Parcel in) {
			return new HomeExpertObj(in);
		}

		public HomeExpertObj[] newArray(int size) {
			return new HomeExpertObj[size];
		}
	};

}
