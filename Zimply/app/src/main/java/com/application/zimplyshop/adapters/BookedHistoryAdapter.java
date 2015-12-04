package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
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
        ((OrderItemHolder) holder).storePic.setImageBitmap(CommonLib.getBitmap(mContext,R.drawable.ic_home_store,mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size),mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size)));

        ((OrderItemHolder)holder).storeAddress.setText(objs.get(position).getVendorTimeObj().getLine1()+"\n"+objs.get(position).getVendorTimeObj().getPincode()+"\nPincode-"+objs.get(position).getVendorTimeObj().getCity());
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


    public void changeAddBtnText(int id){
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return objs.size();
    }

    public class OrderItemHolder extends RecyclerView.ViewHolder{
        TextView  storeName,storeAddress,cancelBooking,bookingStatus;
        ImageView storePic,callCustomer,getDirections;

        LinearLayout btnLayout;
        public OrderItemHolder(View itemView) {
            super(itemView);
            storeName = (TextView)itemView.findViewById(R.id.store_name);
            storeAddress = (TextView)itemView.findViewById(R.id.store_address);
            bookingStatus = (TextView)itemView.findViewById(R.id.booking_status);
            storePic = (ImageView)itemView.findViewById(R.id.store_img);
            cancelBooking = (TextView)itemView.findViewById(R.id.cancel_booking);
            callCustomer = (ImageView)itemView.findViewById(R.id.call_customer);
            getDirections = (ImageView)itemView.findViewById(R.id.get_direction_customer);
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
