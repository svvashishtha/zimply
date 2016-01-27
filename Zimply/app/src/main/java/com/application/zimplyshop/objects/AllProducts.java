package com.application.zimplyshop.objects;

import com.application.zimplyshop.baseobjects.BaseCartProdutQtyObj;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.baseobjects.CategoryTree;
import com.application.zimplyshop.baseobjects.HomeProductCategoryNBookingObj;
import com.application.zimplyshop.baseobjects.OrderList;
import com.application.zimplyshop.baseobjects.ProductListObject;
import com.application.zimplyshop.utils.JSONUtils;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

public class AllProducts {

    static AllProducts sInstance;

    int cartCount;

    int offineCartCount , onlineCartCount;


    public int getOffineCartCount() {
        return offineCartCount;
    }

    public int getOnlineCartCount() {
        return onlineCartCount;
    }

    public void setOffineCartCount(int offineCartCount) {
        this.offineCartCount = offineCartCount;
    }

    public void setOnlineCartCount(int onlineCartCount) {
        this.onlineCartCount = onlineCartCount;
    }

    ArrayList<BaseCartProdutQtyObj> cartObjs;

    ArrayList<BaseCartProdutQtyObj> bookedObjs;

    ArrayList<Integer> vendorIds;

    HomeProductCategoryNBookingObj homeProCatNBookingObj;


    private ArrayList<CategoryTree> category_tree;

    public static AllProducts getInstance() {
        if (sInstance == null) {
            sInstance = new AllProducts();
        }
        return sInstance;
    }

    public ArrayList<BaseCartProdutQtyObj> getBookedObjs() {
        if(bookedObjs == null){
            bookedObjs = new ArrayList<>();
        }
        return bookedObjs;
    }

    public void setBookedObjs(ArrayList<BaseCartProdutQtyObj> bookedObjs) {

        this.bookedObjs = bookedObjs;
    }

    public ArrayList<BaseCartProdutQtyObj> getCartObjs() {
        if(cartObjs == null){
            cartObjs = new ArrayList<>();
        }
        return cartObjs;
    }

    public void setCartObjs(ArrayList<BaseCartProdutQtyObj> cartObjs) {
        this.cartObjs = cartObjs;
    }

    public void parseCartData(String responseString){
        JSONArray jsonArray = JSONUtils.getJSONArray(JSONUtils.getJSONObject(responseString), "products");
        cartObjs = new ArrayList<>();
        if(jsonArray!=null){
            for(int i=0;i<jsonArray.length();i++){
                cartObjs.add(new Gson().fromJson(JSONUtils.getJSONObject(jsonArray,i).toString(),BaseCartProdutQtyObj.class));
            }
            cartCount = jsonArray.length();
        }
        JSONArray bookedJsonArray = JSONUtils.getJSONArray(JSONUtils.getJSONObject(responseString), "booked_products");
        bookedObjs = new ArrayList<>();
        vendorIds = new ArrayList<>();
        if(bookedJsonArray!=null){
            for(int i=0;i<bookedJsonArray.length();i++){
                vendorIds.add(Integer.parseInt(JSONUtils.getStringObject(bookedJsonArray,i)));
            }

        }

    }

    public Object parseSearchResultData(String responseString) {


            /*JSONObject responseJson = null;
            try {
                responseJson = new JSONObject(responseString);
                if( responseJson.has("products") && responseJson.get("products") instanceof JSONArray ) {
                    JSONArray jsonArray = responseJson.getJSONArray("products");
                    ArrayList<HomeProductObj> homeProductObjArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //parse the product
                        JSONObject bookingJson = jsonArray.getJSONObject(i);
                        HomeProductObj product = new HomeProductObj();
                        if (bookingJson.has("sku"))
                            product.setSku(String.valueOf(bookingJson.get("sku")));
                        if (bookingJson.has("min_shipping_days") && bookingJson.get("min_shipping_days") instanceof Integer)
                            product.setMinShippingDays(bookingJson.getInt("min_shipping_days"));
                        if (bookingJson.has("slug")) {
                            product.setSlug(String.valueOf(bookingJson.get("slug")));
                        }
                        if (bookingJson.has("shipping_charges"))
                            product.setShippingCharges(bookingJson.getInt("shipping_charges"));
                        if (bookingJson.has("name")) {
                            product.setName(String.valueOf(bookingJson.get("name")));
                        }

                        if (bookingJson.has("is_o2o") && bookingJson.get("is_o2o") instanceof Boolean)
                            product.setIs_o2o(bookingJson.getBoolean("is_o2o"));
                        if (bookingJson.has("is_cod") && bookingJson.get("is_cod") instanceof Boolean)
                            product.setIsCod(bookingJson.getBoolean("is_cod"));
                        if (bookingJson.has("price") && bookingJson.get("price") instanceof Double)
                            product.setPrice(bookingJson.getDouble("price"));
                        if (bookingJson.has("tax") && bookingJson.get("tax") instanceof Double)
                            product.setTax(bookingJson.getDouble("tax"));
                        if (bookingJson.has("qty") && bookingJson.get("qty") instanceof Integer)
                            product.setQuantity(bookingJson.getInt("qty"));
                        if (bookingJson.has("max_shipping_days") && bookingJson.get("max_shipping_days") instanceof Integer)
                            product.setMaxShippingDays(bookingJson.getInt("max_shipping_days"));
                        if (bookingJson.has("score") && bookingJson.get("score") instanceof Integer)
                            product.setScore(bookingJson.getInt("score"));
                        if (bookingJson.has("vpc"))
                            product.setVpc(String.valueOf(bookingJson.get("vpc")));
                        if (bookingJson.has("active") && bookingJson.get("active") instanceof Boolean)
                            product.setIsActive(bookingJson.getBoolean("active"));
                        if (bookingJson.has("category"))
                            product.setCategory(String.valueOf(bookingJson.get("category")));
                        if (bookingJson.has("id") && bookingJson.get("id") instanceof Integer)
                            product.setId(bookingJson.getInt("id"));
                        if (bookingJson.has("description"))
                            product.setDescription(String.valueOf(bookingJson.get("description")));
                        if (bookingJson.has("return_policy"))
                            product.setReturnPolicy(String.valueOf(bookingJson.get("return_policy")));

                        if (bookingJson.has("is_favourite"))
                            product.setIs_favourite(bookingJson.getBoolean("is_favourite"));

                        if (bookingJson.has("favourite_item_id"))
                            product.setFavourite_item_id(String.valueOf(bookingJson.get("favourite_item_id")));

                        if (bookingJson.has("image"))
                            product.setImage(String.valueOf(bookingJson.get("image")));

                        if (bookingJson.has("attribute") && bookingJson.get("attribute") instanceof JSONArray) {

                            ArrayList<ProductAttribute> attributes = new ArrayList<ProductAttribute>();
                            JSONArray attributeArr = bookingJson.getJSONArray("attribute");
                            for (int k = 0; k < attributeArr.length(); k++) {
                                JSONObject attributeJson = attributeArr.getJSONObject(k);
                                ProductAttribute attrs = new ProductAttribute();
                                attrs.setKey(JSONUtils.getStringfromJSON(attributeJson, "key"));
                                attrs.setUnit(JSONUtils.getStringfromJSON(attributeJson, "unit"));
                                attrs.setValue(JSONUtils.getStringfromJSON(attributeJson, "value"));
                                attributes.add(attrs);
                            }
                            product.setAttributes(attributes);
                        }

                        if (bookingJson.has("images") && bookingJson.get("images") instanceof JSONArray) {
                            ArrayList<String> images = new ArrayList<String>();
                            JSONArray imagesArr = bookingJson.getJSONArray("images");
                            for (int j = 0; j < imagesArr.length(); j++) {
                                String image = String.valueOf(imagesArr.get(j));
                                images.add(image);
                            }
                            product.setImageUrls(images);
                        }

                        if (bookingJson.has("thumbs") && bookingJson.get("thumbs") instanceof JSONArray) {
                            ArrayList<String> images = new ArrayList<String>();
                            JSONArray imagesArr = bookingJson.getJSONArray("thumbs");
                            for (int k = 0; k < imagesArr.length(); k++) {
                                String image = String.valueOf(imagesArr.get(k));
                                images.add(image);
                            }
                            product.setThumbs(images);
                        }
                        //parse the vendor
                        if(bookingJson.has("vendor") && bookingJson.get("vendor") instanceof  JSONObject) {

                            JSONObject vendorJson = bookingJson.getJSONObject("vendor");

                            VendorObj vendor = new VendorObj();

                            if (vendorJson.has("company_name"))
                                vendor.setCompany_name(String.valueOf(vendorJson.get("company_name")));

                            if (vendorJson.has("book_product_id") && vendorJson.get("book_product_id") instanceof Integer)
                                vendor.setBook_product_id(vendorJson.getInt("book_product_id"));

                            if (vendorJson.has("vendor_id") && vendorJson.get("vendor_id") instanceof Integer)
                                vendor.setVendor_id(vendorJson.getInt("vendor_id"));

                            if (vendorJson.has("reg_add") && vendorJson.get("reg_add") instanceof JSONObject) {
                                VendorAddressObj addressObj = new VendorAddressObj();
                                JSONObject addressJson = vendorJson.getJSONObject("reg_add");

                                if (addressJson.has("id") && addressJson.get("id") instanceof Integer)
                                    addressObj.setId(addressJson.getInt("id"));

                                if (addressJson.has("line1"))
                                    addressObj.setLine1(String.valueOf(addressJson.get("line1")));

                                if (addressJson.has("phone"))
                                    addressObj.setPhone(String.valueOf(addressJson.get("phone")));

                                if (addressJson.has("location") && addressJson.get("location") instanceof JSONObject) {
                                    JSONObject locationJson = addressJson.getJSONObject("location");

                                    VendorLocationObj locationObj = new VendorLocationObj();
                                    if (locationJson.has("latitude") && locationJson.get("latitude") instanceof Double) {
                                        locationObj.setLatitude(locationJson.getDouble("latitude"));
                                    } else if (locationJson.has("latitude") && locationJson.get("latitude") instanceof Integer) {
                                        locationObj.setLatitude(locationJson.getInt("latitude") + 0.0);
                                    }

                                    if (locationJson.has("longitude") && locationJson.get("longitude") instanceof Double) {
                                        locationObj.setLongitude(locationJson.getDouble("longitude"));
                                    } else if (locationJson.has("longitude") && locationJson.get("longitude") instanceof Integer) {
                                        locationObj.setLongitude(locationJson.getInt("longitude") + 0.0);
                                    }

                                    if (locationJson.has("serve") && locationJson.get("serve") instanceof Boolean) {
                                        locationObj.setServe(locationJson.getBoolean("serve"));
                                    }

                                    if (locationJson.has("id") && locationJson.get("id") instanceof Integer) {
                                        locationObj.setId(locationJson.getInt("id"));
                                    }

                                    if (locationJson.has("name")) {
                                        locationObj.setName(String.valueOf(locationJson.get("name")));
                                    }

                                    addressObj.setLocation(locationObj);
                                }
                                vendor.setReg_add(addressObj);
                            }
                            product.setVendor(vendor);
                        }
                        homeProductObjArrayList.add(product);
                    }
                    return homeProductObjArrayList;
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }*/
       // }
        return new Gson().fromJson(responseString,ProductListObject.class);
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public Object parseData(String responseString) {
       /* ProductListObject listObject = new ProductListObject();
        if(responseString != null) {
            JSONObject responseJson = null;
            try {
                responseJson = new JSONObject(responseString);
                if( responseJson.has("products") && responseJson.get("products") instanceof JSONArray ) {
                    JSONArray jsonArray = responseJson.getJSONArray("products");
                    ArrayList<BaseProductListObject> homeProductObjArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //parse the product
                        JSONObject bookingJson = jsonArray.getJSONObject(i);
                        BaseProductListObject product = new BaseProductListObject();
                        if (bookingJson.has("sku"))
                            product.setSku(String.valueOf(bookingJson.get("sku")));
                        if (bookingJson.has("min_shipping_days") && bookingJson.get("min_shipping_days") instanceof Integer)
                            product.setMinShippingDays(bookingJson.getInt("min_shipping_days"));
                        if (bookingJson.has("slug")) {
                            product.setSlug(String.valueOf(bookingJson.get("slug")));
                        }
                        if (bookingJson.has("shipping_charges"))
                            product.setShippingCharges(bookingJson.getInt("shipping_charges"));
                        if (bookingJson.has("name")) {
                            product.setName(String.valueOf(bookingJson.get("name")));
                        }

                        if (bookingJson.has("is_o2o") && bookingJson.get("is_o2o") instanceof Boolean)
                            product.setIs_o2o(bookingJson.getBoolean("is_o2o"));
                        if (bookingJson.has("is_cod") && bookingJson.get("is_cod") instanceof Boolean)
                            product.setIsCod(bookingJson.getBoolean("is_cod"));
                        if (bookingJson.has("price") && bookingJson.get("price") instanceof Double)
                            product.setPrice(bookingJson.getDouble("price"));
                        if (bookingJson.has("tax") && bookingJson.get("tax") instanceof Double)
                            product.setTax(bookingJson.getDouble("tax"));
                        if (bookingJson.has("qty") && bookingJson.get("qty") instanceof Integer)
                            product.setQuantity(bookingJson.getInt("qty"));
                        if (bookingJson.has("max_shipping_days") && bookingJson.get("max_shipping_days") instanceof Integer)
                            product.setMaxShippingDays(bookingJson.getInt("max_shipping_days"));
                        if (bookingJson.has("score") && bookingJson.get("score") instanceof Integer)
                            product.setScore(bookingJson.getInt("score"));
                        if (bookingJson.has("vpc"))
                            product.setVpc(String.valueOf(bookingJson.get("vpc")));
                        if (bookingJson.has("active") && bookingJson.get("active") instanceof Boolean)
                            product.setIsActive(bookingJson.getBoolean("active"));
                        if (bookingJson.has("category"))
                            product.setCategory(String.valueOf(bookingJson.get("category")));
                        if (bookingJson.has("id") && bookingJson.get("id") instanceof Integer)
                            product.setId(bookingJson.getInt("id"));
                        if (bookingJson.has("description"))
                            product.setDescription(String.valueOf(bookingJson.get("description")));
                        if (bookingJson.has("return_policy"))
                            product.setReturnPolicy(String.valueOf(bookingJson.get("return_policy")));

                        if (bookingJson.has("is_favourite"))
                            product.setIs_favourite(bookingJson.getBoolean("is_favourite"));

                        if (bookingJson.has("favourite_item_id"))
                            product.setFavourite_item_id(String.valueOf(bookingJson.get("favourite_item_id")));

                        if (bookingJson.has("image"))
                            product.setImage(String.valueOf(bookingJson.get("image")));

                        if (bookingJson.has("attribute") && bookingJson.get("attribute") instanceof JSONArray) {

                            ArrayList<ProductAttribute> attributes = new ArrayList<ProductAttribute>();
                            JSONArray attributeArr = bookingJson.getJSONArray("attribute");
                            for (int k = 0; k < attributeArr.length(); k++) {
                                JSONObject attributeJson = attributeArr.getJSONObject(k);
                                ProductAttribute attrs = new ProductAttribute();
                                attrs.setKey(JSONUtils.getStringfromJSON(attributeJson, "key"));
                                attrs.setUnit(JSONUtils.getStringfromJSON(attributeJson, "unit"));
                                attrs.setValue(JSONUtils.getStringfromJSON(attributeJson, "value"));
                                attributes.add(attrs);
                            }
                            product.setAttributes(attributes);
                        }

                        if (bookingJson.has("images") && bookingJson.get("images") instanceof JSONArray) {
                            ArrayList<String> images = new ArrayList<String>();
                            JSONArray imagesArr = bookingJson.getJSONArray("images");
                            for (int j = 0; j < imagesArr.length(); j++) {
                                String image = String.valueOf(imagesArr.get(j));
                                images.add(image);
                            }
                            product.setImageUrls(images);
                        }

                        if (bookingJson.has("thumbs") && bookingJson.get("thumbs") instanceof JSONArray) {
                            ArrayList<String> images = new ArrayList<String>();
                            JSONArray imagesArr = bookingJson.getJSONArray("thumbs");
                            for (int k = 0; k < imagesArr.length(); k++) {
                                String image = String.valueOf(imagesArr.get(k));
                                images.add(image);
                            }
                            product.setThumbs(images);
                        }
                        //parse the vendor
                        if(bookingJson.has("vendor") && bookingJson.get("vendor") instanceof  JSONObject) {

                            JSONObject vendorJson = bookingJson.getJSONObject("vendor");

                            VendorObj vendor = new VendorObj();

                            if (vendorJson.has("company_name"))
                                vendor.setCompany_name(String.valueOf(vendorJson.get("company_name")));

                            if (vendorJson.has("book_product_id") && vendorJson.get("book_product_id") instanceof Integer)
                                vendor.setBook_product_id(vendorJson.getInt("book_product_id"));

                            if (vendorJson.has("vendor_id") && vendorJson.get("vendor_id") instanceof Integer)
                                vendor.setVendor_id(vendorJson.getInt("vendor_id"));

                            if (vendorJson.has("reg_add") && vendorJson.get("reg_add") instanceof JSONObject) {
                                VendorAddressObj addressObj = new VendorAddressObj();
                                JSONObject addressJson = vendorJson.getJSONObject("reg_add");

                                if (addressJson.has("id") && addressJson.get("id") instanceof Integer)
                                    addressObj.setId(addressJson.getInt("id"));

                                if (addressJson.has("line1"))
                                    addressObj.setLine1(String.valueOf(addressJson.get("line1")));

                                if (addressJson.has("phone"))
                                    addressObj.setPhone(String.valueOf(addressJson.get("phone")));

                                if (addressJson.has("location") && addressJson.get("location") instanceof JSONObject) {
                                    JSONObject locationJson = addressJson.getJSONObject("location");

                                    VendorLocationObj locationObj = new VendorLocationObj();
                                    if (locationJson.has("latitude") && locationJson.get("latitude") instanceof Double) {
                                        locationObj.setLatitude(locationJson.getDouble("latitude"));
                                    } else if (locationJson.has("latitude") && locationJson.get("latitude") instanceof Integer) {
                                        locationObj.setLatitude(locationJson.getInt("latitude") + 0.0);
                                    }

                                    if (locationJson.has("longitude") && locationJson.get("longitude") instanceof Double) {
                                        locationObj.setLongitude(locationJson.getDouble("longitude"));
                                    } else if (locationJson.has("longitude") && locationJson.get("longitude") instanceof Integer) {
                                        locationObj.setLongitude(locationJson.getInt("longitude") + 0.0);
                                    }

                                    if (locationJson.has("serve") && locationJson.get("serve") instanceof Boolean) {
                                        locationObj.setServe(locationJson.getBoolean("serve"));
                                    }

                                    if (locationJson.has("id") && locationJson.get("id") instanceof Integer) {
                                        locationObj.setId(locationJson.getInt("id"));
                                    }

                                    if (locationJson.has("name")) {
                                        locationObj.setName(String.valueOf(locationJson.get("name")));
                                    }

                                    addressObj.setLocation(locationObj);
                                }
                                vendor.setReg_add(addressObj);
                            }
                            product.setVendor(vendor);
                        }
                        homeProductObjArrayList.add(product);
                    }
                    listObject.setProducts(homeProductObjArrayList);
                    ArrayList<ShopSubCategoryObj> objs = new ArrayList<>();
                    if(responseJson.has("subcategory")){
                        JSONArray subCatArray = JSONUtils.getJSONArray(responseJson,"subcategory");
                        if(subCatArray!=null){
                            for(int i=0;i<subCatArray.length();i++){
                                objs.add(new Gson().fromJson(JSONUtils.getJSONObject(subCatArray,i).toString(),ShopSubCategoryObj.class));
                            }
                            listObject.setSubcategory(objs);
                        }
                    }
                    if( responseJson.has("next_url") ) {
                        listObject.setNext_url(String.valueOf(responseJson.get("next_url")));
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return listObject;*/
        return new Gson().fromJson(responseString,ProductListObject.class)   ;
    }

    public Object parseOrders(String responseString) {
        return new Gson().fromJson(responseString, OrderList.class);
    }



    public Object parseCart(String responseString) {
        return new Gson().fromJson(responseString, CartObject.class);
    }

    public ArrayList<CategoryTree> getCategory_tree() {
        return category_tree;
    }

    public void setCategory_tree(ArrayList<CategoryTree> category_tree) {
        this.category_tree = category_tree;
    }

    public boolean cartContains(long productId){
        if(cartObjs!=null && cartObjs.size()>0){
            for(BaseCartProdutQtyObj obj: cartObjs){
                if(obj.getId() == productId){
                    return true;
                }
            }
        }
        return false;
    }

    public void removeCartItem(int productId){
        if(cartObjs!=null){
            for(int i=0;i<cartObjs.size();i++) {
                if (cartObjs.get(i).getId() == productId) {
                    cartObjs.remove(i);
                    return;
                }
            }
        }
    }

   /* public Object parseWishlistData(String responseString){*//*
        MyWishListObject obj = new MyWishListObject();
        JSONArray jsonArray = JSONUtils.getJSONArray(JSONUtils.getJSONObject(responseString),"favourite");
        if(jsonArray!=null){
            for(int k=0;k<jsonArray.length();k++){

                    BaseProductListObject product = new BaseProductListObject();
                    try {
                        JSONObject productObjectJson = JSONUtils.getJSONObject(jsonArray, k);
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
                        //parse the vendor
                        if (productObjectJson.has("vendor") && productObjectJson.get("vendor") instanceof JSONObject) {

                            JSONObject vendorJson = productObjectJson.getJSONObject("vendor");

                            VendorObj vendor = new VendorObj();

                            if (vendorJson.has("company_name"))
                                vendor.setCompany_name(String.valueOf(vendorJson.get("company_name")));

                            if (vendorJson.has("book_product_id") && vendorJson.get("book_product_id") instanceof Integer)
                                vendor.setBook_product_id(vendorJson.getInt("book_product_id"));

                            if (vendorJson.has("vendor_id") && vendorJson.get("vendor_id") instanceof Integer)
                                vendor.setVendor_id(vendorJson.getInt("vendor_id"));

                            if (vendorJson.has("map") && vendorJson.get("map") instanceof String)
                                vendor.setMap(vendorJson.getString("map"));
                            if (vendorJson.has("reg_add") && vendorJson.get("reg_add") instanceof JSONObject) {
                                VendorAddressObj addressObj = new VendorAddressObj();
                                JSONObject addressJson = vendorJson.getJSONObject("reg_add");

                                if (addressJson.has("id") && addressJson.get("id") instanceof Integer)
                                    addressObj.setId(addressJson.getInt("id"));

                                if (addressJson.has("line1"))
                                    addressObj.setLine1(String.valueOf(addressJson.get("line1")));

                                if (addressJson.has("phone"))
                                    addressObj.setPhone(String.valueOf(addressJson.get("phone")));
                                if (addressJson.has("city"))
                                    addressObj.setCity(String.valueOf(addressJson.get("city")));
                                if (addressJson.has("pincode"))
                                    addressObj.setPincode(String.valueOf(addressJson.get("pincode")));
                                if (addressJson.has("location") && addressJson.get("location") instanceof JSONObject) {
                                    JSONObject locationJson = addressJson.getJSONObject("location");

                                    VendorLocationObj locationObj = new VendorLocationObj();
                                    if (locationJson.has("latitude") && locationJson.get("latitude") instanceof Double) {
                                        locationObj.setLatitude(locationJson.getDouble("latitude"));
                                    } else if (locationJson.has("latitude") && locationJson.get("latitude") instanceof Integer) {
                                        locationObj.setLatitude(locationJson.getInt("latitude") + 0.0);
                                    }

                                    if (locationJson.has("longitude") && locationJson.get("longitude") instanceof Double) {
                                        locationObj.setLongitude(locationJson.getDouble("longitude"));
                                    } else if (locationJson.has("longitude") && locationJson.get("longitude") instanceof Integer) {
                                        locationObj.setLongitude(locationJson.getInt("longitude") + 0.0);
                                    }

                                    if (locationJson.has("serve") && locationJson.get("serve") instanceof Boolean) {
                                        locationObj.setServe(locationJson.getBoolean("serve"));
                                    }

                                    if (locationJson.has("id") && locationJson.get("id") instanceof Integer) {
                                        locationObj.setId(locationJson.getInt("id"));
                                    }

                                    if (locationJson.has("name")) {
                                        locationObj.setName(String.valueOf(locationJson.get("name")));
                                    }

                                    addressObj.setLocation(locationObj);
                                }
                                vendor.setReg_add(addressObj);
                            }
                            product.setVendor(vendor);
                        }
                        if (productObjectJson.has("category"))
                            product.setCategory(String.valueOf(productObjectJson.get("category")));
                        if (productObjectJson.has("id") && productObjectJson.get("id") instanceof Integer)
                            product.setId(productObjectJson.getInt("id"));
                        if (productObjectJson.has("description"))
                            product.setDescription(String.valueOf(productObjectJson.get("description")));
                        if (productObjectJson.has("return"))
                            product.setReturnPolicy(String.valueOf(productObjectJson.get("return")));
                        if (productObjectJson.has("faq"))
                            product.setFaq(String.valueOf(productObjectJson.get("faq")));
                        if (productObjectJson.has("care"))
                            product.setCare(String.valueOf(productObjectJson.get("care")));

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
                    }catch(Exception e){

                    }
                obj.getFavourite().add(product);
            }
        }
        obj.setNext_url(JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(responseString),"next_url"));
        return obj;*//*
    }*/

    public boolean bookedProductsContains(int productId){
        if(bookedObjs!=null && bookedObjs.size()>0){
            for(BaseCartProdutQtyObj obj: bookedObjs){
                if(obj.getId() == productId){
                    return true;
                }
            }
        }
        return false;
    }


    public HomeProductCategoryNBookingObj getHomeProCatNBookingObj() {
        if(homeProCatNBookingObj == null)
            homeProCatNBookingObj = new HomeProductCategoryNBookingObj();
        return homeProCatNBookingObj;
    }

    public void setHomeProCatNBookingObj(HomeProductCategoryNBookingObj homeProCatNBookingObj) {
        this.homeProCatNBookingObj = homeProCatNBookingObj;
    }

    public ArrayList<Integer> getVendorIds() {
        if(vendorIds == null)
            vendorIds = new ArrayList<>();
        return vendorIds;
    }

    public void setVendorIds(ArrayList<Integer> vendorIds) {
        this.vendorIds = vendorIds;
    }

    public boolean vendorIdsContains(int vendorId){
        if(vendorIds == null)
            return false;
        for(Integer vendor : vendorIds){
            if(vendor == vendorId){
                return  true;
            }
        }
        return false;
    }
}

