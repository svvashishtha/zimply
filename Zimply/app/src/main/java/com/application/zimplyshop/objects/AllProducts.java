package com.application.zimplyshop.objects;

import com.application.zimplyshop.baseobjects.BaseCartProdutQtyObj;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.baseobjects.CategoryTree;
import com.application.zimplyshop.baseobjects.HomeProductCategoryNBookingObj;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.baseobjects.MyWishListObject;
import com.application.zimplyshop.baseobjects.OrderList;
import com.application.zimplyshop.baseobjects.ProductAttribute;
import com.application.zimplyshop.baseobjects.ProductListObject;
import com.application.zimplyshop.baseobjects.VendorAddressObj;
import com.application.zimplyshop.baseobjects.VendorLocationObj;
import com.application.zimplyshop.baseobjects.VendorObj;
import com.application.zimplyshop.utils.JSONUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        if(responseString != null) {
            JSONObject responseJson = null;
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
            }
        }
        return null;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public Object parseData(String responseString) {
        ProductListObject listObject = new ProductListObject();
        if(responseString != null) {
            JSONObject responseJson = null;
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
                    listObject.setProducts(homeProductObjArrayList);

                    if( responseJson.has("next_url") ) {
                        listObject.setNext_url(String.valueOf(responseJson.get("next_url")));
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return listObject;
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

    public boolean cartContains(int productId){
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

    public Object parseWishlistData(String responseString){
        return new Gson().fromJson(responseString , MyWishListObject.class);
    }

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


    public boolean vendorIdsContains(int vendorId){
        for(Integer vendor : vendorIds){
            if(vendor == vendorId){
                return  true;
            }
        }
        return false;
    }
}

