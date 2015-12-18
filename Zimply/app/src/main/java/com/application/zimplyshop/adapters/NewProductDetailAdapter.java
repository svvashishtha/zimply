package com.application.zimplyshop.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.baseobjects.ProductAttribute;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.widgets.CustomTextView;
import com.application.zimplyshop.widgets.ProductThumbListItemDecorator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Umesh Lohani on 12/11/2015.
 */
public class NewProductDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    int TYPE_TOP_IMAGE = 0;

    int TYPE_PRODUCT_INFO_1 = 1;
    int TYPE_PRODUCT_INFO_2 = 2;
    int TYPE_PRODUCT_INFO_3 = 3;
    int TYPE_PRODUCT_INFO_4 = 4;

    int BOOK_BTN_CLICK=1;
    int PROGRESS_TIME_COMPLETE = 2;
    int PROGRESS_LOADING_COMPLETE=3;
    int BOOK_PROCESS_COMPLETE = 4;

    ProductInfoHolder2 refernceHolder;

    boolean isDescShown,isSpecsShown,isReturnPolicyShown,isCancelBookingShown,isCareShown,isFaqShown;

    HomeProductObj obj;

    Context mContext;

    int displayWidth,displayHeight;

    boolean isAvailableAtPincode;



    public NewProductDetailAdapter(Context context,int displayWidth,int displayHeight,HomeProductObj obj){
        this.mContext = context;
        this.displayHeight = displayHeight;
        this.displayWidth = displayWidth;
        this.obj = obj;
    }


    public HomeProductObj getObj() {
        return obj;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == TYPE_TOP_IMAGE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_tab_info_top,parent,false);
            holder = new ImageViewHolder(view);
        }else if(viewType == TYPE_PRODUCT_INFO_1){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_product_info_layout_1,parent,false);
            holder = new ProductInfoHolder1(view);
        }else if(viewType == TYPE_PRODUCT_INFO_2){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_product_info_layout_2,parent,false);
            holder = new ProductInfoHolder2(view);
            this.refernceHolder = (ProductInfoHolder2)holder;
        }else if(viewType == TYPE_PRODUCT_INFO_3){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_product_info_layout_3,parent,false);
            holder = new ProductInfoHolder3(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_product_info_layout_desc,parent,false);
            holder = new ProductInfoHolder4(view);

        }
        return holder;
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

        if(getItemViewType(position) == TYPE_TOP_IMAGE){
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(displayWidth,displayWidth);
            ((ImageViewHolder)holder).productImg.setLayoutParams(lp);

            ((ImageViewHolder)holder).imageFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProductPhotoZoomActivity.class);
                    intent.putExtra("product_obj", obj);
                    intent.putExtra("position", (Integer) ((ImageViewHolder) holder).productNewImage.getTag());
                    mContext.startActivity(intent);
                }
            });

            new ImageLoaderManager((NewProductDetailActivity) mContext).setImageFromUrl(obj.getThumbs().get(0), ((ImageViewHolder) holder).productImg, "users", displayWidth / 2, displayHeight / 20, false,
                    false);

            ((ImageViewHolder)holder).productNewImage.setLayoutParams(lp);
            ((ImageViewHolder)holder).productNewImage.setTag(0);
            new ImageLoaderManager((NewProductDetailActivity) mContext).setImageFromUrlNew(obj.getImageUrls().get(0), ((ImageViewHolder) holder).productNewImage, "photo_details", displayWidth / 2, displayHeight / 20, false,
                    false, new ImageLoaderManager.ImageLoaderCallback() {
                        @Override
                        public void loadingStarted() {

                        }

                        @Override
                        public void loadingFinished(Bitmap bitmap) {
                            ((ImageViewHolder)holder).productImg.setVisibility(View.GONE);
                        }
                    });
            if(((ImageViewHolder)holder).productTabIcons.getAdapter()==null) {
                ((ImageViewHolder) holder).productTabIcons.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

                ((ImageViewHolder) holder).productTabIcons.addItemDecoration(new ProductThumbListItemDecorator(mContext.getResources().getDimensionPixelSize(R.dimen.margin_small)));
                final ProductThumbAdapters adapter = new ProductThumbAdapters(mContext, obj.getThumbs(), mContext.getResources().getDimensionPixelSize(R.dimen.z_item_height_48), mContext.getResources().getDimensionPixelSize(R.dimen.z_item_height_48));
                ((ImageViewHolder) holder).productTabIcons.setAdapter(adapter);
                adapter.setOnItemClickListener(new ProductThumbAdapters.OnItemClickListener() {
                    @Override
                    public void onItemClick(final int pos) {
                        if (adapter.getSelectedPos() != pos) {
                            adapter.setSelectedPos(pos);
                            ((ImageViewHolder)holder).productImg.setVisibility(View.VISIBLE);
                            ((ImageViewHolder) holder).productNewImage.setTag(pos);
                            new ImageLoaderManager((NewProductDetailActivity) mContext).setImageFromUrl(obj.getThumbs().get(pos), ((ImageViewHolder) holder).productImg, "users", displayWidth / 2, displayHeight / 20, false,
                                    false);

                            new ImageLoaderManager((NewProductDetailActivity) mContext).setImageFromUrlNew(obj.getImageUrls().get(pos), ((ImageViewHolder) holder).productNewImage, "photo_details", displayWidth / 2, displayHeight / 20, false,
                                    false, new ImageLoaderManager.ImageLoaderCallback() {
                                        @Override
                                        public void loadingStarted() {

                                        }

                                        @Override
                                        public void loadingFinished(Bitmap bitmap) {
                                            ((ImageViewHolder)holder).productImg.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    }
                });
            }
        }else if(getItemViewType(position) == TYPE_PRODUCT_INFO_1){
            ((ProductInfoHolder1)holder).productPrice.setText(mContext.getString(R.string.rs_text)+" "+Math.round(obj.getPrice()));
            ((ProductInfoHolder1)holder).productName.setText(obj.getName());
            ((ProductInfoHolder1)holder).favImage.setSelected(obj.is_favourite());
            ((ProductInfoHolder1)holder).favImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppPreferences.isUserLogIn(mContext)) {
                        if (obj.is_favourite()) {
                            obj.setIs_favourite(false);
                            ((ProductInfoHolder1)holder).favImage.setSelected(false);
                            mListener.onMarkUnFavorite();
                            Toast.makeText(mContext, "Successfully removed from wishlist", Toast.LENGTH_SHORT).show();
                        } else {
                            obj.setIs_favourite(true);
                            ((ProductInfoHolder1)holder).favImage.setSelected(true);
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
        }else if(getItemViewType(position) == TYPE_PRODUCT_INFO_2){
            FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,displayHeight/5);
            ((ProductInfoHolder2) holder).mapImage.setLayoutParams(lp1);
            new ImageLoaderManager((NewProductDetailActivity)mContext).setImageFromUrl(obj.getVendor().getMap(), ((ProductInfoHolder2) holder).mapImage, "users", displayWidth, displayHeight / 5, false, false);
            if(isCancelBookingShown){
                ((ProductInfoHolder2)holder).storeAddress.setText(obj.getVendor().getCompany_name() + ", " + obj.getVendor().getReg_add().getLine1() + ", " + obj.getVendor().getReg_add().getCity());
                ((ProductInfoHolder2) holder).bookStoreVisit.setVisibility(View.GONE);
                ((ProductInfoHolder2) holder).bookingConfirmCard.setVisibility(View.VISIBLE);
                ((ProductInfoHolder2) holder).cancelBooking.setVisibility(View.GONE);
                ((ProductInfoHolder2) holder).cancelBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener!=null){
                            mListener.onCancelBookingRequest(((ProductInfoHolder2) holder));
                        }
                    }
                });
            }else{
                ((ProductInfoHolder2)holder).storeAddress.setText(obj.getVendor().getReg_add().getCity());
                ((ProductInfoHolder2) holder).bookStoreVisit.setVisibility(View.VISIBLE);
                ((ProductInfoHolder2) holder).bookingConfirmCard.setVisibility(View.GONE);
                ((ProductInfoHolder2) holder).cancelBooking.setVisibility(View.GONE);
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


            ((ProductInfoHolder2)holder).callLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + obj.getVendor().getReg_add().getPhone()));
                    mContext.startActivity(callIntent);
                }
            });
            ((ProductInfoHolder2)holder).direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + obj.getVendor().getReg_add().getLocation().getLatitude() + "," + obj.getVendor().getReg_add().getLocation().getLongitude() + "(" + obj.getVendor().getCompany_name() + ")");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);
                }
            });
            ((ProductInfoHolder2)holder).mapImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + obj.getVendor().getReg_add().getLocation().getLatitude() + "," + obj.getVendor().getReg_add().getLocation().getLongitude() + "(" + obj.getVendor().getCompany_name() + ")");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);
                }
            });
            ((ProductInfoHolder2)holder).directionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + obj.getVendor().getReg_add().getLocation().getLatitude() + "," + obj.getVendor().getReg_add().getLocation().getLongitude() + "(" + obj.getVendor().getCompany_name() + ")");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);

                }
            });

        }else if(getItemViewType(position) == TYPE_PRODUCT_INFO_3){
            if (AppPreferences.isPincodeSaved(mContext)) {
                String pincode = AppPreferences.getSavedPincode(mContext);
                ((ProductInfoHolder3)holder).pincode.setText(pincode);
                // mListener.onCheckPincodeClicked(false, pincode);
            }

            if(isShowAvailableText){
                ((ProductInfoHolder3) holder).isAvailableText.setVisibility(View.VISIBLE);
            }else{
                ((ProductInfoHolder3) holder).isAvailableText.setVisibility(View.GONE);
            }

            if(isAvailableAtPincode) {

                ((ProductInfoHolder3) holder).isAvailableText.setTextColor(mContext.getResources().getColor(R.color.green_text_color));
                ((ProductInfoHolder3) holder).isAvailableText.setText("Available at selected pincode");
            }else{

                ((ProductInfoHolder3) holder).isAvailableText.setTextColor(mContext.getResources().getColor(R.color.red_text_color));
                ((ProductInfoHolder3) holder).isAvailableText.setText("Not Available at selected pincode");
            }
            ((ProductInfoHolder3)holder).checkPincode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ProductInfoHolder3) holder).pincode.getText().toString().length() == 6) {
                        mListener.onCheckPincodeClicked(true, ((ProductInfoHolder3) holder).pincode.getText().toString());
                    } else {
                        // showToast("Please enter a valid pincode");
                        //  Toast.makeText(ProductDetailsActivity.this, "Please enter a valid pincode", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            /*((ProductInfoHolder3) holder).pincode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String input = s.toString();
                    if (input.length() == 6) {
                        mListener.onCheckPincodeClicked(true, ((ProductInfoHolder3) holder).pincode.getText().toString());
                    }
                }
            });*/
            ((ProductInfoHolder3) holder).delivery.setText(obj.getMinShippingDays() + "-" + obj.getMaxShippingDays() + " Days");
            ((ProductInfoHolder3) holder).shipping_charges.setText((obj.getShippingCharges()==0)?"Free":mContext.getString(R.string.rs_text) + " " + Math.round(obj.getShippingCharges()));
            ((ProductInfoHolder3) holder).coc_avaiable.setText((obj.is_o2o()) ? "Available" : "Not Available");
            SpannableString string = new SpannableString(obj.getVendor().getCompany_name());
            string.setSpan(new UnderlineSpan(), 0, obj.getVendor().getCompany_name().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            string.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.z_rate_btn_blue_color)), 0, obj.getVendor().getCompany_name().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((ProductInfoHolder3) holder).sold_by.setText(string);
            ((ProductInfoHolder3)holder).moreFromSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent bookIntent = new Intent(mContext, BookingStoreProductListingActivity.class);
                    bookIntent.putExtra("hide_filter",true);
                    bookIntent.putExtra("vendor_id", obj.getVendor().getVendor_id());
                    bookIntent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
                    bookIntent.putExtra("vendor_name", obj.getVendor().getCompany_name());
                    mContext.startActivity(bookIntent);
                }
            });
        }else {

            if(position == 3){
                ((ProductInfoHolder4)holder).descTitle.setText("Summary");

                if(isDescShown){
                    changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_up_black);
                    ((ProductInfoHolder4)holder).descText.setText(obj.getDescription());
                    ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                    ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                }else{
                    changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_down_black);
                    ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                    ((ProductInfoHolder4)holder).descText.setVisibility(View.GONE);
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
                    }
                });
            }else if(position == 4){
                if(obj.is_o2o()) {
                    ((ProductInfoHolder4) holder).descTitle.setText("Summary");

                    if (isDescShown) {

                        changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_up_black);
                        ((ProductInfoHolder4) holder).descText.setText(obj.getDescription());
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                        ((ProductInfoHolder4) holder).descText.setVisibility(View.VISIBLE);
                    } else {
                        changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_down_black);
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
                        }
                    });
                }else{
                    ((ProductInfoHolder4)holder).descTitle.setText("Specifications");
                    if(isSpecsShown) {
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);
                        if(((ProductInfoHolder4) holder).descLayout.getChildCount()==0) {
                            LinearLayout linLayout = new LinearLayout((mContext));
                            linLayout.setOrientation(LinearLayout.VERTICAL);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            linLayout.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small));
                            for (ProductAttribute attribute : obj.getAttributes()) {

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

                        }else{

                        }
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.GONE);

                    }else{
                        ((ProductInfoHolder4) holder).descLayout.removeAllViewsInLayout();
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle,R.drawable.ic_down_black);
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.GONE);
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.GONE);
                    }
                    ((ProductInfoHolder4)holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isSpecsShown){
                                isSpecsShown= false;
                            }else{
                                isSpecsShown=true;
                            }
                            notifyItemChanged(position);
                        }
                    });
                }
            }else if(position == 5){
                if(obj.is_o2o()) {
                    ((ProductInfoHolder4) holder).descTitle.setText("Specifications");
                    if (isSpecsShown) {
                        changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_up_black);
                        ((ProductInfoHolder4) holder).descLayout.setVisibility(View.VISIBLE);
                        if(((ProductInfoHolder4) holder).descLayout.getChildCount()==0) {
                            LinearLayout linLayout = new LinearLayout((mContext));
                            linLayout.setOrientation(LinearLayout.VERTICAL);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            linLayout.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small), mContext.getResources().getDimensionPixelSize(R.dimen.margin_small));
                            for (ProductAttribute attribute : obj.getAttributes()) {

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                LinearLayout layout = new LinearLayout(mContext);
                                layout.setOrientation(LinearLayout.HORIZONTAL);
                                layout.setLayoutParams(params);
                                layout.setPadding(0,0,0,mContext.getResources().getDimensionPixelSize(R.dimen.margin_small));
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
                        }else{

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
                        }
                    });
                }else{
                    changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_down_black);
                    ((ProductInfoHolder4)holder).descTitle.setText("Care");
                    ((ProductInfoHolder4)holder).descLayout.setVisibility(View.GONE);
                    if(isCareShown){
                        changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_up_black);
                        ((ProductInfoHolder4)holder).descText.setText(Html.fromHtml(obj.getCare()));
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.VISIBLE);
                    }else{
                        changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_down_black);
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.GONE);
                    }
                    ((ProductInfoHolder4)holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent(mContext, ZWebView.class);
                            intent.putExtra("title", mContext.getResources().getString(R.string.about_us_terms_of_use));
                            intent.putExtra("url", "http://www.zimply.co/care/?src=mob");
                            mContext.startActivity(intent);*/
                            if(isCareShown){
                                isCareShown = false;
                            }else{
                                isCareShown = true;
                            }
                            notifyItemChanged(position);
                        }
                    });

                }
            }else if(position == 6){
                if(obj.is_o2o()){
                    ((ProductInfoHolder4)holder).descLayout.setVisibility(View.GONE);
                    changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_down_black);
                    ((ProductInfoHolder4)holder).descTitle.setText("Care");
                    if(isCareShown){
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.VISIBLE);
                        ((ProductInfoHolder4)holder).descText.setText(Html.fromHtml(obj.getCare()));
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                    }else{
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.GONE);
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                    }
                    ((ProductInfoHolder4)holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent(mContext, ZWebView.class);
                            intent.putExtra("title", mContext.getResources().getString(R.string.about_us_terms_of_use));
                            intent.putExtra("url", "http://www.zimply.co/care/?src=mob");
                            mContext.startActivity(intent);*/
                            if(isCareShown){
                                isCareShown = false;
                            }else{
                                isCareShown = true;
                            }
                            notifyItemChanged(position);
                        }
                    });
                }else{
                    ((ProductInfoHolder4)holder).descLayout.setVisibility(View.GONE);
                    changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_down_black);
                    ((ProductInfoHolder4)holder).descTitle.setText("Return & Damage Policy");
                    if(isReturnPolicyShown){
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.VISIBLE);
                        ((ProductInfoHolder4)holder).descText.setText(Html.fromHtml(obj.getReturnPolicy()));
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                    }else{
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.GONE);

                    }

                    ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent(mContext, ZWebView.class);
                            intent.putExtra("title","Return & Damage Policy");
                            intent.putExtra("url", "http://www.zimply.co/return/?src=mob");
                            mContext.startActivity(intent);*/
                            if(isReturnPolicyShown){
                                isReturnPolicyShown=false;
                            }else{
                                isReturnPolicyShown=true;
                            }
                            notifyItemChanged(position);
                        }
                    });
                }
            }else if(position == 7){
                if(obj.is_o2o()){
                    ((ProductInfoHolder4)holder).descLayout.setVisibility(View.GONE);
                    changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_down_black);
                    ((ProductInfoHolder4)holder).descTitle.setText("Return & Damage Policy");

                    if(isReturnPolicyShown){
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.VISIBLE);
                        ((ProductInfoHolder4)holder).descText.setText(Html.fromHtml(obj.getReturnPolicy()));
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                    }else{
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.GONE);
                    }

                    ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent(mContext, ZWebView.class);
                            intent.putExtra("title","Return & Damage Policy");
                            intent.putExtra("url", "http://www.zimply.co/return/?src=mob");
                            mContext.startActivity(intent);*/
                            if(isReturnPolicyShown){
                                isReturnPolicyShown=false;
                            }else{
                                isReturnPolicyShown=true;
                            }
                            notifyItemChanged(position);
                        }
                    });
                }else{
                    ((ProductInfoHolder4)holder).descLayout.setVisibility(View.GONE);
                    changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_down_black);
                    ((ProductInfoHolder4)holder).descTitle.setText("FAQ's");
                    if(isFaqShown){
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.VISIBLE);
                        ((ProductInfoHolder4)holder).descText.setText(Html.fromHtml(obj.getFaq()));
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                    }else{
                        changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                        ((ProductInfoHolder4)holder).descText.setVisibility(View.GONE);
                    }

                    ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent(mContext, ZWebView.class);
                            intent.putExtra("title","FAQ's");
                            intent.putExtra("url", "http://www.zimply.co/faq/?src=mob");
                            mContext.startActivity(intent);*/
                            if(isFaqShown){
                                isFaqShown = false;
                            }else{
                                isFaqShown = true;
                            }
                            notifyItemChanged(position);
                        }
                    });
                }
            }else if(position == 8){
                ((ProductInfoHolder4)holder).descLayout.setVisibility(View.GONE);
                changeDrawableRight(((ProductInfoHolder4)holder).descTitle,R.drawable.ic_down_black);
                ((ProductInfoHolder4)holder).descTitle.setText("FAQ's");
                if(isFaqShown){
                    ((ProductInfoHolder4)holder).descText.setVisibility(View.VISIBLE);
                    ((ProductInfoHolder4)holder).descText.setText(Html.fromHtml(obj.getFaq()));
                    changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_up_black);
                }else{
                    changeDrawableRight(((ProductInfoHolder4) holder).descTitle, R.drawable.ic_down_black);
                    ((ProductInfoHolder4)holder).descText.setVisibility(View.GONE);
                }

                ((ProductInfoHolder4) holder).descTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            /*Intent intent = new Intent(mContext, ZWebView.class);
                            intent.putExtra("title","FAQ's");
                            intent.putExtra("url", "http://www.zimply.co/faq/?src=mob");
                            mContext.startActivity(intent);*/
                        if(isFaqShown){
                            isFaqShown = false;
                        }else{
                            isFaqShown = true;
                        }
                        notifyItemChanged(position);
                    }
                });
            }
        }

    }

    public void changeDrawableRight(TextView view,int resId){
        Drawable drawable = mContext.getResources().getDrawable(resId);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        view.setCompoundDrawables(null,null,drawable,null);
    }

    public void showtransition(ProductInfoHolder2 holder){
        slideViewsRightToLeft(holder, holder.bookStoreVisit, holder.loadingLayout, BOOK_BTN_CLICK);
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

    @Override
    public int getItemCount() {
        if(obj!=null){
            if(obj.is_o2o()){
                return 9;
            }else{
                return 8;
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(position ==0){
            return TYPE_TOP_IMAGE;
        }else if(position==1){
            return TYPE_PRODUCT_INFO_1;
        }else if(position == 2){
            if(obj!=null && obj.is_o2o()) {
                return TYPE_PRODUCT_INFO_2;
            }else{
                return TYPE_PRODUCT_INFO_3;
            }
        }else if(position ==3){
            if(obj!=null && obj.is_o2o()) {
                return TYPE_PRODUCT_INFO_3;
            }else{
                return TYPE_PRODUCT_INFO_4;
            }
        }else{
            return TYPE_PRODUCT_INFO_4;
        }
    }

    public void slideViewsRightToLeft(final ProductInfoHolder2 holder,final View v1,View v2,final int checkCase){

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(v1,View.TRANSLATION_X,0,-displayWidth);
        anim1.setDuration(300);
        v2.setVisibility(View.VISIBLE);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v2,View.TRANSLATION_X,displayWidth,0);
        anim2.setDuration(300);
        set.playTogether(anim1, anim2);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v1.setVisibility(View.GONE);
                if(checkCase==BOOK_BTN_CLICK){
                    startProgressBarLayout(holder);
                }else if(checkCase == PROGRESS_TIME_COMPLETE){
                    makeProductPreviewRequest(holder);
                }else if(checkCase == PROGRESS_LOADING_COMPLETE){
                    moveBookingCompleteCardIn(holder);
                }else if(checkCase == BOOK_PROCESS_COMPLETE){
                    holder.storeAddress.setText(obj.getVendor().getCompany_name() + ", " + obj.getVendor().getReg_add().getLine1() + ", " + obj.getVendor().getReg_add().getCity());
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

    public void setIsAvailableAtPincode(boolean isAvailableAtPincode) {
        this.isAvailableAtPincode = isAvailableAtPincode;
        this.isShowAvailableText =true;
        notifyItemChanged(3);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView productImg,productNewImage;
        RecyclerView productTabIcons;
        FrameLayout imageFrame;
        public ImageViewHolder(View itemView) {
            super(itemView);
            productImg = (ImageView)itemView.findViewById(R.id.product_img);
            productTabIcons = (RecyclerView)itemView.findViewById(R.id.product_thumb_icons);
            productNewImage = (ImageView)itemView.findViewById(R.id.product_new_img);
            imageFrame = (FrameLayout)itemView.findViewById(R.id.image_layout);
        }
    }
    public class ProductInfoHolder1 extends RecyclerView.ViewHolder{
        TextView productName;
        TextView productPrice;
        ImageView favImage;
        public ProductInfoHolder1(View itemView) {
            super(itemView);
            productName = (TextView)itemView.findViewById(R.id.product_title);
            productPrice=(TextView)itemView.findViewById(R.id.product_price);
            favImage = (ImageView)itemView.findViewById(R.id.product_fav);
        }
    }

    public class ProductInfoHolder2 extends RecyclerView.ViewHolder{
        TextView storeAddress,direction,bookStoreVisit,crossText;
        public ImageView mapImage,cancelBooking;
        public FrameLayout loadingLayout,requestLoaderBg;
        ProgressBar loadingBar,bookProgress;
        public LinearLayout callLayout,directionLayout,congratsLayout,mapParent;
        View bookingConfirmCard;

        public ProductInfoHolder2(View itemView) {
            super(itemView);
            storeAddress= (TextView)itemView.findViewById(R.id.store_address);
            direction =(TextView)itemView.findViewById(R.id.get_address);
            mapImage= (ImageView)itemView.findViewById(R.id.map_img);
            bookStoreVisit = (TextView)itemView.findViewById(R.id.book_store_visit);
            loadingLayout = (FrameLayout)itemView.findViewById(R.id.loading_layout);
            crossText = (TextView)itemView.findViewById(R.id.cross_img);
            loadingBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
            bookProgress = (ProgressBar)itemView.findViewById(R.id.book_progress);
            bookingConfirmCard= itemView.findViewById(R.id.booking_confirm_card);
            callLayout = (LinearLayout)itemView.findViewById(R.id.call_customer);
            directionLayout = (LinearLayout)itemView.findViewById(R.id.get_direction_customer);
            congratsLayout = (LinearLayout)itemView.findViewById(R.id.congrats_layout);
            mapParent = (LinearLayout)itemView.findViewById(R.id.map_parent);
            requestLoaderBg = (FrameLayout)itemView.findViewById(R.id.request_loader_bg);
            cancelBooking = (ImageView)itemView.findViewById(R.id.close_card);
        }
    }

    public class ProductInfoHolder3 extends RecyclerView.ViewHolder{
        TextView pincode,checkPincode,delivery,shipping_charges,sold_by,coc_avaiable,isAvailableText;
        LinearLayout moreFromSeller;
        public ProductInfoHolder3(View itemView) {
            super(itemView);
            pincode = (TextView)itemView.findViewById(R.id.pincode);
            checkPincode = (TextView)itemView.findViewById(R.id.pincode_check);
            delivery = (TextView)itemView.findViewById(R.id.delivery);
            shipping_charges = (TextView)itemView.findViewById(R.id.shipping_charges);
            sold_by = (TextView)itemView.findViewById(R.id.sold_by);
            coc_avaiable = (TextView)itemView.findViewById(R.id.coc_avaiable);
            moreFromSeller = (LinearLayout)itemView.findViewById(R.id.more_from_seller);
            isAvailableText = (TextView)itemView.findViewById(R.id.is_available_pincode);

        }
    }

    public class ProductInfoHolder4 extends RecyclerView.ViewHolder{
        TextView descTitle,descText;
        LinearLayout descLayout;
        public ProductInfoHolder4(View itemView) {
            super(itemView);
            descText = (TextView)itemView.findViewById(R.id.description_text);
            descTitle = (TextView)itemView.findViewById(R.id.description_title);
            descLayout = (LinearLayout)itemView.findViewById(R.id.desc_lin_layout);
        }
    }

    OnViewsClickedListener mListener;
    public void setOnViewsClickedListener(OnViewsClickedListener listener){
        this.mListener = listener;
    }

    public interface OnViewsClickedListener{

        void onCheckPincodeClicked(boolean isShowProgress,String pincode);

        void markReviewRequest(ProductInfoHolder2 holder);

        void onCancelBookingRequest(ProductInfoHolder2 holder);

        void onMarkFavorite();

        void onMarkUnFavorite();

    }
    int seconds=30;
    public void startProgressBarLayout(final ProductInfoHolder2 holder){
        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((NewProductDetailActivity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        seconds -= 1;
                        if (seconds <= 0) {
                            seconds = 30;
                            timer.cancel();
                            // emptyBtn.setVisibility(View.GONE);
                            slideViewsRightToLeft(holder,holder.loadingLayout, holder.bookProgress,PROGRESS_TIME_COMPLETE);

                            //makeProductPreviewRequest();

                        } else {
                            holder.loadingBar.setProgress(100 - ((seconds*100)/30));
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
            }
        });
    }

    public void slideViewsLeftToRight(final View v1,final View v2,int checkCase){

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(v1,View.TRANSLATION_X,0,displayWidth);
        anim1.setDuration(300);
        v2.setVisibility(View.VISIBLE);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v2,View.TRANSLATION_X,-displayWidth,0);
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

    public void makeProductPreviewRequest(ProductInfoHolder2 holder){
        if(mListener!=null) {
            mListener.markReviewRequest(holder);
        }
    }


    public void onBookComplete(ProductInfoHolder2 holder){
        slideViewsRightToLeft(holder, holder.bookProgress, holder.congratsLayout, PROGRESS_LOADING_COMPLETE);

        //  moveBookingCompleteCardIn(holder);
    }

    public void moveBookingCompleteCardIn(final ProductInfoHolder2 holder){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mContext != null)
                    slideViewsRightToLeft(holder, holder.congratsLayout, holder.bookingConfirmCard, BOOK_PROCESS_COMPLETE);
            }
        }, 1000);
    }

    public void onBookCancelledSuccessfully(final ProductInfoHolder2 holder){
        isCancelBookingShown=false;
        notifyDataSetChanged();
        // slideViewsLeftToRight(holder.bookingConfirmCard,holder.bookStoreVisit,BOOK_BTN_CLICK);
    }
}

