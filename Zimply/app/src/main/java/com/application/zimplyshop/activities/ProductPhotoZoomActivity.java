package com.application.zimplyshop.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.ProductThumbAdapters;
import com.application.zimplyshop.baseobjects.ProductObject;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.widgets.ProductThumbListItemDecorator;
import com.application.zimplyshop.widgets.TouchImageView;

/**
 * Created by Umesh Lohani on 11/4/2015.
 */
public class ProductPhotoZoomActivity extends BaseActivity{
    TouchImageView zoomImage,zoomImageNew;
    ProductObject product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_photo_zoom_layout);
        ((ImageView)findViewById(R.id.close_popup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getIntent() != null && getIntent().getSerializableExtra("product_obj") != null) {
            final int position =  getIntent().getIntExtra("position",0);
            product = (ProductObject)getIntent().getSerializableExtra("product_obj");
            final int width = getDisplayMetrics().widthPixels;
            final int height = getDisplayMetrics().heightPixels;
            zoomImage = (TouchImageView) findViewById(R.id.zoom_imageview);
            zoomImageNew = (TouchImageView) findViewById(R.id.zoom_imageview_new);
            new ImageLoaderManager(this).setImageFromUrl(product.getThumbs().get(position), zoomImage, "users", width / 2, height / 20, false,
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
                    });

            RecyclerView thumbList = (RecyclerView) findViewById(R.id.product_thumb_icons);
            thumbList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            thumbList.addItemDecoration(new ProductThumbListItemDecorator(getResources().getDimensionPixelSize(R.dimen.margin_small)));
            final ProductThumbAdapters adapter = new ProductThumbAdapters(this, product.getThumbs(), getResources().getDimensionPixelSize(R.dimen.pro_image_size), getResources().getDimensionPixelSize(R.dimen.pro_image_size));
            thumbList.setAdapter(adapter);

            adapter.setOnItemClickListener(new ProductThumbAdapters.OnItemClickListener() {
                @Override
                public void onItemClick(final int pos) {
                    adapter.setSelectedPos(pos);
                    zoomImage.setVisibility(View.VISIBLE);
                    zoomImage.resetZoom();
                    zoomImageNew.resetZoom();
                    new ImageLoaderManager(ProductPhotoZoomActivity.this).setImageFromUrl(product.getThumbs().get(pos), zoomImage, "users", width / 2, height / 20, false,
                            false);

                    new ImageLoaderManager(ProductPhotoZoomActivity.this).setImageFromUrlNew(product.getImages().get(pos), zoomImageNew, "photo_details", width / 2, height / 20, false,
                            false, new ImageLoaderManager.ImageLoaderCallback() {
                                @Override
                                public void loadingStarted() {

                                }

                                @Override
                                public void loadingFinished(Bitmap bitmap) {
                                    zoomImage.setVisibility(View.GONE);
                                }
                            });


                }
            });
        }else{
            finish();
        }

    }
    boolean destroyed;
    @Override
    protected void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }
}
