package com.application.zimplyshop.extras;

import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.baseobjects.AppConfig;
import com.application.zimplyshop.baseobjects.BannerObject;
import com.application.zimplyshop.baseobjects.BookedProductHistoryObject;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.CategoryTree;
import com.application.zimplyshop.baseobjects.FavListItemObject;
import com.application.zimplyshop.baseobjects.FavListObject;
import com.application.zimplyshop.baseobjects.HomeArticleObj;
import com.application.zimplyshop.baseobjects.HomeExpertObj;
import com.application.zimplyshop.baseobjects.HomePhotoObj;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.baseobjects.ImageObject;
import com.application.zimplyshop.baseobjects.LatestBookingObject;
import com.application.zimplyshop.baseobjects.ParentCategory;
import com.application.zimplyshop.baseobjects.ProductAttribute;
import com.application.zimplyshop.baseobjects.ProductVendorTimeObj;
import com.application.zimplyshop.objects.AllArticles;
import com.application.zimplyshop.objects.AllCategories;
import com.application.zimplyshop.objects.AllCities;
import com.application.zimplyshop.objects.AllExperts;
import com.application.zimplyshop.objects.AllHomeObjects;
import com.application.zimplyshop.objects.AllNotifications;
import com.application.zimplyshop.objects.AllPhotos;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.objects.AllUsers;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.JSONUtils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParserClass implements ObjectTypes {

    public static Object parseData(String responseString, int objectType) {
        switch (objectType) {
            case OBJECT_TYPE_HOME_OBJECT:
                return AllHomeObjects.getInstance().parseData(responseString);

            case OBJECT_TYPE_ARTICLE_LIST_OBJECT:
                return AllArticles.getInstance().parseData(responseString);
            case OBJECT_TYPE_EXPERT_LIST_OBJECT:
                return AllExperts.getInstance().parseData(responseString);

            case OBJECT_TYPE_PHOTO_LIST_OBJECT:
                return AllPhotos.getInstance().parseData(responseString);
            case OBJECT_TYPE_HOME_PHOTO:
                return AllPhotos.getInstance().parseDataObject(responseString);
            case OBJECT_TYPE_PRODUCT_LIST_OBJECT:
                return AllProducts.getInstance().parseData(responseString);
            case OBJECT_TYPE_PRODUCT_SEARCH_RESULT:
                return AllProducts.getInstance().parseSearchResultData(responseString);
            case OBJECT_TYPE_SHOP_CATEGORY_OBJECT:
                return AllCategories.getInstance().parseData(responseString);
            case OBJECT_TYPE_SHOP_SUB_CATEGORY_OBJECT:
                return AllCategories.getInstance().parseSubCategoryData(responseString);
            case OBJECT_TYPE_COMPLETE_CATEGORY_LIST:
                return AllCategories.getInstance().parseCompleteCategoryData(responseString);
            case OBJECT_TYPE_ARTICLE_PHOTO_CATEGORY_LIST:
                return AllCategories.getInstance().parseArticlePhotoCategoryData(responseString);
            case OBJECT_TYPE_ARTICLE_DETAIL:
                return AllArticles.getInstance().parseArticleData(responseString);

            case OBJECT_TYPE_SIGNUP:
                return AllUsers.getInstance().parseUserSignup(responseString);

            case OBJECT_TYPE_CITY_LIST: {
                JSONObject citiesJson = null;
                ArrayList<CategoryObject> cities = new ArrayList<CategoryObject>();
                try {
                    citiesJson = new JSONObject(responseString);
                    if (AllCategories.getInstance().getCities() == null) {
                        if (citiesJson.has("city_list") && citiesJson.get("city_list") instanceof JSONArray) {
                            JSONArray citiesArr = citiesJson.getJSONArray("city_list");
                            for (int i = 0; i < citiesArr.length(); i++) {
                                JSONObject cityObject = citiesArr.getJSONObject(i);
                                CategoryObject city = new CategoryObject();
                                if (cityObject.has("id"))
                                    city.setId(String.valueOf(cityObject.get("id")));
                                if (cityObject.has("name"))
                                    city.setName(String.valueOf(cityObject.get("name")));
                                cities.add(city);
                            }
                            AllCategories.getInstance().setCities(cities);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return cities;
            }
            case OBJECT_TYPE_FORGOT_PASSWORD:
                return JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(responseString), "success");
            case OBJECT_TYPE_RESPONSE_FILTER:
                return JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(responseString), "query_id") + "";

            case OBJECT_TYPE_QUERY_RESPONSE_FILTER:
                return JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(responseString), "success");

            case OBJECT_TYPE_MARKED_FAV:
                return JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(responseString), "favorite_item_id");
            case OBJECT_TYPE_MARKED_UNFAV:

                return JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(responseString), "success");

            case OBJECT_TYPE_FAV_LIST:
                FavListObject obj = new FavListObject();
                obj.setNext_url(JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(responseString), "next_url"));
                JSONArray jsonArray = JSONUtils.getJSONArray(JSONUtils.getJSONObject(responseString), "favourite");
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        FavListItemObject itemObj = new FavListItemObject();
                        int type = JSONUtils.getIntegerfromJSON(JSONUtils.getJSONObject(jsonArray, i), "type");
                        itemObj.setType(type);
                        if (type == AppConstants.ITEM_TYPE_PHOTO) {

                            itemObj.setObj(new Gson().fromJson(JSONUtils.getJSONObject(jsonArray, i).toString(),
                                    HomePhotoObj.class));
                        } else if (type == AppConstants.ITEM_TYPE_ARTICLE) {
                            itemObj.setObj(new Gson().fromJson(JSONUtils.getJSONObject(jsonArray, i).toString(),
                                    HomeArticleObj.class));
                        }
                        obj.getObjects().add(itemObj);
                    }
                }
                return obj;
            case OBJECT_TYPE_REGIONLIST:
                return AllCities.getInsance().parseCityList(responseString);
            case OBJECT_TYPE_CATEGORY_OBJECT:
                return AllCities.getInsance().getSelectedCity(responseString);
            case OBJECT_TYPE_PRODUCT_CATEGORY: {
                ArrayList<CategoryObject> productCategories = new ArrayList<CategoryObject>();
                JSONObject productCategoriesJson = null;
                try {
                    productCategoriesJson = new JSONObject(responseString);
                    if (productCategoriesJson != null && productCategoriesJson.has("category") && productCategoriesJson.get("category") instanceof JSONArray) {
                        JSONArray productsArr = productCategoriesJson.getJSONArray("category");

                        for (int i = 0; i < productsArr.length(); i++) {
                            CategoryObject productCategory = new CategoryObject();
                            JSONObject productCatJson = productsArr.getJSONObject(i);
                            productCategory.setImg(new Gson().fromJson(productCatJson.getJSONObject("img").toString(), ImageObject.class));
                            if (productCatJson.has("image")) {
                                productCategory.setImage(String.valueOf(productCatJson.get("image")));
                            }
                            if (productCatJson.has("name")) {
                                productCategory.setName(String.valueOf(productCatJson.get("name")));
                            }

                            if (productCatJson.has("id")) {
                                productCategory.setId(String.valueOf(productCatJson.get("id")));
                            }
                            productCategories.add(productCategory);
                        }
                        AllProducts.getInstance().getHomeProCatNBookingObj().setProduct_category(productCategories);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
                return productCategories;
            }
            case OBJECT_TYPE_SEARCHED_PRODUCTS: {
                ParentCategory parentCategory = new ParentCategory();
                JSONObject parentCategoryJson = null;
                try {
                    parentCategoryJson = new JSONObject(responseString);
                    if (parentCategoryJson != null) {
                        if (parentCategoryJson.has("subcategories") && parentCategoryJson.get("subcategories") instanceof JSONArray) {
                            JSONArray subcategoriesArr = parentCategoryJson.getJSONArray("subcategories");
                            ArrayList<CategoryObject> subcategories = new ArrayList<CategoryObject>();
                            for (int i = 0; i < subcategoriesArr.length(); i++) {
                                CategoryObject subCategory = new CategoryObject();
                                JSONObject subcategoryJson = subcategoriesArr.getJSONObject(i);

                                if (subcategoryJson.has("id")) {
                                    subCategory.setId(String.valueOf(subcategoryJson.get("id")));
                                }

                                if (subcategoryJson.has("name")) {
                                    subCategory.setName(String.valueOf(subcategoryJson.get("name")));
                                }

                                if (subcategoryJson.has("banner")) {
                                    subCategory.setImage(String.valueOf(subcategoryJson.get("banner")));
                                }

                                subCategory.setType(CommonLib.SUB_CATEGORY);
                                subcategories.add(subCategory);
                            }
                            parentCategory.setSubCategories(subcategories);
                        }
                        if (parentCategoryJson.has("categories") && parentCategoryJson.get("categories") instanceof JSONArray) {
                            JSONArray categoriesArr = parentCategoryJson.getJSONArray("categories");
                            ArrayList<CategoryObject> categories = new ArrayList<CategoryObject>();
                            for (int i = 0; i < categoriesArr.length(); i++) {
                                CategoryObject category = new CategoryObject();
                                JSONObject subcategoryJson = categoriesArr.getJSONObject(i);

                                if (subcategoryJson.has("id")) {
                                    category.setId(String.valueOf(subcategoryJson.get("id")));
                                }

                                if (subcategoryJson.has("name")) {
                                    category.setName(String.valueOf(subcategoryJson.get("name")));
                                }

                                if (subcategoryJson.has("banner")) {
                                    category.setImage(String.valueOf(subcategoryJson.get("banner")));
                                }
                                category.setType(CommonLib.CATEGORY);

                                categories.add(category);
                            }
                            parentCategory.setCategories(categories);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
                return parentCategory;
            }
            case OBJECT_TYPE_PRODUCT_DETAIL: {
                HomeProductObj product = new HomeProductObj();
                JSONObject productObjectJson = null;
                try {
                    productObjectJson = new JSONObject(responseString);
                    if (productObjectJson.has("product") && productObjectJson.get("product") instanceof JSONObject) {
                        productObjectJson = productObjectJson.getJSONObject("product");
                        if (productObjectJson.has("sku"))
                            product.setSku(String.valueOf(productObjectJson.get("sku")));
                        if (productObjectJson.has("min_shipping_days") && productObjectJson.get("min_shipping_days") instanceof Integer)
                            product.setMinShippingDays(productObjectJson.getInt("min_shipping_days"));
                        if (productObjectJson.has("slug")) {
                            product.setSlug(String.valueOf(productObjectJson.get("slug")));
                        }
                        if (productObjectJson.has("shipping_charges"))
                            product.setShippingCharges(productObjectJson.getInt("shipping_charges"));
                        if (productObjectJson.has("name")) {
                            product.setName(String.valueOf(productObjectJson.get("name")));
                        }

                        if (productObjectJson.has("is_o2o") && productObjectJson.get("is_o2o") instanceof Boolean)
                            product.setIs_o2o(productObjectJson.getBoolean("is_o2o"));
                        if (productObjectJson.has("is_cod") && productObjectJson.get("is_cod") instanceof Boolean)
                            product.setIsCod(productObjectJson.getBoolean("is_cod"));
                        if (productObjectJson.has("price") && productObjectJson.get("price") instanceof Double)
                            product.setPrice(productObjectJson.getDouble("price"));
                        if (productObjectJson.has("tax") && productObjectJson.get("tax") instanceof Double)
                            product.setTax(productObjectJson.getDouble("tax"));
                        if (productObjectJson.has("qty") && productObjectJson.get("qty") instanceof Integer)
                            product.setQuantity(productObjectJson.getInt("qty"));
                        if (productObjectJson.has("max_shipping_days") && productObjectJson.get("max_shipping_days") instanceof Integer)
                            product.setMaxShippingDays(productObjectJson.getInt("max_shipping_days"));
                        if (productObjectJson.has("score") && productObjectJson.get("score") instanceof Integer)
                            product.setScore(productObjectJson.getInt("score"));
                        if (productObjectJson.has("vpc"))
                            product.setVpc(String.valueOf(productObjectJson.get("vpc")));
                        if (productObjectJson.has("active") && productObjectJson.get("active") instanceof Boolean)
                            product.setIsActive(productObjectJson.getBoolean("active"));
                        if (productObjectJson.has("vendor"))
                            product.setVendor(String.valueOf(productObjectJson.get("vendor")));
                        if (productObjectJson.has("category"))
                            product.setCategory(String.valueOf(productObjectJson.get("category")));
                        if (productObjectJson.has("id") && productObjectJson.get("id") instanceof Integer)
                            product.setId(productObjectJson.getInt("id"));
                        if (productObjectJson.has("description"))
                            product.setDescription(String.valueOf(productObjectJson.get("description")));
                        if (productObjectJson.has("return_policy"))
                            product.setReturnPolicy(String.valueOf(productObjectJson.get("return_policy")));

                        if (productObjectJson.has("is_favourite"))
                            product.setIs_favourite(productObjectJson.getBoolean("is_favourite"));

                        if (productObjectJson.has("favourite_item_id"))
                            product.setFavourite_item_id(String.valueOf(productObjectJson.get("favourite_item_id")));

                        if (productObjectJson.has("attribute") && productObjectJson.get("attribute") instanceof JSONArray) {

                            ArrayList<ProductAttribute> attributes = new ArrayList<ProductAttribute>();
                            JSONArray attributeArr = productObjectJson.getJSONArray("attribute");
                            for (int i = 0; i < attributeArr.length(); i++) {
                                JSONObject attributeJson = attributeArr.getJSONObject(i);
                                ProductAttribute attrs = new ProductAttribute();
                                attrs.setKey(JSONUtils.getStringfromJSON(attributeJson, "key"));
                                attrs.setUnit(JSONUtils.getStringfromJSON(attributeJson, "unit"));
                                attrs.setValue(JSONUtils.getStringfromJSON(attributeJson, "value"));
                                attributes.add(attrs);
                            }
                            product.setAttributes(attributes);
                        }

                        if (productObjectJson.has("images") && productObjectJson.get("images") instanceof JSONArray) {
                            ArrayList<String> images = new ArrayList<String>();
                            JSONArray imagesArr = productObjectJson.getJSONArray("images");
                            for (int i = 0; i < imagesArr.length(); i++) {
                                String image = String.valueOf(imagesArr.get(i));
                                images.add(image);
                            }
                            product.setImageUrls(images);
                        }

                        if (productObjectJson.has("thumbs") && productObjectJson.get("thumbs") instanceof JSONArray) {
                            ArrayList<String> images = new ArrayList<String>();
                            JSONArray imagesArr = productObjectJson.getJSONArray("thumbs");
                            for (int i = 0; i < imagesArr.length(); i++) {
                                String image = String.valueOf(imagesArr.get(i));
                                images.add(image);
                            }
                            product.setThumbs(images);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
                return product;
            }
            case OBJECT_TYPE_ALL_BOOKED_PRODUCTS:
                JSONArray array = JSONUtils.getJSONArray(JSONUtils.getJSONObject(responseString), "books");
                ArrayList<BookedProductHistoryObject> bookedObj = new ArrayList<>();
                if (array != null) {

                    for (int i = 0; i < array.length(); i++) {
                        BookedProductHistoryObject newObj = new BookedProductHistoryObject();
                        newObj.setName(JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(array, i), "name"));
                        newObj.setPrice(JSONUtils.getIntegerfromJSON(JSONUtils.getJSONObject(array, i), "price"));
                        newObj.setProductImg(JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(array, i), "image"));
                        newObj.setStatus(JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(array, i), "status"));
                        newObj.setId(JSONUtils.getIntegerfromJSON(JSONUtils.getJSONObject(array, i), "id"));
                        newObj.setVendorTimeObj(new Gson().fromJson(JSONUtils.getJSONObject(array, i).toString(), ProductVendorTimeObj.class));
                        bookedObj.add(newObj);
                    }
                    return bookedObj;
                }
                return null;

            case OBJECT_TYPE_ORDER_LIST:
                return AllProducts.getInstance().parseOrders(responseString);
            case OBJECT_TYPE_ITEM_REMOVED:
                return JSONUtils.getJSONObject(responseString);
            case OBJECT_TYPE_GET_ADDRESSES: {
                ArrayList<AddressObject> addresses = new ArrayList<AddressObject>();
                JSONObject addressObjectJson = null;
                try {
                    addressObjectJson = new JSONObject(responseString);
                    if (addressObjectJson.has("Addresses") && addressObjectJson.get("Addresses") instanceof JSONArray) {
                        JSONArray addressArr = addressObjectJson.getJSONArray("Addresses");
                        for (int i = 0; i < addressArr.length(); i++) {
                            AddressObject address = new AddressObject();
                            addressObjectJson = addressArr.getJSONObject(i);
                            if (addressObjectJson.has("city"))
                                address.setCity(String.valueOf(addressObjectJson.get("city")));
                            if (addressObjectJson.has("name"))
                                address.setName(String.valueOf(addressObjectJson.get("name")));
                            if (addressObjectJson.has("line2"))
                                address.setLine2(String.valueOf(addressObjectJson.get("line2")));
                            if (addressObjectJson.has("line1"))
                                address.setLine1(String.valueOf(addressObjectJson.get("line1")));
                            if (addressObjectJson.has("pincode"))
                                address.setPincode(String.valueOf(addressObjectJson.get("pincode")));
                            if (addressObjectJson.has("phone"))
                                address.setPhone(String.valueOf(addressObjectJson.get("phone")));
                            if (addressObjectJson.has("state"))
                                address.setState(String.valueOf(addressObjectJson.get("state")));
                            if (addressObjectJson.has("location") && addressObjectJson.get("location") instanceof Integer)
                                address.setLocation(addressObjectJson.getInt("location"));
                            if (addressObjectJson.has("id") && addressObjectJson.get("id") instanceof Integer)
                                address.setId(addressObjectJson.getInt("id"));
                            addresses.add(address);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
                return addresses;
            }
            case OBJECT_ADD_TO_CART:
                JSONObject jsonObject = JSONUtils.getJSONObject(responseString);
                return jsonObject;
            case OBJECT_TYPE_CART:
                return AllProducts.getInstance().parseCart(responseString);
            case OBJECT_TYPE_CHECK_PINCODE:
                return JSONUtils.getBoolfromJSON(JSONUtils.getJSONObject(responseString), "status");
            case OBJECT_USER_DETAILS:

                // AllProducts.getInstance().setCartCount(JSONUtils.getIntegerfromJSON(JSONUtils.getJSONObject(responseString),"cart_count"));
                AllProducts.getInstance().parseCartData(responseString);
                return true;
            case OBJECT_TYPE_SAVE_LIST: {
                AddressObject addresses = new AddressObject();
                JSONObject addressObjectJson = null;
                try {
                    addressObjectJson = new JSONObject(responseString);

                    if (addressObjectJson.has("Address") && addressObjectJson.get("Address") instanceof JSONObject) {
                        addressObjectJson = addressObjectJson.getJSONObject("Address");

                        if (addressObjectJson.has("city")) {
                            addresses.setCity(String.valueOf(addressObjectJson.get("city")));
                        }

                        if (addressObjectJson.has("name")) {
                            addresses.setName(String.valueOf(addressObjectJson.get("name")));
                        }

                        if (addressObjectJson.has("line2")) {
                            addresses.setLine2(String.valueOf(addressObjectJson.get("line2")));
                        }

                        if (addressObjectJson.has("line1")) {
                            addresses.setLine1(String.valueOf(addressObjectJson.get("line1")));
                        }

                        if (addressObjectJson.has("id") && addressObjectJson.get("id") instanceof Integer) {
                            addresses.setId(addressObjectJson.getInt("id"));
                        }

                        if (addressObjectJson.has("phone")) {
                            addresses.setPhone(String.valueOf(addressObjectJson.get("phone")));
                        }

                        if (addressObjectJson.has("state")) {
                            addresses.setState(String.valueOf(addressObjectJson.get("state")));
                        }

                        if (addressObjectJson.has("pincode")) {
                            addresses.setPincode(String.valueOf(addressObjectJson.get("pincode")));
                        }

                        if (addressObjectJson.has("email")) {
                            addresses.setEmail(String.valueOf(addressObjectJson.get("email")));
                        }
                        return addresses;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            case OBJECT_TYPE_SEARCHED_EXPERTS: {
                Object[] objects = new Object[2];
                JSONObject searchExpertsJson = null;
                try {
                    searchExpertsJson = new JSONObject(responseString);
                    ArrayList<HomeExpertObj> experts = new ArrayList<HomeExpertObj>();
                    ArrayList<CategoryObject> categories = new ArrayList<CategoryObject>();
                    //parse experts
                    if (searchExpertsJson.has("experts") && searchExpertsJson.get("experts") instanceof JSONArray) {

                        JSONArray expertsArr = searchExpertsJson.getJSONArray("experts");
                        for (int i = 0; i < expertsArr.length(); i++) {

                            HomeExpertObj expert = new HomeExpertObj();

                            JSONObject expertJson = expertsArr.getJSONObject(i);

                            if (expertJson.has("category") && expertJson.get("category") instanceof JSONArray) {
                                ArrayList<String> expCategories = new ArrayList<String>();
                                JSONArray categoriesArr = expertJson.getJSONArray("category");
                                for (int j = 0; j < categoriesArr.length(); j++) {
                                    String category = String.valueOf(categoriesArr.get(j));
                                    expCategories.add(category);
                                }
                                expert.setCategory(expCategories);
                            }

                            if (expertJson.has("rating") && expertJson.get("rating") instanceof Double) {
                                expert.setRating(expertJson.getDouble("rating"));
                            }

                            if (expertJson.has("title")) {
                                expert.setTitle(String.valueOf(expertJson.get("title")));
                            }

                            if (expertJson.has("cover")) {
                                expert.setCover(String.valueOf(expertJson.get("cover")));
                            }

                            if (expertJson.has("id") && expertJson.get("id") instanceof Integer) {
                                expert.setId(expertJson.getInt("id"));
                            }

                            if (expertJson.has("logo")) {
                                expert.setLogo(String.valueOf(expertJson.get("logo")));
                            }

                            if (expertJson.has("slug")) {
                                expert.setSlug(String.valueOf(expertJson.get("slug")));
                            }

                            if (expertJson.has("desc")) {
                                expert.setDesc(String.valueOf(expertJson.get("desc")));
                            }
                            experts.add(expert);
                        }
                    }
                    //parse categories
                    if (searchExpertsJson.has("categories") && searchExpertsJson.get("categories") instanceof JSONArray) {
                        JSONArray categoriesArr = searchExpertsJson.getJSONArray("categories");
                        for (int i = 0; i < categoriesArr.length(); i++) {
                            CategoryObject category = new CategoryObject();
                            JSONObject categoryJson = categoriesArr.getJSONObject(i);
                            if (categoryJson.has("name")) {
                                category.setName(String.valueOf(categoryJson.get("name")));
                            }
                            if (categoryJson.has("id")) {
                                category.setId(String.valueOf(categoryJson.get("id")));
                            }
                            categories.add(category);
                        }
                    }
                    objects[0] = categories;
                    objects[1] = experts;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return objects;
            }
            case OBJECT_TYPE_APPCONFIG: {
                Object[] objects = new Object[3];
                JSONObject appConfigJson = null;
                try {
                    appConfigJson = new JSONObject(responseString);

                    if (appConfigJson.has("update") && appConfigJson.get("update") instanceof JSONObject) {
                        appConfigJson = appConfigJson.getJSONObject("update");

                        if (appConfigJson.has("version") && appConfigJson.get("version") instanceof Double) {
                            objects[0] = appConfigJson.getDouble("version") > CommonLib.VERSION;
                        }
                        if (appConfigJson.has("text")) {
                            objects[1] = String.valueOf(appConfigJson.get("text"));
                        }
                        if (appConfigJson.has("force") && appConfigJson.get("force") instanceof Boolean) {
                            objects[2] = appConfigJson.getBoolean("force");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return objects;
            }
            case OBJECT_TYPE_FILTER_PRODUCTS: {
                ArrayList<CategoryTree> parentCategory = new ArrayList<CategoryTree>();
                JSONObject parentCategoryJson = null;
                try {
                    parentCategoryJson = new JSONObject(responseString);
                    if (parentCategoryJson != null) {
                        if (parentCategoryJson.has("tree") && parentCategoryJson.get("tree") instanceof JSONArray) {
                            JSONArray treeArr = parentCategoryJson.getJSONArray("tree");

                            for (int x = 0; x < treeArr.length(); x++) {
                                CategoryTree treeObj = new CategoryTree();
                                JSONObject treeObjJson = treeArr.getJSONObject(x);

                                if (treeObjJson.has("subcategory")) {
                                    JSONArray subcategoriesArr = treeObjJson.getJSONArray("subcategory");

                                    ArrayList<CategoryObject> subcategories = new ArrayList<CategoryObject>();
                                    for (int i = 0; i < subcategoriesArr.length(); i++) {
                                        CategoryObject subCategory = new CategoryObject();
                                        JSONObject subcategoryJson = subcategoriesArr.getJSONObject(i);

                                        if (subcategoryJson.has("id")) {
                                            subCategory.setId(String.valueOf(subcategoryJson.get("id")));
                                        }

                                        if (subcategoryJson.has("name")) {
                                            subCategory.setName(String.valueOf(subcategoryJson.get("name")));
                                        }

                                        subCategory.setType(CommonLib.SUB_CATEGORY);
                                        subcategories.add(subCategory);
                                    }
                                    treeObj.setSubCategories(subcategories);
                                }
                                CategoryObject object = new CategoryObject();
                                if (treeObjJson.has("id")) {
                                    object.setId(String.valueOf(treeObjJson.get("id")));
                                }
                                if (treeObjJson.has("name")) {
                                    object.setName(String.valueOf(treeObjJson.get("name")));
                                }
                                object.setType(CommonLib.CATEGORY);
                                treeObj.setCategory(object);

                                parentCategory.add(treeObj);
                            }
                        }
                    }
                    AllProducts.getInstance().setCategory_tree(parentCategory);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
                return parentCategory;
            }
            case OBJECT_TYPE_PLACE_ORDER:
                return JSONUtils.getJSONObject(responseString);

            case OBJECT_TYPE_APPCONFIG_2: {
                JSONObject appConfigJson = null;
                AppConfig appConfig = new AppConfig();
                try {
                    appConfigJson = new JSONObject(responseString);
                    if (appConfigJson != null) {

                        //parse the contact number
                        String number = appConfigJson.getString("no");
                        AppApplication.getInstance().setContactNumber(number);
                        //Parse all categories
                        if (appConfigJson.has("category") && appConfigJson.get("category") instanceof JSONObject) {

                            JSONObject categoryJson = appConfigJson.getJSONObject("category");

                            //Parse the article category and store them in All Categories Object.
                            if (categoryJson.has("article_category") && categoryJson.get("article_category") instanceof JSONArray) {
                                JSONArray articleCategoryArr = categoryJson.getJSONArray("article_category");
                                ArrayList<CategoryObject> articleCategories = new ArrayList<CategoryObject>();
                                for (int i = 0; i < articleCategoryArr.length(); i++) {
                                    JSONObject articleCategoryJson = articleCategoryArr.getJSONObject(i);
                                    CategoryObject articleCategory = new CategoryObject();

                                    if (articleCategoryJson.has("id")) {
                                        articleCategory.setId(String.valueOf(articleCategoryJson.get("id")));
                                    }

                                    if (articleCategoryJson.has("name")) {
                                        articleCategory.setName(String.valueOf(articleCategoryJson.get("name")));
                                    }

                                    articleCategories.add(articleCategory);
                                }
                                AllCategories.getInstance().getPhotoCateogryObjs().setArticle_category(articleCategories);
                                appConfig.getPhotoCateogryObjs().setArticle_category(articleCategories);
                            }

                            //Parse the photo category and store them in All Categories Object.
                            if (categoryJson.has("photo_category") && categoryJson.get("photo_category") instanceof JSONArray) {
                                JSONArray photoCategoryArr = categoryJson.getJSONArray("photo_category");
                                ArrayList<CategoryObject> photoCategories = new ArrayList<CategoryObject>();
                                for (int i = 0; i < photoCategoryArr.length(); i++) {
                                    JSONObject photoCategoryJson = photoCategoryArr.getJSONObject(i);
                                    CategoryObject photoCategory = new CategoryObject();

                                    if (photoCategoryJson.has("id")) {
                                        photoCategory.setId(String.valueOf(photoCategoryJson.get("id")));
                                    }

                                    if (photoCategoryJson.has("name")) {
                                        photoCategory.setName(String.valueOf(photoCategoryJson.get("name")));
                                    }

                                    photoCategories.add(photoCategory);
                                }
                                AllCategories.getInstance().getPhotoCateogryObjs().setPhoto_category(photoCategories);
                                appConfig.getPhotoCateogryObjs().setPhoto_category(photoCategories);
                            }

                            //Parse the expert category and store them in All Categories Object.
                            if (categoryJson.has("expert_category") && categoryJson.get("expert_category") instanceof JSONArray) {
                                JSONArray expertCategoryArr = categoryJson.getJSONArray("expert_category");
                                ArrayList<CategoryObject> expertCategories = new ArrayList<CategoryObject>();
                                for (int i = 0; i < expertCategoryArr.length(); i++) {
                                    JSONObject expertCategoryJson = expertCategoryArr.getJSONObject(i);
                                    CategoryObject expertCategory = new CategoryObject();

                                    if (expertCategoryJson.has("id")) {
                                        expertCategory.setId(String.valueOf(expertCategoryJson.get("id")));
                                    }

                                    if (expertCategoryJson.has("name")) {
                                        expertCategory.setName(String.valueOf(expertCategoryJson.get("name")));
                                    }

                                    expertCategories.add(expertCategory);
                                }
                                AllCategories.getInstance().getPhotoCateogryObjs().setExpert_category(expertCategories);
                                appConfig.getPhotoCateogryObjs().setExpert_category(expertCategories);
                            }

                            //Parse the expert category and store them in All Categories Object.
                            if (categoryJson.has("expert_base_category") && categoryJson.get("expert_base_category") instanceof JSONArray) {
                                JSONArray expertBaseCategoryArr = categoryJson.getJSONArray("expert_base_category");
                                ArrayList<CategoryObject> expertBaseCategories = new ArrayList<CategoryObject>();
                                for (int i = 0; i < expertBaseCategoryArr.length(); i++) {
                                    JSONObject expertBaseCategoryJson = expertBaseCategoryArr.getJSONObject(i);
                                    CategoryObject expertBaseCategory = new CategoryObject();

                                    if (expertBaseCategoryJson.has("id")) {
                                        expertBaseCategory.setId(String.valueOf(expertBaseCategoryJson.get("id")));
                                    }

                                    if (expertBaseCategoryJson.has("name")) {
                                        expertBaseCategory.setName(String.valueOf(expertBaseCategoryJson.get("name")));
                                    }

                                    if (expertBaseCategoryJson.has("image")) {
                                        expertBaseCategory.setImage(String.valueOf(expertBaseCategoryJson.get("image")));
                                    }

                                    expertBaseCategories.add(expertBaseCategory);
                                }
                                AllCategories.getInstance().getPhotoCateogryObjs().setExpert_base_category(expertBaseCategories);
                                appConfig.getPhotoCateogryObjs().setExpert_base_category(expertBaseCategories);
                            }
                        }

                        //Parse the user details
//                        if(appConfigJson.has("user_detail") && appConfigJson.get("user_detail") instanceof JSONObject) {
//
//                            JSONObject userDetailJson = appConfigJson.getJSONObject("user_detail");
//
//                            if(userDetailJson.has("cart_count") && userDetailJson.get("cart_count") instanceof Integer) {
//                                AllProducts.getInstance().setCartCount(userDetailJson.getInt("cart_count"));
//                            }
//                        }

                        if (appConfigJson.has("pro_category_tree") && appConfigJson.get("pro_category_tree") instanceof JSONObject) {
                            JSONObject proCategoryTreeJson = appConfigJson.getJSONObject("pro_category_tree");

                            if (proCategoryTreeJson.has("tree") && proCategoryTreeJson.get("tree") instanceof JSONArray) {
                                JSONArray treeArr = proCategoryTreeJson.getJSONArray("tree");

                                ArrayList<CategoryTree> categoryTree = new ArrayList<CategoryTree>();
                                for (int i = 0; i < treeArr.length(); i++) {
                                    CategoryTree treeObj = new CategoryTree();
                                    JSONObject treeObjJson = treeArr.getJSONObject(i);

                                    //Parse the sub category in the tree
                                    if (treeObjJson.has("subcategory")) {
                                        JSONArray subcategoriesArr = treeObjJson.getJSONArray("subcategory");

                                        ArrayList<CategoryObject> subcategories = new ArrayList<CategoryObject>();
                                        for (int j = 0; j < subcategoriesArr.length(); j++) {
                                            CategoryObject subCategory = new CategoryObject();
                                            JSONObject subcategoryJson = subcategoriesArr.getJSONObject(j);

                                            if (subcategoryJson.has("id")) {
                                                subCategory.setId(String.valueOf(subcategoryJson.get("id")));
                                            }

                                            if (subcategoryJson.has("name")) {
                                                subCategory.setName(String.valueOf(subcategoryJson.get("name")));
                                            }

                                            subCategory.setType(CommonLib.SUB_CATEGORY);
                                            subcategories.add(subCategory);
                                        }
                                        treeObj.setSubCategories(subcategories);
                                    }

                                    //Parse the category in the tree
                                    CategoryObject object = new CategoryObject();
                                    if (treeObjJson.has("id")) {
                                        object.setId(String.valueOf(treeObjJson.get("id")));
                                    }
                                    if (treeObjJson.has("name")) {
                                        object.setName(String.valueOf(treeObjJson.get("name")));
                                    }
                                    object.setType(CommonLib.CATEGORY);
                                    treeObj.setCategory(object);

                                    categoryTree.add(treeObj);
                                }
                                AllProducts.getInstance().setCategory_tree(categoryTree);
                                appConfig.setCategoryTree(categoryTree);
                            }
                        }
                        //Parse all cities
                        if (appConfigJson.has("city_list") && appConfigJson.get("city_list") instanceof JSONArray) {
                            JSONArray citiesArr = appConfigJson.getJSONArray("city_list");
                            ArrayList<CategoryObject> cities = new ArrayList<CategoryObject>();
                            for (int i = 0; i < citiesArr.length(); i++) {
                                JSONObject cityObject = citiesArr.getJSONObject(i);
                                if(cityObject.getBoolean("serve")) {
                                    CategoryObject city = new CategoryObject();
                                    if (cityObject.has("id"))
                                        city.setId(String.valueOf(cityObject.get("id")));
                                    if (cityObject.has("name"))
                                        city.setName(String.valueOf(cityObject.get("name")));
                                    if (cityObject.has("serve") && cityObject.get("serve") instanceof Boolean)
                                        city.setServe(cityObject.getBoolean("serve"));
                                    cities.add(city);
                                }
                            }
                            AllCategories.getInstance().setCities(cities);
                            AllCities.getInsance().setCities(cities);
                            appConfig.setCities(cities);
                        }

                        if (appConfigJson.has("update") && appConfigJson.get("update") instanceof JSONObject) {
                            JSONObject updateConfigJson = appConfigJson.getJSONObject("update");

                            Object[] updateObj = new Object[3];
                            if (updateConfigJson.has("version") && updateConfigJson.get("version") instanceof Double) {
                                updateObj[0] = updateConfigJson.getDouble("version") > CommonLib.VERSION;
                                appConfig.setUpdateRequired((Boolean) updateObj[0]);
                            }
                            if (updateConfigJson.has("text")) {
                                updateObj[1] = String.valueOf(updateConfigJson.get("text"));
                                appConfig.setUpdateText((String) updateObj[1]);
                            }
                            if (updateConfigJson.has("force") && updateConfigJson.get("force") instanceof Boolean) {
                                updateObj[2] = updateConfigJson.getBoolean("force");
                                appConfig.setForceUpdate((Boolean) updateObj[2]);
                            }
//                            objects[0] = updateObj;
                        }

                        if (appConfigJson.has("reg") && appConfigJson.get("reg") instanceof JSONObject) {

                            JSONObject regJson = appConfigJson.getJSONObject("reg");

                            if (regJson.has("message")) {
                                appConfig.setMessage(String.valueOf(regJson.get("message")));
//                                objects[1] = String.valueOf(regJson.get("message"));
                            }
                        }

                        return appConfig;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            case OBJECT_TYPE_CANCEL_ORDER:
                return responseString;
            case OBJECT_TYPE_PRODUCT_WISHLIST:
                return AllProducts.getInstance().parseWishlistData(responseString);
            case OBJECT_TYPE_BANNER_OBJECT:
                return new Gson().fromJson(responseString, BannerObject.class);
            case OBJECT_TYPE_MARK_PRODUCT_REVIEW:
                return new Gson().fromJson(JSONUtils.getJSONObject(JSONUtils.getJSONObject(responseString), "context").toString(), ProductVendorTimeObj.class);
            case OBJECT_TYPE_REMOVE_PRODUCT_REVIEW:
                return responseString;
            case OBJECT_TYPE_PHONE_VERIFICATION:
            {
                Object[] responseObj = new Object[2];
                try {
                    JSONObject phoneVerificationJson = new JSONObject(responseString);
                    if (phoneVerificationJson != null) {

                        //parse the contact number
                        if (phoneVerificationJson.has("mobile"))
                            responseObj[0] = phoneVerificationJson.get("mobile");

                        if (phoneVerificationJson.has("is_verified") && phoneVerificationJson.get("is_verified") instanceof Boolean)
                            responseObj[1] = phoneVerificationJson.get("is_verified");

                        return responseObj;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                responseObj[0] = "";
                responseObj[1] = false;
                return responseObj;
            }
            case OBJECT_TYPE_PHONE_VERIFICATION_INPUT: {
                String response = "";
                try {
                    JSONObject phoneVerificationJson = new JSONObject(responseString);
                    if (phoneVerificationJson != null) {

                        //parse the contact number
                        if (phoneVerificationJson.has("message"))
                            response = String.valueOf(phoneVerificationJson.get("message"));

                        return response;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return response;
            }
            case OBJECT_TYPE_PHONE_VERIFICATION_OTP:
            {
                String response = "";
                try {
                    JSONObject phoneVerificationJson = new JSONObject(responseString);
                    if (phoneVerificationJson != null) {

                        //parse the contact number
                        if (phoneVerificationJson.has("message"))
                            response = String.valueOf(phoneVerificationJson.get("message"));

                        return response;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return response;
            }
            case OBJECT_TYPE_NOTIFICATION_LIST_OBJ:
                return AllNotifications.getsInstance().parseNotifListData(responseString);
            case OBJECT_TYPE_NOTIFICATION_COUNT:
                AllNotifications.getsInstance().setNewNotificationCount(JSONUtils.getIntegerfromJSON(JSONUtils.getJSONObject(responseString),"notifications"));
                return true;
            case OBJECT_TYPE_LATEST_BOOKKING_OBJ:
                ArrayList<LatestBookingObject> objs = new ArrayList<>();
                JSONObject bookingObj = JSONUtils.getJSONObject(responseString);
                JSONArray jsonArray1 = JSONUtils.getJSONArray(bookingObj,"bookings");
                if(jsonArray1 != null && jsonArray1.length()>0){
                    for(int i=0;i<jsonArray1.length();i++){
                        objs.add(new Gson().fromJson(JSONUtils.getJSONObject(jsonArray1,i).toString(),LatestBookingObject.class));
                    }
                    AllProducts.getInstance().getHomeProCatNBookingObj().setLatest_bookings(objs);
                }
                return true;
            default:
                return null;
        }
    }
}
