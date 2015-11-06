package com.application.zimply.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.activities.ExpertsListActivity;
import com.application.zimply.activities.HomeActivity;
import com.application.zimply.activities.SearchActivity;
import com.application.zimply.baseobjects.CategoryObject;
import com.application.zimply.managers.ImageLoaderManager;
import com.application.zimply.preferences.AppPreferences;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 9/23/2015.
 */
public class ExpertCategoryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    int height;
    Context mContext;
    int TYPE_HEADER = 0;
    int TYPE_CATEGORY = 1;
    int TYPE_TITLE = 2;
    private ArrayList<CategoryObject> objs;

    public ExpertCategoryGridAdapter(Context context , ArrayList<CategoryObject> objs , int height){
        this.objs = objs;
        this.height=height;
        this.mContext=context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == TYPE_CATEGORY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expert_category_item_layout, null);
            holder = new ExpertCategoryViewHolder(view);
        }else if(viewType == TYPE_TITLE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expert_category_title_layout
                    ,null);
            holder = new ExpertTitleViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expert_category_header_layout
                    , null);
            holder = new ExpertCategoryHeaderViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_CATEGORY) {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            ((ExpertCategoryViewHolder) holder).parentFrame.setLayoutParams(lp);
            ((ExpertCategoryViewHolder) holder).categoryName.setText(objs.get(position-1).getName());
            if (objs.get(position-1).getImage() != null) {
                new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl(objs.get(position-1).getImage(), ((ExpertCategoryViewHolder) holder).categoryImg, "users", height, height, false, false);
            }
        }else{

        }
    }

    @Override
    public int getItemCount() {
        if(objs !=null) {
            return objs.size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        /*if(position == 0){
            return TYPE_HEADER;
        }else */
        if(position == 0){
            return TYPE_TITLE;
        }else{
            return TYPE_CATEGORY;
        }
    }

    public class ExpertCategoryViewHolder extends  RecyclerView.ViewHolder{
        ImageView categoryImg;
        TextView categoryName;
        FrameLayout parentFrame;
        public ExpertCategoryViewHolder(View itemView) {
            super(itemView);
            categoryImg = (ImageView)itemView.findViewById(R.id.category_img);
            categoryName = (TextView)itemView.findViewById(R.id.category_name);
            parentFrame = (FrameLayout)itemView.findViewById(R.id.parent_layout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ExpertsListActivity.class);
                    intent.putExtra("parent_category_id", objs.get(getAdapterPosition()-1).getId());
                    intent.putExtra("category_name", objs.get(getAdapterPosition()-1).getName());
                    intent.putExtra("city_id", AppPreferences.getSavedCityId(mContext));
                    intent.putExtra("city_name", AppPreferences.getSavedCity(mContext));
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public class ExpertCategoryHeaderViewHolder extends  RecyclerView.ViewHolder{

        public ExpertCategoryHeaderViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SearchActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public class ExpertTitleViewHolder extends  RecyclerView.ViewHolder{

        public ExpertTitleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
