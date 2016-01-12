package com.application.zimplyshop.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.NewProductDetailActivity;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.widgets.CustomTextView;
import com.google.android.gms.analytics.ecommerce.ProductAction;

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
            holder.name.setText(cartObject.getCart().getDetail().get(position).getProduct().getName());
            Resources r = context.getResources();

            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, r.getDisplayMetrics());
            new ImageLoaderManager((Activity) context).setImageFromUrl(cartObject.getCart().getDetail().get(position).getProduct().getImage()
                    , holder.product_image, "", (int) px, (int) px, false, false);
            //holder.price.setText(context.getResources().getString(R.string.Rs) + " " + cartObject.getCart().getDetail().get(position).getProduct().getPrice());


            try {
                if (cartObject.getCart().getDetail().get(position).getProduct().getMrp() != cartObject.getCart().getDetail().get(position).getProduct().getPrice()) {
                    holder.productDiscountedPrice
                            .setText(context.getString(R.string.Rs) + " "
                                    + cartObject.getCart().getDetail().get(position).getProduct().getPrice());
                    holder.productPrice.setVisibility(View.VISIBLE);
                    holder.productPrice.setText(context
                            .getString(R.string.Rs)
                            + " "
                            + cartObject.getCart().getDetail().get(position).getProduct().getMrp());
                    holder.productPrice
                            .setPaintFlags(holder.productPrice
                                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.productDiscountFactor.setVisibility(View.VISIBLE);
                    holder.productDiscountFactor.setText(cartObject.getCart().getDetail().get(position).getProduct().getDiscount() + "% OFF");
                } else {
                    holder.productDiscountedPrice
                            .setText(context.getString(R.string.Rs) + " "
                                    + Math.round(cartObject.getCart().getDetail().get(position).getProduct().getPrice()));

                    holder.productPrice.setVisibility(View.GONE);
                    holder.productDiscountFactor.setVisibility(View.GONE);
                }
            } catch (NumberFormatException e) {

            }


            holder.itemCount.setText("#Item " + (position + 1));
            holder.quantity.setText(cartObject.getCart().getDetail().get(position).getQty() + "");
            holder.subTotal.setText(context.getResources().getString(R.string.Rs) + " " + (cartObject.getCart().getDetail().get(position).getQty() * cartObject.getCart().getDetail().get(position).getProduct().getPrice()));
            holder.shippingPrice.setText(cartObject.getCart().getDetail().get(position).getShipping_charge() == 0 ? "Free" :
                    context.getString(R.string.rs_text) + " " + cartObject.getCart().getDetail().get(position).getShipping_charge() * cartObject.getCart().getDetail().get(position).getQty());
            holder.totalPrice.setText(context.getResources().getString(R.string.Rs) + " " + (cartObject.getCart().getDetail().get(position).getShipping_charge() == 0 ? (cartObject.getCart().getDetail().get(position).getQty() * cartObject.getCart().getDetail().get(position).getProduct().getPrice()) : ((cartObject.getCart().getDetail().get(position).getQty() * cartObject.getCart().getDetail().get(position).getProduct().getPrice()) + (cartObject.getCart().getDetail().get(position).getQty() * cartObject.getCart().getDetail().get(position).getShipping_charge()))));
            if (cartObject.getCart().getDetail().get(position).getProduct().is_cod()) {
                holder.isCocText.setVisibility(View.VISIBLE);
                holder.isCocText.setText(Html.fromHtml("<b>Cash-on-Delivery" + "</b>" + "<font color=#B5CA01> Available" + "</font>"));
                changeDrawableLeft(holder.isCocText, R.drawable.ic_tick);

            } else {
                holder.isCocText.setVisibility(View.GONE);
                //  changeDrawableLeft(holder.isCocText, R.drawable.ic_cross_red);

            }

            if (cartObject.getCart().getDetail().get(position).getProduct().is_o2o()) {
                holder.buyOfflineTag.setVisibility(View.VISIBLE);
            } else {
                holder.buyOfflineTag.setVisibility(View.GONE);
            }

            if (cartObject.getCart().getDetail().get(position).isShowingPaymentDesc()) {
                holder.shortSubTotalLayout.setVisibility(View.GONE);
                holder.priceDescLayout.setVisibility(View.VISIBLE);
            } else {
                holder.priceDescLayout.setVisibility(View.GONE);
                holder.shortSubTotalLayout.setVisibility(View.VISIBLE);
                holder.shortTotal.setText(context.getResources().getString(R.string.Rs) + " " + (cartObject.getCart().getDetail().get(position).getShipping_charge() == 0 ? (cartObject.getCart().getDetail().get(position).getQty() * cartObject.getCart().getDetail().get(position).getProduct().getPrice()) : ((cartObject.getCart().getDetail().get(position).getQty() * cartObject.getCart().getDetail().get(position).getProduct().getPrice()) + (cartObject.getCart().getDetail().get(position).getQty() * cartObject.getCart().getDetail().get(position).getShipping_charge()))));
            }

            holder.cancelCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonLib.isNetworkAvailable(context)) {
                        mListener.itemDeleted(position);
                    } else {
                        Toast.makeText(context, "No network available", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            holder.quantityView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonLib.isNetworkAvailable(context)) {
                        final Dialog dialog = new Dialog(context);

                        View view = LayoutInflater.from(context).inflate(R.layout.select_quantity_dialog_view, null);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(view);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.simple_list_item, R.id.text1);
                        for (int i = 0; i < cartObject.getCart().getDetail().get(position).getAvailable_qty(); i++) {
                            adapter.add((i + 1) + "");
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
                    } else {
                        Toast.makeText(context, "No network available", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            holder.removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonLib.isNetworkAvailable(context)) {
                        mListener.itemDeleted(position);
                    } else {
                        Toast.makeText(context, "No network available", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.moveToWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonLib.isNetworkAvailable(context)) {
                        mListener.itemMovedToWishlist(position);
                    } else {
                        Toast.makeText(context, "No network available", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.shortSubTotalLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartObject.getCart().getDetail().get(position).setIsShowingPaymentDesc(true);
                    notifyItemChanged(position);
                }
            });
            holder.priceDescLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartObject.getCart().getDetail().get(position).setIsShowingPaymentDesc(false);
                    notifyItemChanged(position);
                }
            });
            holder.productCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, NewProductDetailActivity.class);
                    intent.putExtra("slug", cartObject.getCart().getDetail().get(position).getProduct().getSlug());
                    intent.putExtra("id", cartObject.getCart().getDetail().get(position).getProduct().getId());
                    intent.putExtra("title", cartObject.getCart().getDetail().get(position).getProduct().getName());

                    //        GA Ecommerce
                    intent.putExtra("productActionListName", "Cart List Item Click");
                    intent.putExtra("screenName", "My Cart Activity");
                    intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);

                    context.startActivity(intent);

                }
            });
        } else {
            SummaryHolder summaryHolder = (SummaryHolder) defaultHolder;
            totalPrice = cartObject.getCart().getTotal_price();
            float price = cartObject.getCart().getPrice();
            if (totalPrice == 0) {
                summaryHolder.totalSum.setText(context.getResources().getString(R.string.Rs) + " " + cartObject.getCart().getPrice());
                summaryHolder.totalPayment.setText(context.getResources().getString(R.string.Rs) + " " + cartObject.getCart().getTotal_price());
            }
            summaryHolder.totalSum.setText(context.getResources().getString(R.string.Rs) + " " + Math.round(price));
            summaryHolder.totalPayment.setText(context.getResources().getString(R.string.Rs) + " " + Math.round(totalPrice));
            summaryHolder.shipping.setText((cartObject.getCart().getTotal_shipping() == 0) ? "Free" : context.getResources().getString(R.string.Rs) + " " + cartObject.getCart().getTotal_shipping());

        }
    }

    public void changeDrawableLeft(CustomTextView textview, int drawableId) {
        Drawable drawable = context.getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        textview.setCompoundDrawables(drawable, null, null, null);
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

        void itemMovedToWishlist(int position);
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView productPrice, name, delivery_date, quantity, viewDetails, hideDetails, itemCount, productDiscountedPrice, productDiscountFactor;
        ImageView product_image, cancelCartItem, buyOfflineTag;
        View quantityView;
        CustomTextView subTotal, shippingPrice, totalPrice, isCocText, shortTotal;
        LinearLayout shortSubTotalLayout, priceDescLayout, moveToWishlist, removeItem, productCard, discountLayout;


        public CartItemHolder(View itemView) {
            super(itemView);
            //itemView.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.white)));
            productDiscountedPrice = (TextView) itemView
                    .findViewById(R.id.product_disounted_price);
            productPrice = (TextView) itemView
                    .findViewById(R.id.product_price);
            productDiscountFactor = (TextView) itemView.findViewById(R.id.product_disounted_factor);
            quantity = (TextView) itemView.findViewById(R.id.cart_quantity);
            delivery_date = (TextView) itemView.findViewById(R.id.expected_delivery_date_text);
            quantityView = itemView.findViewById(R.id.quantity_view);
            name = (TextView) itemView.findViewById(R.id.product_name);
            product_image = (ImageView) itemView.findViewById(R.id.product_image);
            cancelCartItem = (ImageView) itemView.findViewById(R.id.cancel_cart_item);

            subTotal = (CustomTextView) itemView.findViewById(R.id.subtotal);
            shippingPrice = (CustomTextView) itemView.findViewById(R.id.shipping_charges);
            totalPrice = (CustomTextView) itemView.findViewById(R.id.total_payment);
            isCocText = (CustomTextView) itemView.findViewById(R.id.is_coc_text);
            buyOfflineTag = (ImageView) itemView.findViewById(R.id.buy_offline_tag);

            shortSubTotalLayout = (LinearLayout) itemView.findViewById(R.id.short_payment_desc_layout);
            shortTotal = (CustomTextView) itemView.findViewById(R.id.short_subtotal);
            priceDescLayout = (LinearLayout) itemView.findViewById(R.id.price_desc_layout);
            viewDetails = (TextView) itemView.findViewById(R.id.view_details);
            hideDetails = (TextView) itemView.findViewById(R.id.hide_details);
            moveToWishlist = (LinearLayout) itemView.findViewById(R.id.move_to_wishlist);
            removeItem = (LinearLayout) itemView.findViewById(R.id.remove_item);
            productCard = (LinearLayout) itemView.findViewById(R.id.product_card);
            itemCount = (TextView) itemView.findViewById(R.id.item_count);
            discountLayout = (LinearLayout) itemView.findViewById(R.id.applied_coupon_discount);
            discountLayout.setVisibility(View.GONE);
        }
    }


    public class SummaryHolder extends RecyclerView.ViewHolder {
        TextView totalPayment, checkCoupon, shipping, totalSum;
        LinearLayout discountLayout;

        public SummaryHolder(View itemView) {
            super(itemView);
            totalPayment = (TextView) itemView.findViewById(R.id.total_payment);

            shipping = (TextView) itemView.findViewById(R.id.shipping_charges);
            totalSum = (TextView) itemView.findViewById(R.id.total_sum);
            discountLayout = (LinearLayout) itemView.findViewById(R.id.applied_coupon_discount);
            discountLayout.setVisibility(View.GONE);
        }
    }
}
