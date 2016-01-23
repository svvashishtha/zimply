package com.application.zimplyshop.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.NewProductDetailActivity;
import com.application.zimplyshop.baseobjects.BaseProductListObject;
import com.application.zimplyshop.db.RecentProductsDBWrapper;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.ArrayList;

public class BookedStoreProductListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public int TYPE_DATA = 1;
    public int TYPE_LOADER = 2;
    public int TYPE_HEADER = 0;

    ArrayList<BaseProductListObject> objs;

    Context mContext;

    int height;

    boolean isFooterRemoved;

    Activity activity;

    BaseProductListObject obj;

    public BookedStoreProductListAdapter(Activity activity, Context context,
                                         int height, BaseProductListObject obj) {
        this.mContext = context;
        this.objs = new ArrayList<BaseProductListObject>();
        this.height = height;
        this.activity = activity;
        this.obj = obj;
    }

    public void addData(ArrayList<BaseProductListObject> objs) {
        ArrayList<BaseProductListObject> newObjs = new ArrayList<BaseProductListObject>(objs);
        this.objs.addAll(this.objs.size(), newObjs);
        notifyDataSetChanged();
    }

    public void updateList(Object objectId, int type) {
        if (type == RequestTags.MARK_UN_FAVOURITE_REQUEST_TAG) {
            long objId = -1;
            try {
                objId = Long.parseLong(String.valueOf(objectId));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (objId == -1)
                return;
            boolean found = false;
            int prodIdToRemove = -1;
            for (int i = 0; i < objs.size(); i++) {
                BaseProductListObject product = objs.get(i);
                if (product.getId() == objId) {
                    found = true;
                    prodIdToRemove = i;
                    break;
                }
            }
            if (found && prodIdToRemove != -1) {
                objs.remove(prodIdToRemove);
            }
            notifyDataSetChanged();
        } else if (type == RequestTags.MARK_FAVOURITE_REQUEST_TAG) {
            if (objectId instanceof BaseProductListObject) {
                objs.add((BaseProductListObject) objectId);
                notifyDataSetChanged();
            }
        }
    }

    public void removeItem() {
        isFooterRemoved = true;
        notifyItemRemoved(objs.size());
    }

    @Override
    public int getItemCount() {
        if (objs != null) {
            if (isFooterRemoved) {
                if (obj != null) {
                    return objs.size() + 1;
                } else {
                    return objs.size();
                }
            } else {
                if (obj != null) {
                    return objs.size() + 2;
                } else {
                    return objs.size() + 1;
                }
            }
        }
        return 0;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (obj != null) {
                return TYPE_HEADER;
            } else {
                return TYPE_DATA;
            }
        } else if (position == objs.size() + 1) {
            return TYPE_LOADER;
        } else if (position == objs.size()) {
            if (obj != null) {
                return TYPE_DATA;
            } else {
                return TYPE_LOADER;
            }
        } else {
            return TYPE_DATA;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_DATA) {
            final int newPos = (obj != null) ? position - 1 : position;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, height);
            ((ProductViewHolder) holder).img.setLayoutParams(lp);
            if (objs.get(newPos).getImage() != null) {
                if (((ProductViewHolder) holder).img.getTag() == null
                        || !(((String) ((ProductViewHolder) holder).img
                        .getTag()).equalsIgnoreCase(objs.get(newPos)
                        .getImage()))) {

                    new ImageLoaderManager(activity).setImageFromUrl(
                            objs.get(newPos).getImage(),
                            ((ProductViewHolder) holder).img, "users", height,
                            height, true, false);

                    ((ProductViewHolder) holder).img.setTag(objs.get(newPos)
                            .getImage());
                }
            }
            if (objs.get(newPos).is_o2o()) {
                ((ProductViewHolder) holder).buyOfflineTag.setVisibility(View.VISIBLE);
            } else {
                ((ProductViewHolder) holder).buyOfflineTag.setVisibility(View.GONE);
            }
            ((ProductViewHolder) holder).productName.setText(objs.get(newPos)
                    .getName());
            /*((ProductViewHolder) holder).productDiscountedPrice
                    .setText(mContext.getString(R.string.Rs) + " "
                            + objs.get(newPos).getPrice());*/

            ((ProductViewHolder) holder).productPrice.setVisibility(View.GONE);
            ((ProductViewHolder) holder).productDiscountFactor.setVisibility(View.GONE);
            try {
                if (objs.get(newPos).getMrp() != objs.get(newPos).getPrice()) {
                    ((ProductViewHolder) holder).productDiscountedPrice
                            .setText(mContext.getString(R.string.Rs) + " "
                                    + objs.get(newPos).getPrice());
                    ((ProductViewHolder) holder).productPrice.setVisibility(View.VISIBLE);
                    ((ProductViewHolder) holder).productPrice.setText(mContext
                            .getString(R.string.Rs)
                            + " "
                            + objs.get(newPos).getMrp());
                    ((ProductViewHolder) holder).productPrice
                            .setPaintFlags(((ProductViewHolder) holder).productPrice
                                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((ProductViewHolder) holder).productDiscountFactor.setVisibility(View.VISIBLE);
                    ((ProductViewHolder) holder).productDiscountFactor.setText(objs.get(newPos).getDiscount() + "% OFF");
                } else {
                    ((ProductViewHolder) holder).productDiscountedPrice
                            .setText(mContext.getString(R.string.Rs) + " "
                                    + Math.round(objs.get(newPos).getPrice()));

                    ((ProductViewHolder) holder).productPrice.setVisibility(View.GONE);
                    ((ProductViewHolder) holder).productDiscountFactor.setVisibility(View.GONE);
                }
            } catch (NumberFormatException e) {

            }

            ((ProductViewHolder) holder).img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(AppPreferences.isUserLogIn(mContext)){

                    }else{
                        RecentProductsDBWrapper.addProduct(objs.get(newPos), 1, System.currentTimeMillis());
                    }

                    Intent intent = new Intent(mContext, NewProductDetailActivity.class);
                    intent.putExtra("slug", objs.get(newPos).getSlug());
                    intent.putExtra("id", objs.get(newPos).getId());
                    intent.putExtra("title", objs.get(newPos).getName());

                    //        GA Ecommerce
                    intent.putExtra("productActionListName", "Product List Item Click");
                    intent.putExtra("screenName", "Booked Store Product List Activity");
                    intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);

                    mContext.startActivity(intent);
                }
            });
        } else if (getItemViewType(position) == TYPE_HEADER) {
            ((HeaderViewHolder) holder).productName.setText(obj.getName());
            ((HeaderViewHolder) holder).productLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(AppPreferences.isUserLogIn(mContext)){

                    }else{
                        RecentProductsDBWrapper.addProduct(obj, 1, System.currentTimeMillis());
                    }
                    Intent intent = new Intent(mContext, NewProductDetailActivity.class);
                    intent.putExtra("slug", obj.getSlug());
                    intent.putExtra("id", obj.getId());
                    intent.putExtra("title", obj.getName());

                    //        GA Ecommerce
                    intent.putExtra("productActionListName", "Header Clicked");
                    intent.putExtra("screenName", "Booked Store Product List Activity");
                    intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);

                    mContext.startActivity(intent);
                }
            });
            new ImageLoaderManager(activity).setImageFromUrl(
                    obj.getImage(), ((HeaderViewHolder) holder).productImg, "users", height,
                    height, true, false);

            try {
                if (obj.getMrp() != obj.getPrice()) {
                    ((HeaderViewHolder) holder).productDiscountedPrice
                            .setText(mContext.getString(R.string.Rs) + " "
                                    + obj.getPrice());
                    ((HeaderViewHolder) holder).productPrice.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).productPrice.setText(mContext
                            .getString(R.string.Rs)
                            + " "
                            + obj.getMrp());
                    ((HeaderViewHolder) holder).productPrice
                            .setPaintFlags(((HeaderViewHolder) holder).productPrice
                                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((HeaderViewHolder) holder).productDiscountFactor.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).productDiscountFactor.setText(obj.getDiscount() + "% OFF");
                } else {
                    ((HeaderViewHolder) holder).productDiscountedPrice
                            .setText(mContext.getString(R.string.Rs) + " "
                                    + Math.round(obj.getPrice()));

                    ((HeaderViewHolder) holder).productPrice.setVisibility(View.GONE);
                    ((HeaderViewHolder) holder).productDiscountFactor.setVisibility(View.GONE);
                }
            } catch (NumberFormatException e) {

            }


        } else {

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGrp,
                                                      int itemViewType) {
        RecyclerView.ViewHolder holder;
        if (itemViewType == TYPE_DATA) {
            View view = LayoutInflater.from(viewGrp.getContext()).inflate(
                    R.layout.product_grid_item_layout, null);
            holder = new ProductViewHolder(view);
        } else if (itemViewType == TYPE_LOADER) {
            View view = LayoutInflater.from(viewGrp.getContext()).inflate(
                    R.layout.progress_footer_layout, viewGrp, false);
            holder = new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGrp.getContext()).inflate(
                    R.layout.booking_list_first_item_layout, viewGrp, false);
            holder = new HeaderViewHolder(view);
        }
        return holder;
    }

    public void removePreviousData() {
        objs.clear();
        isFooterRemoved = false;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView img, buyOfflineTag;
        TextView productName, productDiscountedPrice, productPrice, productDiscountFactor;

        public ProductViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.product_img);
            productName = (TextView) view.findViewById(R.id.product_name);
            productDiscountedPrice = (TextView) view
                    .findViewById(R.id.product_disounted_price);
            productPrice = (TextView) view
                    .findViewById(R.id.product_price);
            productDiscountFactor = (TextView) view.findViewById(R.id.product_disounted_factor);
            buyOfflineTag = (ImageView) view.findViewById(R.id.buy_offline_tag);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View view) {
            super(view);

        }

    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView productName, productPrice, productDiscountedPrice, productDiscountFactor;
        ImageView productImg;
        RelativeLayout productLayout;

        public HeaderViewHolder(View view) {
            super(view);
            productImg = (ImageView) view.findViewById(R.id.product_img);
            productName = (TextView) view.findViewById(R.id.product_name);
            productDiscountedPrice = (TextView) view
                    .findViewById(R.id.product_disounted_price);
            productPrice = (TextView) view
                    .findViewById(R.id.product_price);
            productDiscountFactor = (TextView) view.findViewById(R.id.product_disounted_factor);
            productLayout = (RelativeLayout) view.findViewById(R.id.product_card);
        }

    }


}