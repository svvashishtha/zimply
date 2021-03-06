package com.application.zimply.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
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

import com.application.zimply.R;
import com.application.zimply.baseobjects.AddressObject;
import com.application.zimply.baseobjects.CartObject;
import com.application.zimply.managers.ImageLoaderManager;
import com.application.zimply.utils.CommonLib;

/**
 * Created by Saurabh on 09-10-2015.
 */
public class CartItemListAdapter extends RecyclerView.Adapter {
    Context context;
    cartEditListener mListener;
    CartObject cartObject;
    //long shippingCharges = 0;
    float totalPrice = 0;
    AddressObject billingAddress, shippingAddress;
    int ITEM_TYPE_CART_ITEM = 1, ITEM_TYPE_SUMMARY = 0, ITEM_TYPE_ADDRESS = 2;

    public CartItemListAdapter(Context context, CartObject cartObject, AddressObject billingAddress,
                               AddressObject shippingAddress) {
        this.context = context;
        this.cartObject = cartObject;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 )//|| position == 1)
            return ITEM_TYPE_ADDRESS;
        else if (position <= (cartObject.getCart().getDetail().size()))
            return ITEM_TYPE_CART_ITEM;
        else return ITEM_TYPE_SUMMARY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CART_ITEM)
            return new CartItemHolder(LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false));
        else if (viewType == ITEM_TYPE_SUMMARY)
            return new SummaryHolder(LayoutInflater.from(context).inflate(R.layout.cart_summary_layout, parent, false));
        else
            return new AddressHolder(LayoutInflater.from(context).inflate(R.layout.deafult_address_snippet, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder defaultHolder, final int position) {
        if (position == 0) {
            AddressHolder holder = (AddressHolder) defaultHolder;


            if (position == -1) {
//                ((TextView) holder.itemView.findViewById(R.id.delivery_address_text)).setText("Billing Address");
//                holder.phone.setText(billingAddress.getPhone()+"");
//                String addressString = billingAddress.getName() + ", " + billingAddress.getLine1() +
//                        ", " + billingAddress.getLine2() + ", " + billingAddress.getCity() + ", " + billingAddress.getPincode();
//
//                holder.address.setText(addressString);
//                holder.change.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mListener.changeAddressBilling();
//                    }
//                });
            } else if (position == 0) {
                ((TextView) holder.itemView.findViewById(R.id.delivery_address_text)).setText("Shipping Address");
                if(shippingAddress!=null ) {
                    holder.phone.setText((shippingAddress.getPhone() != null) ? shippingAddress.getPhone() : "");
                    String addressString = shippingAddress.getName() + ", " + shippingAddress.getLine1() +
                            (shippingAddress.getLine2().trim().length() > 0 ? ", " + shippingAddress.getLine2() : "") + ", " + shippingAddress.getCity() + ", " + shippingAddress.getPincode();

                    holder.address.setText(addressString);
                }
                holder.change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(CommonLib.isNetworkAvailable(context)) {
                            mListener.changeAddressShipping();
                        }else{
                            Toast.makeText(context, "No network available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else if (position > 0 && position <= (cartObject.getCart().getDetail().size())) {
            //shippingCharges = shippingCharges + cartObject.getCart().getDetail().get(position - 2).getShipping_charges();
            final CartItemHolder holder = (CartItemHolder) defaultHolder;
            holder.name.setText(cartObject.getCart().getDetail().get(position - 1).getName());
            Resources r = context.getResources();

            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, r.getDisplayMetrics());
            new ImageLoaderManager((Activity) context).setImageFromUrl(cartObject.getCart().getDetail().get(position - 1).getImage()
                    , holder.product_image, "", (int) px, (int) px, false, false);
            holder.price.setText(context.getResources().getString(R.string.Rs) + cartObject.getCart().getDetail().get(position - 1).getPrice());

            holder.quantity.setText(cartObject.getCart().getDetail().get(position - 1).getQuantity());

            holder.cancelCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CommonLib.isNetworkAvailable(context)) {
                        mListener.itemDeleted(position - 1);
                    }else{
                        Toast.makeText(context,"No network available",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            /*holder..setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(holder.quantity.getText().toString()) + 1 < cartObject.getCart().getDetail().get(position).getAvailable_quantity()) {
                        holder.quantity.setText(Integer.parseInt(holder.quantity.getText().toString()) + 1 + "");
                        cartObject.getCart().getDetail().get(position).setQuantity(holder.quantity.getText().toString());
                        totalPrice += Float.parseFloat(cartObject.getCart().getDetail().get(position).getPrice());
                        mListener.checkOut(position);
                    }
                }
            });
             holder.negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(holder.quantity.getText().toString()) > 1) {
                        holder.quantity.setText(Integer.parseInt(holder.quantity.getText().toString()) - 1 + "");
                        cartObject.getCart().getDetail().get(position).setQuantity(holder.quantity.getText().toString());
                        mListener.itemQuantityChanged(position);
                        totalPrice -= Float.parseFloat(cartObject.getCart().getDetail().get(position).getPrice());
                    }

                }
            });*/

            holder.quantityView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CommonLib.isNetworkAvailable(context)) {
                        final Dialog dialog = new Dialog(context);

                        View view = LayoutInflater.from(context).inflate(R.layout.select_quantity_dialog_view, null);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(view);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.simple_list_item, R.id.text1);
                        for (int i = 1; i < cartObject.getCart().getDetail().get(position - 1).getAvailable_quantity(); i++) {
                            adapter.add(i + "");
                        }
                        ListView listView = ((ListView) dialog.findViewById(R.id.listview));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int quantity, long id) {
                                //holder.quantity.setText(quantity + 1 + "");
                                dialog.dismiss();
                                mListener.itemQuantityChanged(position - 1, quantity + 1);
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
                        mListener.itemDeleted(position - 1);
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
            summaryHolder.shipping.setText((Float.parseFloat(cartObject.getCart().getTotal_shipping()) == 0)?"Free":(context.getResources().getString(R.string.Rs) + cartObject.getCart().getTotal_shipping()));
            summaryHolder.checkOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CommonLib.isNetworkAvailable(context)) {
                        mListener.checkOut(position);
                    }else{
                        Toast.makeText(context,"No network available",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {

        if (cartObject.getCart().getDetail() != null)
            return cartObject.getCart().getDetail().size() + 2;
        return 0;
    }

    public void setCartEditListener(cartEditListener mListener) {
        this.mListener = mListener;
    }

    public interface cartEditListener {
        void checkOut(int position);

        void itemQuantityChanged(int position, int quantity);

        void itemDeleted(int position);

        void changeAddressBilling();

        void changeAddressShipping();
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView price, name, delivery_date, quantity;
        ImageView product_image, cancelCartItem;
        //ImageButton positive, negative;
        View quantityView;

        public CartItemHolder(View itemView) {
            super(itemView);
            itemView.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.white)));
            price = (TextView) itemView.findViewById(R.id.product_price);
            quantity = (TextView) itemView.findViewById(R.id.cart_quantity);
            delivery_date = (TextView) itemView.findViewById(R.id.expected_delivery_date_text);
            quantityView = itemView.findViewById(R.id.quantity_view);
            name = (TextView) itemView.findViewById(R.id.product_name);
            product_image = (ImageView) itemView.findViewById(R.id.product_image);
            //positive = (ImageButton) itemView.findViewById(R.id.cart_positive);
            //negative = (ImageButton) itemView.findViewById(R.id.cart_negative);
            cancelCartItem = (ImageView) itemView.findViewById(R.id.cancel_cart_item);
        }
    }

    public class AddressHolder extends RecyclerView.ViewHolder {
        TextView address, phone, change;

        public AddressHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.address_snippet_root)
                    .setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.white)));
            address = (TextView) itemView.findViewById(R.id.address);
            phone = (TextView) itemView.findViewById(R.id.phone);
            change = (TextView) itemView.findViewById(R.id.change);
        }
    }

    public class SummaryHolder extends RecyclerView.ViewHolder {
        TextView totalPayment, checkOut, checkCoupon, shipping, totalSum;

        public SummaryHolder(View itemView) {
            super(itemView);
            totalPayment = (TextView) itemView.findViewById(R.id.total_payment);
            checkOut = (TextView) itemView.findViewById(R.id.check_out);
            shipping = (TextView) itemView.findViewById(R.id.shipping_charges);
            totalSum = (TextView) itemView.findViewById(R.id.total_sum);
        }
    }
}
