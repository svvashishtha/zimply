package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimplyshop.activities.BookedForReviewActivity;
import com.application.zimplyshop.baseobjects.BookedProductHistoryObject;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.utils.TimeUtils;
import com.application.zimplyshop.widgets.CustomTextView;

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        new ImageLoaderManager((BookedForReviewActivity)mContext).setImageFromUrl(objs.get(position).getProductImg(), ((OrderItemHolder) holder).itemPic, "users", mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), false, false);
        ((OrderItemHolder)holder).orderDate.setText(Html.fromHtml("Visit store by <b>" + TimeUtils.getTimeStampDate(objs.get(position).getVendorTimeObj().getCreated_on(), TimeUtils.DATE_TYPE_DAY_MON_DD_YYYY)+"</b>"));
        ((OrderItemHolder)holder).storeAddress.setText(objs.get(position).getVendorTimeObj().getLine1()+"\n"+objs.get(position).getVendorTimeObj().getCity()+"\nPincode-"+objs.get(position).getVendorTimeObj().getPincode());
        ((OrderItemHolder)holder).itemName.setText(objs.get(position).getName());
        ((OrderItemHolder)holder).storeName.setText(objs.get(position).getVendorTimeObj().getVendor());
        ((OrderItemHolder)holder).itemPrice.setText(mContext.getResources().getString(R.string.rs_text) + objs.get(position).getPrice() + "");

        if(objs.get(position).getStatus().equalsIgnoreCase("CANCEL") || objs.get(position).getStatus().equalsIgnoreCase("EXPIRED")){
            ((OrderItemHolder)holder).bookingStatus.setText("CANCELLED");
            ((OrderItemHolder)holder).cancelBooking.setVisibility(View.GONE);
            ((OrderItemHolder)holder).separatorView.setVisibility(View.GONE);
        }else{
            ((OrderItemHolder)holder).bookingStatus.setText(objs.get(position).getStatus());
            ((OrderItemHolder)holder).cancelBooking.setVisibility(View.VISIBLE);
            ((OrderItemHolder)holder).separatorView.setVisibility(View.VISIBLE);
            ((OrderItemHolder)holder).cancelBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCancelClick(position, objs.get(position).getVendorTimeObj().getBook_product_id());
                    }
                }
            });
        }

    }

    public void removePos(int pos){
        objs.get(pos).setStatus("CANCEL");
        //objs.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return objs.size();
    }

    public class OrderItemHolder extends RecyclerView.ViewHolder{
        TextView  orderDate,itemPrice,storeName,storeAddress,itemName,cancelBooking,bookingStatus;
        ImageView itemPic;
        View separatorView;
        public OrderItemHolder(View itemView) {
            super(itemView);
            separatorView = itemView.findViewById(R.id.separator);
            itemName = (TextView)itemView.findViewById(R.id.product_name);
            orderDate = (TextView)itemView.findViewById(R.id.product_delivery_date);
            storeName = (TextView)itemView.findViewById(R.id.store_name);
            storeAddress = (TextView)itemView.findViewById(R.id.store_address);
            itemPrice = (TextView)itemView.findViewById(R.id.product_price);
            itemPic = (ImageView)itemView.findViewById(R.id.product_img);
            cancelBooking = (TextView)itemView.findViewById(R.id.cancel_booking);
            bookingStatus = (CustomTextView)itemView.findViewById(R.id.booking_status);
        }
    }

    OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    public interface OnItemClickListener {
        void onCancelClick(int pos , int bookProductId);
    }
}
