package com.application.zimplyshop.fragments;

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

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.ExpertProfileActivity;
import com.application.zimplyshop.activities.ExpertsListActivity;
import com.application.zimplyshop.activities.NewSearchActivity;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.HomeExpertObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllCategories;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;

import java.util.ArrayList;

/**
 * Created by apoorvarora on 04/11/15.
 */
public class ExpertSearchFragment extends BaseFragment implements GetRequestListener, AppConstants {

    private boolean destroyed = false;
    private AsyncTask mAsyncRunning;
    private ListView mExpertListView, mCategoryListView;

    View getView;
    Activity activity;
    SharedPreferences prefs;
    int width, height;
    LayoutInflater vi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expert_search_layout, null);
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
        mCategoryListView = (ListView) getView.findViewById(R.id.category_listview);
        mExpertListView = (ListView) getView.findViewById(R.id.expert_list);

        if(AllCategories.getInstance() != null && AllCategories.getInstance().getPhotoCateogryObjs() != null && AllCategories.getInstance().getPhotoCateogryObjs().getExpert_base_category() != null) {
            ArrayList<CategoryObject> categoryObjects = AllCategories.getInstance().getPhotoCateogryObjs().getExpert_base_category();
            ExpertCategoryListAdapter mCategoryAdapter = new ExpertCategoryListAdapter(activity, R.layout.simple_list_item, categoryObjects);
            mCategoryListView.setAdapter(mCategoryAdapter);
            CommonLib.setListViewHeightBasedOnChildren(mCategoryListView);
        }
    }

    public void performSearch(String input) {
        if(input == null)
            return;
        if(input.trim().length() >= 2) {
            if( mAsyncRunning != null )//if not running, start the executor
                mAsyncRunning.cancel(true);
            //if running, cancel the executor, and then start the executor
            loadData(input.trim());
        } else {
            if( input.trim().length() == 0 ) {
                if(AllCategories.getInstance() != null && AllCategories.getInstance().getPhotoCateogryObjs() != null && AllCategories.getInstance().getPhotoCateogryObjs().getExpert_base_category() != null) {
                    ArrayList<CategoryObject> categoryObjects = AllCategories.getInstance().getPhotoCateogryObjs().getExpert_base_category();
                    ExpertCategoryListAdapter mCategoryAdapter = new ExpertCategoryListAdapter(activity, R.layout.simple_list_item, categoryObjects);
                    mCategoryListView.setAdapter(mCategoryAdapter);
                    CommonLib.setListViewHeightBasedOnChildren(mCategoryListView);
                    getView.findViewById(R.id.category_list_container).setVisibility(View.VISIBLE);
                } else
                    getView.findViewById(R.id.category_list_container).setVisibility(View.GONE);
                getView.findViewById(R.id.expert_list_container).setVisibility(View.GONE);
            }
        }
    }

    private void loadData (String param) {
        //replace all spaces with '+'
        if(param == null)
            return;
        String[] params = param.split(" ");
        String builder = "";
        for ( String pam:params ) {
            builder += (pam + "+");
        }
        if(builder.trim().length()>0) {
            builder = builder.substring(0, builder.length() - 1);
            String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_SEARCHED_EXPERTS_LIST + "?q=" + builder;
            mAsyncRunning = GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.SEARCHED_EXPERTS_REQUEST_TAG,
                    ObjectTypes.OBJECT_TYPE_SEARCHED_EXPERTS);
        }
    }


    @Override
    public void onDestroy() {
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    public void scrollSearchToTop() {
        try {
//            if (getView.findViewById(R.id.home_fragment_scroll_root) != null) {
//                ((ScrollView) getView.findViewById(R.id.home_fragment_scroll_root)).smoothScrollTo(0, 0);
//            }
        } catch (Exception e) {
        }
    }

    private class ExpertListAdapter extends ArrayAdapter<HomeExpertObj> {

        private ArrayList<HomeExpertObj> wishes;
        private Activity mContext;

        public ExpertListAdapter(Activity context, int resourceId, ArrayList<HomeExpertObj> wishes) {
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
            final HomeExpertObj product = wishes.get(position);
            if (v == null || v.findViewById(R.id.list_root) == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, null);
            }

            ViewHolder viewHolder = (ViewHolder) v.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) v.findViewById(R.id.text1);
                v.setTag(viewHolder);
            }

            viewHolder.text.setText(product.getTitle());

            viewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ExpertProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("expert_obj", product);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
            return v;
        }

        protected class ViewHolder {
            TextView text;
        }
    }

    private class ExpertCategoryListAdapter extends ArrayAdapter<CategoryObject> {

        private ArrayList<CategoryObject> wishes;
        private Activity mContext;

        public ExpertCategoryListAdapter(Activity context, int resourceId, ArrayList<CategoryObject> wishes) {
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
            viewHolder.text.setTypeface(null, Typeface.BOLD);
            viewHolder.text.setText(product.getName());

            viewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ExpertsListActivity.class);
                    intent.putExtra("parent_category_id", product.getId());
                    intent.putExtra("category_name", product.getName());
                    intent.putExtra("city_id", AppPreferences.getSavedCityId(mContext));
                    intent.putExtra("city_name", AppPreferences.getSavedCity(mContext));
                    mContext.startActivity(intent);
                }
            });
            return v;
        }

        protected class ViewHolder {
            TextView text;
        }

    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag.equals(RequestTags.SEARCHED_EXPERTS_REQUEST_TAG)) {
            if (!destroyed){
                getView.findViewById(R.id.progress_container).setVisibility(View.VISIBLE);
                getView.findViewById(R.id.expert_list_container).setVisibility(View.GONE);
                getView.findViewById(R.id.category_list_container).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag.equals(RequestTags.SEARCHED_EXPERTS_REQUEST_TAG)) {
            mAsyncRunning = null;
            if (!destroyed && obj != null && obj instanceof Object[]) {
                //play with the views
                getView.findViewById(R.id.progress_container).setVisibility(View.GONE);
                getView.findViewById(R.id.category_list_container).setVisibility(View.VISIBLE);
                getView.findViewById(R.id.expert_list_container).setVisibility(View.VISIBLE);

                ArrayList<CategoryObject> categoryObjects = (ArrayList<CategoryObject>) ((Object[])obj)[0];
                ExpertCategoryListAdapter mCategoryAdapter = new ExpertCategoryListAdapter(activity, R.layout.simple_list_item, categoryObjects);
                mCategoryListView.setAdapter(mCategoryAdapter);
                CommonLib.setListViewHeightBasedOnChildren(mCategoryListView);

                ArrayList<HomeExpertObj> expertObjects = (ArrayList<HomeExpertObj>) ((Object[])obj)[1];
                ExpertListAdapter mExpertLAdapter = new ExpertListAdapter(activity, R.layout.simple_list_item, expertObjects);
                mExpertListView.setAdapter(mExpertLAdapter);
                CommonLib.setListViewHeightBasedOnChildren(mExpertListView);

                CommonLib.hideKeyBoard(activity, ((NewSearchActivity)activity).getActionBarView().findViewById(R.id.search_category));
            }
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag.equals(RequestTags.SEARCHED_EXPERTS_REQUEST_TAG)) {
            mAsyncRunning = null;
            if (!destroyed) {
                getView.findViewById(R.id.progress_container).setVisibility(View.GONE);
            }
        }
    }



}
