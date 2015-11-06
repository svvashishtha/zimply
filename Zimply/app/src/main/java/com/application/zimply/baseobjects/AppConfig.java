package com.application.zimply.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by apoorvarora on 20/10/15.
 */
public class AppConfig implements Serializable{

    private BaseArticlePhotoCategoryObj photoCateogryObjs = new BaseArticlePhotoCategoryObj();
    private ArrayList<CategoryTree> categoryTree = new ArrayList<CategoryTree>();
    private ArrayList<CategoryObject> cities = new ArrayList<CategoryObject>();
    private boolean updateRequired;
    private String updateText;
    private boolean forceUpdate;
    private String message;

    public BaseArticlePhotoCategoryObj getPhotoCateogryObjs() {
        return photoCateogryObjs;
    }

    public void setPhotoCateogryObjs(BaseArticlePhotoCategoryObj photoCateogryObjs) {
        this.photoCateogryObjs = photoCateogryObjs;
    }

    public ArrayList<CategoryTree> getCategoryTree() {
        return categoryTree;
    }

    public void setCategoryTree(ArrayList<CategoryTree> categoryTree) {
        this.categoryTree = categoryTree;
    }

    public ArrayList<CategoryObject> getCities() {
        return cities;
    }

    public void setCities(ArrayList<CategoryObject> cities) {
        this.cities = cities;
    }

    public boolean isUpdateRequired() {
        return updateRequired;
    }

    public void setUpdateRequired(boolean updateRequired) {
        this.updateRequired = updateRequired;
    }

    public String getUpdateText() {
        return updateText;
    }

    public void setUpdateText(String updateText) {
        this.updateText = updateText;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
