package com.application.zimply.objects;

import com.application.zimply.baseobjects.BaseCartProdutQtyObj;
import com.application.zimply.baseobjects.CartObject;
import com.application.zimply.baseobjects.CategoryObject;
import com.application.zimply.baseobjects.CategoryTree;
import com.application.zimply.baseobjects.HomeProductObj;
import com.application.zimply.baseobjects.MyWishListObject;
import com.application.zimply.baseobjects.OrderList;
import com.application.zimply.baseobjects.ProductListObject;
import com.application.zimply.utils.JSONUtils;
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
    private ArrayList<CategoryObject> product_category;
    private ArrayList<CategoryTree> category_tree;

    public static AllProducts getInstance() {
        if (sInstance == null) {
            sInstance = new AllProducts();
        }
        return sInstance;
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
        JSONArray jsonArray = JSONUtils.getJSONArray(JSONUtils.getJSONObject(responseString),"products");
        cartObjs = new ArrayList<>();
        if(jsonArray!=null){
            for(int i=0;i<jsonArray.length();i++){
                cartObjs.add(new Gson().fromJson(JSONUtils.getJSONObject(jsonArray,i).toString(),BaseCartProdutQtyObj.class));
            }
            cartCount = jsonArray.length();
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
}
