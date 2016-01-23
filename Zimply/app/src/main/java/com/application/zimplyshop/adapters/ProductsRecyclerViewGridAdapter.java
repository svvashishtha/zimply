package com.application.zimplyshop.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.NewProductDetailActivity;
import com.application.zimplyshop.baseobjects.BaseProductListObject;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.widgets.CustomTextView;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.ArrayList;

public class ProductsRecyclerViewGridAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_DATA = 0;
    public static final int TYPE_LOADER = 1;
    public static final int TYPE_HEADER = 2;

    ArrayList<BaseProductListObject> objs;

    CheckLayoutOptions mListener;

    Context mContext;

    int height;

    boolean isFooterRemoved;

    Activity activity;
    int count;

    public ProductsRecyclerViewGridAdapter(Activity activity, Context context,
                                           int height) {
        this.mContext = context;
        this.objs = new ArrayList<BaseProductListObject>();
        this.height = height;
        this.activity = activity;
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
            if (objectId instanceof HomeProductObj) {
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
                return objs.size() + 1;
            } else {
                return objs.size() + 2;
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else if (position == objs.size() + 1) {
            return TYPE_LOADER;
        } else {
            return TYPE_DATA;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderCom, int position) {
        if (getItemViewType(position) == TYPE_DATA) {
            ProductViewHolder holder = (ProductViewHolder) holderCom;
            position--;
            final int positionTemp = position;

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, height);
            holder.img.setLayoutParams(lp);
            if (objs.get(position).getImage() != null) {
                if (holder.img.getTag() == null
                        || !(((String) holder.img
                        .getTag()).equalsIgnoreCase(objs.get(position)
                        .getImage()))) {

                    new ImageLoaderManager(activity).setImageFromUrl(
                            objs.get(position).getImage(),
                            holder.img, "users", height,
                            height, true, false);

                    holder.img.setTag(objs.get(position)
                            .getImage());
                }
            }
            if (objs.get(position).is_o2o()) {
                ((ProductViewHolder) holder).buyOfflineTag.setVisibility(View.VISIBLE);
            } else {
                ((ProductViewHolder) holder).buyOfflineTag.setVisibility(View.GONE);
            }
            ((ProductViewHolder) holder).productName.setText(objs.get(position)
                    .getName());
            ((ProductViewHolder) holder).productDiscountedPrice
                    .setText(mContext.getString(R.string.Rs) + " "
                            + Math.round(objs.get(position).getPrice()));

            ((ProductViewHolder) holder).productPrice.setVisibility(View.GONE);
            ((ProductViewHolder) holder).productDiscountFactor.setVisibility(View.GONE);
            try {
                if (objs.get(position).getMrp() != objs.get(position).getPrice()) {
                    ((ProductViewHolder) holder).productDiscountedPrice
                            .setText(mContext.getString(R.string.Rs) + " "
                                    + objs.get(position).getPrice());
                    ((ProductViewHolder) holder).productPrice.setVisibility(View.VISIBLE);
                    ((ProductViewHolder) holder).productPrice.setText(mContext
                            .getString(R.string.Rs)
                            + " "
                            + objs.get(position).getMrp());
                    ((ProductViewHolder) holder).productPrice
                            .setPaintFlags(((ProductViewHolder) holder).productPrice
                                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((ProductViewHolder) holder).productDiscountFactor.setVisibility(View.VISIBLE);
                    ((ProductViewHolder) holder).productDiscountFactor.setText(objs.get(position).getDiscount() + "% OFF");
                } else {
                    ((ProductViewHolder) holder).productDiscountedPrice
                            .setText(mContext.getString(R.string.Rs) + " "
                                    + Math.round(objs.get(position).getPrice()));

                    ((ProductViewHolder) holder).productPrice.setVisibility(View.GONE);
                    ((ProductViewHolder) holder).productDiscountFactor.setVisibility(View.GONE);
                }
            } catch (NumberFormatException e) {

            }


            ((ProductViewHolder) holder).img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NewProductDetailActivity.class);
                    intent.putExtra("slug", objs.get(positionTemp).getSlug());
                    intent.putExtra("id", objs.get(positionTemp).getId());
                    intent.putExtra("title", objs.get(positionTemp).getName());

                    //        GA Ecommerce
                    intent.putExtra("productActionListName", "Product List Item Click");
                    intent.putExtra("screenName", "Product List Activity");
                    intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);

                    mContext.startActivity(intent);
                }
            });
        } else if (getItemViewType(position) == TYPE_HEADER) {

            final HeaderViewHolder holder = (HeaderViewHolder) holderCom;
            holder.productCount.setText(count + " Products");
            if (mListener.checkIsRecyclerViewInLongItemMode()) {
                holder.gridIcon.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.list_long_icon));
            } else {
                holder.gridIcon.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.grid_icon));
            }
            holder.gridIconContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.switchRecyclerViewLayoutManager();
                }
            });
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGrp,
                                                      int itemViewType) {
        RecyclerView.ViewHolder holder;
        if (itemViewType == TYPE_DATA) {
            View view = LayoutInflater.from(viewGrp.getContext()).inflate(
                    R.layout.product_grid_item_layout, viewGrp, false);
            holder = new ProductViewHolder(view);
        } else if (itemViewType == TYPE_HEADER) {
            View v = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.product_listing_activity_list_header_layout, viewGrp, false);
            holder = new HeaderViewHolder(v);
        } else {
            View view = LayoutInflater.from(viewGrp.getContext()).inflate(
                    R.layout.progress_footer_layout, viewGrp, false);
            holder = new LoadingViewHolder(view);
        }
        return holder;
    }

    public void removePreviousData() {
        objs.clear();
        isFooterRemoved = false;
        notifyDataSetChanged();
    }

    public void setCount(int count) {
        this.count = count;
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

        LinearLayout gridIconContainer;
        ImageView gridIcon;
        CustomTextView productCount;

        public HeaderViewHolder(View v) {
            super(v);
            productCount = (CustomTextView) v.findViewById(R.id.product_count);
            gridIcon = (ImageView) v.findViewById(R.id.gridicon);
            gridIconContainer = (LinearLayout) v.findViewById(R.id.gridbuttonswitcher);
        }
    }
    public void setCheckLayoutOptionListener(CheckLayoutOptions mListener)
    {
        this.mListener = mListener;
    }
    public interface CheckLayoutOptions{
        boolean checkIsRecyclerViewInLongItemMode();
        void switchRecyclerViewLayoutManager();
    }
}