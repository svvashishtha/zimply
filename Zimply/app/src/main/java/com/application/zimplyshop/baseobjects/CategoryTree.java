package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryTree  implements Serializable{

    private ArrayList<CategoryObject> subCategories;
    private CategoryObject category;

    public CategoryTree () {
        subCategories = new ArrayList<CategoryObject>();
        category = new CategoryObject();
    }

    public ArrayList<CategoryObject> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<CategoryObject> subCategories) {
        this.subCategories = subCategories;
    }

    public CategoryObject getCategory() {
        return category;
    }

    public void setCategory(CategoryObject category) {
        this.category = category;
    }
}
