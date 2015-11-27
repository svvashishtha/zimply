package com.application.zimplyshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.HomeActivity;
import com.application.zimplyshop.activities.ProductListingActivity;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.widgets.CustomTextViewBold;

import java.util.ArrayList;

public class ProductsCategoryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int height;
    Context mContext;
    int TYPE_HEADER = 0;
    int TYPE_CATEGORY = 1;

    private ArrayList<CategoryObject> objs;

    int displayWidth;

    public ProductsCategoryGridAdapter(Context context, ArrayList<CategoryObject> objs, int height,int displayWidth) {
        this.objs = objs;
        this.height = height;
        this.mContext = context;
        this.displayWidth = displayWidth;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_CATEGORY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_item_layout, null);
            holder = new ProductsCategoryViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookings_recyclerview_layout
                    , null);
            holder = new HeaderViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CATEGORY) {
            int height = (objs.get(position-1).getImg().getHeight()*displayWidth)/objs.get(position-1).getImg().getWidth();

            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            ((ProductsCategoryViewHolder) holder).parentFrame.setLayoutParams(lp);
            ((ProductsCategoryViewHolder) holder).categoryName.setText(objs.get(position-1).getName());
            if (objs.get(position-1 ).getImage() != null) {
                new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl(objs.get(position-1).getImg().getImage(), ((ProductsCategoryViewHolder) holder).categoryImg, "users", height, height, false, false);
            }
        } else {
            ((HeaderViewHolder)holder).recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
           // ((HeaderViewHolder)holder).recyclerView.addItemDecoration(new SpaceItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.margin_small)));
            HomePageBookingsAdapter adapter = new HomePageBookingsAdapter(mContext);
           /* LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int)mContext.getResources().getDimension(R.dimen.booking_card_height));
            ((HeaderViewHolder)holder).recyclerView.setLayoutParams(lp);*/
            ((HeaderViewHolder) holder).recyclerView.setAdapter(adapter);
          //  CommonLib.setListViewHeightBasedOnChildren(((HeaderViewHolder) holder).recyclerView.get);
            //((HeaderViewHolder)holder).recyclerView.setNestedScrollingEnabled(true);
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
        if (position == 0) {
            return TYPE_HEADER;
        }  else {
            return TYPE_CATEGORY;
        }
    }

    public class ProductsCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImg;
        CustomTextViewBold categoryName;
        FrameLayout parentFrame;

        public ProductsCategoryViewHolder(View itemView) {
            super(itemView);
            categoryImg = (ImageView) itemView.findViewById(R.id.category_img);
            categoryName = (CustomTextViewBold) itemView.findViewById(R.id.category_name);
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

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public HeaderViewHolder(View view) {
            super(view);
            recyclerView = (RecyclerView)view.findViewById(R.id.booking_list);
        }

    }


}
