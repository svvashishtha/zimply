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
import com.application.zimplyshop.activities.CashOnCounterOrderCompletionActivity;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.managers.ImageLoaderManager;

/**
 * Created by Umesh Lohani on 12/10/2015.
 */
public class CocOrderCompletionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;

    int TYPE_DATA=1;

    int TYPE_HEADER=2;

    CartObject cartObj;
    String orderId;

    boolean isCoc;

    AddressObject addressObj;

    public CocOrderCompletionAdapter(Context context,CartObject cartObj,String orderId,boolean isCoc,AddressObject addressObj){
        this.mContext = context;
        this.cartObj= cartObj;
        this.orderId = orderId;
        this.isCoc = isCoc;
        this.addressObj = addressObj;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == TYPE_DATA){
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.coc_ordercompletion_item_layout,parent,false);
            holder = new ProductViewHolder(view);
        }else{
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.coc_ordercompletion_header,parent,false);
            holder = new HeaderViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        if(getItemViewType(position) ==TYPE_DATA){
            ((ProductViewHolder)holder).productName.setText(cartObj.getCart().getDetail().get(position-1).getName());
            ((ProductViewHolder)holder).productPrice.setText(mContext.getString(R.string.rs_text) + " " + cartObj.getCart().getDetail().get(position - 1).getPrice());
            ((ProductViewHolder)holder).productQty.setText("Qty: " + cartObj.getCart().getDetail().get(position - 1).getQuantity());
            SpannableString string = new SpannableString("Store Address "+cartObj.getCart().getDetail().get(position - 1).getVendor().getReg_add().getLine1()
                    +", "+(cartObj.getCart().getDetail().get(position - 1).getVendor().getReg_add().getCity()!=null?cartObj.getCart().getDetail().get(position - 1).getVendor().getReg_add().getCity():"")
                    +"\nPincode:"+cartObj.getCart().getDetail().get(position - 1).getVendor().getReg_add().getPincode());
            string.setSpan(new RelativeSizeSpan(1.1f), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            string.setSpan(new StyleSpan(Typeface.BOLD), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            string.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.heading_text_color)), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((ProductViewHolder) holder).storeAddress.setText(string);
            new ImageLoaderManager((CashOnCounterOrderCompletionActivity)mContext).setImageFromUrl(cartObj.getCart().getDetail().get(position - 1).getImage(),
                    ((ProductViewHolder) holder).productImage, "users", mContext.getResources().getDimensionPixelSize(R.dimen.pro_cover_img_height),
                    mContext.getResources().getDimensionPixelSize(R.dimen.pro_cover_img_height), false, false);
            ((ProductViewHolder) holder).payAmount.setText(mContext.getString(R.string.rs_text)+" "+Math.round((Integer.parseInt(cartObj.getCart().getDetail().get(position - 1).getQuantity()) * Double.parseDouble(cartObj.getCart().getDetail().get(position - 1).getPrice()))));
            if(isCoc){
                ((ProductViewHolder) holder).deliveryAddressLayout.setVisibility(View.GONE);
                ((ProductViewHolder) holder).btnLayout.setVisibility(View.VISIBLE);
                ((ProductViewHolder) holder).callLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + cartObj.getCart().getDetail().get(position - 1).getVendor().getReg_add().getPhone()));
                        mContext.startActivity(callIntent);
                    }
                });
                ((ProductViewHolder)holder).directionLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + cartObj.getCart().getDetail().get(position - 1).getVendor().getReg_add().getLocation().getLatitude() + "," + cartObj.getCart().getDetail().get(position - 1).getVendor().getReg_add().getLocation().getLongitude() + "(" + cartObj.getCart().getDetail().get(position - 1).getVendor().getCompany_name() + ")");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                });
                ((ProductViewHolder) holder).toPay.setText("To Pay");
                ((ProductViewHolder) holder).payAtStore.setVisibility(View.VISIBLE);
            }else{
                ((ProductViewHolder) holder).toPay.setText("You have paid");
                ((ProductViewHolder) holder).payAtStore.setVisibility(View.GONE);
                SpannableString address = new SpannableString("Delivery Address :"+addressObj.getLine1()+", "+(addressObj.getLine2()!=null?addressObj.getLine2()+", ":"")+addressObj.getCity()+addressObj.getState()+","+"\nPincode "+addressObj.getPincode());
                address.setSpan(new RelativeSizeSpan(1.1f), 0, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                address.setSpan(new StyleSpan(Typeface.BOLD), 0, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                address.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.heading_text_color)), 0, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((ProductViewHolder) holder).deliveryAddressLayout.setVisibility(View.VISIBLE);
                ((ProductViewHolder) holder).btnLayout.setVisibility(View.GONE);
                ((ProductViewHolder) holder).deliveryAddress.setText(address);
                ((ProductViewHolder) holder).deliverTime.setText("The approximate time of delivery is 4-7 days");
            }
        }else{
            ((HeaderViewHolder) holder).orderId.setText("Order id- "+orderId);
        }
    }

    @Override
    public int getItemCount() {
        if(cartObj!=null){
            return  cartObj.getCart().getDetail().size()+1;
        }
        return 0;
    }


    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }else{
            return TYPE_DATA;
        }
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView productName,productPrice,productQty,itemNum,storeAddress,payAmount,paymentStatus,deliveryAddress,deliverTime,payAtStore,toPay;
        ImageView productImage;
        LinearLayout callLayout,directionLayout,deliveryAddressLayout,btnLayout;

        public ProductViewHolder(View itemView) {
            super(itemView);
            itemNum = (TextView)itemView.findViewById(R.id.item_no);
            productName = (TextView)itemView.findViewById(R.id.product_name);
            productPrice = (TextView)itemView.findViewById(R.id.product_price);
            productQty = (TextView)itemView.findViewById(R.id.product_qty);
            storeAddress = (TextView)itemView.findViewById(R.id.store_address);
            payAmount = (TextView)itemView.findViewById(R.id.pay_amount);
            paymentStatus =(TextView)itemView.findViewById(R.id.payment_status);
            productImage = (ImageView)itemView.findViewById(R.id.product_img);
            callLayout = (LinearLayout)itemView.findViewById(R.id.call_layout);
            directionLayout = (LinearLayout)itemView.findViewById(R.id.direction_layout);
            deliveryAddressLayout = (LinearLayout)itemView.findViewById(R.id.delivery_address_layout);
            btnLayout = (LinearLayout)itemView.findViewById(R.id.btn_layout);
            deliveryAddress = (TextView)itemView.findViewById(R.id.delivery_address);
            deliverTime = (TextView)itemView.findViewById(R.id.delivery_time);
            payAtStore = (TextView)itemView.findViewById(R.id.pay_at_store);
            toPay = (TextView)itemView.findViewById(R.id.to_pay);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView orderId;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            orderId = (TextView)itemView.findViewById(R.id.order_id);
        }
    }
}