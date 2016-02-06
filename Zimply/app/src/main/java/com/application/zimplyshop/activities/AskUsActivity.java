package com.application.zimplyshop.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.PostQuestionReceivedObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.fragments.AskUsQuestionsFragment;
import com.application.zimplyshop.fragments.BaseFragment;
import com.application.zimplyshop.fragments.PostQuestionFragment;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.serverapis.RequestTags;

/**
 * Created by Umesh Lohani on 2/1/2016.
 */
public class AskUsActivity extends BaseActivity implements GetRequestListener, AppConstants, RequestTags {

    String nextUrl;

    RecyclerView recyclerView;

    int productId;

    PostQuestionReceivedObject receviedObj;

    boolean isPostFragmentFirst;

    String ASK_US_QUESTIONS_TAG = "questionstag";
    String POST_QUESTION_TAG = "postquestionstag";

    String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.askus_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        productName = getIntent().getStringExtra("name");
        productId = getIntent().getIntExtra("id", 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent().getIntExtra("fragment_type", 1) == 1) {
            isPostFragmentFirst = false;
            showQuestionsFragment();
        } else {
            isPostFragmentFirst = true;
            showPostQuestionsFragmentFirst();
        }
    }

    BaseFragment questionsFragment, postQuestionFragment;
    int fragmentType;

    int FRAGMENT_TYPE_QUESTIONS = 1;
    int FRAGMENT_TYPE_POST_QUESTION = 2;

    public void showQuestionsFragmentAfterReplace() {
        if (questionsFragment == null) {
            fragmentType = FRAGMENT_TYPE_QUESTIONS;
            Bundle bundle = new Bundle();
            bundle.putInt("id", productId);
            questionsFragment = AskUsQuestionsFragment.newInstance(bundle);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, questionsFragment, ASK_US_QUESTIONS_TAG).commit();
    }

    public void showQuestionsFragment() {
        if (questionsFragment == null) {
            fragmentType = FRAGMENT_TYPE_QUESTIONS;
            Bundle bundle = new Bundle();
            bundle.putInt("id", productId);
            questionsFragment = AskUsQuestionsFragment.newInstance(bundle);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, questionsFragment, ASK_US_QUESTIONS_TAG).commit();
    }

    public void deleteQuestionRequest() {
        receviedObj = null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPostQuestionsFragmentFirst() {
        if (postQuestionFragment == null) {
            fragmentType = FRAGMENT_TYPE_POST_QUESTION;
            Bundle bundle = new Bundle();
            bundle.putInt("id", productId);
            bundle.putString("name",productName);
            postQuestionFragment = PostQuestionFragment.newInstance(bundle);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, postQuestionFragment, POST_QUESTION_TAG).commit();
    }


    public void showPostQuestionsFragment() {
        if (postQuestionFragment == null) {
            fragmentType = FRAGMENT_TYPE_POST_QUESTION;
            Bundle bundle = new Bundle();
            bundle.putInt("id", productId);
            bundle.putString("name", productName);
            postQuestionFragment = PostQuestionFragment.newInstance(bundle);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit, R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit)
                .replace(R.id.container, postQuestionFragment, POST_QUESTION_TAG)
                .addToBackStack(null).commit();
    }

    //Edited question
    public void showPostQuestionsFragment(PostQuestionReceivedObject obj) {

        fragmentType = FRAGMENT_TYPE_POST_QUESTION;
        Bundle bundle = new Bundle();
        bundle.putInt("id", productId);
        bundle.putString("name", productName);
        bundle.putSerializable("question", obj);
        postQuestionFragment = PostQuestionFragment.newInstance(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit, R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit)
                .replace(R.id.container, postQuestionFragment, POST_QUESTION_TAG)
                .addToBackStack(null).commit();
    }


    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar,false);
        ((TextView) view.findViewById(R.id.title_textview)).setText("Ask Us");
        toolbar.addView(view);
    }

    public PostQuestionReceivedObject getReceviedObj() {
        return receviedObj;
    }

    public void setPostedQuestion(PostQuestionReceivedObject obj) {
        this.receviedObj = obj;
        if (!isPostFragmentFirst) {
            onBackPressed();
        } else {
            isPostFragmentFirst = false;
            showQuestionsFragmentAfterReplace();
        }
    }
}
