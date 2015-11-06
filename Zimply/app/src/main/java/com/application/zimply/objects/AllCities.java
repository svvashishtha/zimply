package com.application.zimply.objects;

import com.application.zimply.baseobjects.CategoryObject;
import com.application.zimply.utils.JSONUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class AllCities implements Serializable{

    static AllCities sInstance;

    ArrayList<CategoryObject> cities;

    public static AllCities getInsance() {
        if (sInstance == null) {
            sInstance = new AllCities();
        }
        return sInstance;
    }

    public ArrayList<CategoryObject> getCities() {
        if(cities == null){
            cities = new ArrayList<>();
        }
        return cities;
    }

    public void setCities(ArrayList<CategoryObject> cities) {
        this.cities = cities;
    }

    public Object parseCityList(String response) {
       cities= new ArrayList<>();
        JSONArray array = JSONUtils.getJSONArray(response);
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                cities.add(new Gson().fromJson(JSONUtils.getJSONObject(array, i).toString(), CategoryObject.class));
            }
        }
        return cities;
    }

    public Object getSelectedCity(String response) {
        JSONObject jsonObject = JSONUtils.getJSONObject(response);
        if (jsonObject != null) {

            return jsonObject;
        } else return false;
    }
}
