package com.application.zimplyshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.HomeActivity;
import com.application.zimplyshop.activities.ProductListingActivity;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.HomeProductCategoryNBookingObj;
import com.application.zimplyshop.baseobjects.LatestBookingObject;
import com.application.zimplyshop.baseobjects.OffersObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.widgets.CirclePageIndicator;
import com.application.zimplyshop.widgets.CustomTextViewBold;

import java.util.ArrayList;

public class ProductsCategoryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int height;
    Context mContext;
    int TYPE_TITLE = 0;
    int TYPE_HEADER = 1;
    int TYPE_CATEGORY = 2;
    int TYPE_OFFERS = 3;

    HomeProductCategoryNBookingObj obj;
    OffersObject offersObject;
    int viewPagerHeight = 0;

    int displayWidth;

    public ProductsCategoryGridAdapter(Context context, int height, int displayWidth) {
        obj = new HomeProductCategoryNBookingObj();

        this.height = height;
        this.mContext = context;
        this.displayWidth = displayWidth;
    }

    public void addCategoryData(ArrayList<CategoryObject> objs) {
        obj.setProduct_category(objs);
        notifyDataSetChanged();
    }

    public void addLatestBookingsData(ArrayList<LatestBookingObject> objs) {
        if (objs != null) {
            obj.setLatest_bookings(objs);
        } else {
            obj.getLatest_bookings().clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_CATEGORY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_item_layout, parent, false);
            holder = new ProductsCategoryViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookings_recyclerview_layout, parent, false);
            holder = new HeaderViewHolder(view);
        } else if (viewType == TYPE_OFFERS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_list_fragment_offer_layout, parent, false);
            holder = new OffersViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_title_layout, parent, false);
            holder = new TitleViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CATEGORY) {
            int newPos;
            if (offersObject != null && offersObject.getOffers().size() > 0) {
                if (obj.getLatest_bookings().size() > 0) {
                    newPos = position - 2 - 1;
                } else {
                    newPos = position - 1;
                }
            } else {
                if (obj.getLatest_bookings().size() > 0) {
                    newPos = position - 2;
                } else {
                    newPos = position;
                }
            }
            int height = (obj.getProduct_category().get(newPos).getImg().getHeight() * (displayWidth)) / obj.getProduct_category().get(newPos).getImg().getWidth();

            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(displayWidth, height);
            ((ProductsCategoryViewHolder) holder).parentFrame.setLayoutParams(lp);
            //((ProductsCategoryViewHolder) holder).categoryName.setText(obj.getProduct_category().get(newPos).getName());

            //((ProductsCategoryViewHolder) holder).categoryImg.setBackgroundResource(R.drawable.bg_dropshadow);
            if (obj.getProduct_category().get(newPos).getImg().getImage() != null) {
                new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl(obj.getProduct_category().get(newPos).getImg().getImage(), ((ProductsCategoryViewHolder) holder).categoryImg, "users", (displayWidth), height, false, false);
            }
        } else if (getItemViewType(position) == TYPE_HEADER) {
            ((HeaderViewHolder) holder).recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            // ((HeaderViewHolder)holder).recyclerView.addItemDecoration(new SpaceItemDecoration(mContext.getResources().getDimensionPixelSize(R.dimen.margin_small)));
            HomePageBookingsAdapter adapter = new HomePageBookingsAdapter(mContext, displayWidth, mContext.getResources().getDimensionPixelSize(R.dimen.booking_card_height));
            adapter.addData(obj.getLatest_bookings());
           /* LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int)mContext.getResources().getDimension(R.dimen.booking_card_height));
            ((HeaderViewHolder)holder).recyclerView.setLayoutParams(lp);*/
            ((HeaderViewHolder) holder).recyclerView.setAdapter(adapter);

            //  CommonLib.setListViewHeightBasedOnChildren(((HeaderViewHolder) holder).recyclerView.get);
            //((HeaderViewHolder)holder).recyclerView.setNestedScrollingEnabled(true);
        } else if (getItemViewType(position) == TYPE_OFFERS) {
            OffersViewHolder holder1 = (OffersViewHolder) holder;
            OffersPagerAdapter adapter = new OffersPagerAdapter();
            holder1.viewPager.setAdapter(adapter);
            viewPagerHeight = (int) ((displayWidth * ((float) offersObject.getOffers().get(0).getHeight() / offersObject.getOffers().get(0).getWidth())));
            holder1.viewPagerContainer.setLayoutParams(new FrameLayout.LayoutParams(displayWidth, viewPagerHeight));
            holder1.circlePageIndicator.setViewPager(holder1.viewPager);
        } else {
            ((TitleViewHolder) holder).customText.setText("Upcoming Visits");
        }

    }

    @Override
    public int getItemCount() {
        if (obj != null) {
            if (obj.getLatest_bookings().size() > 0) {
                if (offersObject == null || offersObject.getOffers().size() == 0)
                    return obj.getProduct_category().size() + 2;
                else
                    return obj.getProduct_category().size() + 2 + 1;
            } else {
                if (offersObject == null || offersObject.getOffers().size() == 0)
                    return obj.getProduct_category().size();
                else
                    return obj.getProduct_category().size() + 1;
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (offersObject != null && offersObject.getOffers().size() > 0) {
            if (obj.getLatest_bookings().size() > 0) {
                if (position == 0) {
                    return TYPE_OFFERS;
                } else if (position == 1) {
                    return TYPE_TITLE;
                } else if (position == 2) {
                    return TYPE_HEADER;
                } else {
                    return TYPE_CATEGORY;
                }
            } else {
                if (position == 0) {
                    return TYPE_OFFERS;
                } else {
                    return TYPE_CATEGORY;
                }
            }
        } else if (obj.getLatest_bookings().size() > 0) {
            if (position == 0) {
                return TYPE_TITLE;
            } else if (position == 1) {
                return TYPE_HEADER;
            } else {
                return TYPE_CATEGORY;
            }
        } else {
            return TYPE_CATEGORY;
        }

    }

    public void addOffersData(OffersObject offersData) {
        this.offersObject = offersData;
        notifyDataSetChanged();
    }

    public class ProductsCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImg;
        CustomTextViewBold categoryName;
        FrameLayout parentFrame;

        public ProductsCategoryViewHolder(View itemView) {
            super(itemView);
            categoryImg = (ImageView) itemView.findViewById(R.id.category_img);
            categoryName = (CustomTextViewBold) itemView.findViewById(R.id.category_name);
            parentFrame = (FrameLayout) itemView.findViewById(R.id.parent_layout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos;
                    if (obj.getLatest_bookings().size() > 0) {
                        pos = getAdapterPosition() - 3;
                    } else {
                        pos = getAdapterPosition() - 1;
                    }
                    Intent intent = new Intent(mContext, ProductListingActivity.class);
                    intent.putExtra("category_id", obj.getProduct_category().get(pos).getId());
                    intent.putExtra("category_name", obj.getProduct_category().get(pos).getName());
                    intent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public class OffersViewHolder extends RecyclerView.ViewHolder {
        ViewPager viewPager;
        CirclePageIndicator circlePageIndicator;
        View viewPagerContainer;

        public OffersViewHolder(View v) {
            super(v);
            viewPagerContainer = v.findViewById(R.id.view_pager_container);
            viewPager = (ViewPager) v.findViewById(R.id.viewpageroffers);
            circlePageIndicator = (CirclePageIndicator) v.findViewById(R.id.circlepageindicatorl);
        }
    }

    class OffersPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.products_list_fragment_offer_image_layout, container, false);
            ImageView image = (ImageView) view.findViewById(R.id.offerimage);
           // viewPagerHeight = viewPagerHeight - (2 * (mContext.getResources().getDimensionPixelSize(R.dimen.margin_small)));
            if (offersObject.getOffers().get(position).getImage() != null)
                new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl(offersObject.getOffers().get(position).getImage(),
                        image, "users", displayWidth, viewPagerHeight, false, false);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent listIntent = new Intent(mContext, ProductListingActivity.class);
                    listIntent.putExtra("category_id", "0");
                    listIntent.putExtra("hide_filter", false);
                    listIntent.putExtra("category_name", offersObject.getOffers().get(position).getName());
                    listIntent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
                    listIntent.putExtra("discount_id", Integer.parseInt(offersObject.getOffers().get(position).getSlug()));
                    mContext.startActivity(listIntent);
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return offersObject.getOffers().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(((View) object));
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public HeaderViewHolder(View view) {
            super(view);
            recyclerView = (RecyclerView) view.findViewById(R.id.booking_list);
        }

    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView customText;

        public TitleViewHolder(View itemView) {
            super(itemView);
            customText = (TextView) itemView.findViewById(R.id.header_footer);
        }
    }

}
