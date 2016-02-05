package com.application.zimplyshop.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.serverapis.RequestTags;

/**
 * Created by Umesh Lohani on 2/5/2016.
 */
public class MyQuestionsActivity extends BaseActivity implements GetRequestListener,AppConstants,RequestTags{

    RecyclerView questionsList;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.experts_list_layout);
        questionsList = (RecyclerView)findViewById(R.id.experts_list);
        questionsList.setLayoutManager(new LinearLayoutManager(this));
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setLoadingVariables();
        retryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRequestFailed) {
                    loadData();
                }
            }
        });

        loadData();
    }

    public void loadData(){
        String url = AppApplication.getInstance().getBaseUrl()+ AppConstants.MY_QUESTION_LIST_URL;
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.MY_QUESTIONS_REQUEST_TAG, ObjectTypes.OBJECT_TYPE_MY_QUESTIONS);
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.common_toolbar_text_layout, null);
        ((TextView) view.findViewById(R.id.title_textview)).setText("Ask Us");
        toolbar.addView(view);
    }


}
