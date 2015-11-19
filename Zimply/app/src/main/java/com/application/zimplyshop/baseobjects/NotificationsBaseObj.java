package com.application.zimplyshop.baseobjects;

import com.application.zimplyshop.objects.NotificationListObj;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/19/2015.
 */
public class NotificationsBaseObj {

    private ArrayList<NotificationListObj> notifications;

    private String next_url;

    public ArrayList<NotificationListObj> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<NotificationListObj> notifications) {
        this.notifications = notifications;
    }

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String next_url) {
        this.next_url = next_url;
    }


}

