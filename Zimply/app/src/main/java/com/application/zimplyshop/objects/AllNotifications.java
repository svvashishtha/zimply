package com.application.zimplyshop.objects;

import com.application.zimplyshop.baseobjects.NotificationsBaseObj;
import com.google.gson.Gson;

/**
 * Created by Umesh Lohani on 11/20/2015.
 */
public class AllNotifications {

    static AllNotifications  sInstance;

    private int newNotificationCount;


    public void setNewNotificationCount(int newNotificationCount) {
        this.newNotificationCount = newNotificationCount;
    }

    public int getNewNotificationCount() {
        return newNotificationCount;
    }

    public static AllNotifications getsInstance(){
        if(sInstance ==null ){
            sInstance = new AllNotifications();

        }
        return  sInstance;
    }


    public Object parseNotifListData(String responseString){
        return new Gson().fromJson(responseString,NotificationsBaseObj.class);
    }
}
