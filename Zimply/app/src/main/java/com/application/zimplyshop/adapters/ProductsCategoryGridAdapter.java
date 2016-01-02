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
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.HomeActivity;
import com.application.zimplyshop.activities.ProductListingActivity;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.HomeProductCategoryNBookingObj;
import com.application.zimplyshop.baseobjects.LatestBookingObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.widgets.CustomTextViewBold;

import java.util.ArrayList;

public class ProductsCategoryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int height;
    Context mContext;
    int TYPE_TITLE=0;
    int TYPE_HEADER = 1;
    int TYPE_CATEGORY = 2;

    HomeProductCategoryNBookingObj obj;


    int displayWidth;

    public ProductsCategoryGridAdapter(Context context,int height,int displayWidth) {
        obj = new HomeProductCategoryNBookingObj();

        this.height = height;
        this.mContext = context;
        this.displayWidth = displayWidth;
    }

    public void addCategoryData(ArrayList<CategoryObject> objs){
        obj.setProduct_category(objs);
        notifyDataSetChanged();
    }

    public void addLatestBookingsData(ArrayList<LatestBookingObject> objs){
        if(objs!=null) {
            obj.setLatest_bookings(objs);
        }else{
            obj.getLatest_bookings().clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_CATEGORY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_item_layout, parent,false);
            holder = new ProductsCategoryViewHolder(view);
        }else if(viewType == TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookings_recyclerview_layout,parent,false);
            holder = new HeaderViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_title_layout,parent,false);
            holder = new TitleViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CATEGORY) {
            int newPos ;
            if(obj.getLatest_bookings().size()>0){
                newPos = position-2;
            }else{
                newPos = position;
            }
            int height = (obj.getProduct_category().get(newPos).getImg().getHeight()*(displayWidth-(2*mContext.getResources().getDimensionPixelSize(R.dimen.margin_small))))/obj.getProduct_category().get(newPos).getImg().getWidth();

            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            ((ProductsCategoryViewHolder) holder).parentFrame.setLayoutParams(lp);
            //((ProductsCategoryViewHolder) holder).categoryName.setText(obj.getProduct_category().get(newPos).getName());

            //((ProductsCategoryViewHolder) holder).categoryImg.setBackgroundResource(R.drawable.bg_dropshadow);
            if (obj.getProduct_category().get(newPos).getImg().getImage() != null) {
                new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl(obj.getProduct_category().get(newPos).getImg().getImage(), ((ProductsCategoryViewHolder) holder).categoryImg, "users", height, height, false, false);
            }
        } else if(getItemViewType(position) == TYPE_HEADER){
            ((HeaderViewHolder)holder).recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
           // ((HeaderViewHolder)holder).recyclerView.addItemDecoration(new SpaceItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.margin_small)));
            HomePageBookingsAdapter adapter = new HomePageBookingsAdapter(mContext,displayWidth,mContext.getResources().getDimensionPixelSize(R.dimen.booking_card_height));
            adapter.addData(obj.getLatest_bookings());
           /* LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int)mContext.getResources().getDimension(R.dimen.booking_card_height));
            ((HeaderViewHolder)holder).recyclerView.setLayoutParams(lp);*/
            ((HeaderViewHolder) holder).recyclerView.setAdapter(adapter);

          //  CommonLib.setListViewHeightBasedOnChildren(((HeaderViewHolder) holder).recyclerView.get);
            //((HeaderViewHolder)holder).recyclerView.setNestedScrollingEnabled(true);
        }else{
            ((TitleViewHolder) holder).customText.setText("Upcoming Visits");
        }

    }

    @Override
    public int getItemCount() {
        if (obj != null) {
            if(obj.getLatest_bookings().size()>0){
                return obj.getProduct_category().size()+2;
            }else {
                return obj.getProduct_category().size();
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(obj.getLatest_bookings().size()>0){
            if(position == 0){
                return TYPE_TITLE;
            }else if(position == 1){
                return TYPE_HEADER;
            }else{
                return TYPE_CATEGORY;
            }
        }else{
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
                    int pos ;
                    if(obj.getLatest_bookings().size()>0){
                        pos = getAdapterPosition()-2;
                    }else{
                        pos = getAdapterPosition();
                    }
                    Intent intent = new Intent(mContext, ProductListingActivity.class);
                    intent.putExtra("category_id", obj.getProduct_category().get(pos).getId());
                    intent.putExtra("category_name", obj.getProduct_category().get(pos).getName());
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

    public class TitleViewHolder extends RecyclerView.ViewHolder{
        TextView customText;

        public TitleViewHolder(View itemView) {
            super(itemView);
            customText = (TextView)itemView.findViewById(R.id.header_footer);
        }
    }

}
