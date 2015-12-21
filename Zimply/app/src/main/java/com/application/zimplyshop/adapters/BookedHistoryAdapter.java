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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.BookedForReviewActivity;
import com.application.zimplyshop.activities.BookingStoreProductListingActivity;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.LatestBookingObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.ImageLoaderManager;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/14/2015.
 */
public class BookedHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    public int TYPE_DATA = 0;

    public int TYPE_LOADER = 1;


    ArrayList<LatestBookingObject> objs;

    boolean isFooterRemoved;

    Context mContext;
    public BookedHistoryAdapter(Context context ){
        mContext = context;
        this.objs=new ArrayList<>();
    }

    public void addData(ArrayList<LatestBookingObject> objs) {
        ArrayList<LatestBookingObject> newObjs = new ArrayList<LatestBookingObject>(objs);
        this.objs.addAll(this.objs.size(), newObjs);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == TYPE_DATA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booked_item_layout, parent, false);
            holder = new OrderItemHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_footer_layout, parent, false);
            holder = new LoadingViewHolder(view);
        }
        return holder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(getItemViewType(position) == TYPE_DATA) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.booking_card_height));
            ((OrderItemHolder) holder).bookCard.setLayoutParams(lp);

            new ImageLoaderManager((BookedForReviewActivity) mContext).setImageFromUrl(objs.get(position).getProduct().getImage(), ((OrderItemHolder) holder).storePic, "users", mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), false, false);
            //((OrderItemHolder) holder).storePic.setImageBitmap(CommonLib.getBitmap(mContext, R.drawable.ic_home_store, mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size)));
            SpannableString string = new SpannableString("Store Address " + objs.get(position).getVendor().getAddress().getLine1() + " " + objs.get(position).getVendor().getAddress().getCity() + " Pincode-" + objs.get(position).getVendor().getAddress().getPincode());
            string.setSpan(new RelativeSizeSpan(1.1f), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            string.setSpan(new StyleSpan(Typeface.BOLD), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            string.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.heading_text_color)), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((OrderItemHolder) holder).storeAddress.setText(string);
            ((OrderItemHolder) holder).storeName.setText(objs.get(position).getVendor().getName());


       /* ColorFilter filter = new LightingColorFilter(
                objs.get(position).getStatus().equalsIgnoreCase("CANCEL") ?mContext.getResources().getColor(R.color.red_text_color) : mContext.getResources().getColor(R.color.button_green),
                objs.get(position).getStatus().equalsIgnoreCase("CANCEL") ?mContext.getResources().getColor(R.color.red_text_color) : mContext.getResources().getColor(R.color.button_green));
        ((OrderItemHolder) holder).bookingStatus.getBackground().setColorFilter(filter);*/

            if (objs.get(position).getBook().getBooking_status().equalsIgnoreCase("CANCEL")) {
                ((OrderItemHolder) holder).bookingStatus.setVisibility(View.VISIBLE);
                ((OrderItemHolder) holder).bookingStatus.setText("CANCELLED");
                ((OrderItemHolder) holder).cancelBooking.setVisibility(View.GONE);
            } else if (objs.get(position).getBook().getBooking_status().equalsIgnoreCase("EXPIRED")) {
                ((OrderItemHolder) holder).bookingStatus.setVisibility(View.VISIBLE);
                ((OrderItemHolder) holder).bookingStatus.setText(objs.get(position).getBook().getBooking_status());
                ((OrderItemHolder) holder).cancelBooking.setVisibility(View.GONE);
            } else {
                ((OrderItemHolder) holder).bookingStatus.setVisibility(View.GONE);
                ((OrderItemHolder) holder).cancelBooking.setVisibility(View.VISIBLE);

                ((OrderItemHolder) holder).cancelBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onCancelClick(position, objs.get(position).getBook().getId());
                        }
                    }
                });
            }

            ((OrderItemHolder) holder).callCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + objs.get(position).getVendor().getAddress().getPhone()));
                    mContext.startActivity(callIntent);
                }
            });

            ((OrderItemHolder) holder).getDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + objs.get(position).getVendor().getAddress().getLatitude() + "," + objs.get(position).getVendor().getAddress().getLongitude() + "(" + objs.get(position).getVendor().getName() + ")");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);

                }
            });

            ((OrderItemHolder) holder).bookCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BookingStoreProductListingActivity.class);
                    intent.putExtra("booked_obj", objs.get(position).getProduct());
                    intent.putExtra("hide_filter", true);
                    intent.putExtra("vendor_id", objs.get(position).getVendor().getId());
                    intent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
                    intent.putExtra("vendor_name", objs.get(position).getVendor().getName());
                    mContext.startActivity(intent);
                }
            });
        /*ColorFilter filter = new LightingColorFilter(
                objs.get(position).getStatus().equalsIgnoreCase("CANCEL") ?mContext.getResources().getColor(R.color.red_text_color) : mContext.getResources().getColor(R.color.button_green),
                objs.get(position).getStatus().equalsIgnoreCase("CANCEL") ?mContext.getResources().getColor(R.color.red_text_color) : mContext.getResources().getColor(R.color.button_green));
*/

            // ((OrderItemHolder) holder).bookingStatus.getBackground().setColorFilter(filter);
        }else{

        }
    }

    public void removePos(int pos){
        objs.get(pos).getBook().setBooking_status("CANCEL");
        //objs.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == objs.size()) {
            return TYPE_LOADER;
        } else {
            return TYPE_DATA;
        }
    }

    public Object getItem(int pos){
        if(objs!=null )
            return objs.get(pos);
        return null;
    }

    public void removeItem() {
        isFooterRemoved = true;
        notifyItemRemoved(objs.size());
    }

    public void changeAddBtnText(int id){
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (objs != null) {
            if (isFooterRemoved) {
                return objs.size();
            } else {
                return objs.size() + 1;
            }
        }
        return 0;
    }

    public class OrderItemHolder extends RecyclerView.ViewHolder{
        TextView  storeName,storeAddress,cancelBooking,bookingStatus;
        ImageView storePic;
        LinearLayout btnLayout,callCustomer,getDirections;
        RelativeLayout bookCard;
        public OrderItemHolder(View itemView) {
            super(itemView);
            storeName = (TextView)itemView.findViewById(R.id.store_name);
            storeAddress = (TextView)itemView.findViewById(R.id.store_address);
            bookingStatus = (TextView)itemView.findViewById(R.id.booking_status);
            storePic = (ImageView)itemView.findViewById(R.id.store_img);
            cancelBooking = (TextView)itemView.findViewById(R.id.cancel_booking);
            callCustomer = (LinearLayout)itemView.findViewById(R.id.call_layout);
            getDirections = (LinearLayout)itemView.findViewById(R.id.direction_layout);
            bookCard = (RelativeLayout)itemView.findViewById(R.id.item_info_parent);
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

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View view) {
            super(view);

        }

    }
}
