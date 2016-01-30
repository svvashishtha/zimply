package com.application.zimplyshop.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.NewProductDetailActivity;
import com.application.zimplyshop.activities.NewSearchActivity;
import com.application.zimplyshop.activities.SearchResultsActivity;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BaseProductListObject;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.ParentCategory;
import com.application.zimplyshop.baseobjects.RecentSearchObject;
import com.application.zimplyshop.db.RecentProductsDBWrapper;
import com.application.zimplyshop.db.RecentSearchesDBWrapper;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.ZTracker;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.ArrayList;

/**
 * Created by apoorvarora on 04/11/15.
 */
public class ProductSearchFragment extends BaseFragment implements GetRequestListener, AppConstants {

    private boolean destroyed = false;
    private ArrayList<CategoryObject> products;
    private AsyncTask mAsyncRunning;
    private ListView mListView;
    private ProductListAdapter adapter1;

    View getView;
    Activity activity;
    SharedPreferences prefs;
    int width, height;
    LayoutInflater vi;

    double requestTime;
    private ListView recentSearchesListView;
    private RecentProductListAdapter recentSearchesAdapter;
    private NewProductListAdapter newProductListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_search_layout, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        getView = getView();

        prefs = activity.getSharedPreferences("application_settings", 0);
        width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        height = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        vi = LayoutInflater.from(activity.getApplicationContext());

        setUpHomeViews();
    }

    private void setUpHomeViews() {
        //Add callbacks
        GetRequestManager.getInstance().addCallbacks(this);

        //init
        mListView = (ListView) getView.findViewById(R.id.listview);
        recentSearchesListView = (ListView) getView.findViewById(R.id.recent_searches_listview);

        recentSearchesListView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        loadData(null, true);
    }

    public void performSearch(String input) {
        if (input == null)
            return;

        if (input.trim().length() >= 2) {
            if (mAsyncRunning != null)
                mAsyncRunning.cancel(true);
            loadData(input.trim(), false);
        } else {
            if (input.trim().length() == 0) {
                if (mAsyncRunning != null)
                    mAsyncRunning.cancel(true);

                recentSearchesListView.setVisibility(View.VISIBLE);
                getView.findViewById(R.id.recent_searches_container).setVisibility(View.VISIBLE);
                getView.findViewById(R.id.clear_searches).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recentSearchesAdapter != null)
                            recentSearchesAdapter.clearData();
                        try {
                            int userId = Integer.parseInt(AppPreferences.getUserID(activity));
                            RecentSearchesDBWrapper.removeProducts(userId);
                        } catch (Exception e) {
                            RecentSearchesDBWrapper.removeProducts(1);
                        }
                    }
                });
                getView.findViewById(R.id.listview_container).setVisibility(View.GONE);
                getView.findViewById(R.id.progress_container).setVisibility(View.GONE);
                getView.findViewById(R.id.progress_recent).setVisibility(View.GONE);
            }
        }
    }

    private void loadData(String param, boolean loadFromCache) {
        if (loadFromCache) {
            mAsyncRunning = new GetDataFromCache().execute();
        } else {
            //remove all spaces
            if (param == null)
                return;
            String[] params = param.split(" ");
            String builder = "";
            for (String pam : params) {
                builder += (pam + "+");
            }
            try {
                ZTracker.logGAEvent(getActivity(), "Search Terms", builder, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (builder.length() > 0) {
                requestTime = System.currentTimeMillis();
                builder = builder.substring(0, builder.length() - 1);
                String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_SEARCHED_PRODUCTS_LIST + "?q=" + builder;
                mAsyncRunning = GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.SEARCHED_PRODUCTS_REQUEST_TAG,
                        ObjectTypes.OBJECT_TYPE_SEARCHED_PRODUCTS);
            }
        }
    }


    @Override
    public void onDestroy() {
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    public void scrollHomeToTop() {
        try {
//            if (getView.findViewById(R.id.home_fragment_scroll_root) != null) {
//                ((ScrollView) getView.findViewById(R.id.home_fragment_scroll_root)).smoothScrollTo(0, 0);
//            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag.equals(RequestTags.SEARCHED_PRODUCTS_REQUEST_TAG)) {
            if (!destroyed) {
                getView.findViewById(R.id.progress_container).setVisibility(View.VISIBLE);
                getView.findViewById(R.id.progress_recent).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag.equals(RequestTags.SEARCHED_PRODUCTS_REQUEST_TAG)) {
            mAsyncRunning = null;
            if (!destroyed && obj != null && obj instanceof ParentCategory) {
                CommonLib.ZLog("Request Time", "Product search(Suggestion) Request :" + (System.currentTimeMillis() - requestTime) + " mS");
                CommonLib.writeRequestData("Product search(Suggestion) Request :" + (System.currentTimeMillis() - requestTime) + " mS");
                products = ((ParentCategory) obj).getSubCategories();
                products.addAll(((ParentCategory) obj).getCategories());

                ArrayList<CategoryObject> objects = new ArrayList<CategoryObject>();

//                Collections.copy(objects, products);

                for (CategoryObject dups : products) {
                    objects.add(new CategoryObject(dups));
                }

                for (CategoryObject dups : objects) {
                    dups.setDupType(CommonLib.DUP_TYPE_TRUE);
                }

                products.addAll(objects);
                newProductListAdapter = new NewProductListAdapter((ParentCategory) obj, activity);
                //adapter1 = new ProductListAdapter(activity, R.layout.simple_list_item, products);
                mListView.setAdapter(newProductListAdapter);
                getView.findViewById(R.id.progress_container).setVisibility(View.GONE);

                //if products size is 0, display recent
                if (products != null && products.size() > 0) {
                    getView.findViewById(R.id.listview_container).setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.VISIBLE);
                    recentSearchesListView.setVisibility(View.GONE);
                } else {//else display products
                    recentSearchesListView.setVisibility(View.VISIBLE);
                    getView.findViewById(R.id.listview_container).setVisibility(View.GONE);
                    getView.findViewById(R.id.recent_searches_container).setVisibility(View.VISIBLE);
                    getView.findViewById(R.id.clear_searches).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (recentSearchesAdapter != null)
                                recentSearchesAdapter.clearData();
                            try {
                                int userId = Integer.parseInt(AppPreferences.getUserID(activity));
                                RecentSearchesDBWrapper.removeProducts(userId);
                            } catch (Exception e) {
                                RecentSearchesDBWrapper.removeProducts(1);
                            }
                        }
                    });
                    getView.findViewById(R.id.progress_container).setVisibility(View.GONE);
                    getView.findViewById(R.id.progress_recent).setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag.equals(RequestTags.SEARCHED_PRODUCTS_REQUEST_TAG)) {
            mAsyncRunning = null;
            if (!destroyed) {
                getView.findViewById(R.id.progress_container).setVisibility(View.GONE);
            }
        }
    }

    private class ProductListAdapter extends ArrayAdapter<CategoryObject> {

        private ArrayList<CategoryObject> wishes;
        private Activity mContext;

        public ProductListAdapter(Activity context, int resourceId, ArrayList<CategoryObject> wishes) {
            super(context.getApplicationContext(), resourceId, wishes);
            mContext = context;
            this.wishes = wishes;
        }

        @Override
        public int getCount() {
            if (wishes == null) {
                return 0;
            } else {
                return wishes.size();
            }
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            final CategoryObject product = wishes.get(position);
            if (v == null || v.findViewById(R.id.list_root) == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, null);
            }

            ViewHolder viewHolder = (ViewHolder) v.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) v.findViewById(R.id.text1);
                viewHolder.editSearch = (ImageView)v.findViewById(R.id.edit_recent_search);
                viewHolder.editSearch.setVisibility(View.VISIBLE);
                v.setTag(viewHolder);
            }
            viewHolder.editSearch.setVisibility(View.GONE);
            final String name = ((TextView)((NewSearchActivity)activity).getActionBarView().findViewById(R.id.search_category)).getText().toString();
            if(product.getDupType() == CommonLib.DUP_TYPE_TRUE) {
                viewHolder.text.setTypeface(null, Typeface.NORMAL);
                viewHolder.text.setText("\"" + name + "\"" + " in " + product.getName());
            } else {
                viewHolder.text.setTypeface(null, Typeface.BOLD);
                viewHolder.text.setText(product.getName());
            }

            viewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SearchResultsActivity.class);
                    intent.putExtra("type", product.getType());
                    intent.putExtra("id", product.getId());
                    intent.putExtra("dup_type", product.getDupType());
                    if (product.getName() == null)
                        return;
                    intent.putExtra("category_name", product.getName());
                    String[] params1 = product.getName().split(" ");
                    String name1 = "";
                    for (String pam : params1) {
                        name1 += (pam + "+");
                    }
                    name1 = name1.substring(0, name1.length() - 1);
                    intent.putExtra("name", name1);

                    if (name == null)
                        return;
                    String[] params = name.split(" ");
                    String builder = "";
                    for (String pam : params) {
                        builder += (pam + "+");
                    }
                    builder = builder.substring(0, builder.length() - 1);

                    intent.putExtra("query", builder);
                    intent.putExtra("url", AppConstants.GET_SEARCH_RESULTS);
                    startActivity(intent);
                }
            });
            return v;
        }

        protected class ViewHolder {
            TextView text;
            ImageView editSearch;
        }

    }
    private class NewProductListAdapter extends BaseAdapter {
        ParentCategory parentCategory;
        Context mContext;
        ArrayList<CategoryObject> wishes;

        public NewProductListAdapter(ParentCategory parentCategory, Context mContext) {
            this.parentCategory = parentCategory;
            this.mContext = mContext;
            wishes = new ArrayList<>();
            wishes = parentCategory.getSubCategories();
            wishes.addAll(parentCategory.getCategories());

            ArrayList<CategoryObject> objects = new ArrayList<CategoryObject>();

//                Collections.copy(objects, products);

            for (CategoryObject dups : wishes) {
                objects.add(new CategoryObject(dups));
            }

            for (CategoryObject dups : objects) {
                dups.setDupType(CommonLib.DUP_TYPE_TRUE);
            }

            //  wishes.addAll(objects);
        }

        @Override
        public int getCount() {
            if (parentCategory == null)
                return 0;
            else
                return wishes.size() + (parentCategory.getProducts() != null ? parentCategory.getProducts().size() : 0);
        }

        @Override
        public Object getItem(int position) {
            if (position < wishes.size())
                return wishes.get(position);
            else
                return parentCategory.getProducts().get(position - wishes.size());
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null || convertView.findViewById(R.id.list_root) == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, null);
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) convertView.findViewById(R.id.text1);
                viewHolder.editSearch = (ImageView) convertView.findViewById(R.id.edit_recent_search);
                viewHolder.editSearch.setVisibility(View.VISIBLE);
                convertView.setTag(viewHolder);
            }

            if (position < wishes.size()) {
                final CategoryObject product = wishes.get(position);
                final String name = ((TextView) ((NewSearchActivity) activity).getActionBarView().findViewById(R.id.search_category)).getText().toString();
                if (product.getDupType() == CommonLib.DUP_TYPE_TRUE) {
                    viewHolder.text.setTypeface(null, Typeface.NORMAL);
                    viewHolder.text.setText("\"" + name + "\"" + " in " + product.getName());
                } else {
                    viewHolder.text.setTypeface(null, Typeface.BOLD);
                    viewHolder.text.setText(product.getName());
                }
                viewHolder.editSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((NewSearchActivity) getActivity()).setSearchEditTextValue(product.getName());
                    }
                });
                viewHolder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentSearchObject obj = new RecentSearchObject();
                        obj.setCategoryObj(product);
                        obj.setType(AppConstants.TYPE_CATEGORY);

                        try {
                            int userId = Integer.parseInt(AppPreferences.getUserID(getActivity()));
                            RecentSearchesDBWrapper.addProduct(obj, userId , System.currentTimeMillis());
                        }catch(Exception e){
                            RecentSearchesDBWrapper.addProduct(obj, 1, System.currentTimeMillis());
                        }
                        Intent intent = new Intent(mContext, SearchResultsActivity.class);
                        intent.putExtra("type", product.getType());
                        intent.putExtra("id", product.getId());
                        intent.putExtra("dup_type", product.getDupType());
                        if (product.getName() == null)
                            return;
                        intent.putExtra("category_name", product.getName());
                        String[] params1 = product.getName().split(" ");
                        String name1 = "";
                        for (String pam : params1) {
                            name1 += (pam + "+");
                        }
                        name1 = name1.substring(0, name1.length() - 1);
                        intent.putExtra("name", name1);

                        if (name == null)
                            return;
                        String[] params = name.split(" ");
                        String builder = "";
                        for (String pam : params) {
                            builder += (pam + "+");
                        }
                        builder = builder.substring(0, builder.length() - 1);

                        intent.putExtra("query", builder);
                        intent.putExtra("url", AppConstants.GET_SEARCH_RESULTS);
                        startActivity(intent);
                    }
                });
            } else {
                final BaseProductListObject productListObject = parentCategory.getProducts().get(position - products.size());
                viewHolder.text.setTypeface(null, Typeface.BOLD);
                viewHolder.text.setText(productListObject.getName());
                viewHolder.editSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((NewSearchActivity) getActivity()).setSearchEditTextValue(productListObject.getName());
                    }
                });
                viewHolder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentSearchObject obj = new RecentSearchObject();
                        obj.setType(AppConstants.TYPE_PRODUCT);
                        obj.setProductObj(productListObject);
                        try {
                            int userId = Integer.parseInt(AppPreferences.getUserID(getActivity()));
                            RecentSearchesDBWrapper.addProduct(obj, userId , System.currentTimeMillis());
                        }catch(Exception e){
                            RecentSearchesDBWrapper.addProduct(obj, 1, System.currentTimeMillis());
                        }
                        if(!AppPreferences.isUserLogIn(getActivity())) {
                            RecentProductsDBWrapper.addProduct(productListObject,1,System.currentTimeMillis());
                        }
                        Intent intent = new Intent(mContext, NewProductDetailActivity.class);
                        intent.putExtra("slug", productListObject.getSlug());
                        intent.putExtra("id", productListObject.getId());
                        intent.putExtra("title", productListObject.getName());

                        //        GA Ecommerce
                        intent.putExtra("productActionListName", "Product Name Click");
                        intent.putExtra("screenName", "Search Results Activity");
                        intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);

                        mContext.startActivity(intent);
                    }
                });


            }
            return convertView;
        }

        protected class ViewHolder {
            TextView text;
            ImageView editSearch;
        }
    }
    private class RecentProductListAdapter extends BaseAdapter {

        private ArrayList<RecentSearchObject> wishes;
        private Activity mContext;

        public RecentProductListAdapter(Activity context, ArrayList<RecentSearchObject> wishes) {
            mContext = context;
            this.wishes = wishes;
        }

        @Override
        public int getCount() {
            if (wishes == null) {
                return 0;
            } else {
                return wishes.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return wishes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            final RecentSearchObject product = wishes.get(position);
            if (v == null || v.findViewById(R.id.list_root) == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, null);
            }

            ViewHolder viewHolder = (ViewHolder) v.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) v.findViewById(R.id.text1);
                viewHolder.editSearch = (ImageView)v.findViewById(R.id.edit_recent_search);
                viewHolder.editSearch.setVisibility(View.VISIBLE);
                v.setTag(viewHolder);
            }

            final String name = ((TextView)((NewSearchActivity)activity).getActionBarView().findViewById(R.id.search_category)).getText().toString();
            viewHolder.text.setTypeface(null, Typeface.BOLD);
            if(product.getType()==AppConstants.TYPE_CATEGORY) {
                viewHolder.text.setText(product.getCategoryObj().getName());
            }else{
                viewHolder.text.setText(product.getProductObj().getName());
            }

            viewHolder.editSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(product.getType()==AppConstants.TYPE_CATEGORY) {
                        ((NewSearchActivity) getActivity()).setSearchEditTextValue(product.getCategoryObj().getName());
                    }else{
                        ((NewSearchActivity) getActivity()).setSearchEditTextValue(product.getProductObj().getName());
                    }

                }
            });
            viewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (product.getType() == AppConstants.TYPE_CATEGORY) {
                        Intent intent = new Intent(getActivity(), SearchResultsActivity.class);

                        if (product == null || product.getCategoryObj().getName().length() < 1)
                            return;
                        String[] params = product.getCategoryObj().getName().split(" ");
                        String builder = "";
                        for (String pam : params) {
                            builder += (pam + "+");
                        }
                        builder = builder.substring(0, builder.length() - 1);

                        intent.putExtra("query", builder);
                        intent.putExtra("url", AppConstants.GET_SEARCH_RESULTS);
                        startActivity(intent);
                    }else{


                        Intent intent = new Intent(mContext, NewProductDetailActivity.class);
                        intent.putExtra("slug", product.getProductObj().getSlug());
                        intent.putExtra("id", product.getProductObj().getId());
                        intent.putExtra("title", product.getProductObj().getName());

                        //        GA Ecommerce
                        intent.putExtra("productActionListName", "Product Name Click");
                        intent.putExtra("screenName", "Search Results Activity");
                        intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);

                        mContext.startActivity(intent);
                       }
                }
            });
            return v;
        }

        public void clearData(){
            wishes.clear();
            notifyDataSetChanged();
        }

        protected class ViewHolder {
            TextView text;
            ImageView editSearch;
        }

    }

    public class GetDataFromCache extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            ArrayList<RecentSearchObject> result = null;
            try {
                int userId = Integer.parseInt(AppPreferences.getUserID(activity));
                result = RecentSearchesDBWrapper.getProducts(userId);
            } catch(NumberFormatException e) {
                result = RecentSearchesDBWrapper.getProducts(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            if(!destroyed && result != null && result instanceof ArrayList<?> && ((ArrayList<?>) result).size() > 0 ) {
                recentSearchesAdapter = new RecentProductListAdapter(activity,  (ArrayList<RecentSearchObject>)result);
                recentSearchesListView.setAdapter(recentSearchesAdapter);
                getView.findViewById(R.id.recent_searches_container).setVisibility(View.VISIBLE);
                getView.findViewById(R.id.clear_searches).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recentSearchesAdapter != null)
                            recentSearchesAdapter.clearData();
                        try {
                            int userId = Integer.parseInt(AppPreferences.getUserID(activity));
                            RecentSearchesDBWrapper.removeProducts(userId);
                        } catch (Exception e) {
                            RecentSearchesDBWrapper.removeProducts(1);
                        }
                    }
                });
                getView.findViewById(R.id.progress_recent).setVisibility(View.GONE);
            }
        }

    }


}