package com.application.zimplyshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.NewProductDetailActivity;
import com.application.zimplyshop.baseobjects.MyQuestionAnswerObject;
import com.application.zimplyshop.widgets.CustomTextView;
import com.application.zimplyshop.widgets.CustomTextViewBold;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 2/5/2016.
 */
public class MyQuestionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;

    ArrayList<MyQuestionAnswerObject> objs;

    boolean isFooterRemoved;

    int TYPE_DATA = 0;

    int TYPE_LOADER = 1;

    public MyQuestionListAdapter(Context context){
        this.mContext = context;
        objs = new ArrayList<>();
    }


    public void removeItem(){
        isFooterRemoved = true;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<MyQuestionAnswerObject> objs){
        this.objs.addAll(objs);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == TYPE_DATA){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.askus_question_anwer_layout,parent,false);
            holder = new QuestionAnswerViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_footer_layout, parent, false);
            holder = new LoadingViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if(getItemViewType(position) == TYPE_DATA){
                QuestionAnswerViewHolder holderPro = (QuestionAnswerViewHolder)holder;
                /*SpannableString question = new SpannableString("Q: "+objs.get(position).getQues().getQuestion());
                question.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, 0);*/
                holderPro.questionText.setText(objs.get(position).getQues().getQuestion());
                /*SpannableString answer = new SpannableString("A: "+objs.get(position).getAns().get(0).getQuestion());
                answer.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, 0);*/
                holderPro.answerText.setText(objs.get(position).getAns().get(0).getQuestion());
                if(objs.get(position).getAns().get(0).getTotal() != 0) {
                    holderPro.foundUsefullCount.setVisibility(View.VISIBLE);
                    holderPro.foundUsefullCount.setText(objs.get(position).getAns().get(0).helpful + " out of " + objs.get(position).getAns().get(0).getTotal() + " found this helpful");
                }else{
                    holderPro.foundUsefullCount.setVisibility(View.VISIBLE);
                }
                SpannableString postedBy= new SpannableString("About "+objs.get(position).getProduct().getName());

                postedBy.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.z_rate_btn_blue_color)), 6, postedBy.length(), 0);
                holderPro.postedByText.setText(postedBy);
                holderPro.postedByText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewProductDetailActivity.class);
                        intent.putExtra("slug", objs.get(position).getProduct().getSlug());
                        intent.putExtra("id", objs.get(position).getProduct().getId());
                        intent.putExtra("title", objs.get(position).getProduct().getName());
                        //        GA Ecommerce
                        intent.putExtra("productActionListName", "Product List Item Click");
                        intent.putExtra("screenName", "Product List Activity");
                        intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);
                        mContext.startActivity(intent);
                    }
                });
                SpannableString repliedBy= new SpannableString("Replied By "+objs.get(position).getAns().get(0).getName());
                repliedBy.setSpan(new StyleSpan(Typeface.ITALIC), 11, repliedBy.length(), 0);
                repliedBy.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.heading_text_color)),12,repliedBy.length(),0);
                holderPro.replyByText.setText(repliedBy);
                if(objs.get(position).getAns().get(0).getIs_useful() == -1){
                    holderPro.wasReviewUseful.setText("Was this answer useful?");
                    holderPro.wasReviewUseful.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                    holderPro.reviewUsefulText.setVisibility(View.VISIBLE);
                    holderPro.reviewUsefulText.setText("Yes");
                    holderPro.reviewUsefulText.setTextColor(mContext.getResources().getColor(R.color.btn_green_color_normal));
                    holderPro.reviewNotUseful.setVisibility(View.VISIBLE);
                    holderPro.reviewNotUseful.setText("No");
                    holderPro.reviewNotUseful.setTextColor(mContext.getResources().getColor(R.color.red_text_color));
                }else if(objs.get(position).getAns().get(0).getIs_useful() == 0){
                    holderPro.wasReviewUseful.setText("You didn't found this answer useful.");
                    holderPro.wasReviewUseful.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                    holderPro.reviewNotUseful.setText("Change");
                    holderPro.reviewNotUseful.setTextColor(mContext.getResources().getColor(R.color.z_rate_btn_blue_color));
                    holderPro.reviewUsefulText.setVisibility(View.GONE);
                }else {
                    holderPro.wasReviewUseful.setText("You found this answer useful.");
                    holderPro.wasReviewUseful.setTextColor(mContext.getResources().getColor(R.color.btn_green_color_normal));
                    holderPro.reviewUsefulText.setVisibility(View.GONE);
                    holderPro.reviewNotUseful.setVisibility(View.GONE);
                }

                holderPro.reviewUsefulText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        objs.get(position).getAns().get(0).setIs_useful(1);
                        if(mListener!=null){
                            mListener.onReviewMarkUseful(objs.get(position).getAns().get(0).getId(),1);
                        }
                        notifyItemChanged(position);
                    }
                });
                holderPro.reviewNotUseful.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(objs.get(position).getAns().get(0).getIs_useful() == 0){
                            objs.get(position).getAns().get(0).setIs_useful(-1);

                        }else{
                            objs.get(position).getAns().get(0).setIs_useful(0);
                            if(mListener!=null){
                                mListener.onReviewMarkUseful(objs.get(position).getAns().get(0).getId(),0);
                            }
                        }
                        notifyItemChanged(position);
                    }
                });


            }else{

            }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == objs.size()){
            return TYPE_LOADER;
        }else{
            return TYPE_DATA;
        }
    }

    @Override
    public int getItemCount() {
        if(objs!=null){
            if(isFooterRemoved){
                return objs.size();
            }else{
                return objs.size()+1;
            }
        }
        return 0;
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


    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View view) {
            super(view);

        }

    }

    OnBtnClickListener mListener;

    public void setOnBtnClickListener(OnBtnClickListener listener){
        this.mListener = listener;
    }

    public interface OnBtnClickListener{

        void onReviewMarkUseful(int questionId,int isUseful);
    }
}
