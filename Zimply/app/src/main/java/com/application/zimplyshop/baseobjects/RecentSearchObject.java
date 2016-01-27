package com.application.zimplyshop.baseobjects;

import java.io.Serializable;

/**
 * Created by Umesh Lohani on 1/25/2016.
 */
public class RecentSearchObject implements Serializable{

    int type;

    CategoryObject categoryObj;

    BaseProductListObject productObj;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CategoryObject getCategoryObj() {
        return categoryObj;
    }

    public void setCategoryObj(CategoryObject categoryObj) {
        this.categoryObj = categoryObj;
    }

    public BaseProductListObject getProductObj() {
        return productObj;
    }

    public void setProductObj(BaseProductListObject productObj) {
        this.productObj = productObj;
    }
}
