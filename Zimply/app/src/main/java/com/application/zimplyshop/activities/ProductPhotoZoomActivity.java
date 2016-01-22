package com.application.zimplyshop.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.ProductThumbAdapters;
import com.application.zimplyshop.baseobjects.ProductObject;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.widgets.ProductThumbListItemDecorator;
import com.application.zimplyshop.widgets.TouchImageView;

/**
 * Created by Umesh Lohani on 11/4/2015.
 */
public class ProductPhotoZoomActivity extends BaseActivity {
    //TouchImageView zoomImage, zoomImageNew;
    ProductObject product;
    View photoContainer;
    boolean isRecyclerViewVisible;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    ViewPager imagePager;
    private RecyclerView thumbList;
int oldPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_photo_zoom_layout);
        imagePager = (ViewPager) findViewById(R.id.image_pager);

        photoContainer = findViewById(R.id.photo_container_root);

        ((ImageView) findViewById(R.id.close_popup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getIntent() != null && getIntent().getSerializableExtra("product_obj") != null) {
            final int position = getIntent().getIntExtra("position", 0);
            oldPosition = position;
            product = (ProductObject) getIntent().getSerializableExtra("product_obj");
            final int width = getDisplayMetrics().widthPixels;
            final int height = getDisplayMetrics().heightPixels;
            final MyPagerAdapter pagerAdapter = new MyPagerAdapter(ProductPhotoZoomActivity.this, width, height);
            imagePager.setAdapter(pagerAdapter);
            imagePager.setCurrentItem(position);
            //zoomImage = (TouchImageView) findViewById(R.id.zoom_imageview);
            //zoomImageNew = (TouchImageView) findViewById(R.id.zoom_imageview_new);
            /*new ImageLoaderManager(this).setImageFromUrl(product.getThumbs().get(position), zoomImage, "users", width / 2, height / 20, false,
                    false);

            new ImageLoaderManager(ProductPhotoZoomActivity.this).setImageFromUrlNew(product.getImages().get(position), zoomImageNew, "photo_details", width / 2, height / 20, false,
                    false, new ImageLoaderManager.ImageLoaderCallback() {
                        @Override
                        public void loadingStarted() {

                        }

                        @Override
                        public void loadingFinished(Bitmap bitmap) {
                            zoomImage.setVisibility(View.GONE);
                        }
                    });*/

            thumbList = (RecyclerView) findViewById(R.id.product_thumb_icons);
            thumbList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            thumbList.addItemDecoration(new ProductThumbListItemDecorator(getResources().getDimensionPixelSize(R.dimen.margin_small)));
            final ProductThumbAdapters adapter = new ProductThumbAdapters(this, product.getThumbs(), getResources().getDimensionPixelSize(R.dimen.pro_image_size), getResources().getDimensionPixelSize(R.dimen.pro_image_size));
            thumbList.setAdapter(adapter);
            adapter.setSelectedPos(position);
            imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    adapter.setSelectedPos(position);
                    //pagerAdapter.notifyItemChanged(position);
                    ((TouchImageView)pagerAdapter.pagerViews.get(oldPosition).findViewById(R.id.zoom_imageview)).resetZoom();
                    ((TouchImageView)pagerAdapter.pagerViews.get(oldPosition).findViewById(R.id.zoom_imageview_new)).resetZoom();
                    oldPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            adapter.setOnItemClickListener(new ProductThumbAdapters.OnItemClickListener() {
                @Override
                public void onItemClick(final int pos) {
                    adapter.setSelectedPos(pos);
                    imagePager.setCurrentItem(pos);


                }
            });
            isRecyclerViewVisible = true;
        } else {
            finish();
        }

    }

    boolean destroyed;

    @Override
    protected void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }

    class MyPagerAdapter extends PagerAdapter {
        private final int height, width;
        Context context;
        private TouchImageView zoomImage, zoomImageNew;
        SparseArray<View> pagerViews;

        public MyPagerAdapter(Context context, int width, int height) {
            this.context = context;
            this.width = width;
            this.height = height;
            pagerViews = new SparseArray<>();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.zoom_image_layout, null);
            zoomImage = (TouchImageView) view.findViewById(R.id.zoom_imageview);
            zoomImageNew = (TouchImageView) view.findViewById(R.id.zoom_imageview_new);
            pagerViews.put(position, view);
            new ImageLoaderManager(ProductPhotoZoomActivity.this).setImageFromUrl(product.getThumbs().get(position), zoomImage, "users", width / 2, height / 20, false,
                    false);

            new ImageLoaderManager(ProductPhotoZoomActivity.this).setImageFromUrlNew(product.getImages().get(position), zoomImageNew, "photo_details", width / 2, height / 20, false,
                    false, new ImageLoaderManager.ImageLoaderCallback() {
                        @Override
                        public void loadingStarted() {

                        }

                        @Override
                        public void loadingFinished(Bitmap bitmap) {
                            CommonLib.ZLog("ProductPhotoZoomActivity", position + "");
                            pagerViews.get(position).findViewById(R.id.zoom_imageview).setVisibility(View.GONE);
                            //notifyDataSetChanged();
                        }
                    });

            isRecyclerViewVisible = true;
            zoomImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isRecyclerViewVisible) {
                        {
                            Animation slideOutAnimation = AnimationUtils.loadAnimation(ProductPhotoZoomActivity.this, R.anim.slide_down);
                            slideOutAnimation.setDuration(500);

                            slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    thumbList.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            thumbList.startAnimation(slideOutAnimation);


                            isRecyclerViewVisible = !isRecyclerViewVisible;
                        }
                    } else {
                        Animation slideInAnimation = AnimationUtils.loadAnimation(ProductPhotoZoomActivity.this, R.anim.slide_up);
                        slideInAnimation.setDuration(500);

                        slideInAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                thumbList.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        thumbList.startAnimation(slideInAnimation);

                        isRecyclerViewVisible = !isRecyclerViewVisible;
                    }
                }
            });
            zoomImageNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isRecyclerViewVisible) {
                        {
                            Animation slideOutAnimation = AnimationUtils.loadAnimation(ProductPhotoZoomActivity.this, R.anim.slide_down);
                            slideOutAnimation.setDuration(500);

                            slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    thumbList.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            thumbList.startAnimation(slideOutAnimation);
                            isRecyclerViewVisible = !isRecyclerViewVisible;
                        }
                    } else {
                        Animation slideInAnimation = AnimationUtils.loadAnimation(ProductPhotoZoomActivity.this, R.anim.slide_up);
                        slideInAnimation.setDuration(500);
                        slideInAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                thumbList.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        thumbList.startAnimation(slideInAnimation);

                        isRecyclerViewVisible = !isRecyclerViewVisible;
                    }
                }
            });
            ((ViewPager) container).addView(view, 0);

            return view;
        }

        @Override
        public int getCount() {
            return product.getThumbs().size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void notifyItemChanged(int position) {
            try {
                ((TouchImageView) pagerViews.get(position - 1).findViewById(R.id.zoom_imageview_new)).resetZoom();
                ((TouchImageView) pagerViews.get(position - 1).findViewById(R.id.zoom_imageview)).resetZoom();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ((TouchImageView) pagerViews.get(position + 1).findViewById(R.id.zoom_imageview_new)).resetZoom();
                ((TouchImageView) pagerViews.get(position + 1).findViewById(R.id.zoom_imageview)).resetZoom();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.notifyDataSetChanged();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            ((ViewPager) collection).removeView((FrameLayout) view);

        }
    }
}
