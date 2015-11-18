package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.ProductDetailsActivity;
import com.application.zimplyshop.activities.ProductPhotoZoomActivity;
import com.application.zimplyshop.managers.ImageLoaderManager;

import java.util.ArrayList;

/**
 * Created by Saurabh on 02-11-2015.
 */
public class ProductThumbAdapters extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context mContext;

    int width, height;

    int selectedPos = 0;

    ArrayList<String> imageUrls;
    OnItemClickListener mListener;


    public ProductThumbAdapters(Context context, ArrayList<String> imageUrls, int width, int height) {
        this.mContext = context;
        this.imageUrls = new ArrayList<>(imageUrls);
        this.width = width;
        this.height = height;
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_image_view, null);
        RecyclerView.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        ((ViewHolder) holder).productImg.setLayoutParams(lp);
        ((ViewHolder) holder).productImg.setPadding(3, 3, 3, 3);
        if (selectedPos == position) {
            ((ViewHolder) holder).container.setBackgroundResource(R.drawable.blue_border_card_bg);
        } else {
            ((ViewHolder) holder).container.setBackgroundResource(R.drawable.white_card_rectangle);
        }
        if(mContext instanceof ProductDetailsActivity) {
            new ImageLoaderManager((ProductDetailsActivity) mContext).setImageFromUrl(imageUrls.get(position), ((ViewHolder) holder).productImg, "user", width, height, false, false);
        }else{
            new ImageLoaderManager((ProductPhotoZoomActivity) mContext).setImageFromUrl(imageUrls.get(position), ((ViewHolder) holder).productImg, "user", width, height, false, false);
        }
    }

    @Override
    public int getItemCount() {
        if (imageUrls == null) {
            return 0;
        }
        return imageUrls.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImg;
        RelativeLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            productImg = (ImageView) itemView.findViewById(R.id.featured_image);
            container = (RelativeLayout) itemView.findViewById(R.id.product_image_container);
            productImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
