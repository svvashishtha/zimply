package com.application.zimplyshop.baseobjects;

import org.json.JSONObject;

import com.application.zimplyshop.utils.JSONUtils;

public class NotificationObject {

	// price drop,offer,order update
	public static final String TYPE_OFFER = "offer";
	public static final String TYPE_PRICE_DROP = "price_drop";
	public static final String TYPE_ORDER_UPDATE_SHIPPED = "order_update_shipped";
	public static final String TYPE_ORDER_UPDATE_DELIVERED = "order_update_delivered";

	public static final String HEADING_OFFER = "Offer Alert!";
	public static final String HEADING_PRICE_DROP = "Price Drop Alert!";
	public static final String HEADING_ORDER_UPDATE_SHIPPED = "Order Shipped!";
	public static final String HEADING_ORDER_UPDATE_DELIVERED = "Order Delivered!";

	private String imageUrl;
	private String notification;
	private String notificationType;
	private String notificationHeading = " orderddcjsdal";
	private String timestamp;

	public NotificationObject(String imageUrl, String notification,
			String notificationType, String timestamp) {
		super();
		this.imageUrl = imageUrl;
		this.notification = notification;
		this.notificationType = notificationType;
		this.timestamp = timestamp;
	}

    public NotificationObject(JSONObject jsonObject)
    {
        imageUrl = JSONUtils.getStringfromJSON(jsonObject, "image");
        notificationType = JSONUtils.getStringfromJSON(jsonObject,"title");
        notification =  JSONUtils.getStringfromJSON(jsonObject,"content");
        timestamp =  JSONUtils.getStringfromJSON(jsonObject,"created");
    }

	public String getNotificationHeading() {
		return notificationHeading;
	}

	public void setNotificationHeading(String notificationHeading) {
		this.notificationHeading = notificationHeading;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
}

