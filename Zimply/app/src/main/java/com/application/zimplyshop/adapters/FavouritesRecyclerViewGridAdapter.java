package com.application.zimplyshop.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.MyWishlist;
import com.application.zimplyshop.activities.NewProductDetailActivity;
import com.application.zimplyshop.baseobjects.FavouriteObject;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.serverapis.RequestTags;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 12/19/2015.
 */
public class FavouritesRecyclerViewGridAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public int TYPE_DATA = 0;
    public int TYPE_LOADER = 1;

    ArrayList<FavouriteObject> objs;

    Context mContext;

    int height;

    boolean isFooterRemoved;

    Activity activity;
    public int currentSelectedWishlistItem = -1;
    ProductViewHolder temporaryHolderForWishlistButtons;
    MyClickListener clickListener;
    int heightOfLayoutButtons;
    private long animDurationForButtonUpTime = 400;

    public FavouritesRecyclerViewGridAdapter(Activity activity, Context context,
                                             int height) {
        this.mContext = context;
        this.objs = new ArrayList<FavouriteObject>();
        this.height = height;
        this.activity = activity;
        clickListener = new MyClickListener();
        currentSelectedWishlistItem = -1;
    }

    public void addData(ArrayList<FavouriteObject> objs) {
        ArrayList<FavouriteObject> newObjs = new ArrayList<FavouriteObject>(objs);
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
                FavouriteObject product = objs.get(i);
                if (product.getFavourite_item_id() == objId) {
                    found = true;
                    prodIdToRemove = i;
                    break;
                }
            }
            if (found && prodIdToRemove != -1) {
                objs.remove(prodIdToRemove);
                notifyDataSetChanged();
            }
        } else if (type == RequestTags.MARK_FAVOURITE_REQUEST_TAG) {
            if (objectId instanceof HomeProductObj) {
                objs.add((FavouriteObject) objectId);
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        position = holder.getAdapterPosition();
        if (getItemViewType(position) == TYPE_DATA) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, height);
            ((ProductViewHolder) holder).img.setLayoutParams(lp);
            if (objs.get(position).getProduct().getImage() != null) {
                if (((ProductViewHolder) holder).img.getTag() == null
                        || !(((String) ((ProductViewHolder) holder).img
                        .getTag()).equalsIgnoreCase(objs.get(position)
                        .getProduct().getImage()))) {

                    new ImageLoaderManager(activity).setImageFromUrl(
                            objs.get(position).getProduct().getImage(),
                            ((ProductViewHolder) holder).img, "users", height,
                            height, true, false);

                    ((ProductViewHolder) holder).img.setTag(objs.get(position)
                            .getProduct().getImage());
                }
            }
            if (objs.get(position).getProduct().is_o2o()) {
                ((ProductViewHolder) holder).buyOfflineTag.setVisibility(View.VISIBLE);
            } else {
                ((ProductViewHolder) holder).buyOfflineTag.setVisibility(View.GONE);
            }
            ((ProductViewHolder) holder).productName.setText(objs.get(position)
                    .getProduct().getName());
            ((ProductViewHolder) holder).productDiscountedPrice
                    .setText(mContext.getString(R.string.Rs) + " "
                            + Math.round(objs.get(position).getProduct().getPrice()));

            ((ProductViewHolder) holder).productPrice.setVisibility(View.GONE);
            ((ProductViewHolder) holder).productDiscountFactor.setVisibility(View.GONE);
            /*try {
                if (objs.get(position).getDiscounted_price() != 0) {
                    ((ProductViewHolder) holder).productDiscountedPrice
                            .setText(mContext.getString(R.string.Rs) + " "
                                    + Math.round(objs.get(position).getDiscounted_price()));
                    ((ProductViewHolder) holder).productPrice.setVisibility(View.VISIBLE);
                    ((ProductViewHolder) holder).productPrice.setText(mContext
                            .getString(R.string.Rs)
                            + " "
                            + Math.round(objs.get(position).getPrice()));
                    ((ProductViewHolder) holder).productPrice
                            .setPaintFlags(((ProductViewHolder) holder).productPrice
                                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((ProductViewHolder) holder).productDiscountFactor.setVisibility(View.VISIBLE);
                    ((ProductViewHolder) holder).productDiscountFactor.setText("( " + objs.get(position).getDiscountFactor() + " % )");
                } else {
                    ((ProductViewHolder) holder).productDiscountedPrice
                            .setText(mContext.getString(R.string.Rs) + " "
                                    + Math.round(objs.get(position).getPrice()));

                    ((ProductViewHolder) holder).productPrice.setVisibility(View.GONE);
                    ((ProductViewHolder) holder).productDiscountFactor.setVisibility(View.GONE);
                }
            } catch (NumberFormatException e) {

            }
                */

            final int positionTemp = position;
            ((ProductViewHolder) holder).img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NewProductDetailActivity.class);
                    intent.putExtra("slug", objs.get(positionTemp).getProduct().getSlug());
                    intent.putExtra("id", objs.get(positionTemp).getProduct().getId());
                    intent.putExtra("title", objs.get(positionTemp).getProduct().getName());
                    mContext.startActivity(intent);
                }
            });

            ProductViewHolder holderProduct = (ProductViewHolder) holder;
            if (currentSelectedWishlistItem == position) {
                temporaryHolderForWishlistButtons = holderProduct;
                holderProduct.wishlistButtonsContainerLayout.setVisibility(View.VISIBLE);
                holderProduct.wishlistButtonsBgView.setAlpha(1);
                holderProduct.layoutToAnimateForShowingButtons.setTranslationY(0);
            } else
                holderProduct.wishlistButtonsContainerLayout.setVisibility(View.GONE);

            holderProduct.overflowButtonLayout.setTag(R.integer.wishlist_tag_position, position);
            holderProduct.overflowButtonLayout.setTag(R.integer.wishlist_tag_holder, holderProduct);
            holderProduct.overflowButtonLayout.setOnClickListener(clickListener);

            holderProduct.removeFromWishlistButton.setTag(position);
            holderProduct.removeFromWishlistButton.setOnClickListener(clickListener);
            holderProduct.moveToCartButton.setTag(position);
            holderProduct.moveToCartButton.setOnClickListener(clickListener);
        } else {

        }

    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.wihslistbuttonoverflow:
                    int pos = (int) v.getTag(R.integer.wishlist_tag_position);
                    ProductViewHolder holder = (ProductViewHolder) v.getTag(R.integer.wishlist_tag_holder);
                    overflowButtonClicked(pos, holder);
                    break;
                case R.id.removefromwishlist:
                    pos = (int) v.getTag();
                    ((MyWishlist) mContext).removeProductFromWishlistRequest(pos, objs.get(pos).getFavourite_item_id());
                    break;
                case R.id.movetocartfromwoshlist:
                    pos = (int) v.getTag();
                    break;
            }
        }
    }

    private void overflowButtonClicked(int pos, ProductViewHolder holder) {
        if (currentSelectedWishlistItem == -1) {
            currentSelectedWishlistItem = pos;
            temporaryHolderForWishlistButtons = holder;
            showWishlistButtonsLayoutForPosition(currentSelectedWishlistItem, holder);
        } else if (currentSelectedWishlistItem == pos) {
            hideWishlistButtonsForPosition(currentSelectedWishlistItem, holder, null);
            currentSelectedWishlistItem = -1;
        } else {
            hideWishlistButtonsForPosition(currentSelectedWishlistItem, temporaryHolderForWishlistButtons, holder);
            currentSelectedWishlistItem = pos;
            showWishlistButtonsLayoutForPosition(currentSelectedWishlistItem, holder);
        }
    }

    private void hideWishlistButtonsForPosition(int currentSelectedWishlistItem, final ProductViewHolder holder, ProductViewHolder finalHolder) {
        if (holder != null) {
            holder.layoutToAnimateForShowingButtons.animate().translationY(heightOfLayoutButtons).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    animatioForHidingWishlistButtonBgView(holder);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animatioForHidingWishlistButtonBgView(holder);
                }
            });
        }

        temporaryHolderForWishlistButtons = finalHolder;
    }

    private void animatioForHidingWishlistButtonBgView(final ProductViewHolder holder) {
        holder.wishlistButtonsBgView.animate().alpha(0).setDuration(50).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                holder.wishlistButtonsContainerLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                holder.wishlistButtonsContainerLayout.setVisibility(View.GONE);
            }
        }).start();
    }

    private void showWishlistButtonsLayoutForPosition(int currentSelectedWishlistItem, final ProductViewHolder holder) {
        if (holder != null) {
            holder.wishlistButtonsContainerLayout.setVisibility(View.VISIBLE);
            if (heightOfLayoutButtons == 0) {
                holder.layoutToAnimateForShowingButtons.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        heightOfLayoutButtons = holder.layoutToAnimateForShowingButtons.getHeight();
                        animateLayoutButtonsup(holder);
                    }
                });
            } else {
                animateLayoutButtonsup(holder);
            }
        }
    }

    private void animateLayoutButtonsup(ProductViewHolder holder) {
        holder.layoutToAnimateForShowingButtons.setTranslationY(heightOfLayoutButtons);
        holder.wishlistButtonsBgView.setAlpha(0);
        holder.layoutToAnimateForShowingButtons.animate().translationY(0).setDuration(animDurationForButtonUpTime).setListener(new AnimatorListenerAdapter() {
        }).start();
        holder.wishlistButtonsBgView.animate().alpha(1).setDuration(animDurationForButtonUpTime / 2).setListener(new AnimatorListenerAdapter() {
        }).start();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGrp,
                                                      int itemViewType) {
        RecyclerView.ViewHolder holder;
        if (itemViewType == TYPE_DATA) {
            View view = LayoutInflater.from(viewGrp.getContext()).inflate(
                    R.layout.wishlist_product_grid_item_layout, null);
            holder = new ProductViewHolder(view);
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

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView img, buyOfflineTag;
        TextView productName, productDiscountedPrice, productPrice, productDiscountFactor;
        LinearLayout overflowButtonLayout, layoutToAnimateForShowingButtons, removeFromWishlistButton, moveToCartButton;
        FrameLayout wishlistButtonsContainerLayout;
        View wishlistButtonsBgView;

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
            overflowButtonLayout = (LinearLayout) view.findViewById(R.id.wihslistbuttonoverflow);
            wishlistButtonsContainerLayout = (FrameLayout) view.findViewById(R.id.wishlistmorenbuttonslayout);
            wishlistButtonsBgView = view.findViewById(R.id.wishlistmorebuttonbg);
            layoutToAnimateForShowingButtons = (LinearLayout) view.findViewById(R.id.buttonslayoutwishlstbuttonsanimate);
            removeFromWishlistButton = (LinearLayout) view.findViewById(R.id.removefromwishlist);
            moveToCartButton = (LinearLayout) view.findViewById(R.id.movetocartfromwoshlist);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View view) {
            super(view);

        }

    }
}
