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
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.HomeActivity;
import com.application.zimplyshop.activities.ProductDetailsActivity;
import com.application.zimplyshop.baseobjects.LatestBookingObject;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/26/2015.
 */
public class HomePageBookingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;

    ArrayList<LatestBookingObject> objs;

    public HomePageBookingsAdapter(Context context){
        mContext = context;
        objs = new ArrayList<>();
    }

    public void addData(ArrayList<LatestBookingObject> objs){
        this.objs.addAll(objs);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_booking_layout,parent,false);
        RecyclerView.ViewHolder holder = new BookingHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((BookingHolder)holder).productName.setText(objs.get(position).getProduct().getName());
        ((BookingHolder)holder).productPrice.setText(mContext.getString(R.string.rs_text) + objs.get(position).getProduct().getPrice());
        new ImageLoaderManager((HomeActivity)mContext).setImageFromUrl(objs.get(position).getProduct().getImage(), ((BookingHolder) holder).productImg, "users", mContext.getResources().getDimensionPixelSize(R.dimen.sub_cat_img_size), mContext.getResources().getDimensionPixelSize(R.dimen.sub_cat_img_size), false, false);
        ((BookingHolder)holder).visitTime.setText("* Visit before" + TimeUtils.getTimeStampDate(objs.get(position).getVisit_date(), TimeUtils.DATE_TYPE_DAY_MON_DD_YYYY) + ", 9 PM");
        ((BookingHolder)holder).callCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + objs.get(position).getVendor().getReg_add().getPhone()));
                mContext.startActivity(callIntent);
            }
        });
        ((BookingHolder)holder).checkLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Vendor LAT LONG not available",Toast.LENGTH_SHORT).show();
            }
        });
        ((BookingHolder)holder).productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                intent.putExtra("slug", objs.get(position).getProduct().getSlug());
                intent.putExtra("id", objs.get(position).getProduct().getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objs.size();
    }

    public class BookingHolder extends RecyclerView.ViewHolder{
        ImageView productImg,callCustomer,checkLoc;
        TextView productName,productPrice,visitTime;
        LinearLayout productCard;

        public BookingHolder(View itemView) {
            super(itemView);
            visitTime = (TextView)itemView.findViewById(R.id.visit_time);
            productName = (TextView)itemView.findViewById(R.id.product_name);
            productPrice = (TextView)itemView.findViewById(R.id.product_price);
            productImg = (ImageView)itemView.findViewById(R.id.product_img_src);
            callCustomer = (ImageView)itemView.findViewById(R.id.call_customer);
            checkLoc = (ImageView)itemView.findViewById(R.id.get_direction_customer);
            productCard = (LinearLayout)itemView.findViewById(R.id.product_card);
        }
    }
}
