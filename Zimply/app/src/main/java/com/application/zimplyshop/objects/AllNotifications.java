package com.application.zimplyshop.objects;

import com.application.zimplyshop.baseobjects.NotificationsBaseObj;
import com.google.gson.Gson;

/**
 * Created by Umesh Lohani on 11/20/2015.
 */
public class AllNotifications {

    static AllNotifications  sInstance;


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
