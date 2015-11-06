package com.application.zimply.serverapis;

public interface RequestTags {

    String HOME_PAGE_REQUEST_TAG = "homepagerequesttag";
    String EXPERT_LIST_REQUEST_TAG = "expertlistrequesttag";
    String ARTICLE_LIST_REQUEST_TAG = "articlelistrequesttag";
    String CATEGORY_LIST_REQUEST_TAG = "categorylistrequesttag";
    String SUB_CATEGORY_LIST_REQUEST_TAG = "subcategorylistrequesttag";
    String PRODUCT_LIST_REQUEST_TAG = "productlistrequesttag";
    String PHOTOS_LIST_REQUEST_TAG = "photoslistrequesttag";
    String COMPLETE_CATEGORY_LIST_REQUEST_TAG = "completecategorylistrequesttag";
    String FILTERS_FILTERS_REQUEST_TAG = "filterfiltersrequesttag";
    String ARTICLE_PHOTO_CAT_REQUEST_TAG = "articlephotocatrequesttag";
    String ARTICLE_DETAIL_REQUEST_TAG = "articledetailrequesttag";
    String GET_ADDRESS_REQUEST_TAG = "getAddressRequestTag";
    String EXPERT_DETAIL_REQUEST_TAG = "expertdetailrequesttag";
    String PRODUCT_DETAIL_REQUEST_TAG = "productdetailrequesttag";
    String PRODUCT_CATEGORY_REQUEST_TAG = "productCategories";
    String SEARCHED_PRODUCTS_REQUEST_TAG = "searchedProducts";
    String SEARCHED_EXPERTS_REQUEST_TAG = "searchedExperts";
    String GCM_REGISTRATION = "gcmRegistration";
    String REGIONLIST_REQUESTTAG = "regionlistrequesttag";
    String APP_CONFIG = "appConfig";
    String ORDERLISTREQUESTTAG = "orderlistrequesttag";
    String CHECKPINCODEREQUESTTAG = "checkpincoderequesttag";
    String PRO_REQUEST_TAGS = "pro_request_tags";

    String BANNER_REQUEST_TAG="bannerrequesttag";



    String FAV_LIST_REQUEST_TAG = "favlistrequesttag";
    String FORGOT_PASSWORD_REQUEST_TAG = "forgotPasswordRequestTag";
    String GET_USER_DATA = "userdatatag";
    String NON_LOGGED_IN_CART_CACHE="nonloggedincartcache";
    String USER_WISHLIST = "userwishlist";
    int SIGNUP_REQUEST_TAG_BASE = 0x01;
    int SIGNUP_REQUEST_TAG_SIGNUP = 0x02;
    int SIGNUP_REQUEST_TAG_LOGIN = 0x03;
    int MARK_FAVOURITE_REQUEST_TAG = 0x04;
    int MARK_UN_FAVOURITE_REQUEST_TAG = 0x05;
    int PLACE_ORDER_REQUEST_TAG = 0x06;

    int PLACE_ORDER_SUCCESS_REQUEST_TAG = 0x07;
    int ADD_TO_CART = 0x008;
    int QUANTITY_UPDATE = 0x009;
    int BUY_NOW = 0x0010;

    String GET_PHOTO_SINGLE = "getSinglePhotoObject";
    String GET_CITY_FROM_LL = "getCityformLL";
    String REMOVE_FROM_CART = "removeFromCart";
    String GET_CITY_LIST = "citiesList";
    String GET_CART_DETAILS = "getCartDetails";
    String GET_CART_COMPUTATION = "getcartcomputation";
    int SAVE_ADDRESS = 101;
}
