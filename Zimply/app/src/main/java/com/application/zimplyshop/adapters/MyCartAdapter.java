package com.application.zimplyshop.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.widgets.CustomTextView;

/**
 * Created by Saurabh on 09-10-2015.
 */
public class MyCartAdapter extends RecyclerView.Adapter {
    Context context;
    cartEditListener mListener;
    CartObject cartObject;
  //  long shippingCharges = 0;
    float totalPrice = 0;

    int ITEM_TYPE_CART_ITEM = 1, ITEM_TYPE_SUMMARY = 0;

    public MyCartAdapter(Context context, CartObject cartObject) {
        this.context = context;
        this.cartObject = cartObject;
    }

    @Override
    public int getItemViewType(int position) {

        if (position <= cartObject.getCart().getDetail().size() - 1)
            return ITEM_TYPE_CART_ITEM;
        else return ITEM_TYPE_SUMMARY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CART_ITEM)
            return new CartItemHolder(LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false));
        else
            return new SummaryHolder(LayoutInflater.from(context).inflate(R.layout.cart_summary_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder defaultHolder, final int position) {
        if (position <= cartObject.getCart().getDetail().size() - 1) {
            //shippingCharges = shippingCharges + cartObject.getCart().getDetail().get(position).getShipping_charges();
            final CartItemHolder holder = (CartItemHolder) defaultHolder;
            holder.name.setText(cartObject.getCart().getDetail().get(position).getName());
            Resources r = context.getResources();

            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, r.getDisplayMetrics());
            new ImageLoaderManager((Activity) context).setImageFromUrl(cartObject.getCart().getDetail().get(position).getImage()
                    , holder.product_image, "", (int) px, (int) px, false, false);
            holder.price.setText(context.getResources().getString(R.string.Rs) + " " + cartObject.getCart().getDetail().get(position).getPrice());

            holder.quantity.setText(cartObject.getCart().getDetail().get(position).getQuantity());
            holder.subTotal.setText(context.getResources().getString(R.string.Rs)+" " + cartObject.getCart().getDetail().get(position).getPrice());
            holder.shippingPrice.setText(cartObject.getCart().getDetail().get(position).getShipping_charges()==0?"Free":
                    context.getString(R.string.rs_text)+" "+cartObject.getCart().getDetail().get(position).getShipping_charges());
            holder.totalPrice.setText(context.getResources().getString(R.string.Rs)+" "+(cartObject.getCart().getDetail().get(position).getShipping_charges()==0? (Integer.parseInt(cartObject.getCart().getDetail().get(position).getQuantity())*Double.parseDouble(cartObject.getCart().getDetail().get(position).getPrice())):((Integer.parseInt(cartObject.getCart().getDetail().get(position).getQuantity())*Double.parseDouble(cartObject.getCart().getDetail().get(position).getPrice()))+cartObject.getCart().getDetail().get(position).getShipping_charges())));
            holder.isOnlineText.setText(Html.fromHtml("<b>Online Payment"+"</b>" +"<font color=#B5CA01> Available"+"</font>"));
            changeDrawableLeft(holder.isOnlineText, R.drawable.ic_tick);
            if(cartObject.getCart().getDetail().get(position).is_o2o()){
                holder.isCocText.setVisibility(View.VISIBLE);
                holder.isCocText.setText(Html.fromHtml("<b>Cash-at-Counter"+"</b>" +"<font color=#B5CA01> Available"+"</font>"));
                changeDrawableLeft(holder.isCocText, R.drawable.ic_tick);
                holder.buyOfflineTag.setVisibility(View.VISIBLE);
            }else{
                holder.isCocText.setVisibility(View.GONE);
                changeDrawableLeft(holder.isCocText, R.drawable.ic_cross_red);
                holder.buyOfflineTag.setVisibility(View.GONE);
            }

            holder.cancelCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CommonLib.isNetworkAvailable(context)){
                        mListener.itemDeleted(position);
                    }else{
                        Toast.makeText(context,"No network available",Toast.LENGTH_SHORT).show();
                    }
                }
            });



            holder.quantityView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CommonLib.isNetworkAvailable(context)) {
                        final Dialog dialog = new Dialog(context);

                        View view = LayoutInflater.from(context).inflate(R.layout.select_quantity_dialog_view, null);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(view);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.simple_list_item, R.id.text1);
                        for (int i = 0; i < cartObject.getCart().getDetail().get(position).getAvailable_quantity(); i++) {
                            adapter.add((i +1)+ "");
                        }
                        ListView listView = ((ListView) dialog.findViewById(R.id.listview));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int quantity, long id) {
                                // holder.quantity.setText(quantity + 1 + "");
                                dialog.dismiss();
                                mListener.itemQuantityChanged(position, quantity + 1);

                            }
                        });
                        listView.setAdapter(adapter);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                    }else{
                        Toast.makeText(context,"No network available",Toast.LENGTH_SHORT).show();
                    }
                }

            });
            holder.cancelCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CommonLib.isNetworkAvailable(context)) {
                        mListener.itemDeleted(position);
                    }else{
                        Toast.makeText(context,"No network available",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            SummaryHolder summaryHolder = (SummaryHolder) defaultHolder;
            totalPrice = Float.parseFloat(cartObject.getCart().getTotal_price());
            float price = Float.parseFloat(cartObject.getCart().getPrice());
            if (totalPrice == 0) {
                summaryHolder.totalSum.setText(context.getResources().getString(R.string.Rs) + cartObject.getCart().getPrice());
                summaryHolder.totalPayment.setText(context.getResources().getString(R.string.Rs) + cartObject.getCart().getTotal_price());
            }
            summaryHolder.totalSum.setText(context.getResources().getString(R.string.Rs) + price);
            summaryHolder.totalPayment.setText(context.getResources().getString(R.string.Rs) + totalPrice);
            summaryHolder.shipping.setText((cartObject.getCart().getTotal_shipping().equalsIgnoreCase("0")) ? "Free" : context.getResources().getString(R.string.Rs) + cartObject.getCart().getTotal_shipping());
            /*summaryHolder.checkOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CommonLib.isNetworkAvailable(context)) {
                        mListener.checkOut(position);
                    }else{
                        Toast.makeText(context,"No network available",Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
        }
    }

    public void changeDrawableLeft(CustomTextView textview , int drawableId){
        Drawable drawable = context.getResources().getDrawable(drawableId);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        textview.setCompoundDrawables(drawable,null,null,null);
    }

    @Override
    public int getItemCount() {
        if (cartObject.getCart().getDetail() != null)
            return cartObject.getCart().getDetail().size() + 1;
        return 0;
    }

    public void setCartEditListener(cartEditListener mListener) {
        this.mListener = mListener;
    }

    public interface cartEditListener {
        void checkOut(int position);

        void itemQuantityChanged(int position, int quantity);

        void itemDeleted(int position);

        void changeAddress();
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView price, name, delivery_date, quantity;
        ImageView product_image, cancelCartItem,buyOfflineTag;
        View quantityView;
        CustomTextView subTotal,shippingPrice,totalPrice,isCocText,isOnlineText;

        public CartItemHolder(View itemView) {
            super(itemView);
            //itemView.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.white)));
            price = (TextView) itemView.findViewById(R.id.product_price);
            quantity = (TextView) itemView.findViewById(R.id.cart_quantity);
            delivery_date = (TextView) itemView.findViewById(R.id.expected_delivery_date_text);
            quantityView = itemView.findViewById(R.id.quantity_view);
            name = (TextView) itemView.findViewById(R.id.product_name);
            product_image = (ImageView) itemView.findViewById(R.id.product_image);
            cancelCartItem = (ImageView) itemView.findViewById(R.id.cancel_cart_item);

            subTotal = (CustomTextView)itemView.findViewById(R.id.subtotal);
            shippingPrice = (CustomTextView)itemView.findViewById(R.id.shipping_charges);
            totalPrice = (CustomTextView)itemView.findViewById(R.id.total_payment);
            isCocText = (CustomTextView)itemView.findViewById(R.id.is_coc_text);
            buyOfflineTag = (ImageView)itemView.findViewById(R.id.buy_offline_tag);
            isOnlineText = (CustomTextView)itemView.findViewById(R.id.is_online_text);
        }
    }


    public class SummaryHolder extends RecyclerView.ViewHolder {
        TextView totalPayment, checkCoupon, shipping, totalSum;

        public SummaryHolder(View itemView) {
            super(itemView);
            totalPayment = (TextView) itemView.findViewById(R.id.total_payment);

            shipping = (TextView) itemView.findViewById(R.id.shipping_charges);
            totalSum = (TextView) itemView.findViewById(R.id.total_sum);
        }
    }
}
