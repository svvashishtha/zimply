package com.application.zimplyshop.objects;

import com.application.zimplyshop.baseobjects.BaseCartProdutQtyObj;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.CategoryTree;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.baseobjects.MyWishListObject;
import com.application.zimplyshop.baseobjects.OrderList;
import com.application.zimplyshop.baseobjects.ProductListObject;
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

    private ArrayList<CategoryObject> product_category;
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
        if(bookedJsonArray!=null){
            for(int i=0;i<bookedJsonArray.length();i++){
                bookedObjs.add(new Gson().fromJson(JSONUtils.getJSONObject(bookedJsonArray,i).toString(),BaseCartProdutQtyObj.class));
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
                    for (int i = 0; i < jsonArray.length(); i++)
                        homeProductObjArrayList.add(new Gson().fromJson(jsonArray.get(i).toString(), HomeProductObj.class));
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
        return new Gson().fromJson(responseString, ProductListObject.class);
    }

    public Object parseOrders(String responseString) {
        return new Gson().fromJson(responseString, OrderList.class);
    }


    public ArrayList<CategoryObject> getProduct_category() {
        return product_category;
    }

    public void setProduct_category(ArrayList<CategoryObject> product_category) {
        this.product_category = product_category;
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
}

