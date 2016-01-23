package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 1/23/2016.
 */
public class CacheProductListObject implements Serializable{
    public ArrayList<BaseProductListObject> objects;

    public long timeStamp;

    public ArrayList<BaseProductListObject> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<BaseProductListObject> objects) {
        this.objects = objects;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}

