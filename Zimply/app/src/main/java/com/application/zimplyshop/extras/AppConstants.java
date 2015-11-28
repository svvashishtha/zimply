package com.application.zimplyshop.extras;

public interface AppConstants {

    String FONT_PATH = "";
    String HOMEPAGE_URL = "content/home-page-new/";
    String EXPERT_LIST_URL = "explore/expert-list/";
    String ARTICLE_LIST_URL = "explore/article-list/";
    String CATEGORY_LIST_URL = "content/get-product-category/";
    String SUB_CATEGORY_LIST_URL = "content/get-product-subcategory/";
    String SUB_CATEGORY_PRODUCT_LIST_URL = "content/get-subcategory-product/";
    String PHOTOS_LISTING_URL = "explore/photo-list/";
    String DEALS_PRODUCT_LISTING_URL = "content/deals/";
    String PRO_CATEGORY_TREE = "pro/category-tree";

    String FEATURED_PRODUCT_LISTING_URL = "content/featured-products/";
    String COMPLETE_CATEGORY_LISTING_URL = "content/categories/";
    String ARTICLE_PHOTO_CATEGORY_REQUEST_URL = "explore/get-category/";
    String ARTICLE_DESCRIPTION_REQUETS_URL = "content/article/";
    String SIGNUP_REQUEST_URL = "zimply-auth/new-sign-up/";
    String GCM_REGISTRATIONS = "notification/register-device/";
    String ORDER_LIST_URL = "ecommerce/all-orders/";
    String CHECK_PINCODE = "ecommerce/pincode/";
    String GET_USER_DATA = "zimply-auth/user-detail/";
    String BOOKED_PRODUCTS_URL = "ecommerce/all-book/";

    String EXPERT_PHOTO_REQUETS_URL = "explore/expert-photo-list/";
    String FORGOT_PASSWORD = "zimply-auth/new-forget-password/";
    String PRODUCT_DESCRIPTION_REQUETS_URL = "ecommerce/product-detail/";

    String MARK_FAVOURITE_URL = "zimply-auth/add-item-in-favourite/";
    String MARK_UNFAVOURITE_URL = "zimply-auth/delete-item-from-favourite/";

    String MARK_PRODUCT_REVIEW_URL = "ecommerce/add-book-product/";
    String REMOVE_PRODUCT_REVIEW_URL = "ecommerce/remove-book/";

    String GET_FAV_LIST_URL = "zimply-auth/new-favourite/";

    String PLACE_ORDER_URL = "ecommerce/order/";

    String PLACE_ORDER_SUCCESS_URL = "ecommerce/order-success/";

    String SAVE_FILTER = "explore/save-query/";
    String GET_CITY_LIST = "explore/city-list/";
    String GET_REGION_LIST = "ecommerce/locality_list/";
    String GET_PRODUCT_CATEGORY_LIST = "ecommerce/product-category/";
    String GET_SEARCHED_PRODUCTS_LIST = "ecommerce/product-search/";
    String GET_SEARCHED_EXPERTS_LIST = "explore/expert-search/";
    String GET_PRODUCT_LIST = "ecommerce/product-list/";
    String ADD_TO_CART_URL = "ecommerce/add-cart/";
    String GET_CART_URL = "ecommerce/cart-detail/";
    String GET_SEARCH_RESULTS = "ecommerce/esearch/";
    String GET_CART_COMPUTATION = "ecommerce/cart-computation/";
    String USER_WISHLIST = "zimply-auth/wishlist/";
    String BANNER_URL = "explore/get-banner/";
    String NOTIFICATIONS_LIST = "notification/gcmnotification";
    String NOTIFICATION_COUNT = "notification/gcmnotification-count/";
    String LATEST_BOOKINGS = "ecommerce/latest-bookings/";



    String FILTER_CATEGORY_FRAGMENT_TAG = "filtercategoryfragmenttag";
    String FILTER_SUBCATEGORY_FRAGMENT_TAG = "filtersubcategoryfragmenttag";
    String FILTER_SUBSUBCATEGORY_FRAGMENT_TAG = "filtersubsubcategoryfragmenttag";
    String FILTER_FILTER_FRAGMENT_TAG = "filterfilterfragmenttag";
    String ARTICLE_PHOTO_CATEGROY_DIALOG_TAG = "articlefiltercategorydialogtag";
    String ARTICLE_SORT_CATEGROY_DIALOG_TAG = "articlesortfiltercategorydialogtag";

    int TYPE_CATEGORY = 1;
    int TYPE_SUB_CATEGORY = 2;
    int TYPE_SUB_SUB_CATEGORY = 3;

    int TYPE_PHOTO_FILTER_STYLE = 1;
    int TYPE_PHOTO_FILTER_BUDGET = 2;
    int TYPE_PHOTO_FILTER_SIZE = 3;
    int TYPE_PHOTO_FILTER_TYPE = 4;
    int TYPE_PHOTO_FILTER_RESET = 5;

    int REQUEST_PHOTO_FILTER_ACTIVITY = 101;

    String[] quotes = {"Simplicity is the ultimate sophistication",
            "We don't believe in moving to a new place and replicating the one you left behind. where's the fun in that?",
            "A room should never allow the eye to settle in one place. It should smile at you and create fantasy.",
            "Home is the nicest word there is.",
            "Serious is a word that must be entirely avoided when it comes to decoration",
            "Less is more, texture is essential as well as scale",
            "Have nothing in your house which you do not know to be useful or believe to be beautiful",
            "There's nothing like a bright accent color to enliven your room",
            "Trays instantly make an assortment of accessories look chic and styled",
            "Every room needs a pop of color even if its just a flower",
            "A home is never complete untill you decorate your walls",
            "Install a dimmer switch on your light fixtures for instant ambience.",
            "Hang drapery from floor to ceiling to make a room appear taller",
            "Cut up leftover wallpaper and use as drawer liners. Pretty and Usefull",
            "Sometimes a vase of fresh flowers is all a room needs",
            "Paint the inside of your closet a bright and cheerful color for a good morning surprise",
            "Never buy just to fill a space, wait and invest in something you love",
            "Install dimmers on every light fixture to create perfect mood lighting",
            "Your house becomes a home when you fall in love with it",
            "Mix solid and patterned toss cushions for a dynamic look in your sofa",
            "A classic tuxedo sofa is always in style", "Personalize a big box kitchen vintage cabinet knobs",
            "Buying what you love always comes before buying what you need",
            "Use warm white tones in north facing rooms and cool white tones in a south facing one",
            "Painting your walls yellow is like creating a room full of sunshine"};

    String[] categories = {"Home Decor", "Lamps & Lighting", "Housekeeping", "Appliances", ""};

    String[] styles = {"Modern", "Eclectic ( Mixture of styles )", "Traditional/Classic",
            "Minimalist"};

    String[] photoSize = {"Compact", "Medium", "Large", "Expensive"};


    int ITEM_TYPE_PHOTO = 1;
    int ITEM_TYPE_EXPERT = 4;
    int ITEM_TYPE_ARTICLE = 3;
    int ITEM_TYPE_PRODUCT = 2;
    int REQUEST_PHOTO_DETAILS_REQUEST_CODE = 0x01;
    int REQUEST_ARTICLE_DETAILS_REQUEST_CODE = 0x02;

    int NOTIFICATION_TYPE_SHOP_LISTING = 1;
    int NOTIFICATION_TYPE_WEBVIEW = 2;
    int NOTIFICATION_TYPE_PHOTO_LISTING = 3;
    int NOTIFICATION_TYPE_HOME_PAGE = 4;
    int NOTIFICATION_TYPE_ARTICLE_DETAILS = 5;
    int NOTIFICATION_TYPE_PHOTO_DETAILS = 6;
    int NOTIFICATION_TYPE_APP_UPDATE = 7;
    //Location receiver
    int SUCCESS_RESULT = 0;
    int FAILURE_RESULT = 1;
    String PACKAGE_NAME =
            "com.application.zimply";
    String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    String LOCATION_LAT_LONG = "getLatLong";
    String LOCATION_ADDRESS = "getAddress";

    String GET_CITY_LL = "explore/get-location/";

    String SAVE_ADDRESS = "ecommerce/save-address/";
    String APP_CONFIG = "notification/app-config/";
    String GET_ADDRESSES = "ecommerce/get-address/";
    String PHONE_VERIFICATION = "zimply-auth/mobile/";
    String PHONE_VERIFICATION_INPUT_DIALOG = "zimply-auth/mobile/";

    int CANCEL_ORDER = 2;
    int RETURN_ORDER = 6;



    int BANNER_TYPE_WEBVIEW = 1;
    int BANNER_TYPE_SCAN_OFFLINE = 2;

    int REQUEST_TYPE_FROM_SEARCH = 10000;
    int REQUEST_TYPE_FROM_PRODUCT = 10001;


    int BUYING_CHANNEL_OFFLINE = 0;
    int BUYING_CHANNEL_ONLINE = 1;


    int NOTIFICATION_TYPE_PRODUCT_LIST = 1;
    int NOTIFICATION_TYPE_PRODUCT_DETAIL = 3;

}

