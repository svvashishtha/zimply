package com.application.zimplyshop.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.AskUsActivity;
import com.application.zimplyshop.baseobjects.PostQuestionReceivedObject;
import com.application.zimplyshop.baseobjects.QuestionAnswerObject;
import com.application.zimplyshop.widgets.CustomEdittext;
import com.application.zimplyshop.widgets.CustomTextView;
import com.application.zimplyshop.widgets.CustomTextViewBold;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 2/1/2016.
 */
public class AskUsQuestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    int TYPE_SEARCH_CONTAINER = 0;
    int TYPE_POST_QUERY_LAYOUT = 1;
    int TYPE_QUESTIONS_LAYOUT = 2;
    int TYPE_LOADER = 3;
    int TYPE_NO_QUESTION_YET_LAYOUT = 4;

    Context mContext;

    ArrayList<QuestionAnswerObject> objs;

    public AskUsQuestionsAdapter(Context context){
        mContext = context;
        objs = new ArrayList<>();
    }


    OnBtnClickListener mListener;




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == TYPE_SEARCH_CONTAINER){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.askus_search_layout,parent,false);
            holder = new AskUsSearchViewHolder(view);
        }else if(viewType == TYPE_POST_QUERY_LAYOUT){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.askus_post_query_layout,parent,false);
            holder = new PostQueryViewHolder(view);
        }else if(viewType == TYPE_LOADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_footer_layout, parent, false);
            holder = new LoadingViewHolder(view);
        }else if(viewType == TYPE_NO_QUESTION_YET_LAYOUT){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.no_questions_yet_layout,parent,false);
            holder = new NoQuestionYetViewHolder(view);
        }else{
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.askus_question_anwer_layout,parent,false);
            holder = new QuestionAnswerViewHolder(view);
        }
        return  holder;
    }

    public void setOnBtnClickListener(OnBtnClickListener listener){
        this.mListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(getItemViewType(position) == TYPE_QUESTIONS_LAYOUT){
            QuestionAnswerViewHolder holderPro = (QuestionAnswerViewHolder)holder;
            SpannableString question = new SpannableString("Q: "+objs.get(position - 2).getQues().getQuestion());
            question.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, 0);
            holderPro.questionText.setText(question);
            SpannableString answer = new SpannableString("A: "+objs.get(position-2).getAns().get(0).getQuestion());
            answer.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, 0);
            holderPro.answerText.setText(answer);
            if(objs.get(position-2).getAns().get(0).getTotal() != 0) {
                holderPro.foundUsefullCount.setVisibility(View.VISIBLE);
                holderPro.foundUsefullCount.setText(objs.get(position - 2).getAns().get(0).helpful + " out of " + objs.get(position - 2).getAns().get(0).getTotal() + " found this helpful");
            }else{
                holderPro.foundUsefullCount.setVisibility(View.VISIBLE);
            }
            SpannableString postedBy= new SpannableString("Posted by "+objs.get(position - 2).getQues().getName());
            postedBy.setSpan(new StyleSpan(Typeface.ITALIC), 10, postedBy.length(), 0);
            postedBy.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.heading_text_color)), 11, postedBy.length(), 0);
            holderPro.postedByText.setText(postedBy);

            SpannableString repliedBy= new SpannableString("Replied By "+objs.get(position-2).getAns().get(0).getName());
            repliedBy.setSpan(new StyleSpan(Typeface.ITALIC), 11, repliedBy.length(), 0);
            repliedBy.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.heading_text_color)),12,repliedBy.length(),0);
            holderPro.replyByText.setText(repliedBy);
            if(objs.get(position-2).getAns().get(0).getIs_useful() == -1){
                holderPro.wasReviewUseful.setText("Was this answer useful?");
                holderPro.reviewUsefulText.setVisibility(View.VISIBLE);
                holderPro.reviewUsefulText.setText("Yes");
                holderPro.reviewNotUseful.setVisibility(View.VISIBLE);
                holderPro.reviewNotUseful.setText("No");
            }else if(objs.get(position-2).getAns().get(0).getIs_useful() == 0){
                holderPro.wasReviewUseful.setText("You didn't found this answer useful.");
                holderPro.reviewNotUseful.setText("Change");
                holderPro.reviewUsefulText.setVisibility(View.GONE);
            }else {
                holderPro.wasReviewUseful.setText("You found this answer useful.");
                holderPro.reviewUsefulText.setVisibility(View.GONE);
                holderPro.reviewNotUseful.setVisibility(View.GONE);
            }

            holderPro.reviewUsefulText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objs.get(position-2).getAns().get(0).setIs_useful(1);
                    if(mListener!=null){
                        mListener.onReviewMarkUseful(objs.get(position-2).getAns().get(0).getId(),1);
                    }
                    notifyItemChanged(position);
                }
            });
            holderPro.reviewNotUseful.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(objs.get(position-2).getAns().get(0).getIs_useful() == 0){
                        objs.get(position-2).getAns().get(0).setIs_useful(-1);

                    }else{
                        objs.get(position-2).getAns().get(0).setIs_useful(0);
                        if(mListener!=null){
                            mListener.onReviewMarkUseful(objs.get(position-2).getAns().get(0).getId(),0);
                        }
                    }
                    notifyItemChanged(position);
                }
            });


        }else if(getItemViewType(position) == TYPE_POST_QUERY_LAYOUT){
            PostQueryViewHolder holderQuery = (PostQueryViewHolder)holder;
            if(postedQuestion!=null){
                holderQuery.questionPostSuccessLayout.setVisibility(View.VISIBLE);
                holderQuery.postQryLayout.setVisibility(View.GONE);
                SpannableString answer = new SpannableString("Q: "+postedQuestion.getQuestion());
                answer.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, 0);
                holderQuery.questionText.setText(answer);
                holderQuery.editQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((AskUsActivity) mContext).showPostQuestionsFragment(postedQuestion);
                    }
                });
                holderQuery.deleteQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener!=null){
                            mListener.onDeleteClick();
                        }
                    }
                });
            }else{
                holderQuery.postQryLayout.setVisibility(View.VISIBLE);
                holderQuery.questionPostSuccessLayout.setVisibility(View.GONE);
                holderQuery.postBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((AskUsActivity)mContext).showPostQuestionsFragment();
                    }
                });
            }
        }else if(getItemViewType(position) == TYPE_SEARCH_CONTAINER){

        }
    }

    public void addData(ArrayList<QuestionAnswerObject> objs){
        this.objs.addAll(objs);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if( objs.size()>0){
            if(isFooterRemoved){
                return objs.size()+2;
            }else{
                return objs.size()+3;
            }
        }
        return 3;
    }

    PostQuestionReceivedObject postedQuestion;
    public void setPostedQuestion(PostQuestionReceivedObject obj){
        postedQuestion = obj;
        notifyDataSetChanged();
    }


    public void removePostedQuestion(){
        postedQuestion = null;
        notifyItemChanged(1);
    }
    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_SEARCH_CONTAINER;

        }else if(position == 1){
            return TYPE_POST_QUERY_LAYOUT;
        }else if(position == 2){
            if(objs.size()>0){
                return TYPE_QUESTIONS_LAYOUT;
            }else{
                return TYPE_NO_QUESTION_YET_LAYOUT;
            }
        }else if(position == objs.size()+2){
            return TYPE_LOADER;

        }else{
            return TYPE_QUESTIONS_LAYOUT;
        }

    }

    public class AskUsSearchViewHolder extends RecyclerView.ViewHolder{

        CustomEdittext editText ;
        public AskUsSearchViewHolder(View itemView) {
            super(itemView);
            editText = (CustomEdittext)itemView.findViewById(R.id.search_edittext);
        }
    }

    public class PostQueryViewHolder extends RecyclerView.ViewHolder{

        CustomTextView postBtn,editQuestion,deleteQuestion;
        LinearLayout postQryLayout,questionPostSuccessLayout;
        CustomTextViewBold questionText;
        public PostQueryViewHolder (View itemView) {
            super(itemView);
            postBtn = (CustomTextView)itemView.findViewById(R.id.post_query_btn);
            postQryLayout = (LinearLayout)itemView.findViewById(R.id.post_query_layout);
            questionPostSuccessLayout = (LinearLayout)itemView.findViewById(R.id.question_post_success_layout);
            questionText = (CustomTextViewBold)itemView.findViewById(R.id.question_text);
            editQuestion = (CustomTextView)itemView.findViewById(R.id.edit_btn);
            deleteQuestion= (CustomTextView)itemView.findViewById(R.id.delete_btn);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View view) {
            super(view);

        }

    }

    public class NoQuestionYetViewHolder extends RecyclerView.ViewHolder {


        public NoQuestionYetViewHolder(View view) {
            super(view);
        }

    }

    public class QuestionAnswerViewHolder extends RecyclerView.ViewHolder{

        CustomTextView answerText,postedByText,replyByText,reviewUsefulText,reviewNotUseful,foundUsefullCount,wasReviewUseful;
        CustomTextViewBold questionText;
        public QuestionAnswerViewHolder(View itemView) {
            super(itemView);
            questionText = (CustomTextViewBold)itemView.findViewById(R.id.question_text);
            answerText = (CustomTextView)itemView.findViewById(R.id.answer_text);
            postedByText = (CustomTextView)itemView.findViewById(R.id.posted_by_text);
            replyByText = (CustomTextView)itemView.findViewById(R.id.reply_text);
            foundUsefullCount = (CustomTextView)itemView.findViewById(R.id.useful_count_text);
            reviewUsefulText = (CustomTextView)itemView.findViewById(R.id.reply_useful);
            reviewNotUseful = (CustomTextView)itemView.findViewById(R.id.reply_not_useful);
            wasReviewUseful = (CustomTextView)itemView.findViewById(R.id.was_review_useful);
        }
    }

    boolean isFooterRemoved;

    public void removeItem(){
        isFooterRemoved = true;
        notifyDataSetChanged();
    }


    public interface OnBtnClickListener{
        void onDeleteClick();
        void onReviewMarkUseful(int questionId,int isUseful);
    }
}
