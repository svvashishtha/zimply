package com.application.zimplyshop.db;

import android.content.Context;

import com.application.zimplyshop.baseobjects.BaseProductListObject;
import com.application.zimplyshop.baseobjects.CacheProductListObject;

import java.util.ArrayList;

public class RecentProductsDBWrapper {

    public static RecentProductsDBManager helper;

    public static void Initialize(Context context) {
        helper = new RecentProductsDBManager(context);
    }

    public static int addProduct(BaseProductListObject location, int userId, long timestamp) {
        return helper.addProduct(location, userId, timestamp);
    }

    public static int removeProducts(int userId) {
        return helper.removeUsers(userId);
    }

    public static ArrayList<BaseProductListObject> getProducts(int userId) {
        return helper.getProducts(userId);
    }

    public static CacheProductListObject getProducts(int userId,long timeStamp,int limit) {
        return helper.getProducts(userId,timeStamp,limit);
    }
}