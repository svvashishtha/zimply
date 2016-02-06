package com.application.zimplyshop.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.AskUsActivity;
import com.application.zimplyshop.adapters.AskUsQuestionsAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AskUsQuestionsObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.QuestionAnswerObject;
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
 * Created by Umesh Lohani on 2/1/2016.
 */
public class AskUsQuestionsFragment extends BaseFragment implements GetRequestListener,AppConstants,RequestTags,UploadManagerCallback,View.OnClickListener{

    boolean isRequestAllowed;

    public static AskUsQuestionsFragment newInstance(Bundle bundle){
        AskUsQuestionsFragment fragment = new AskUsQuestionsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    RecyclerView recyclerView;

    String nextUrl;

    int visibleItemCount,totalItemCount,pastVisiblesItems;

    boolean isLoading,isDestroyed;

    AsyncTask mAsynTask;

    boolean isSearching,isDataRefreshed;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.experts_list_layout,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.experts_list);
        recyclerView.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_small),
                getResources().getDimensionPixelSize(R.dimen.margin_small),
                getResources().getDimensionPixelSize(R.dimen.margin_small),
                getResources().getDimensionPixelSize(R.dimen.margin_small));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new CartSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        return view;
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(questions!=null) {
            setAdapterData(questions);
            showView();
            changeViewVisiblity(recyclerView,View.VISIBLE);
        }
    }

    ArrayList<QuestionAnswerObject> questions;

    public void setAdapterData(ArrayList<QuestionAnswerObject> objs){

        if (recyclerView.getAdapter() == null) {

            AskUsQuestionsAdapter adapter = new AskUsQuestionsAdapter(getActivity());

            recyclerView.setAdapter(adapter);
            recyclerView
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
                            new ImageLoaderManager(getActivity())
                                    .setScrollState(newState);
                            super.onScrollStateChanged(recyclerView, newState);
                        }
                    });

            ((AskUsQuestionsAdapter) recyclerView.getAdapter()).setOnBtnClickListener(new AskUsQuestionsAdapter.OnBtnClickListener() {
                @Override
                public void onDeleteClick() {
                    deleteQuestionRequest();
                }
                public void onReviewMarkUseful(int answerId, int is_useful) {
                    String url = AppApplication.getInstance().getBaseUrl()+REVIEW_ANSWER_URL;
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("ans_id", answerId+ ""));
                    list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(getActivity())));
                    list.add(new BasicNameValuePair("is_useful", is_useful+""));

                    UploadManager.getInstance().makeAyncRequest(url, REVIEW_ANSWER_TAG, answerId+ "",
                            ObjectTypes.OBJECT_TYPE_REVIEW_ANSWER, answerId, list, null);

                }

                @Override
                public void onSearchParam(String text) {
                    if(text.trim().length()>0){
                        if(mAsynTask!=null){
                            mAsynTask.cancel(true);
                        }
                        isSearching = true;
                        isDataRefreshed=true;
                        searchString = text.trim();
                        nextUrl = null;
                        loadData();
                    }else{
                        if(mAsynTask!=null){
                            mAsynTask.cancel(true);
                        }
                        isSearching = false;
                        nextUrl = null;
                        isDataRefreshed=true;
                        searchString =null;
                        loadData();
                    }
                }
            });
        }
        ((AskUsQuestionsAdapter) recyclerView.getAdapter())
                .addData(objs);
        ((AskUsQuestionsAdapter) recyclerView.getAdapter()).setPostedQuestion(((AskUsActivity) getActivity()).getReceviedObj());
        if(questions == null){
            questions = new ArrayList<>();
        }
        questions.addAll(objs);
    }




    int productId;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        productId = getArguments().getInt("id");
        GetRequestManager.getInstance().addCallbacks(this);
        loadData();

    }

    public void deleteQuestionRequest(){
        final AlertDialog logoutDialog;
        logoutDialog = new AlertDialog.Builder(getActivity())
                .setTitle( "Delete Question?")
                .setMessage("Are you sure you want to delete this question?")
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendDeleteQuestionRequest();
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        logoutDialog.show();


    }


    public void sendDeleteQuestionRequest(){
        String url = AppApplication.getInstance().getBaseUrl()+DELETE_QUESTION_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("ques_id",((AskUsActivity)getActivity()).getReceviedObj().getId() +""));
        UploadManager.getInstance().addCallback(this);
        UploadManager.getInstance().makeAyncRequest(url, DELETE_QUESTION_REQUEST_TAG, productId + "",
                ObjectTypes.OBJECT_TYPE_DELETE_QUESTION, productId, list, null);

    }


    @Override
    public void onDestroy() {
        UploadManager.getInstance().removeCallback(this);
        GetRequestManager.getInstance().removeCallbacks(this);
        isDestroyed = true;
        super.onDestroy();
    }
    String searchString=null;

    public void loadData(){
        String finalUrl;
        if (nextUrl == null) {
            if(!isSearching) {

                finalUrl = AppApplication.getInstance().getBaseUrl() + ASKUS_QUESTIONS_URL + "?product_id=" + productId + "&size=10"
                        + (AppPreferences.isUserLogIn(getActivity()) ? "&userid=" + AppPreferences.getUserID(getActivity()) : "");
            }else{
                finalUrl = AppApplication.getInstance().getBaseUrl() + ASKUS_QUESTIONS_SEARCH_URL + "?query="+searchString+"&product_id=" + productId + "&size=10"
                        + (AppPreferences.isUserLogIn(getActivity()) ? "&userid=" + AppPreferences.getUserID(getActivity()) : "");
            }
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }
        mAsynTask = GetRequestManager.getInstance().makeAyncRequest(finalUrl, ASKUS_QUESTIONS_REQUEST_TAG ,
                ObjectTypes.OBJECT_TYPES_ASKUS_QUESTIONS);

    }


    @Override
    public void onRequestStarted(String requestTag) {
        if(requestTag.equalsIgnoreCase(ASKUS_QUESTIONS_REQUEST_TAG)){
            if(recyclerView.getAdapter()==null || recyclerView.getAdapter().getItemCount() == 0){
                showLoadingView();
                changeViewVisiblity(recyclerView,View.GONE);
            }else if(isDataRefreshed){
                ((AskUsQuestionsAdapter)recyclerView.getAdapter()).showFooter();
            }
            isLoading=true;
        }

    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if(!isDestroyed &&  requestTag.equalsIgnoreCase(ASKUS_QUESTIONS_REQUEST_TAG)){
            if (((AskUsQuestionsObject) obj).getAll().size() == 0) {
                if (recyclerView.getAdapter() == null
                        || recyclerView.getAdapter().getItemCount() == 1) {
                    //showNullCaseView("No Products");
                    setAdapterData(((AskUsQuestionsObject) obj).getAll());
                    showView();
                    changeViewVisiblity(recyclerView, View.VISIBLE);
                    isRequestAllowed = false;
                } else {
                    showToast("No more Questions");
                    ((AskUsQuestionsAdapter) recyclerView.getAdapter())
                            .removeItem();
                }
                isRequestAllowed = false;
            } else {

                setAdapterData(((AskUsQuestionsObject) obj).getAll());
                try {
                    //log ga event for product listing activity


                } catch (Exception e) {
                    e.printStackTrace();
                }
                nextUrl = ((AskUsQuestionsObject) obj).getNext_url();

                showView();
                changeViewVisiblity(recyclerView, View.VISIBLE);
                if (((AskUsQuestionsObject) obj).getAll().size() < 10) {
                    isRequestAllowed = false;
                    ((AskUsQuestionsAdapter) recyclerView.getAdapter())
                            .removeItem();
                } else {
                    isRequestAllowed = true;
                }
            }
            if(isDataRefreshed){
                isDataRefreshed=false;
            }
            isLoading=false;
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(ASKUS_QUESTIONS_REQUEST_TAG)){
            if (recyclerView.getAdapter() == null
                    || recyclerView.getAdapter().getItemCount() == 1) {
                showNetworkErrorView();
                changeViewVisiblity(recyclerView, View.GONE);

            } else {
                if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");

                } else {
                    showToast(((ErrorObject) obj).getErrorMessage());

                }

                ((AskUsQuestionsAdapter)recyclerView.getAdapter())
                        .removeItem();
                isRequestAllowed = false;
            }
            isLoading = false;
            if(isDataRefreshed){
                isDataRefreshed=false;
            }
        }
    }


    ProgressDialog progress;
    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if(!isDestroyed && requestType ==DELETE_QUESTION_REQUEST_TAG){
            if(progress!=null){
                progress.dismiss();
            }
            if(status){

                showToast("Question deleted successfully");
                ((AskUsQuestionsAdapter)recyclerView.getAdapter()).removePostedQuestion();
                ((AskUsActivity)getActivity()).deleteQuestionRequest();

            }else{
                showToast("Could not delete question. Please try again");
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if(!isDestroyed && requestType ==DELETE_QUESTION_REQUEST_TAG){
            progress = ProgressDialog.show(getActivity(),null,"Deleting question. Please wait..");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.retry_layout:
                loadData();
                break;
        }

    }
}
