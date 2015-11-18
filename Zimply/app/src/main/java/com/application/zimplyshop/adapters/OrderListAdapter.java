package com.application.zimplyshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.ProductDetailsActivity;
import com.application.zimplyshop.activities.PurchaseListActivity;
import com.application.zimplyshop.baseobjects.IndividualOrderItemObj;
import com.application.zimplyshop.baseobjects.OrderItemObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 10/9/2015.
 */
public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context mContext;

    ArrayList<OrderItemObj> objs;

    boolean isFooterRemoved;

    private int TYPE_LOADER = 0;
    private int TYPE_DATA = 1;


    public OrderListAdapter(Context context){
        this.mContext = context;
        objs = new ArrayList<>();
        isFooterRemoved = true;
    }


    public void changeStatus(int position,int orderId,int status){
        for(IndividualOrderItemObj obj :objs.get(position).getOrderitem()){
            if(obj.getId() == orderId){
                if(status == AppConstants.CANCEL_ORDER){
                    obj.setStatus("Cancelled");
                    obj.setCancel_orderitem(false);
                }else{
                    obj.setStatus("Return in process");
                    obj.setReturn_orderitem(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void addData(ArrayList<OrderItemObj> objs) {
        ArrayList<OrderItemObj> newObjs = new ArrayList<OrderItemObj>(objs);
        this.objs.addAll(this.objs.size(), newObjs);
        notifyDataSetChanged();
    }

    public void removeItem() {
        isFooterRemoved = true;
        notifyItemRemoved(objs.size());
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == TYPE_DATA) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_parent, parent,false);
            holder = new OrderItemHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_footer_layout, parent,false);
            holder = new LoadingViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(getItemViewType(position) == TYPE_DATA) {
            ((OrderItemHolder) holder).orderPrice.setText(mContext.getString(R.string.rs_text) + " " + objs.get(position).getTotal_price());
            ((OrderItemHolder) holder).orderDate.setText("Placed on "+ TimeUtils.getTimeStampDate(objs.get(position).getOrdered_on(), TimeUtils.DATE_TYPE_DAY_MON_DD_YYYY));
            ((OrderItemHolder) holder).orderId.setText("Order Id: "+objs.get(position).getOrder_id());
           /* ((OrderItemHolder) holder).orderAddress.setText(objs.get(position).getShipping_address().getName() + "\n" +
                    objs.get(position).getShipping_address().getLine1() + "\n" +
                    objs.get(position).getShipping_address().getPhone());*/
            ((OrderItemHolder) holder).orderAddress.setText(objs.get(position).getShipping_address().getLine1());
            ((OrderItemHolder) holder).orderItemContainer.removeAllViews();
            for (final IndividualOrderItemObj obj : objs.get(position).getOrderitem()) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.order_list_order_item_layout, null);
                new ImageLoaderManager((PurchaseListActivity) mContext).setImageFromUrl(obj.getImage(), ((ImageView) view.findViewById(R.id.product_img)), "", mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), false, false);
                ((TextView) view.findViewById(R.id.product_name)).setText(obj.getName());
                ((TextView) view.findViewById(R.id.product_price)).setText(mContext.getString(R.string.rs_text) + " " +obj.getItem_price());
                ((TextView) view.findViewById(R.id.product_qty)).setText(Html.fromHtml("Qty: " + "<font color=#76b082>" + obj.getQty() + "</font>"));
                ((TextView) view.findViewById(R.id.product_status)).setText(obj.getStatus());
                ((TextView) view.findViewById(R.id.product_delivery_date)).setText("Delivery by:"+TimeUtils.getTimeStampDate(obj.getEstimated_delivery(),TimeUtils.DATE_TYPE_DAY_MON_DD_YYYY));
                //((TextView)view.findViewById(R.id.product_delivery_date)).setText(Html.fromHtml("Delivery By: "+));
                ((RelativeLayout)view.findViewById(R.id.item_info_parent)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                        intent.putExtra("slug",obj.getSlug());
                        intent.putExtra("id", (long)obj.getId());
                        mContext.startActivity(intent);
                    }
                });
                //  ((TextView) view.findViewById(R.id.product_status)).setBackgroundColor(Color.parseColor(obj.getColor()));
                ((OrderItemHolder) holder).orderItemContainer.addView(view);
                if(obj.isCancel_orderitem()){
                    view.findViewById(R.id.status_btn).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.status_btn)).setText("Cancel");
                }else if(obj.isReturn_orderitem()){
                    view.findViewById(R.id.status_btn).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.status_btn)).setText("Return");
                }else if(!obj.getStatus().equalsIgnoreCase("Cancelled")){
                    view.findViewById(R.id.status_btn).setVisibility(View.GONE);
                    //((TextView) view.findViewById(R.id.status_btn)).setText("Track");

                }else{
                    view.findViewById(R.id.status_btn).setVisibility(View.GONE);
                }

                view.findViewById(R.id.status_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (obj.isCancel_orderitem()) {
                            mListener.onCancelClick(position, obj.getId());
                        } else if (obj.isReturn_orderitem()) {
                            mListener.onReturnClick(position, obj.getId());
                        } else {

                        }

                    }
                });


            }
            /*((OrderItemHolder) holder).orderDate.setText("Placed on "+TimeUtils.getTimeStampDate(objs.get(position).getOrdered_on(),TimeUtils.DATE_TYPE_DAY_MON_DD_YYYY));
            ((OrderItemHolder) holder).orderPrice.setText(mContext.getString(R.string.rs_text) + " 2795" );
            ((OrderItemHolder) holder).orderId.setText("Order Id: "+objs.get(position).getOrder_id());
            ((OrderItemHolder) holder).orderAddress.setText("Umesh Lohani" + "\n" +
                    "H.No. 693 , Type IV, Laxmi Bai Nagar, New Delhi"+ "\n" +
                    "9810206554");
            for (int i=0;i<4;i++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.order_list_order_item_layout, null);
                new ImageLoaderManager((PurchaseListActivity) mContext).setImageFromUrl(objs.get(0).getOrderitem().get(0).getImage(), ((ImageView) view.findViewById(R.id.product_img)), "", mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), false, false);
                ((TextView) view.findViewById(R.id.product_name)).setText("Sleep Well Sofa set");
                ((TextView) view.findViewById(R.id.product_price)).setText(mContext.getString(R.string.rs_text) + " 2975");
                ((TextView) view.findViewById(R.id.product_qty)).setText(Html.fromHtml("Qty: "+"<font color=#76b082>"+"2"+"</font>"));
                ((TextView) view.findViewById(R.id.product_status)).setText(objs.get(0).getOrderitem().get(0).getStatus());
                //((TextView)view.findViewById(R.id.status_btn)).setText(obj);
                ((OrderItemHolder) holder).orderItemContainer.addView(view);
            }*/
        }else{

        }
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

    @Override
    public int getItemViewType(int position) {
        if (position == objs.size()) {
            return TYPE_LOADER;
        } else {
            return TYPE_DATA;
        }

    }

    public class OrderItemHolder extends RecyclerView.ViewHolder{
        TextView orderId , orderDate,orderPrice,orderAddress;
        LinearLayout orderItemContainer;
        public OrderItemHolder(View itemView) {
            super(itemView);
            orderId = (TextView)itemView.findViewById(R.id.order_number);
            orderDate = (TextView)itemView.findViewById(R.id.order_date);
            orderAddress = (TextView)itemView.findViewById(R.id.order_address);
            orderPrice = (TextView)itemView.findViewById(R.id.order_price);
            orderItemContainer = (LinearLayout)itemView.findViewById(R.id.order_items);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View view) {
            super(view);
        }
    }

    OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public interface OnItemClickListener{
        void onCancelClick(int position,int childPos);
        void onReturnClick(int position,int childPos);
    }
}
