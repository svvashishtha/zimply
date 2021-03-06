package com.application.zimply.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.activities.NewSearchActivity;
import com.application.zimply.activities.ProductDetailsActivity;
import com.application.zimply.activities.SearchResultsActivity;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.CategoryObject;
import com.application.zimply.baseobjects.HomeProductObj;
import com.application.zimply.baseobjects.ParentCategory;
import com.application.zimply.db.RecentProductsDBWrapper;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.CommonLib;

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

    private ListView recentSearchesListView;
    private RecentProductListAdapter recentSearchesAdapter;


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

        loadData(null, true);
    }

    public void performSearch(String input) {
        if(input == null)
            return;

        if(input.trim().length() >= 2) {
            if(mAsyncRunning != null)
                mAsyncRunning.cancel(true);
            loadData(input.trim(), false);
        } else {
            if( input.trim().length() == 0 ) {
                getView.findViewById(R.id.recent_searches_container).setVisibility(View.VISIBLE);
                getView.findViewById(R.id.listview_container).setVisibility(View.GONE);
            }
        }
    }

    private void loadData (String param, boolean loadFromCache) {
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
            if(builder.length()>0) {
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
            if (!destroyed){
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
                products = ((ParentCategory)obj).getSubCategories();
                products.addAll(((ParentCategory) obj).getCategories());

                ArrayList<CategoryObject> objects = new ArrayList<CategoryObject>();

//                Collections.copy(objects, products);

                for(CategoryObject dups: products ) {
                    objects.add(new CategoryObject(dups));
                }

                for(CategoryObject dups: objects ) {
                    dups.setDupType(CommonLib.DUP_TYPE_TRUE);
                }

                products.addAll(objects);

                adapter1 = new ProductListAdapter(activity, R.layout.simple_list_item, products);
                mListView.setAdapter(adapter1);
                getView.findViewById(R.id.progress_container).setVisibility(View.GONE);

                //if products size is 0, display recent
                if(products != null && products.size() > 0) {
                    getView.findViewById(R.id.listview_container).setVisibility(View.VISIBLE);
                } else {//else display products
                    getView.findViewById(R.id.listview_container).setVisibility(View.GONE);
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
                v.setTag(viewHolder);
            }

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
                    if(product.getName() == null)
                        return;
                    intent.putExtra("category_name", product.getName());
                    String[] params1 = product.getName().split(" ");
                    String name1 = "";
                    for ( String pam:params1 ) {
                        name1 += (pam + "+");
                    }
                    name1 = name1.substring(0, name1.length() - 1);
                    intent.putExtra("name", name1);

                    if(name == null)
                        return;
                    String[] params = name.split(" ");
                    String builder = "";
                    for ( String pam:params ) {
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
        }

    }

    private class RecentProductListAdapter extends ArrayAdapter<HomeProductObj> {

        private ArrayList<HomeProductObj> wishes;
        private Activity mContext;

        public RecentProductListAdapter(Activity context, int resourceId, ArrayList<HomeProductObj> wishes) {
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
            final HomeProductObj product = wishes.get(position);
            if (v == null || v.findViewById(R.id.list_root) == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, null);
            }

            ViewHolder viewHolder = (ViewHolder) v.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) v.findViewById(R.id.text1);
                v.setTag(viewHolder);
            }

            final String name = ((TextView)((NewSearchActivity)activity).getActionBarView().findViewById(R.id.search_category)).getText().toString();
            viewHolder.text.setTypeface(null, Typeface.BOLD);
            viewHolder.text.setText(product.getName());

            viewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                    intent.putExtra("slug", product.getSlug());
                    intent.putExtra("id", product.getId());
                    startActivity(intent);
                }
            });
            return v;
        }

        protected class ViewHolder {
            TextView text;
        }

    }

    public class GetDataFromCache extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            ArrayList<HomeProductObj> result = null;
            try {
                int userId = Integer.parseInt(AppPreferences.getUserID(activity));
                result = RecentProductsDBWrapper.getProducts(userId);
            } catch(NumberFormatException e) {
                result = RecentProductsDBWrapper.getProducts(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            if(!destroyed && result != null && result instanceof ArrayList<?> && ((ArrayList<?>) result).size() > 0 ) {
                recentSearchesAdapter = new RecentProductListAdapter(activity, R.layout.simple_list_item, (ArrayList<HomeProductObj>)result);
                recentSearchesListView.setAdapter(recentSearchesAdapter);
                getView.findViewById(R.id.recent_searches_container).setVisibility(View.VISIBLE);
                getView.findViewById(R.id.progress_recent).setVisibility(View.GONE);
            }
        }

    }





}
