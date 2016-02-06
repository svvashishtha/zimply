package com.application.zimplyshop.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.AskUsActivity;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.PostQuestionReceivedObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.CustomTextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umesh Lohani on 2/1/2016.
 */
public class PostQuestionFragment extends BaseFragment implements UploadManagerCallback,View.OnClickListener,AppConstants,RequestTags{


    EditText questionText;
    public static PostQuestionFragment newInstance(Bundle bundle){
        PostQuestionFragment fragment = new PostQuestionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    PostQuestionReceivedObject postedQuestion;

    int productId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.post_question_fragment,container,false);
        questionText = (EditText)view.findViewById(R.id.question_text);
        ((CustomTextView)view.findViewById(R.id.submit_btn)).setOnClickListener(this);
        ((CustomTextView)view.findViewById(R.id.cancel_btn)).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        productId = getArguments().getInt("id");
        if(getArguments().containsKey("question")){
            postedQuestion = (PostQuestionReceivedObject)getArguments().getSerializable("question");
            questionText.setText(postedQuestion.getQuestion());
        }
        UploadManager.getInstance().addCallback(this);
    }

    boolean isDestroyed;
    @Override
    public void onDestroy() {
        super.onDestroy();
        UploadManager.getInstance().removeCallback(this);
        isDestroyed = true;
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if(!isDestroyed && requestType == POST_QUESTION_REQUEST_TAG){
            if(progress!=null)
                progress.dismiss();
            if(status) {
                if(postedQuestion == null) {
                    showToast("Your question has been uploaded for review.");
                }else{
                    showToast("Your question has been edited.");
                }
                ((AskUsActivity) getActivity()).setPostedQuestion((PostQuestionReceivedObject) response);
            }else{
                Toast.makeText(getActivity(),((ErrorObject)response).getErrorMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    ProgressDialog progress;
    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if(!isDestroyed && requestType == POST_QUESTION_REQUEST_TAG){
            progress = ProgressDialog.show(getActivity(),null,"Uploading question. Please wait..");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.submit_btn:
                CommonLib.hideKeyBoard(getActivity(),questionText);
                postQuestionRequest();
                break;
            case R.id.cancel_btn:
                ((AskUsActivity)getActivity()).onBackPressed();
                break;
        }

    }


    public void postQuestionRequest(){
        if(questionText!=null && questionText.getText()!=null && questionText.getText().toString().trim().length()>0){
            String url = AppApplication.getInstance().getBaseUrl()+POST_QUESTION_URL;

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("product_id", productId + ""));
            list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(getActivity())));
            list.add(new BasicNameValuePair("ques", questionText.getText().toString().trim()));
            if(postedQuestion!=null){
                list.add(new BasicNameValuePair("ques_id ",postedQuestion.getId()+"" ));
            }
            UploadManager.getInstance().makeAyncRequest(url, POST_QUESTION_REQUEST_TAG, productId+ "",
                    ObjectTypes.OBJECT_TYPE_POST_QUESTION , productId, list, null);
        }else{
            showToast("Please enter your question");
        }
    }
}
