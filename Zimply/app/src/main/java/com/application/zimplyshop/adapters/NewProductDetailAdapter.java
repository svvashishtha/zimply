package com.application.zimplyshop.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.BaseLoginSignupActivity;
import com.application.zimplyshop.activities.BookingStoreProductListingActivity;
import com.application.zimplyshop.activities.NewProductDetailActivity;
import com.application.zimplyshop.activities.ProductPhotoZoomActivity;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BaseProductListObject;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.baseobjects.ProductAttribute;
import com.application.zimplyshop.db.RecentProductsDBWrapper;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.ZTracker;
import com.application.zimplyshop.widgets.CustomTextView;
import com.application.zimplyshop.widgets.ProductThumbListItemDecorator;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Umesh Lohani on 12/11/2015.
 */
public class NewProductDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int TYPE_TOP_IMAGE = 0;

    int TYPE_PRODUCT_INFO_1 = 1;
    int TYPE_PRODUCT_INFO_2 = 2;
    int TYPE_PRODUCT_INFO_3 = 3;
    int TYPE_PRODUCT_INFO_4 = 4;
    int TYPE_SIMILAR_PRODUCTS = 5;
    int TYPE_RECENTLY_VIEWED_PRODUCTS = 6;
    int TYPE_LOADER = 7;

    int BOOK_BTN_CLICK = 1;
    int PROGRESS_TIME_COMPLETE = 2;
    int PROGRESS_LOADING_COMPLETE = 3;
    int BOOK_PROCESS_COMPLETE = 4;

    ProductInfoHolder2 refernceHolder;

    boolean isDescShown, isSpecsShown, isReturnPolicyShown, isCancelBookingShown, isCareShown, isFaqShown;

    HomeProductObj obj;

    Context mContext;

    int displayWidth, displayHeight;

    boolean isAvailableAtPincode;

    ArrayList<BaseProductListObject> similarProducts;
    int similarProductsItemHeight, similarProductsRecyclerHeight, similarProductsItemWidth;
    int similarProductsitemMargin;


    boolean isFooterRemoved;

    public NewProductDetailAdapter(Context context, int displayWidth, int displayHeight, HomeProductObj obj) {
        this.mContext = context;
        this.displayHeight = displayHeight;
        this.displayWidth = displayWidth;
        this.obj = obj;
        similarProductsItemHeight = (mContext.getResources().getDisplayMetrics().widthPixels - 3 * ((int) mContext.getResources()
                .getDimension(R.dimen.margin_mini))) / 2;
        similarProductsRecyclerHeight = similarProductsItemHeight + mContext.getResources().getDimensionPixelSize(R.dimen.product_description_similar_recycler_additional_height);
        similarProductsItemWidth = (int) ((mContext.getResources()).getDisplayMetrics().widthPixels / 2.3);
        similarProductsitemMargin = mContext.getResources().getDimensionPixelSize(R.dimen.z_margin_mini);
        products = new ArrayList<>();
    }


    public HomeProductObj getObj() {
        return obj;
    }

    public void setIsFooterRemoved(boolean isFooterRemoved) {
        this.isFooterRemoved = isFooterRemoved;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_TOP_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_tab_info_top, parent, false);
            holder = new ImageViewHolder(view);
        } else if (viewType == TYPE_PRODUCT_INFO_1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_product_info_layout_1, parent, false);
            holder = new ProductInfoHolder1(view);
        } else if (viewType == TYPE_PRODUCT_INFO_2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_product_info_layout_2, parent, false);
            holder = new ProductInfoHolder2(view);
            this.refernceHolder = (ProductInfoHolder2) holder;
        } else if (viewType == TYPE_PRODUCT_INFO_3) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_product_info_layout_3, parent, false);
            holder = new ProductInfoHolder3(view);
        } else if (viewType == TYPE_SIMILAR_PRODUCTS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_description_similar_products_item_layout, parent, false);
            holder = new ProductSimilarProductsHolder(view);
        } else if (viewType == TYPE_RECENTLY_VIEWED_PRODUCTS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_description_similar_products_item_layout, parent, false);
            holder = new ProductRecentProductsHolder(view);
        } else if (viewType == TYPE_LOADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_footer_layout, parent, false);
            holder = new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_product_info_layout_desc, parent, false);
            holder = new ProductInfoHolder4(view);
        }
        return holder;
    }

    ArrayList<BaseProductListObject> products;

    public void addRecentlyViewed(ArrayList<BaseProductListObject> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    public void setIsCancelBookingShown(boolean isCancelBookingShown) {
        this.isCancelBookingShown = isCancelBookingShown;
        // notifyItemChanged(2);
    }

    public ProductInfoHolder2 getRefernceHolder() {
        return refernceHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (getItemViewType(position) == TYPE_TOP_IMAGE) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(displayWidth, displayWidth);
            ((ImageViewHolder) holder).productImg.setLayoutParams(lp);

            ((ImageViewHolder) holder).imageFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProductPhotoZoomActivity.class);
                    try {
                        ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                "Full Screen Image View", "Product Description Page");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("product_obj", obj.getProduct());
                    intent.putExtra("position", (Integer) ((ImageViewHolder) holder).productNewImage.getTag());
                    mContext.startActivity(intent);
                }
            });

            new ImageLoaderManager((NewProductDetailActivity) mContext).setImageFromUrl(obj.getProduct().getThumbs().get(0), ((ImageViewHolder) holder).productImg, "users", displayWidth / 2, displayHeight / 20, false,
                    false);

            ((ImageViewHolder) holder).productNewImage.setLayoutParams(lp);
            ((ImageViewHolder) holder).productNewImage.setTag(0);
            new ImageLoaderManager((NewProductDetailActivity) mContext).setImageFromUrlNew(obj.getProduct().getImages().get(0), ((ImageViewHolder) holder).productNewImage, "photo_details", displayWidth / 2,/* displayHeight / 20*/displayWidth / 2, false,
                    false, new ImageLoaderManager.ImageLoaderCallback() {
                        @Override
                        public void loadingStarted() {

                        }

                        @Override
                        public void loadingFinished(Bitmap bitmap) {
                            ((ImageViewHolder) holder).productImg.setVisibility(View.GONE);
                        }
                    });
            if (((ImageViewHolder) holder).productTabIcons.getAdapter() == null) {
                ((ImageViewHolder) holder).productTabIcons.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

                ((ImageViewHolder) holder).productTabIcons.addItemDecoration(new ProductThumbListItemDecorator(mContext.getResources().getDimensionPixelSize(R.dimen.margin_small)));
                final ProductThumbAdapters adapter = new ProductThumbAdapters(mContext, obj.getProduct().getThumbs(), mContext.getResources().getDimensionPixelSize(R.dimen.z_item_height_48), mContext.getResources().getDimensionPixelSize(R.dimen.z_item_height_48));
                ((ImageViewHolder) holder).productTabIcons.setAdapter(adapter);
                adapter.setOnItemClickListener(new ProductThumbAdapters.OnItemClickListener() {
                    @Override
                    public void onItemClick(final int pos) {
                        if (adapter.getSelectedPos() != pos) {
                            adapter.setSelectedPos(pos);
                            ((ImageViewHolder) holder).productImg.setVisibility(View.VISIBLE);
                            ((ImageViewHolder) holder).productNewImage.setTag(pos);
                            new ImageLoaderManager((NewProductDetailActivity) mContext).setImageFromUrl(obj.getProduct().getThumbs().get(pos), ((ImageViewHolder) holder).productImg, "users", displayWidth / 2, displayHeight / 20, false,
                                    false);

                            new ImageLoaderManager((NewProductDetailActivity) mContext).setImageFromUrlNew(obj.getProduct().getImages().get(pos), ((ImageViewHolder) holder).productNewImage, "photo_details", displayWidth / 2, /*displayHeight / 20*/displayWidth / 2, false,
                                    false, new ImageLoaderManager.ImageLoaderCallback() {
                                        @Override
                                        public void loadingStarted() {

                                        }

                                        @Override
                                        public void loadingFinished(Bitmap bitmap) {
                                            ((ImageViewHolder) holder).productImg.setVisibility(View.GONE);


                                        }
                                    });
                        }
                    }
                });
            }
        } else if (getItemViewType(position) == TYPE_PRODUCT_INFO_1) {
            if (obj.getProduct().getAvailable_quantity() == 0) {
                ((ProductInfoHolder1) holder).outOfStock.setVisibility(View.VISIBLE);
            } else {
                ((ProductInfoHolder1) holder).outOfStock.setVisibility(View.GONE);
            }

            if (obj.getProduct().getMrp() != obj.getProduct().getSp()) {
                if (obj.getProduct().getPrice() != obj.getProduct().getSp()) {
                    ((ProductInfoHolder1) holder).priceType.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder1) holder).marketPrice.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder1) holder).discount.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder1) holder).priceType.setText("Offer Price");
                    ((ProductInfoHolder1) holder).spTag.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder1) holder).mrpTag.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder1) holder).productPrice.setText(mContext.getResources().getString(R.string.rs_text) + " " + obj.getProduct().getPrice());
                    ((ProductInfoHolder1) holder).marketPrice.setText(mContext.getResources().getString(R.string.rs_text) + " " + obj.getProduct().getMrp());
                    ((ProductInfoHolder1) holder).marketPrice
                            .setPaintFlags(((ProductInfoHolder1) holder).marketPrice
                                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((ProductInfoHolder1) holder).sellingPrice.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder1) holder).sellingPrice.setText(mContext.getResources().getString(R.string.rs_text) + " " + obj.getProduct().getSp());
                    ((ProductInfoHolder1) holder).sellingPrice
                            .setPaintFlags(((ProductInfoHolder1) holder).sellingPrice
                                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    ((ProductInfoHolder1) holder).discount.setText(obj.getProduct().getDiscount() + "% OFF");
                } else {
                    ((ProductInfoHolder1) holder).priceType.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder1) holder).priceType.setText("Selling Price");
                    ((ProductInfoHolder1) holder).productPrice.setText(mContext.getResources().getString(R.string.rs_text) + " " + obj.getProduct().getPrice());
                    ((ProductInfoHolder1) holder).marketPrice.setText(mContext.getResources().getString(R.string.rs_text) + " " + obj.getProduct().getMrp());
                    ((ProductInfoHolder1) holder).spTag.setVisibility(View.GONE);
                    ((ProductInfoHolder1) holder).mrpTag.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder1) holder).marketPrice
                            .setPaintFlags(((ProductInfoHolder1) holder).marketPrice
                                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((ProductInfoHolder1) holder).sellingPrice.setVisibility(View.GONE);
                    ((ProductInfoHolder1) holder).discount.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder1) holder).discount.setText(obj.getProduct().getDiscount() + "% OFF");
                }
            } else {
                ((ProductInfoHolder1) holder).productPrice.setText(mContext.getResources().getString(R.string.rs_text) + " " + obj.getProduct().getPrice());
                ((ProductInfoHolder1) holder).priceType.setVisibility(View.GONE);
                ((ProductInfoHolder1) holder).marketPrice.setVisibility(View.GONE);
                ((ProductInfoHolder1) holder).sellingPrice.setVisibility(View.GONE);
                ((ProductInfoHolder1) holder).discount.setVisibility(View.GONE);
                ((ProductInfoHolder1) holder).spTag.setVisibility(View.GONE);
                ((ProductInfoHolder1) holder).mrpTag.setVisibility(View.GONE);
            }
            //((ProductInfoHolder1)holder).productPrice.setText(mContext.getString(R.string.rs_text)+" "+Math.round(obj.getProduct().getPrice()));
            ((ProductInfoHolder1) holder).productName.setText(obj.getProduct().getName());
            ((ProductInfoHolder1) holder).favImage.setSelected(obj.getProduct().is_favourite());
            ((ProductInfoHolder1) holder).favImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppPreferences.isUserLogIn(mContext)) {
                        if (obj.getProduct().is_favourite()) {
                            obj.getProduct().setIs_favourite(false);
                            ((ProductInfoHolder1) holder).favImage.setSelected(false);
                            mListener.onMarkUnFavorite();
                            Toast.makeText(mContext, "Successfully removed from wishlist", Toast.LENGTH_SHORT).show();
                        } else {
                            obj.getProduct().setIs_favourite(true);
                            ((ProductInfoHolder1) holder).favImage.setSelected(true);
                            mListener.onMarkFavorite();
                            Toast.makeText(mContext, "Successfully added to wishlist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "Please login to continue", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, BaseLoginSignupActivity.class);
                        intent.putExtra("inside", true);
                        mContext.startActivity(intent);
                    }
                }
            });
        } else if (getItemViewType(position) == TYPE_RECENTLY_VIEWED_PRODUCTS) {
            ProductRecentProductsHolder holderPro = (ProductRecentProductsHolder) holder;
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            holderPro.recyclerView.setLayoutManager(layoutManager);
            holderPro.recentProductsTag.setText("Recently Viewed");
            ProductRecentProductsAdapter adapter = new ProductRecentProductsAdapter();
            holderPro.recyclerView.setAdapter(adapter);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holderPro.recyclerView.getLayoutParams();
            params.height = similarProductsRecyclerHeight;
            holderPro.recyclerView.setLayoutParams(params);
            holderPro.viewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                "Recently Viewwed Products(View All)", "Product Description Page");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ((NewProductDetailActivity) mContext).openRecentlyViewed();

                }
            });
            /*holderPro.viewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((NewProductDetailActivity) mContext).openSimilarProductsActivity();
                }
            });*/
        } else if (getItemViewType(position) == TYPE_PRODUCT_INFO_2) {
            FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, displayHeight / 5);
            ((ProductInfoHolder2) holder).mapImage.setLayoutParams(lp1);
            new ImageLoaderManager((NewProductDetailActivity) mContext).setImageFromUrl(obj.getVendor().getMap(), ((ProductInfoHolder2) holder).mapImage, "users", displayWidth, displayHeight / 5, false, false);
            if (isCancelBookingShown) {
                ((ProductInfoHolder2) holder).storeAddress.setText(obj.getVendor().getName() + ", " + obj.getVendor().getAddress().getLine1() + ", " + obj.getVendor().getAddress().getCity());
                ((ProductInfoHolder2) holder).bookStoreVisit.setVisibility(View.GONE);
                ((ProductInfoHolder2) holder).bookingConfirmCard.setVisibility(View.VISIBLE);
                ((ProductInfoHolder2) holder).cancelBooking.setVisibility(View.GONE);
                ((ProductInfoHolder2) holder).cancelBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onCancelBookingRequest(((ProductInfoHolder2) holder));
                        }
                    }
                });

                ((ProductInfoHolder2) holder).mapImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + obj.getVendor().getAddress().getLatitude() + "," + obj.getVendor().getAddress().getLongitude() + "(" + obj.getVendor().getName() + ")");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                });
                ((ProductInfoHolder2) holder).direction.setVisibility(View.VISIBLE);
                ((ProductInfoHolder2) holder).direction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + obj.getVendor().getAddress().getLatitude() + "," + obj.getVendor().getAddress().getLongitude() + "(" + obj.getVendor().getName() + ")");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                });
            } else {
                ((ProductInfoHolder2) holder).mapImage.setOnClickListener(null);
                ((ProductInfoHolder2) holder).storeAddress.setText(obj.getVendor().getAddress().getCity());
                ((ProductInfoHolder2) holder).bookStoreVisit.setVisibility(View.VISIBLE);
                ((ProductInfoHolder2) holder).bookingConfirmCard.setVisibility(View.GONE);
                ((ProductInfoHolder2) holder).cancelBooking.setVisibility(View.GONE);
                ((ProductInfoHolder2) holder).direction.setVisibility(View.INVISIBLE);
            }
            ((ProductInfoHolder2) holder).bookStoreVisit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppPreferences.isUserLogIn(mContext)) {
                        showtransition(((ProductInfoHolder2) holder));

                    } else {
                        Intent intent = new Intent(mContext, BaseLoginSignupActivity.class);
                        intent.putExtra("inside", true);
                        mContext.startActivity(intent);
                    }
                }
            });


            ((ProductInfoHolder2) holder).callLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + obj.getVendor().getAddress().getPhone()));
                    mContext.startActivity(callIntent);
                }
            });


            ((ProductInfoHolder2) holder).directionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + obj.getVendor().getAddress().getLatitude() + "," + obj.getVendor().getAddress().getLongitude() + "(" + obj.getVendor().getName() + ")");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);

                }
            });

        } else if (getItemViewType(position) == TYPE_PRODUCT_INFO_3) {
            if (AppPreferences.isPincodeSaved(mContext)) {
                String pincode = AppPreferences.getSavedPincode(mContext);
                ((ProductInfoHolder3) holder).pincode.setText(pincode);
                // mListener.onCheckPincodeClicked(false, pincode);
            }

            if (isShowAvailableText) {
                ((ProductInfoHolder3) holder).isAvailableText.setVisibility(View.VISIBLE);
            } else {
                ((ProductInfoHolder3) holder).isAvailableText.setVisibility(View.GONE);
            }

            if (isAvailableAtPincode) {

                ((ProductInfoHolder3) holder).isAvailableText.setTextColor(mContext.getResources().getColor(R.color.green_text_color));
                ((ProductInfoHolder3) holder).isAvailableText.setText("Available at selected pincode");
            } else {

                ((ProductInfoHolder3) holder).isAvailableText.setTextColor(mContext.getResources().getColor(R.color.red_text_color));
                ((ProductInfoHolder3) holder).isAvailableText.setText("Not Available at selected pincode");
            }
            ((ProductInfoHolder3) holder).checkPincode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ProductInfoHolder3) holder).pincode.getText().toString().length() == 6) {
                        mListener.onCheckPincodeClicked(true, ((ProductInfoHolder3) holder).pincode.getText().toString(), position);
                    } else {
                        // showToast("Please enter a valid pincode");
                        //  Toast.makeText(ProductDetailsActivity.this, "Please enter a valid pincode", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            ((ProductInfoHolder3) holder).delivery.setText(obj.getProduct().getMin_shipping_days() + "-" + obj.getProduct().getMax_shipping_days() + " Days");
            ((ProductInfoHolder3) holder).shipping_charges.setText((obj.getProduct().getShipping_charges() == 0) ? "Free" : mContext.getString(R.string.rs_text) + " " + Math.round(obj.getProduct().getShipping_charges()));
            ((ProductInfoHolder3) holder).cod_avaiable.setText((obj.getProduct().is_cod()) ? "Available" : "Not Available");
            SpannableString string = new SpannableString(obj.getVendor().getName());
            string.setSpan(new UnderlineSpan(), 0, obj.getVendor().getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            string.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.z_rate_btn_blue_color)), 0, obj.getVendor().getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((ProductInfoHolder3) holder).sold_by.setText(string);
            ((ProductInfoHolder3) holder).moreFromSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent bookIntent = new Intent(mContext, BookingStoreProductListingActivity.class);
                    bookIntent.putExtra("hide_filter", false);
                    bookIntent.putExtra("vendor_id", obj.getVendor().getId());
                    bookIntent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
                    bookIntent.putExtra("vendor_name", obj.getVendor().getName());
                    try {

                        ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                "More Products from " + obj.getVendor().getName(), "Product Description Page");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mContext.startActivity(bookIntent);
                }
            });
        } else if (getItemViewType(position) == TYPE_SIMILAR_PRODUCTS) {
            ProductSimilarProductsHolder holderPro = (ProductSimilarProductsHolder) holder;
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            holderPro.recyclerView.setLayoutManager(layoutManager);

            SimilarProductsRecyclerAdapter adapter = new SimilarProductsRecyclerAdapter();
            holderPro.recyclerView.setAdapter(adapter);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holderPro.recyclerView.getLayoutParams();
            params.height = similarProductsRecyclerHeight;
            holderPro.recyclerView.setLayoutParams(params);

            holderPro.viewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((NewProductDetailActivity) mContext).openSimilarProductsActivity();
                    try {
                        ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                "Similar Products(View All)", "Product Description Page");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (getItemViewType(position) == TYPE_LOADER) {

        } else {

            if (position == 3) {
                ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descTitle.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).separator.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);

                ((ProductInfoHolder4) holder).descTitle.setText("Summary");

                if (isDescShown) {
                    changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                    ((ProductInfoHolder4) holder).descText.setText(obj.getProduct().getDescription());
                    ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                    ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                } else {
                    changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                    ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                    ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                }
                ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isDescShown) {
                            isDescShown = false;
                        } else {
                            isDescShown = true;
                        }
                        notifyItemChanged(position);
                        if (isSpecsShown) {
                            isSpecsShown = false;
                            if (obj.getProduct().is_o2o()) {
                                notifyItemChanged(5);
                            } else {
                                notifyItemChanged(4);
                            }
                        }
                        if (isCareShown) {
                            isCareShown = false;
                            if (obj.getProduct().is_o2o()) {
                                notifyItemChanged(6);
                            } else {
                                notifyItemChanged(5);
                            }
                        }
                        if (isReturnPolicyShown) {
                            isReturnPolicyShown = false;
                            if (obj.getProduct().is_o2o()) {
                                notifyItemChanged(7);
                            } else {
                                notifyItemChanged(6);
                            }
                        }
                        if (isFaqShown) {
                            isFaqShown = false;
                            if (obj.getProduct().is_o2o()) {
                                notifyItemChanged(8);
                            } else {
                                notifyItemChanged(7);
                            }
                        }
                        try {
                            ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                    ((ProductInfoHolder4) holder).descTitle.getText() + " Opened", "Product Description Page");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if (position == 4) {
                ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descTitle.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).separator.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);

                if (obj.getProduct().is_o2o()) {
                    ((ProductInfoHolder4) holder).descTitle.setText("Summary");

                    if (isDescShown) {
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                        ((ProductInfoHolder4) holder).descText.setText(obj.getProduct().getDescription());
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                    } else {
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                    }
                    ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isDescShown) {
                                isDescShown = false;
                            } else {
                                isDescShown = true;
                            }
                            notifyItemChanged(position);
                            if (isSpecsShown) {
                                isSpecsShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(5);
                                } else {
                                    notifyItemChanged(4);
                                }
                            }
                            if (isCareShown) {
                                isCareShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(6);
                                } else {
                                    notifyItemChanged(5);
                                }
                            }
                            if (isReturnPolicyShown) {
                                isReturnPolicyShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(7);
                                } else {
                                    notifyItemChanged(6);
                                }
                            }
                            if (isFaqShown) {
                                isFaqShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(8);
                                } else {
                                    notifyItemChanged(7);
                                }
                            }
                            try {
                                ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                        ((ProductInfoHolder4) holder).descTitle.getText() + " Opened", "Product Description Page");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    ((ProductInfoHolder4) holder).descTitle.setText("Specifications");
                    if (isSpecsShown) {
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);
                        if (((ProductInfoHolder4) holder).descLayout.getChildCount() == 0) {
                            LinearLayout linLayout = new LinearLayout((mContext));
                            linLayout.setOrientation(LinearLayout.VERTICAL);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            linLayout.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small));
                            if (obj.getProduct().getSku() != null && obj.getProduct().getSku().length() > 0) {// for adding sku code
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                LinearLayout layout = new LinearLayout(mContext);
                                layout.setOrientation(LinearLayout.HORIZONTAL);
                                layout.setLayoutParams(params);
                                layout.setPadding(0, 0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_small));
                                CustomTextView unit = new CustomTextView(mContext);
                                unit.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));
                                ((LinearLayout.LayoutParams) unit.getLayoutParams()).weight = 1;
                                unit.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                                unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.font_medium));
                                unit.setText(Html.fromHtml("<font color=#535353>" + "Product Code" + "</font>"));
                                CustomTextView value = new CustomTextView(mContext);
                                value.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));
                                ((LinearLayout.LayoutParams) value.getLayoutParams()).weight = 1;
                                value.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                                value.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.font_medium));
                                value.setText(Html.fromHtml("<font color=#535353>" + obj.getProduct().getSku() + "</font>"));
                                layout.addView(unit);
                                layout.addView(value);
                                linLayout.addView(layout);
                            }
                            for (ProductAttribute attribute : obj.getProduct().getAttribute()) {

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                LinearLayout layout = new LinearLayout(mContext);
                                layout.setOrientation(LinearLayout.HORIZONTAL);
                                layout.setLayoutParams(params);
                                layout.setPadding(0, 0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_small));
                                CustomTextView unit = new CustomTextView(mContext);
                                unit.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));
                                ((LinearLayout.LayoutParams) unit.getLayoutParams()).weight = 1;
                                unit.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                                unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.font_medium));
                                unit.setText(Html.fromHtml("<font color=#535353>" + attribute.getKey() + "</font>"));
                                CustomTextView value = new CustomTextView(mContext);
                                value.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));
                                ((LinearLayout.LayoutParams) value.getLayoutParams()).weight = 1;
                                value.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                                value.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.font_medium));
                                value.setText(Html.fromHtml("<font color=#535353>" + attribute.getValue() + " " + attribute.getUnit() + "</font>"));
                                layout.addView(unit);
                                layout.addView(value);
                                linLayout.addView(layout);
                            }
                            linLayout.setLayoutParams(lp);
                            ((ProductInfoHolder4) holder).descLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                            ((ProductInfoHolder4) holder).descLayout.addView(linLayout);

                        } else {

                        }
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);

                    } else {
                        ((ProductInfoHolder4) holder).descLayout.removeAllViewsInLayout();
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                    }
                    ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isSpecsShown) {
                                isSpecsShown = false;
                            } else {
                                isSpecsShown = true;
                            }
                            notifyItemChanged(position);
                            if (isDescShown) {
                                isDescShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(4);
                                } else {
                                    notifyItemChanged(3);
                                }
                            }
                            if (isCareShown) {
                                isCareShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(6);
                                } else {
                                    notifyItemChanged(5);
                                }
                            }
                            if (isReturnPolicyShown) {
                                isReturnPolicyShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(7);
                                } else {
                                    notifyItemChanged(6);
                                }
                            }
                            if (isFaqShown) {
                                isFaqShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(8);
                                } else {
                                    notifyItemChanged(7);
                                }
                            }
                            try {
                                ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                        ((ProductInfoHolder4) holder).descTitle.getText() + " Opened", "Product Description Page");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else if (position == 5) {
                ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descTitle.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).separator.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);

                if (obj.getProduct().is_o2o()) {
                    ((ProductInfoHolder4) holder).descTitle.setText("Specifications");
                    if (isSpecsShown) {
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);
                        if (((ProductInfoHolder4) holder).descLayout.getChildCount() == 0) {
                            LinearLayout linLayout = new LinearLayout((mContext));
                            linLayout.setOrientation(LinearLayout.VERTICAL);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            linLayout.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small));

                            if (obj.getProduct().getSku() != null && obj.getProduct().getSku().length() > 0) {
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                LinearLayout layout = new LinearLayout(mContext);
                                layout.setOrientation(LinearLayout.HORIZONTAL);
                                layout.setLayoutParams(params);
                                layout.setPadding(0, 0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_small));
                                CustomTextView unit = new CustomTextView(mContext);
                                unit.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));
                                ((LinearLayout.LayoutParams) unit.getLayoutParams()).weight = 1;
                                unit.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                                unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.font_medium));
                                unit.setText(Html.fromHtml("<font color=#535353>" + "Product Code" + "</font>"));
                                CustomTextView value = new CustomTextView(mContext);
                                value.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));
                                ((LinearLayout.LayoutParams) value.getLayoutParams()).weight = 1;
                                value.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                                value.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.font_medium));
                                value.setText(Html.fromHtml("<font color=#535353>" + obj.getProduct().getSku() + "</font>"));
                                layout.addView(unit);
                                layout.addView(value);
                                linLayout.addView(layout);
                            }

                            for (ProductAttribute attribute : obj.getProduct().getAttribute()) {

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                LinearLayout layout = new LinearLayout(mContext);
                                layout.setOrientation(LinearLayout.HORIZONTAL);
                                layout.setLayoutParams(params);
                                layout.setPadding(0, 0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_small));
                                CustomTextView unit = new CustomTextView(mContext);
                                unit.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));
                                ((LinearLayout.LayoutParams) unit.getLayoutParams()).weight = 1;
                                unit.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                                unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.font_medium));
                                unit.setText(Html.fromHtml("<font color=#535353>" + attribute.getKey() + "</font>"));
                                CustomTextView value = new CustomTextView(mContext);
                                value.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));
                                ((LinearLayout.LayoutParams) value.getLayoutParams()).weight = 1;
                                value.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                                value.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.font_medium));
                                value.setText(Html.fromHtml("<font color=#535353>" + attribute.getValue() + " " + attribute.getUnit() + "</font>"));
                                layout.addView(unit);
                                layout.addView(value);
                                linLayout.addView(layout);

                            }
                            linLayout.setLayoutParams(lp);
                            ((ProductInfoHolder4) holder).descLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                            ((ProductInfoHolder4) holder).descLayout.addView(linLayout);
                        } else {

                        }
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);

                    } else {
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4) holder).descLayout.removeAllViewsInLayout();
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                    }
                    ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isSpecsShown) {
                                isSpecsShown = false;
                            } else {
                                isSpecsShown = true;
                            }
                            notifyItemChanged(position);
                            if (isDescShown) {
                                isDescShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(4);
                                } else {
                                    notifyItemChanged(3);
                                }
                            }
                            if (isCareShown) {
                                isCareShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(6);
                                } else {
                                    notifyItemChanged(5);
                                }
                            }
                            if (isReturnPolicyShown) {
                                isReturnPolicyShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(7);
                                } else {
                                    notifyItemChanged(6);
                                }
                            }
                            if (isFaqShown) {
                                isFaqShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(8);
                                } else {
                                    notifyItemChanged(7);
                                }
                            }
                            try {
                                ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                        ((ProductInfoHolder4) holder).descTitle.getText() + " Opened", "Product Description Page");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });
                } else {
                    if (obj.getProduct().getCare().trim().length() > 0) {
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4) holder).descTitle.setText("Care");
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                        if (isCareShown) {
                            changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                            ((ProductInfoHolder4) holder).descText.setText(Html.fromHtml(obj.getProduct().getCare()));
                            ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                        } else {
                            changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                            ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                        }
                        ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (isCareShown) {
                                    isCareShown = false;
                                } else {
                                    isCareShown = true;
                                }
                                notifyItemChanged(position);
                                if (isDescShown) {
                                    isDescShown = false;
                                    if (obj.getProduct().is_o2o()) {
                                        notifyItemChanged(4);
                                    } else {
                                        notifyItemChanged(3);
                                    }
                                }
                                if (isSpecsShown) {
                                    isCareShown = false;
                                    if (obj.getProduct().is_o2o()) {
                                        notifyItemChanged(5);
                                    } else {
                                        notifyItemChanged(4);
                                    }
                                }
                                if (isReturnPolicyShown) {
                                    isReturnPolicyShown = false;
                                    if (obj.getProduct().is_o2o()) {
                                        notifyItemChanged(7);
                                    } else {
                                        notifyItemChanged(6);
                                    }
                                }
                                if (isFaqShown) {
                                    isFaqShown = false;
                                    if (obj.getProduct().is_o2o()) {
                                        notifyItemChanged(8);
                                    } else {
                                        notifyItemChanged(7);
                                    }
                                }
                                try {
                                    ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                            ((ProductInfoHolder4) holder).descTitle.getText() + " Opened", "Product Description Page");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        ((ProductInfoHolder4) holder).descTitle.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).separator.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                    }
                }
            } else if (position == 6) {
                ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descTitle.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).separator.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);

                if (obj.getProduct().is_o2o()) {
                    if (obj.getProduct().getCare().trim().length() > 0) {
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4) holder).descTitle.setText("Care");
                        if (isCareShown) {
                            ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                            ((ProductInfoHolder4) holder).descText.setText(Html.fromHtml(obj.getProduct().getCare()));
                            changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                        } else {
                            ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                            changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        }
                        ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (isCareShown) {
                                    isCareShown = false;
                                } else {
                                    isCareShown = true;
                                }
                                notifyItemChanged(position);
                                if (isDescShown) {
                                    isDescShown = false;
                                    if (obj.getProduct().is_o2o()) {
                                        notifyItemChanged(4);
                                    } else {
                                        notifyItemChanged(3);
                                    }
                                }
                                if (isSpecsShown) {
                                    isSpecsShown = false;
                                    if (obj.getProduct().is_o2o()) {
                                        notifyItemChanged(5);
                                    } else {
                                        notifyItemChanged(4);
                                    }
                                }
                                if (isReturnPolicyShown) {
                                    isReturnPolicyShown = false;
                                    if (obj.getProduct().is_o2o()) {
                                        notifyItemChanged(7);
                                    } else {
                                        notifyItemChanged(6);
                                    }
                                }
                                if (isFaqShown) {
                                    isFaqShown = false;
                                    if (obj.getProduct().is_o2o()) {
                                        notifyItemChanged(8);
                                    } else {
                                        notifyItemChanged(7);
                                    }
                                }
                                try {
                                    ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                            ((ProductInfoHolder4) holder).descTitle.getText() + " Opened", "Product Description Page");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).descTitle.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).separator.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                    }
                } else {
                    ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder4) holder).descTitle.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder4) holder).separator.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                    changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                    ((ProductInfoHolder4) holder).descTitle.setText("Return & Damage Policy");
                    if (isReturnPolicyShown) {
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                        ((ProductInfoHolder4) holder).descText.setText(Html.fromHtml(obj.getProduct().getReturn_damage()));
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                    } else {
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);

                    }

                    ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (isReturnPolicyShown) {
                                isReturnPolicyShown = false;
                            } else {
                                isReturnPolicyShown = true;
                            }
                            notifyItemChanged(position);
                            if (isDescShown) {
                                isDescShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(4);
                                } else {
                                    notifyItemChanged(3);
                                }
                            }
                            if (isSpecsShown) {
                                isSpecsShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(5);
                                } else {
                                    notifyItemChanged(4);
                                }
                            }
                            if (isCareShown) {
                                isCareShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(6);
                                } else {
                                    notifyItemChanged(5);
                                }
                            }
                            if (isFaqShown) {
                                isFaqShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(8);
                                } else {
                                    notifyItemChanged(7);
                                }
                            }
                            try {
                                ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                        ((ProductInfoHolder4) holder).descTitle.getText() + " Opened", "Product Description Page");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }
            } else if (position == 7) {
                ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descTitle.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).separator.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);

                if (obj.getProduct().is_o2o()) {
                    ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                    changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                    ((ProductInfoHolder4) holder).descTitle.setText("Return & Damage Policy");

                    if (isReturnPolicyShown) {
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                        ((ProductInfoHolder4) holder).descText.setText(Html.fromHtml(obj.getProduct().getReturn_damage()));
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                    } else {
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                    }

                    ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent(mContext, ZWebView.class);
                            intent.putExtra("title","Return & Damage Policy");
                            intent.putExtra("url", "http://www.zimply.co/return/?src=mob");
                            mContext.startActivity(intent);*/
                            if (isReturnPolicyShown) {
                                isReturnPolicyShown = false;
                            } else {
                                isReturnPolicyShown = true;
                            }
                            notifyItemChanged(position);
                            if (isDescShown) {
                                isDescShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(4);
                                } else {
                                    notifyItemChanged(3);
                                }
                            }
                            if (isSpecsShown) {
                                isSpecsShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(5);
                                } else {
                                    notifyItemChanged(4);
                                }
                            }
                            if (isCareShown) {
                                isCareShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(6);
                                } else {
                                    notifyItemChanged(5);
                                }
                            }
                            if (isFaqShown) {
                                isFaqShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(8);
                                } else {
                                    notifyItemChanged(7);
                                }
                            }
                            try {
                                ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                        ((ProductInfoHolder4) holder).descTitle.getText() + " Opened", "Product Description Page");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                    changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                    ((ProductInfoHolder4) holder).descTitle.setText("FAQ's");
                    if (isFaqShown) {
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                        ((ProductInfoHolder4) holder).descText.setText(Html.fromHtml(obj.getProduct().getFaq()));
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                    } else {
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                    }

                    ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (isFaqShown) {
                                isFaqShown = false;
                            } else {
                                isFaqShown = true;
                            }
                            notifyItemChanged(position);
                            if (isDescShown) {
                                isDescShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(4);
                                } else {
                                    notifyItemChanged(3);
                                }
                            }
                            if (isSpecsShown) {
                                isSpecsShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(5);
                                } else {
                                    notifyItemChanged(4);
                                }
                            }
                            if (isCareShown) {
                                isCareShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(6);
                                } else {
                                    notifyItemChanged(5);
                                }
                            }
                            if (isReturnPolicyShown) {
                                isReturnPolicyShown = false;
                                if (obj.getProduct().is_o2o()) {
                                    notifyItemChanged(7);
                                } else {
                                    notifyItemChanged(6);
                                }
                            }
                            try {
                                ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                        ((ProductInfoHolder4) holder).descTitle.getText() + " Opened", "Product Description Page");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else if (position == 8) {
                ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descTitle.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).separator.setVisibility(View.VISIBLE);
                ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);

                ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                ((ProductInfoHolder4) holder).descTitle.setText("FAQ's");
                if (isFaqShown) {
                    ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder4) holder).descText.setText(Html.fromHtml(obj.getProduct().getFaq()));
                    changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                } else {
                    changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                    ((ProductInfoHolder4) holder).descText.setVisibility(View.GONE);
                }

                ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isFaqShown) {
                            isFaqShown = false;
                        } else {
                            isFaqShown = true;
                        }
                        notifyItemChanged(position);
                        if (isDescShown) {
                            isDescShown = false;
                            if (obj.getProduct().is_o2o()) {
                                notifyItemChanged(4);
                            } else {
                                notifyItemChanged(3);
                            }
                        }
                        if (isSpecsShown) {
                            isSpecsShown = false;
                            if (obj.getProduct().is_o2o()) {
                                notifyItemChanged(5);
                            } else {
                                notifyItemChanged(4);
                            }
                        }
                        if (isCareShown) {
                            isCareShown = false;
                            if (obj.getProduct().is_o2o()) {
                                notifyItemChanged(6);
                            } else {
                                notifyItemChanged(5);
                            }
                        }
                        if (isReturnPolicyShown) {
                            isReturnPolicyShown = false;
                            if (obj.getProduct().is_o2o()) {
                                notifyItemChanged(7);
                            } else {
                                notifyItemChanged(6);
                            }
                        }
                        try {
                            ZTracker.logGAEvent(mContext, obj.getProduct().getName() + " Sku " + obj.getProduct().getSku(),
                                    ((ProductInfoHolder4) holder).descTitle.getText() + " Opened", "Product Description Page");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

    }

    public void changeDrawableRight(TextView view, int resId) {
        Drawable drawable = mContext.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        view.setCompoundDrawables(null, null, drawable, null);
    }

    boolean isBookingRequestStarted;


    public void showtransition(ProductInfoHolder2 holder) {
        if (!isBookingRequestStarted) {
            isBookingRequestStarted = true;
            slideViewsRightToLeft(holder, holder.bookStoreVisit, holder.loadingLayout, BOOK_BTN_CLICK);
        }
    }

    public void setIsDescShown(boolean isDescShown) {
        this.isDescShown = isDescShown;
    }

    public void setIsReturnPolicyShown(boolean isReturnPolicyShown) {
        this.isReturnPolicyShown = isReturnPolicyShown;
    }

    public void setIsSpecsShown(boolean isSpecsShown) {
        this.isSpecsShown = isSpecsShown;
    }


    public void addSimilarProducts(ArrayList<BaseProductListObject> objs) {
        this.similarProducts = new ArrayList<>();
        this.similarProducts.addAll(objs);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (obj != null) {
            if (obj.getProduct().is_o2o()) {
                if (similarProducts == null || similarProducts.size() == 0) {
                    if (products == null || products.size() == 0) {
                        if (isFooterRemoved) {
                            return 9;
                        } else {
                            return 10;
                        }
                    } else {
                        if (isFooterRemoved) {
                            return 10;
                        } else {
                            return 11;
                        }
                    }
                } else {
                    if (products == null || products.size() == 0) {
                        if (isFooterRemoved) {
                            return 10;
                        } else {
                            return 11;
                        }
                    } else {
                        if (isFooterRemoved) {
                            return 11;
                        } else {
                            return 12;
                        }
                    }
                }
            } else {
                if (similarProducts == null || similarProducts.size() == 0) {
                    if (products == null || products.size() == 0) {
                        if (isFooterRemoved) {
                            return 8;
                        } else {
                            return 9;
                        }
                    } else {
                        if (isFooterRemoved) {
                            return 9;
                        } else {
                            return 10;
                        }
                    }
                } else {
                    if (products == null || products.size() == 0) {
                        if (isFooterRemoved) {
                            return 9;
                        } else {
                            return 10;
                        }
                    } else {
                        if (isFooterRemoved) {
                            return 10;
                        } else {
                            return 11;
                        }
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP_IMAGE;
        } else if (position == 1) {
            return TYPE_PRODUCT_INFO_1;
        } else if (position == 2) {
            if (obj != null && obj.getProduct().is_o2o()) {
                return TYPE_PRODUCT_INFO_2;
            } else {
                return TYPE_PRODUCT_INFO_3;
            }
        } else if (position == 3) {
            if (obj != null && obj.getProduct().is_o2o()) {
                return TYPE_PRODUCT_INFO_3;
            } else {
                return TYPE_PRODUCT_INFO_4;
            }
        } else if (position == 4 || position == 5 || position == 6 || position == 7) {
            return TYPE_PRODUCT_INFO_4;
        } else if (position == 8) {
            if (obj != null && obj.getProduct().is_o2o()) {
                return TYPE_PRODUCT_INFO_4;
            } else {
                if (similarProducts != null && similarProducts.size() > 0) {
                    return TYPE_SIMILAR_PRODUCTS;
                } else if (products != null && products.size() > 0) {
                    return TYPE_RECENTLY_VIEWED_PRODUCTS;
                } else {
                    return TYPE_LOADER;
                }
            }
        } else if (position == 9) {
            if (obj != null && obj.getProduct().is_o2o()) {
                if (similarProducts != null && similarProducts.size() > 0) {
                    return TYPE_SIMILAR_PRODUCTS;
                } else if (products != null && products.size() > 0) {
                    return TYPE_RECENTLY_VIEWED_PRODUCTS;
                } else {
                    return TYPE_LOADER;
                }
            } else {
                if (products != null && products.size() > 0) {
                    return TYPE_RECENTLY_VIEWED_PRODUCTS;
                } else {
                    return TYPE_LOADER;
                }
            }

        } else if (position == 10) {
            if (products != null && products.size() > 0) {
                return TYPE_RECENTLY_VIEWED_PRODUCTS;
            } else {
                return TYPE_LOADER;
            }
        } else {
            return TYPE_LOADER;
        }

    }

    public void slideViewsRightToLeft(final ProductInfoHolder2 holder, final View v1, View v2, final int checkCase) {

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(v1, View.TRANSLATION_X, 0, -displayWidth);
        anim1.setDuration(300);
        v2.setVisibility(View.VISIBLE);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v2, View.TRANSLATION_X, displayWidth, 0);
        anim2.setDuration(300);
        set.playTogether(anim1, anim2);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v1.setVisibility(View.GONE);
                if (checkCase == BOOK_BTN_CLICK) {
                    startProgressBarLayout(holder);
                } else if (checkCase == PROGRESS_TIME_COMPLETE) {
                    makeProductPreviewRequest(holder);
                } else if (checkCase == PROGRESS_LOADING_COMPLETE) {
                    moveBookingCompleteCardIn(holder);
                } else if (checkCase == BOOK_PROCESS_COMPLETE) {
                    holder.storeAddress.setText(obj.getVendor().getName() + ", " + obj.getVendor().getAddress().getLine1() + ", " + obj.getVendor().getAddress().getCity());
                    holder.direction.setVisibility(View.VISIBLE);
                    holder.direction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + obj.getVendor().getAddress().getLatitude() + "," + obj.getVendor().getAddress().getLongitude() + "(" + obj.getVendor().getName() + ")");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            mContext.startActivity(intent);
                        }
                    });

                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();


    }

    boolean isShowAvailableText;

    public void setIsAvailableAtPincode(boolean isAvailableAtPincode, int position) {
        this.isAvailableAtPincode = isAvailableAtPincode;
        this.isShowAvailableText = true;
        notifyItemChanged(position);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView productImg, productNewImage;
        RecyclerView productTabIcons;
        FrameLayout imageFrame;

        public ImageViewHolder(View itemView) {
            super(itemView);
            productImg = (ImageView) itemView.findViewById(R.id.product_img);
            productTabIcons = (RecyclerView) itemView.findViewById(R.id.product_thumb_icons);
            productNewImage = (ImageView) itemView.findViewById(R.id.product_new_img);
            imageFrame = (FrameLayout) itemView.findViewById(R.id.image_layout);
        }
    }

    public class ProductInfoHolder1 extends RecyclerView.ViewHolder {
        TextView productName, outOfStock;
        TextView productPrice, sellingPrice, discount, marketPrice, priceType, mrpTag, spTag;
        ImageView favImage;

        public ProductInfoHolder1(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.product_title);
            productPrice = (TextView) itemView.findViewById(R.id.product_price);
            favImage = (ImageView) itemView.findViewById(R.id.product_fav);
            outOfStock = (TextView) itemView.findViewById(R.id.out_of_stock_text);
            sellingPrice = (TextView) itemView.findViewById(R.id.sp_price);
            marketPrice = (TextView) itemView.findViewById(R.id.mrp);
            discount = (TextView) itemView.findViewById(R.id.discount);
            priceType = (TextView) itemView.findViewById(R.id.price_type);
            mrpTag = (TextView) itemView.findViewById(R.id.mrp_tag);
            spTag = (TextView) itemView.findViewById(R.id.sp_tag);
        }
    }

    public class ProductInfoHolder2 extends RecyclerView.ViewHolder {
        TextView storeAddress, direction, bookStoreVisit, crossText;
        public ImageView mapImage, cancelBooking;
        public FrameLayout loadingLayout, requestLoaderBg;
        ProgressBar loadingBar, bookProgress;
        public LinearLayout callLayout, directionLayout, congratsLayout, mapParent;
        View bookingConfirmCard;

        public ProductInfoHolder2(View itemView) {
            super(itemView);
            storeAddress = (TextView) itemView.findViewById(R.id.store_address);
            direction = (TextView) itemView.findViewById(R.id.get_address);
            mapImage = (ImageView) itemView.findViewById(R.id.map_img);
            bookStoreVisit = (TextView) itemView.findViewById(R.id.book_store_visit);
            loadingLayout = (FrameLayout) itemView.findViewById(R.id.loading_layout);
            crossText = (TextView) itemView.findViewById(R.id.cross_img);
            loadingBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            bookProgress = (ProgressBar) itemView.findViewById(R.id.book_progress);
            bookingConfirmCard = itemView.findViewById(R.id.booking_confirm_card);
            callLayout = (LinearLayout) itemView.findViewById(R.id.call_customer);
            directionLayout = (LinearLayout) itemView.findViewById(R.id.get_direction_customer);
            congratsLayout = (LinearLayout) itemView.findViewById(R.id.congrats_layout);
            mapParent = (LinearLayout) itemView.findViewById(R.id.map_parent);
            requestLoaderBg = (FrameLayout) itemView.findViewById(R.id.request_loader_bg);
            cancelBooking = (ImageView) itemView.findViewById(R.id.close_card);
        }
    }

    public class ProductInfoHolder3 extends RecyclerView.ViewHolder {
        TextView pincode, checkPincode, delivery, shipping_charges, sold_by, cod_avaiable, isAvailableText;
        LinearLayout moreFromSeller;

        public ProductInfoHolder3(View itemView) {
            super(itemView);
            pincode = (TextView) itemView.findViewById(R.id.pincode);
            checkPincode = (TextView) itemView.findViewById(R.id.pincode_check);
            delivery = (TextView) itemView.findViewById(R.id.delivery);
            shipping_charges = (TextView) itemView.findViewById(R.id.shipping_charges);
            sold_by = (TextView) itemView.findViewById(R.id.sold_by);
            cod_avaiable = (TextView) itemView.findViewById(R.id.cod_avaiable);
            moreFromSeller = (LinearLayout) itemView.findViewById(R.id.more_from_seller);
            isAvailableText = (TextView) itemView.findViewById(R.id.is_available_pincode);

        }
    }

    public class ProductInfoHolder4 extends RecyclerView.ViewHolder {
        TextView descTitle, descText;
        LinearLayout descLayout;
        View separator;

        public ProductInfoHolder4(View itemView) {
            super(itemView);
            separator = itemView.findViewById(R.id.separator);
            descText = (TextView) itemView.findViewById(R.id.description_text);
            descTitle = (TextView) itemView.findViewById(R.id.description_title);
            descLayout = (LinearLayout) itemView.findViewById(R.id.desc_lin_layout);
        }
    }

    public class ProductSimilarProductsHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView viewAll;

        public ProductSimilarProductsHolder(View v) {
            super(v);
            recyclerView = (RecyclerView) v.findViewById(R.id.simiilarproductslist);
            viewAll = (CustomTextView) v.findViewById(R.id.view_all_similarl);
        }
    }

    OnViewsClickedListener mListener;

    public void setOnViewsClickedListener(OnViewsClickedListener listener) {
        this.mListener = listener;
    }

    public interface OnViewsClickedListener {

        void onCheckPincodeClicked(boolean isShowProgress, String pincode, int position);

        void markReviewRequest(ProductInfoHolder2 holder);

        void onCancelBookingRequest(ProductInfoHolder2 holder);

        void onMarkFavorite();

        void onMarkUnFavorite();

    }

    int seconds = 30;

    public void startProgressBarLayout(final ProductInfoHolder2 holder) {
        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((NewProductDetailActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        seconds -= 1;
                        if (seconds <= 0) {
                            seconds = 30;
                            timer.cancel();
                            // emptyBtn.setVisibility(View.GONE);
                            slideViewsRightToLeft(holder, holder.loadingLayout, holder.bookProgress, PROGRESS_TIME_COMPLETE);
                            //makeProductPreviewRequest();

                        } else {
                            holder.loadingBar.setProgress(100 - ((seconds * 100) / 30));
                        }
                    }
                });

            }
        }, 0, 50);

        holder.crossText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timer.cancel();
                seconds = 30;
                holder.loadingBar.setProgress(0);
                slideViewsLeftToRight(holder.loadingLayout, holder.bookStoreVisit, BOOK_BTN_CLICK);
                //scaleView(emptyBtn, 0.15f, 1f, false);
                isBookingRequestStarted = false;
            }
        });
    }

    public void slideViewsLeftToRight(final View v1, final View v2, int checkCase) {

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(v1, View.TRANSLATION_X, 0, displayWidth);
        anim1.setDuration(300);
        v2.setVisibility(View.VISIBLE);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v2, View.TRANSLATION_X, -displayWidth, 0);
        anim2.setDuration(300);
        set.playTogether(anim1, anim2);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v1.setVisibility(View.GONE);


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    public void makeProductPreviewRequest(ProductInfoHolder2 holder) {
        if (mListener != null) {
            mListener.markReviewRequest(holder);
        }
    }


    public void onBookComplete(ProductInfoHolder2 holder) {
        slideViewsRightToLeft(holder, holder.bookProgress, holder.congratsLayout, PROGRESS_LOADING_COMPLETE);

        //  moveBookingCompleteCardIn(holder);
    }

    public void moveBookingCompleteCardIn(final ProductInfoHolder2 holder) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mContext != null)
                    slideViewsRightToLeft(holder, holder.congratsLayout, holder.bookingConfirmCard, BOOK_PROCESS_COMPLETE);
            }
        }, 1000);
    }

    public void onBookCancelledSuccessfully(final ProductInfoHolder2 holder) {
        isCancelBookingShown = false;
        notifyDataSetChanged();
        // slideViewsLeftToRight(holder.bookingConfirmCard,holder.bookStoreVisit,BOOK_BTN_CLICK);
    }

    class SimilarProductsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.product_grid_item_layout, parent, false);
            SimilarProductHolder holder = new SimilarProductHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            RecyclerView.LayoutParams paramsContainer = (RecyclerView.LayoutParams) ((SimilarProductHolder) holder).container.getLayoutParams();
            paramsContainer.leftMargin = similarProductsitemMargin;
            paramsContainer.rightMargin = similarProductsitemMargin;
            ((SimilarProductHolder) holder).container.setLayoutParams(paramsContainer);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    similarProductsItemWidth, similarProductsItemHeight);
            ((SimilarProductHolder) holder).img.setLayoutParams(lp);
            if (similarProducts.get(position).getImage() != null) {
                if (((SimilarProductHolder) holder).img.getTag() == null
                        || !(((String) ((SimilarProductHolder) holder).img
                        .getTag()).equalsIgnoreCase(similarProducts.get(position)
                        .getImage()))) {

                    new ImageLoaderManager(((NewProductDetailActivity) mContext)).setImageFromUrl(
                            similarProducts.get(position).getImage(),
                            ((SimilarProductHolder) holder).img, "users", similarProductsItemHeight,
                            similarProductsItemHeight, true, false);

                    ((SimilarProductHolder) holder).img.setTag(similarProducts.get(position)
                            .getImage());
                }
            }
            if (similarProducts.get(position).is_o2o()) {
                ((SimilarProductHolder) holder).buyOfflineTag.setVisibility(View.VISIBLE);
            } else {
                ((SimilarProductHolder) holder).buyOfflineTag.setVisibility(View.INVISIBLE);
            }

            ((SimilarProductHolder) holder).productName.setText(similarProducts.get(position)
                    .getName());
            try {
                if (similarProducts.get(position).getMrp() != similarProducts.get(position).getPrice()) {
                    ((SimilarProductHolder) holder).productDiscountedPrice.setVisibility(View.VISIBLE);
                    ((SimilarProductHolder) holder).productDiscountedPrice
                            .setText(mContext.getString(R.string.Rs) + " "
                                    + similarProducts.get(position).getPrice());
                    ((SimilarProductHolder) holder).productPrice.setVisibility(View.VISIBLE);
                    ((SimilarProductHolder) holder).productPrice.setText(mContext
                            .getString(R.string.Rs)
                            + " "
                            + similarProducts.get(position).getMrp());
                    ((SimilarProductHolder) holder).productPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.font_small));
                    ((SimilarProductHolder) holder).productPrice.setTextColor(mContext.getResources().getColor(R.color.zdhl5));
                    ((SimilarProductHolder) holder).productPrice.setTypeface(CommonLib.getTypeface(mContext, CommonLib.REGULAR_FONT), Typeface.NORMAL);
                    ((SimilarProductHolder) holder).productPrice
                            .setPaintFlags(((SimilarProductHolder) holder).productPrice
                                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((SimilarProductHolder) holder).productDiscountFactor.setVisibility(View.VISIBLE);
                    ((SimilarProductHolder) holder).productDiscountFactor.setText(similarProducts.get(position).getDiscount() + "% OFF");
                    /*((SimilarProductHolder) holder).productName.setSingleLine(true);
                    ((SimilarProductHolder) holder).productName.setMaxLines(1);
                    ((SimilarProductHolder) holder).productName.requestLayout();*/


                } else {
                    ((SimilarProductHolder) holder).productDiscountedPrice.setVisibility(View.INVISIBLE);
                    ((SimilarProductHolder) holder).productPrice.setTextColor(mContext.getResources().getColor(R.color.heading_text_color));
                    ((SimilarProductHolder) holder).productPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.font_medium));
                    ((SimilarProductHolder) holder).productPrice.setTypeface(CommonLib.getTypeface(mContext, CommonLib.BOLD_FONT), Typeface.BOLD);
                    ((SimilarProductHolder) holder).productPrice.setPaintFlags(0);
                    ((SimilarProductHolder) holder).productPrice.setText(mContext.getString(R.string.Rs) + " "
                            + Math.round(similarProducts.get(position).getPrice()));
                    ((SimilarProductHolder) holder).productDiscountFactor.setVisibility(View.GONE);
                    //((SimilarProductHolder) holder).productName.setSingleLine(false);
                    // ((SimilarProductHolder) holder).productName.setMaxLines(2);
                    // ((SimilarProductHolder) holder).productName.requestLayout();

                }
            } catch (NumberFormatException e) {

            }

            ((SimilarProductHolder) holder).container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppPreferences.isUserLogIn(mContext)) {
                    } else {
                        RecentProductsDBWrapper.addProduct(similarProducts.get(position), 1, System.currentTimeMillis());
                    }
                    Intent intent = new Intent(mContext, NewProductDetailActivity.class);
                    intent.putExtra("slug", similarProducts.get(position).getSlug());
                    intent.putExtra("id", similarProducts.get(position).getId());
                    intent.putExtra("title", similarProducts.get(position).getName());

                    //        GA Ecommerce
                    intent.putExtra("productActionListName", "Similar Product Click on Product ID " + ((NewProductDetailActivity) mContext).productId);
                    intent.putExtra("screenName", "Product Detail Activity");
                    intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);

                    mContext.startActivity(intent);
                    ((NewProductDetailActivity) mContext).finish();
                }
            });


        }

        @Override
        public int getItemCount() {
            return similarProducts.size();
        }

        class SimilarProductHolder extends RecyclerView.ViewHolder {

            ImageView img, buyOfflineTag;
            TextView productName, productDiscountedPrice, productPrice, productDiscountFactor;
            LinearLayout container;

            public SimilarProductHolder(View view) {
                super(view);
                img = (ImageView) view.findViewById(R.id.product_img);
                productName = (TextView) view.findViewById(R.id.product_name);
                productDiscountedPrice = (TextView) view
                        .findViewById(R.id.product_disounted_price);
                productPrice = (TextView) view
                        .findViewById(R.id.product_price);
                productDiscountFactor = (TextView) view.findViewById(R.id.product_disounted_factor);
                buyOfflineTag = (ImageView) view.findViewById(R.id.buy_offline_tag);
                container = (LinearLayout) view.findViewById(R.id.productgriditemcoontainerlayout);
            }
        }
    }

    public class ProductRecentProductsHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView viewAll, recentProductsTag;

        public ProductRecentProductsHolder(View v) {
            super(v);
            recyclerView = (RecyclerView) v.findViewById(R.id.simiilarproductslist);
            viewAll = (CustomTextView) v.findViewById(R.id.view_all_similarl);
            recentProductsTag = (TextView) v.findViewById(R.id.recent_products_tag);
        }
    }

    class ProductRecentProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.product_grid_item_layout, parent, false);
            SimilarProductHolder holder = new SimilarProductHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            RecyclerView.LayoutParams paramsContainer = (RecyclerView.LayoutParams) ((SimilarProductHolder) holder).container.getLayoutParams();
            paramsContainer.leftMargin = similarProductsitemMargin;
            paramsContainer.rightMargin = similarProductsitemMargin;
            ((SimilarProductHolder) holder).container.setLayoutParams(paramsContainer);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    similarProductsItemWidth, similarProductsItemHeight);
            ((SimilarProductHolder) holder).img.setLayoutParams(lp);
            if (products.get(position).getImage() != null) {
                if (((SimilarProductHolder) holder).img.getTag() == null
                        || !(((String) ((SimilarProductHolder) holder).img
                        .getTag()).equalsIgnoreCase(products.get(position)
                        .getImage()))) {

                    new ImageLoaderManager(((NewProductDetailActivity) mContext)).setImageFromUrl(
                            products.get(position).getImage(),
                            ((SimilarProductHolder) holder).img, "users", similarProductsItemHeight,
                            similarProductsItemHeight, true, false);

                    ((SimilarProductHolder) holder).img.setTag(products.get(position)
                            .getImage());
                }
            }
            if (products.get(position).is_o2o()) {
                ((SimilarProductHolder) holder).buyOfflineTag.setVisibility(View.VISIBLE);
            } else {
                ((SimilarProductHolder) holder).buyOfflineTag.setVisibility(View.INVISIBLE);
            }

            ((SimilarProductHolder) holder).productName.setText(products.get(position)
                    .getName());
            try {
                if (products.get(position).getMrp() != products.get(position).getPrice()) {
                    ((SimilarProductHolder) holder).productDiscountedPrice.setVisibility(View.VISIBLE);
                    ((SimilarProductHolder) holder).productDiscountedPrice
                            .setText(mContext.getString(R.string.Rs) + " "
                                    + products.get(position).getPrice());
                    ((SimilarProductHolder) holder).productPrice.setVisibility(View.VISIBLE);
                    ((SimilarProductHolder) holder).productPrice.setText(mContext
                            .getString(R.string.Rs)
                            + " "
                            + products.get(position).getMrp());
                    ((SimilarProductHolder) holder).productPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.font_small));
                    ((SimilarProductHolder) holder).productPrice.setTextColor(mContext.getResources().getColor(R.color.zdhl5));
                    ((SimilarProductHolder) holder).productPrice.setTypeface(CommonLib.getTypeface(mContext, CommonLib.REGULAR_FONT), Typeface.NORMAL);
                    ((SimilarProductHolder) holder).productPrice
                            .setPaintFlags(((SimilarProductHolder) holder).productPrice
                                    .getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((SimilarProductHolder) holder).productDiscountFactor.setVisibility(View.VISIBLE);
                    ((SimilarProductHolder) holder).productDiscountFactor.setText(products.get(position).getDiscount() + "% OFF");
                    /*((SimilarProductHolder) holder).productName.setSingleLine(true);
                    ((SimilarProductHolder) holder).productName.setMaxLines(1);
                    ((SimilarProductHolder) holder).productName.requestLayout();*/


                } else {
                    ((SimilarProductHolder) holder).productDiscountedPrice.setVisibility(View.INVISIBLE);
                    ((SimilarProductHolder) holder).productPrice.setTextColor(mContext.getResources().getColor(R.color.heading_text_color));
                    ((SimilarProductHolder) holder).productPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.font_medium));
                    ((SimilarProductHolder) holder).productPrice.setTypeface(CommonLib.getTypeface(mContext, CommonLib.BOLD_FONT), Typeface.BOLD);
                    ((SimilarProductHolder) holder).productPrice.setPaintFlags(0);
                    ((SimilarProductHolder) holder).productPrice.setText(mContext.getString(R.string.Rs) + " "
                            + Math.round(products.get(position).getPrice()));
                    ((SimilarProductHolder) holder).productDiscountFactor.setVisibility(View.GONE);
                    //((SimilarProductHolder) holder).productName.setSingleLine(false);
                    // ((SimilarProductHolder) holder).productName.setMaxLines(2);
                    // ((SimilarProductHolder) holder).productName.requestLayout();

                }
            } catch (NumberFormatException e) {

            }

            ((SimilarProductHolder) holder).container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (AppPreferences.isUserLogIn(mContext)) {
                    } else {
                        RecentProductsDBWrapper.addProduct(products.get(position), 1, System.currentTimeMillis());
                    }
                    Intent intent = new Intent(mContext, NewProductDetailActivity.class);
                    intent.putExtra("slug", products.get(position).getSlug());
                    intent.putExtra("id", products.get(position).getId());
                    intent.putExtra("title", products.get(position).getName());

                    //        GA Ecommerce
                    intent.putExtra("productActionListName", "Recent Product Click on Product ID " + products.get(position).getId());
                    intent.putExtra("screenName", "Product Detail Activity");
                    intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);

                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        class SimilarProductHolder extends RecyclerView.ViewHolder {

            ImageView img, buyOfflineTag;
            TextView productName, productDiscountedPrice, productPrice, productDiscountFactor;
            LinearLayout container;

            public SimilarProductHolder(View view) {
                super(view);
                img = (ImageView) view.findViewById(R.id.product_img);
                productName = (TextView) view.findViewById(R.id.product_name);
                productDiscountedPrice = (TextView) view
                        .findViewById(R.id.product_disounted_price);
                productPrice = (TextView) view
                        .findViewById(R.id.product_price);
                productDiscountFactor = (TextView) view.findViewById(R.id.product_disounted_factor);
                buyOfflineTag = (ImageView) view.findViewById(R.id.buy_offline_tag);
                container = (LinearLayout) view.findViewById(R.id.productgriditemcoontainerlayout);
            }
        }

    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View view) {
            super(view);
        }

    }
}

