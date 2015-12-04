package com.application.zimplyshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.BookingStoreProductListingActivity;
import com.application.zimplyshop.activities.MapPage;
import com.application.zimplyshop.baseobjects.LatestBookingObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.utils.CommonLib;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/26/2015.
 */
public class HomePageBookingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;

    ArrayList<LatestBookingObject> objs;
    int width,height;

    public HomePageBookingsAdapter(Context context,int width,int height){
        mContext = context;
        objs = new ArrayList<>();
        this.width =width;
        this.height = height;
    }

    public void addData(ArrayList<LatestBookingObject> objs){
        this.objs.addAll(objs);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booked_item_layout,parent,false);
        RecyclerView.ViewHolder holder = new BookingHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,height);
        ((BookingHolder) holder).bookCard.setLayoutParams(lp);
        ((BookingHolder)holder).ziStoreName.setText("Zi Store");
        ((BookingHolder)holder).cancelBooking.setVisibility(View.GONE);
        ((BookingHolder)holder).storeName.setText(objs.get(position).getVendor().getCompany_name());
        ((BookingHolder) holder).storeImg.setImageBitmap(CommonLib.getBitmap(mContext, R.drawable.ic_home_store, mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size)));
        ((BookingHolder) holder).storeAddress.setText(objs.get(position).getVendor().getReg_add().getLine1());
        ((BookingHolder)holder).callCustomerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + objs.get(position).getVendor().getReg_add().getPhone()));
                mContext.startActivity(callIntent);
            }
        });
        ((BookingHolder)holder).getDirectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MapPage.class);
                intent.putExtra("lat",objs.get(position).getVendor().getReg_add().getLocation().getLatitude());
                intent.putExtra("lon", objs.get(position).getVendor().getReg_add().getLocation().getLongitude());
                intent.putExtra("name", objs.get(position).getVendor().getCompany_name());
                mContext.startActivity(intent);
            }
        });
        ((BookingHolder) holder).bookCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BookingStoreProductListingActivity.class);
                intent.putExtra("booked_obj",objs.get(position).getProduct());
                intent.putExtra("hide_filter",true);
                intent.putExtra("vendor_id",objs.get(position).getVendor().getVendor_id());
                intent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
                intent.putExtra("vendor_name",objs.get(position).getVendor().getCompany_name());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objs.size();
    }

    public class BookingHolder extends RecyclerView.ViewHolder{
        ImageView storeImg;
        TextView ziStoreName,storeName,storeAddress,cancelBooking;
        RelativeLayout bookCard;
        LinearLayout callCustomerLayout,getDirectionLayout;

        public BookingHolder(View itemView) {
            super(itemView);
            ziStoreName = (TextView)itemView.findViewById(R.id.zi_store_name);
            storeName = (TextView)itemView.findViewById(R.id.store_name);
            storeAddress= (TextView)itemView.findViewById(R.id.store_address);
            storeImg = (ImageView)itemView.findViewById(R.id.store_img);


            bookCard = (RelativeLayout)itemView.findViewById(R.id.item_info_parent);
            cancelBooking = (TextView)itemView.findViewById(R.id.cancel_booking);
            callCustomerLayout = (LinearLayout)itemView.findViewById(R.id.call_layout);
            getDirectionLayout = (LinearLayout)itemView.findViewById(R.id.direction_layout);
        }
    }
}
