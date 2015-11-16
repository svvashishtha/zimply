package com.application.zimply.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.activities.HomeActivity;
import com.application.zimply.activities.ProductListingActivity;
import com.application.zimply.activities.SearchProductsActivity;
import com.application.zimply.baseobjects.CategoryObject;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.managers.ImageLoaderManager;
import com.application.zimply.preferences.AppPreferences;

import java.util.ArrayList;

public class ProductsCategoryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int height;
    Context mContext;
    int TYPE_HEADER = 0;
    int TYPE_CATEGORY = 1;
    int TYPE_TITLE = 2;
    private ArrayList<CategoryObject> objs;

    public ProductsCategoryGridAdapter(Context context, ArrayList<CategoryObject> objs, int height) {
        this.objs = objs;
        this.height = height;
        this.mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_CATEGORY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_item_layout, null);
            holder = new ProductsCategoryViewHolder(view);
        } else if (viewType == TYPE_TITLE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_title_layout
                    , null);
            holder = new ProductsTitleViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_header_layout
                    , null);
            holder = new ProductsCategoryHeaderViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CATEGORY) {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            ((ProductsCategoryViewHolder) holder).parentFrame.setLayoutParams(lp);
            ((ProductsCategoryViewHolder) holder).categoryName.setText(objs.get(position).getName());
            if (objs.get(position ).getImage() != null) {
                new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl(objs.get(position).getImage(), ((ProductsCategoryViewHolder) holder).categoryImg, "users", height, height, false, false);
            }
        } else {

            if (position == objs.size()) {
                ProductsTitleViewHolder productsTitleViewHolder = (ProductsTitleViewHolder) holder;
                int cityId = Integer.parseInt(AppPreferences.getSavedCityId(mContext));

                if (!AppPreferences.getSavedCityServe(mContext)) {
                    productsTitleViewHolder.footer.setVisibility(View.VISIBLE);
                    productsTitleViewHolder.footer.setTypeface(null, Typeface.ITALIC);
                    productsTitleViewHolder.footer.setText(R.string.delhi_ncr_available);
                } else {
                    productsTitleViewHolder.footer.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (objs != null)
            return objs.size() + 1;
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        /*if (position == 0) {
            return TYPE_HEADER;
        } else */
        if (position == objs.size()) {
            return TYPE_TITLE;
        } else {
            return TYPE_CATEGORY;
        }
    }

    public class ProductsCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImg;
        TextView categoryName;
        FrameLayout parentFrame;

        public ProductsCategoryViewHolder(View itemView) {
            super(itemView);
            categoryImg = (ImageView) itemView.findViewById(R.id.category_img);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
            parentFrame = (FrameLayout) itemView.findViewById(R.id.parent_layout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProductListingActivity.class);
                    intent.putExtra("category_id", objs.get(getAdapterPosition()).getId());
                    intent.putExtra("category_name", objs.get(getAdapterPosition()).getName());
                    intent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public class ProductsCategoryHeaderViewHolder extends RecyclerView.ViewHolder {

        public ProductsCategoryHeaderViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SearchProductsActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public class ProductsTitleViewHolder extends RecyclerView.ViewHolder {
        TextView footer;

        public ProductsTitleViewHolder(View itemView) {
            super(itemView);
            footer = (TextView) itemView.findViewById(R.id.header_footer);
        }
    }
}
