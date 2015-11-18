package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.ExpertProfileActivity;
import com.application.zimplyshop.baseobjects.HomeExpertObj;
import com.application.zimplyshop.baseobjects.HomePhotoObj;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.widgets.RoundedImageView;

import java.util.ArrayList;

public class ExpertDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<HomePhotoObj> objs;

    boolean isDataLoadComplete;

    int TYPE_PRO_DETAIL = 0;

    int TYPE_PRO_DESC = 1;

    int TYPE_PRO_PHOTOS = 2;

    int TYPE_LOADER = 3;
    OnItemClickListener mListener;

    HomeExpertObj mExpertObj;

    Context mContext;

    int width, height, photoHeight;

    public ExpertDetailListAdapter(Context context, HomeExpertObj expertObj, int width, int height, int photoHeight) {
        this.mContext = context;
        this.objs = new ArrayList<HomePhotoObj>();
        this.mExpertObj = expertObj;
        this.width = width;
        this.height = height;
        this.photoHeight = photoHeight;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void addData(ArrayList<HomePhotoObj> objs) {
        ArrayList<HomePhotoObj> newObjs = new ArrayList<HomePhotoObj>(objs);
        this.objs.addAll(this.objs.size(), newObjs);
        notifyDataSetChanged();
    }

    public void removeItem() {
        isDataLoadComplete = true;
        notifyItemRemoved(objs.size() + 2);
    }

    public HomePhotoObj getItem(int pos) {
        return objs.get(pos - 2);
    }

    @Override
    public int getItemCount() {
        if (objs != null) {
            if (!isDataLoadComplete) {
                return objs.size() + 3;
            } else {
                return objs.size() + 2;
            }
        } else {
            return 3;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PRO_DETAIL) {
            ((ProDetailViewHolder) holder).proName.setText(mExpertObj.getTitle());
            ((ProDetailViewHolder) holder).proCat.setText(getExpertCategoryString(mExpertObj.getCategory()));
            ((ProDetailViewHolder) holder).proCat.setVisibility(View.GONE);

            ((ProDetailViewHolder) holder).proName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                    mContext.getResources().getDimension(R.dimen.margin_small));

            if (((ProDetailViewHolder) holder).coverImage.getTag() == null
                    || !(((String) ((ProDetailViewHolder) holder).coverImage.getTag())
                    .equalsIgnoreCase(mExpertObj.getCover()))) {
                new ImageLoaderManager((ExpertProfileActivity) mContext).setImageFromUrl(mExpertObj.getCover(),
                        ((ProDetailViewHolder) holder).coverImage, "users", width, height, true, false);

                ((ProDetailViewHolder) holder).coverImage.setTag(mExpertObj.getCover());
            }

            if (((ProDetailViewHolder) holder).proImage.getTag() == null
                    || !(((String) ((ProDetailViewHolder) holder).proImage.getTag())
                    .equalsIgnoreCase(mExpertObj.getLogo()))) {

                new ImageLoaderManager((ExpertProfileActivity) mContext).setImageFromUrl(mExpertObj.getLogo(),
                        ((ProDetailViewHolder) holder).proImage, "users",
                        mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size),
                        mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), true, false);

                ((ProDetailViewHolder) holder).proImage.setTag(mExpertObj.getLogo());
            }

        } else if (getItemViewType(position) == TYPE_PRO_DESC) {

            ((ProDescriptionViewHolder) holder).desc.setText(mExpertObj.getDesc());

        } else if (getItemViewType(position) == TYPE_PRO_PHOTOS) {

            ((PhotoViewHolder) holder).parentLayout.setBackgroundResource(R.drawable.ic_card_bg);

            if (((PhotoViewHolder) holder).picImage.getTag() == null
                    || !(((String) ((PhotoViewHolder) holder).picImage.getTag())
                    .equalsIgnoreCase(objs.get(position - 2).getImage()))) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        photoHeight);
                ((PhotoViewHolder) holder).picImage.setLayoutParams(lp);

                new ImageLoaderManager((ExpertProfileActivity) mContext).setImageFromUrl(
                        objs.get(position - 2).getImage(), ((PhotoViewHolder) holder).picImage, "users", photoHeight,
                        photoHeight, true, false);
                ((PhotoViewHolder) holder).picImage.setTag(objs.get(position - 2).getImage());
            }

            ((PhotoViewHolder) holder).styleCatText.setText(objs.get(position - 2).getCat() + " - " + (objs.get(position - 2).getStyle()));
        } else {

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGrp, int itemType) {
        RecyclerView.ViewHolder holder;
        if (itemType == TYPE_PRO_DETAIL) {
            View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.homepage_experts_layout, viewGrp,
                    false);
            holder = new ProDetailViewHolder(view);
        } else if (itemType == TYPE_PRO_DESC) {
            View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.expert_detail_desc_card_layout,
                    viewGrp, false);
            holder = new ProDescriptionViewHolder(view);
        } else if (itemType == TYPE_LOADER) {
            View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.progress_footer_layout, viewGrp,
                    false);
            holder = new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.fav_photo_item_layout, viewGrp,
                    false);
            holder = new PhotoViewHolder(view);
        }
        return holder;
    }

    private String getExpertCategoryString(ArrayList<String> category) {
        String text = "";
        for (int i = 0; i < category.size(); i++) {
            text += category.get(i);
            if (i < category.size() - 1) {
                text += ",";
            }
        }
        return text;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_PRO_DETAIL;
        } else if (position == 1) {
            return TYPE_PRO_DESC;
        } else if (position == objs.size() + 2) {
            return TYPE_LOADER;
        } else {
            return TYPE_PRO_PHOTOS;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public class ProDescriptionViewHolder extends RecyclerView.ViewHolder {

        TextView desc;

        public ProDescriptionViewHolder(View itemView) {
            super(itemView);
            desc = (TextView) itemView.findViewById(R.id.pro_desc);
        }

    }

    public class ProDetailViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView coverImage;
        ImageView proImage;
        TextView proName, proCat;

        public ProDetailViewHolder(View itemView) {
            super(itemView);
            coverImage = (RoundedImageView) itemView.findViewById(R.id.cover_img);
            proImage = (ImageView) itemView.findViewById(R.id.expert_pic);
            proName = (TextView) itemView.findViewById(R.id.expert_name);
            proCat = (TextView) itemView.findViewById(R.id.expert_category);
        }

    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView picImage;
        LinearLayout parentLayout;
        TextView styleCatText;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            picImage = (ImageView) itemView.findViewById(R.id.photo_img);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parent_layout);
            styleCatText = (TextView) itemView.findViewById(R.id.style_cat_text);
            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }

    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View view) {
            super(view);

        }

    }
}
