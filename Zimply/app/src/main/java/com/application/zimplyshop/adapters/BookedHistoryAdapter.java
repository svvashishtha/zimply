package com.application.zimplyshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BookedProductHistoryObject;
import com.application.zimplyshop.utils.CommonLib;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/14/2015.
 */
public class BookedHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<BookedProductHistoryObject> objs;

    Context mContext;
    public BookedHistoryAdapter(Context context ,ArrayList<BookedProductHistoryObject> objs ){
        mContext = context;
        this.objs=new ArrayList<>(objs);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booked_item_layout, parent,false);
        RecyclerView.ViewHolder holder = new OrderItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //new ImageLoaderManager((BookedForReviewActivity)mContext).setImageFromUrl(objs.get(position).getProductImg(), ((OrderItemHolder) holder).storePic, "users", mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), false, false);
        ((OrderItemHolder) holder).storePic.setImageBitmap(CommonLib.getBitmap(mContext, R.drawable.ic_home_store, mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size)));
        SpannableString string = new SpannableString("Store Address "+objs.get(position).getVendorTimeObj().getLine1()+" "+objs.get(position).getVendorTimeObj().getPincode()+" Pincode-"+objs.get(position).getVendorTimeObj().getCity());
        string.setSpan(new RelativeSizeSpan(1.1f),0,14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new StyleSpan(Typeface.BOLD),0,14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.heading_text_color)), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((OrderItemHolder)holder).storeAddress.setText(string);
        ((OrderItemHolder)holder).storeName.setText(objs.get(position).getVendorTimeObj().getVendor());


       /* ColorFilter filter = new LightingColorFilter(
                objs.get(position).getStatus().equalsIgnoreCase("CANCEL") ?mContext.getResources().getColor(R.color.red_text_color) : mContext.getResources().getColor(R.color.button_green),
                objs.get(position).getStatus().equalsIgnoreCase("CANCEL") ?mContext.getResources().getColor(R.color.red_text_color) : mContext.getResources().getColor(R.color.button_green));
        ((OrderItemHolder) holder).bookingStatus.getBackground().setColorFilter(filter);*/

        if(objs.get(position).getStatus().equalsIgnoreCase("CANCEL")){
            ((OrderItemHolder) holder).bookingStatus.setVisibility(View.VISIBLE);
            ((OrderItemHolder) holder).bookingStatus.setText("CANCELLED");
            ((OrderItemHolder) holder).cancelBooking.setVisibility(View.GONE);
        }else if(objs.get(position).getStatus().equalsIgnoreCase("EXPIRED")){
            ((OrderItemHolder) holder).bookingStatus.setVisibility(View.VISIBLE);
            ((OrderItemHolder) holder).bookingStatus.setText(objs.get(position).getStatus());
            ((OrderItemHolder) holder).cancelBooking.setVisibility(View.GONE);
        }else {
            ((OrderItemHolder) holder).bookingStatus.setVisibility(View.GONE);
            ((OrderItemHolder) holder).cancelBooking.setVisibility(View.VISIBLE);

            ((OrderItemHolder) holder).cancelBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCancelClick(position, objs.get(position).getVendorTimeObj().getBook_product_id());
                    }
                }
            });
        }

        ((OrderItemHolder) holder).callCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + objs.get(position).getVendorTimeObj().getPhone()));
                mContext.startActivity(callIntent);
            }
        });

        ((OrderItemHolder) holder).getDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + objs.get(position).getVendorTimeObj().getLatitude()+ "," + objs.get(position).getVendorTimeObj().getLongitude() + "(" + objs.get(position).getVendorTimeObj().getVendor() + ")");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
                /*Intent intent = new Intent(mContext, MapPage.class);
                intent.putExtra("lat", product.getVendor().getReg_add().getLocation().getLatitude());
                intent.putExtra("lon", product.getVendor().getReg_add().getLocation().getLongitude());
                intent.putExtra("name", product.getVendor().getReg_add().getLocation().getName());
                mContext.startActivity(intent);*/
            }
        });


        /*ColorFilter filter = new LightingColorFilter(
                objs.get(position).getStatus().equalsIgnoreCase("CANCEL") ?mContext.getResources().getColor(R.color.red_text_color) : mContext.getResources().getColor(R.color.button_green),
                objs.get(position).getStatus().equalsIgnoreCase("CANCEL") ?mContext.getResources().getColor(R.color.red_text_color) : mContext.getResources().getColor(R.color.button_green));
*/

       // ((OrderItemHolder) holder).bookingStatus.getBackground().setColorFilter(filter);

    }

    public void removePos(int pos){
        objs.get(pos).setStatus("CANCEL");
        //objs.remove(pos);
        notifyDataSetChanged();
    }

    public Object getItem(int pos){
        if(objs!=null )
            return objs.get(pos);
        return null;
    }

    public void changeAddBtnText(int id){
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return objs.size();
    }

    public class OrderItemHolder extends RecyclerView.ViewHolder{
        TextView  storeName,storeAddress,cancelBooking,bookingStatus;
        ImageView storePic;
        LinearLayout btnLayout,callCustomer,getDirections;
        public OrderItemHolder(View itemView) {
            super(itemView);
            storeName = (TextView)itemView.findViewById(R.id.store_name);
            storeAddress = (TextView)itemView.findViewById(R.id.store_address);
            bookingStatus = (TextView)itemView.findViewById(R.id.booking_status);
            storePic = (ImageView)itemView.findViewById(R.id.store_img);
            cancelBooking = (TextView)itemView.findViewById(R.id.cancel_booking);
            callCustomer = (LinearLayout)itemView.findViewById(R.id.call_layout);
            getDirections = (LinearLayout)itemView.findViewById(R.id.direction_layout);
        }
    }

    OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    public interface OnItemClickListener {
        void onCancelClick(int pos , int bookProductId);
        void addToCartClick(int pos,int id);
        void moveToCartActivity();
    }
}
