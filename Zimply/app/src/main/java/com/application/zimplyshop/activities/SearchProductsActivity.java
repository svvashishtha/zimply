package com.application.zimplyshop.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.ParentCategory;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.JSONUtils;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchProductsActivity extends BaseActivity implements GetRequestListener {

    private boolean destroyed = false;

    private ArrayList<CategoryObject> products;
    private AsyncTask mAsyncRunning;
    private ListView mListView;
    private ProductListAdapter adapter1;
    View actionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_search_activity_layout);
        getApplication();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mListView = (ListView) findViewById(R.id.listview);

        GetRequestManager.getInstance().addCallbacks(this);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                if (input.length() >= 2) {
                    actionBarView.findViewById(R.id.clear_text_view_category).setVisibility(View.VISIBLE);
                    if (mAsyncRunning != null)
                        mAsyncRunning.cancel(true);
                    loadData(input);
                } else {
                    actionBarView.findViewById(R.id.clear_text_view_category).setVisibility(View.GONE);
                    if (input.length() == 0) {
                        findViewById(R.id.listview_container).setVisibility(View.GONE);
                    }
                }

            }
        };

        ((TextView) actionBarView.findViewById(R.id.search_category)).addTextChangedListener(textWatcher);

    }

    private void addToolbarView(Toolbar toolbar) {
        actionBarView = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        actionBarView.findViewById(R.id.title_textview).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.search_frame).setVisibility(View.VISIBLE);
        actionBarView.findViewById(R.id.barcode_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);*/
                Intent intent = new Intent(SearchProductsActivity.this, BarcodeScannerActivity.class);
                startActivity(intent);
            }
        });
        actionBarView.findViewById(R.id.clear_text_view_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.search_category)).setText("");
            }
        });

        ((EditText) actionBarView.findViewById(R.id.search_category)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        toolbar.addView(actionBarView);
    }

    private void performSearch(String query) {
        Intent intent = new Intent(this, SearchResultsActivity.class);

        if (query == null || query.length() < 1)
            return;
        String[] params = query.split(" ");
        String builder = "";
        for (String pam : params) {
            builder += (pam + "+");
        }
        builder = builder.substring(0, builder.length() - 1);

        intent.putExtra("query", builder);
        intent.putExtra("url", AppConstants.GET_SEARCH_RESULTS);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag.equals(RequestTags.SEARCHED_PRODUCTS_REQUEST_TAG)) {
            if (!destroyed) {
                findViewById(R.id.progress_container).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag.equals(RequestTags.SEARCHED_PRODUCTS_REQUEST_TAG)) {
            mAsyncRunning = null;
            if (!destroyed && obj != null && obj instanceof ParentCategory) {
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

                adapter1 = new ProductListAdapter(SearchProductsActivity.this, R.layout.simple_list_item, products);
                mListView.setAdapter(adapter1);
                findViewById(R.id.progress_container).setVisibility(View.GONE);

                //if products size is 0, display recent
                if (products != null && products.size() > 0) {
                    findViewById(R.id.listview_container).setVisibility(View.VISIBLE);
                } else {//else display products
                    findViewById(R.id.listview_container).setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag.equals(RequestTags.SEARCHED_PRODUCTS_REQUEST_TAG)) {
            mAsyncRunning = null;
            if (!destroyed) {
                findViewById(R.id.progress_container).setVisibility(View.GONE);
            }
        }
    }

    private void loadData(String param) {
        //remove all spaces
        if (param == null)
            return;
        String[] params = param.split(" ");
        String builder = "";
        for (String pam : params) {
            builder += (pam + "+");
        }
        builder = builder.substring(0, builder.length() - 1);
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_SEARCHED_PRODUCTS_LIST + "?q=" + builder;
        mAsyncRunning = GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.SEARCHED_PRODUCTS_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_SEARCHED_PRODUCTS);

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

            final String name = ((TextView) actionBarView.findViewById(R.id.search_category)).getText().toString();
            if (product.getDupType() == CommonLib.DUP_TYPE_TRUE) {
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
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                //Toast.makeText(this , "CONTENT"+contents,Toast.LENGTH_SHORT).show();
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                //Toast.makeText(this , "FORMAT:"+format,Toast.LENGTH_SHORT).show();
                JSONObject obj = JSONUtils.getJSONObject(contents);
                Intent workintent = new Intent(this, NewProductDetailActivity.class);
                intent.putExtra("slug", JSONUtils.getIntegerfromJSON(obj, "slug"));
                workintent.putExtra("id", (long) JSONUtils.getIntegerfromJSON(obj, "id"));

                //        GA Ecommerce
                intent.putExtra("productActionListName", "Scan Product Result");
                intent.putExtra("screenName", "Search Products Activity");
                intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);

                startActivity(workintent);
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }
}
