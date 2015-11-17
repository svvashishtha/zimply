package com.application.zimplyshop.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.application.zimply.R;
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
 * Expert search activity - Search query computes the name of matching categories and experts.
 * Experts lead to Expert details page
 * Categories lead to Expert listing page with the category and query specified.
 * */
public class SearchActivity extends BaseActivity implements GetRequestListener, AppConstants {


    private Activity mContext;
    private boolean destroyed = false;
    private AsyncTask mAsyncRunning;
    private ListView mExpertListView, mCategoryListView;
    private View actionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_layout);

        //Add callbacks
        GetRequestManager.getInstance().addCallbacks(this);

        //init
        mContext = this;
        mCategoryListView = (ListView) findViewById(R.id.category_listview);
        mExpertListView = (ListView) findViewById(R.id.expert_list);

        //toolbar init
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //search text watched
        TextWatcher searchTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                if(input.length() >= 2) {
                    actionBarView.findViewById(R.id.clear_text_view_category).setVisibility(View.VISIBLE);
                    if( mAsyncRunning != null )//if not running, start the executor
                        mAsyncRunning.cancel(true);
                    //if running, cancel the executor, and then start the executor
                    loadData(input);
                } else {
                    actionBarView.findViewById(R.id.clear_text_view_category).setVisibility(View.GONE);
                    if( input.length() == 0 ) {
                        if(AllCategories.getInstance() != null && AllCategories.getInstance().getPhotoCateogryObjs() != null && AllCategories.getInstance().getPhotoCateogryObjs().getExpert_base_category() != null) {
                            ArrayList<CategoryObject> categoryObjects = AllCategories.getInstance().getPhotoCateogryObjs().getExpert_base_category();
                            ExpertCategoryListAdapter mCategoryAdapter = new ExpertCategoryListAdapter(SearchActivity.this, R.layout.simple_list_item, categoryObjects);
                            mCategoryListView.setAdapter(mCategoryAdapter);
                            CommonLib.setListViewHeightBasedOnChildren(mCategoryListView);
                            findViewById(R.id.category_list_container).setVisibility(View.VISIBLE);
                        } else
                            findViewById(R.id.category_list_container).setVisibility(View.GONE);
                        findViewById(R.id.expert_list_container).setVisibility(View.GONE);
                    }
                }

            }
        };
        ((TextView)actionBarView.findViewById(R.id.search_category)).addTextChangedListener(searchTextWatcher);

        if(AllCategories.getInstance() != null && AllCategories.getInstance().getPhotoCateogryObjs() != null && AllCategories.getInstance().getPhotoCateogryObjs().getExpert_base_category() != null) {
            ArrayList<CategoryObject> categoryObjects = AllCategories.getInstance().getPhotoCateogryObjs().getExpert_base_category();
            ExpertCategoryListAdapter mCategoryAdapter = new ExpertCategoryListAdapter(SearchActivity.this, R.layout.simple_list_item, categoryObjects);
            mCategoryListView.setAdapter(mCategoryAdapter);
            CommonLib.setListViewHeightBasedOnChildren(mCategoryListView);
        }

    }
    /**
     * Get request implemented methods. Used to compute the list of experts and categories.
     * */
    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag.equals(RequestTags.SEARCHED_EXPERTS_REQUEST_TAG)) {
            if (!destroyed){
                findViewById(R.id.progress_container).setVisibility(View.VISIBLE);
                findViewById(R.id.expert_list_container).setVisibility(View.GONE);
                findViewById(R.id.category_list_container).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag.equals(RequestTags.SEARCHED_EXPERTS_REQUEST_TAG)) {
            mAsyncRunning = null;
            if (!destroyed && obj != null && obj instanceof Object[]) {
                //play with the views
                mContext.findViewById(R.id.progress_container).setVisibility(View.GONE);
                mContext.findViewById(R.id.category_list_container).setVisibility(View.VISIBLE);
                mContext.findViewById(R.id.expert_list_container).setVisibility(View.VISIBLE);

                ArrayList<CategoryObject> categoryObjects = (ArrayList<CategoryObject>) ((Object[])obj)[0];
                ExpertCategoryListAdapter mCategoryAdapter = new ExpertCategoryListAdapter(SearchActivity.this, R.layout.simple_list_item, categoryObjects);
                mCategoryListView.setAdapter(mCategoryAdapter);
                CommonLib.setListViewHeightBasedOnChildren(mCategoryListView);

                ArrayList<HomeExpertObj> expertObjects = (ArrayList<HomeExpertObj>) ((Object[])obj)[1];
                ExpertListAdapter mExpertLAdapter = new ExpertListAdapter(SearchActivity.this, R.layout.simple_list_item, expertObjects);
                mExpertListView.setAdapter(mExpertLAdapter);
                CommonLib.setListViewHeightBasedOnChildren(mExpertListView);

                CommonLib.hideKeyBoard(mContext, actionBarView.findViewById(R.id.search_category));
            }
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag.equals(RequestTags.SEARCHED_EXPERTS_REQUEST_TAG)) {
            mAsyncRunning = null;
            if (!destroyed) {
                findViewById(R.id.progress_container).setVisibility(View.GONE);
            }
        }
    }

    private void addToolbarView(Toolbar toolbar) {
        actionBarView = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        actionBarView.findViewById(R.id.title_textview).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.search_frame).setVisibility(View.VISIBLE);
        actionBarView.findViewById(R.id.clear_text_view_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.search_category)).setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(findViewById(R.id.search_category), InputMethodManager.SHOW_IMPLICIT);
            }
        });
        toolbar.addView(actionBarView);
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
        builder = builder.substring(0, builder.length() - 1);
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_SEARCHED_EXPERTS_LIST + "?q=" + builder;
        mAsyncRunning = GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.SEARCHED_EXPERTS_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_SEARCHED_EXPERTS);
    }

    /**
     * Remove callbacks and set the destroyed flag to true.
     * */
    @Override
    public void onDestroy() {
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    /**
     * Override to implement the back animation*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                    Intent intent = new Intent(SearchActivity.this, ExpertProfileActivity.class);
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

}