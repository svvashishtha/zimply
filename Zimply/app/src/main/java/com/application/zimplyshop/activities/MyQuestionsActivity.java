package com.application.zimplyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.AskUsQuestionsAdapter;
import com.application.zimplyshop.adapters.MyQuestionListAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.MyQuestionAnswerObject;
import com.application.zimplyshop.baseobjects.MyQuestionListObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.CartSpaceItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umesh Lohani on 2/5/2016.
 */
public class MyQuestionsActivity extends BaseActivity implements GetRequestListener, AppConstants, RequestTags, UploadManagerCallback {

    RecyclerView questionsList;

    boolean isRequestAllowed, isLoading, isNotification;

    String nextUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_layout);
        questionsList = (RecyclerView) findViewById(R.id.categories_list);
        questionsList.setLayoutManager(new LinearLayoutManager(this));
        questionsList.addItemDecoration(new CartSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        PAGE_TYPE = AppConstants.PAGE_TYPE_MESSAGE;
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setLoadingVariables();
        setStatusBarColor();

        isNotification = getIntent().getBooleanExtra("is_notification", false);
        retryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRequestFailed) {
                    loadData();
                }
            }
        });
        GetRequestManager.getInstance().addCallbacks(this);
        UploadManager.getInstance().addCallback(this);
        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isNotification) {
            Intent intent = new Intent(this, HomeActivity.class);
            this.finish();
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    public void loadData() {
        String url;
        if (nextUrl == null) {
            url = AppApplication.getInstance().getBaseUrl() + AppConstants.MY_QUESTION_LIST_URL + "?userid=" + AppPreferences.getUserID(this);
        } else {
            url = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.MY_QUESTIONS_REQUEST_TAG, ObjectTypes.OBJECT_TYPE_MY_QUESTIONS);
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.common_toolbar_text_layout, null);
        ((TextView) view.findViewById(R.id.title_textview)).setText("My Questions");
        toolbar.addView(view);
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(MY_QUESTIONS_REQUEST_TAG)) {
            if (questionsList == null || questionsList.getAdapter() == null || questionsList.getAdapter().getItemCount() == 0) {
                showLoadingView();
                changeViewVisiblity(questionsList, View.GONE);
            } else {

            }
            isLoading = true;
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(MY_QUESTIONS_REQUEST_TAG)) {
            if (((MyQuestionListObject) obj).getAll().size() == 0) {
                if (questionsList.getAdapter() == null
                        || questionsList.getAdapter().getItemCount() == 1) {
                    showNullCaseView("No Questions");
                   /* setAdapterData(((MyQuestionListObject) obj).getAll());
                    showView();
                    changeViewVisiblity(questionsList, View.VISIBLE);*/
                    isRequestAllowed = false;
                } else {
                    showToast("No more Questions");
                    ((AskUsQuestionsAdapter) questionsList.getAdapter())
                            .removeItem();
                }
                isRequestAllowed = false;
            } else {

                setAdapterData(((MyQuestionListObject) obj).getAll());
                try {
                    //log ga event for product listing activity


                } catch (Exception e) {
                    e.printStackTrace();
                }
                nextUrl = ((MyQuestionListObject) obj).getNext_url();

                showView();
                changeViewVisiblity(questionsList, View.VISIBLE);
                if (((MyQuestionListObject) obj).getAll().size() < 10) {
                    isRequestAllowed = false;
                    ((MyQuestionListAdapter) questionsList.getAdapter())
                            .removeItem();
                } else {
                    isRequestAllowed = true;
                }
            }
            isLoading = false;
        }
    }

    int visibleItemCount, totalItemCount, pastVisiblesItems;

    public void setAdapterData(ArrayList<MyQuestionAnswerObject> objs) {
        if (questionsList.getAdapter() == null) {

            MyQuestionListAdapter adapter = new MyQuestionListAdapter(this);

            questionsList.setAdapter(adapter);
            questionsList
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {

                            visibleItemCount = recyclerView.getLayoutManager()
                                    .getChildCount();
                            totalItemCount = recyclerView.getLayoutManager()
                                    .getItemCount();
                            pastVisiblesItems = ((LinearLayoutManager) recyclerView
                                    .getLayoutManager())
                                    .findFirstVisibleItemPosition();

                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount
                                    && !isLoading && isRequestAllowed) {
                                loadData();
                            }
                            //scrollToolbarAndHeaderBy(-dy);

                        }

                        @Override
                        public void onScrollStateChanged(
                                RecyclerView recyclerView, int newState) {
                            new ImageLoaderManager(MyQuestionsActivity.this)
                                    .setScrollState(newState);
                            super.onScrollStateChanged(recyclerView, newState);
                        }
                    });
            /*((GridLayoutManager) productList.getLayoutManager())
                    .setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            switch (productList
                                    .getAdapter().getItemViewType(position)) {
                                case 0:
                                    return 1;
                                case 1:
                                    return 2;
                                default:
                                    return -1;
                            }
                        }
                    });*/
            ((MyQuestionListAdapter) questionsList.getAdapter()).setOnBtnClickListener(new MyQuestionListAdapter.OnBtnClickListener() {

                public void onReviewMarkUseful(int answerId, int is_useful) {
                    String url = AppApplication.getInstance().getBaseUrl() + REVIEW_ANSWER_URL;
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("ans_id", answerId + ""));
                    list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(MyQuestionsActivity.this)));
                    list.add(new BasicNameValuePair("is_useful", is_useful + ""));

                    UploadManager.getInstance().makeAyncRequest(url, REVIEW_ANSWER_TAG, answerId + "",
                            ObjectTypes.OBJECT_TYPE_REVIEW_ANSWER, answerId, list, null);

                }
            });
        }
        ((MyQuestionListAdapter) questionsList.getAdapter())
                .addData(objs);
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(MY_QUESTIONS_REQUEST_TAG)) {
            if (questionsList.getAdapter() == null
                    || questionsList.getAdapter().getItemCount() == 1) {
                showNetworkErrorView();
                changeViewVisiblity(questionsList, View.GONE);

            } else {
                if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");

                } else {
                    showToast(((ErrorObject) obj).getErrorMessage());

                }

                ((MyQuestionListAdapter) questionsList.getAdapter())
                        .removeItem();
                isRequestAllowed = false;
            }
            isLoading = false;
        }
    }

    boolean isDestroyed;

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();

    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {

    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {

    }
}
