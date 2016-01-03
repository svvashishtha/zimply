package com.application.zimplyshop.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.NewProductDetailActivity;
import com.application.zimplyshop.activities.ProductListingActivity;
import com.application.zimplyshop.baseobjects.BannerObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.utils.JSONUtils;
import com.application.zimplyshop.utils.ZWebView;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Umesh Lohani on 11/4/2015.
 */
public class BannerDialogFragment extends DialogFragment{
    BannerObject obj;

    public static BannerDialogFragment newInstance(Bundle bundle) {
        BannerDialogFragment fragment = new BannerDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(),
                R.style.HJCustomDialogTheme);
        final View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.banner_layout, null);
        obj = (BannerObject)getArguments().getSerializable("banner_obj");
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);

        final ImageView bannerImage = (ImageView) view
                .findViewById(R.id.banner_img);
        int height = ((metrics.widthPixels-getResources().getDimensionPixelSize(R.dimen.margin_large))*obj.getHeight())/obj.getWidth();
       // FrameLayout.LayoutParams lp =new FrameLayout.LayoutParams(metrics.widthPixels-getResources().getDimensionPixelSize(R.dimen.margin_large),height);
        //bannerImage.setLayoutParams(lp);
        bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(obj.getType() == AppConstants.BANNER_TYPE_WEBVIEW) {
                    Intent intent = new Intent(getActivity(), ZWebView.class);
                    intent.putExtra("title", obj.getName());
                    intent.putExtra("url", obj.getSlug());
                    getActivity().startActivity(intent);
                }else if(obj.getType() == AppConstants.BANNER_TYPE_SCAN_OFFLINE){
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    PackageManager packageMgr = getActivity().getPackageManager();
                    List<ResolveInfo> activities = packageMgr.queryIntentActivities(intent, 0);
                    if (activities.size() > 0) {
                        getActivity().startActivityForResult(intent, 0);
                    } else {
                        Toast.makeText(getActivity() , "Barcode scanner is not available in your device",Toast.LENGTH_SHORT).show();
                    }
                }else if(obj.getType() == AppConstants.BANNER_TYPE_PRODUCT_LISTING){
                    Intent listIntent = new Intent(getActivity(), ProductListingActivity.class);
                    listIntent .putExtra("category_id", "0");
                    listIntent.putExtra("hide_filter", false);
                    listIntent .putExtra("category_name", obj.getName());
                    listIntent .putExtra("url", AppConstants.GET_PRODUCT_LIST);
                    listIntent.putExtra("discount_id",Integer.parseInt(obj.getSlug()));
                    startActivity(listIntent);
                }else if(obj.getType() == AppConstants.BANNER_TYPE_PRODUCT_DETAIL){
                    JSONObject jsonObj = JSONUtils.getJSONObject(obj.getSlug());
                    Intent intent = new Intent(getActivity() , NewProductDetailActivity.class);
                    intent.putExtra("slug", JSONUtils.getStringfromJSON(jsonObj,"slug"));
                    intent.putExtra("id",Integer.parseInt(JSONUtils.getStringfromJSON(jsonObj, "id")));
                    startActivity(intent);
                }
                dismiss();

            }
        });

        ImageView crossImage = (ImageView) view.findViewById(R.id.cross_banner);
        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        dialog.setCanceledOnTouchOutside(true);

        new ImageLoaderManager(getActivity()).setImageFromUrl(obj.getBanner(), bannerImage, "users", metrics.widthPixels-getResources().getDimensionPixelSize(R.dimen.margin_large),height,false,false);

        dialog.setContentView(view);
        // parent.setVisibility(View.GONE);
        dialog.getWindow().setLayout(metrics.widthPixels-getResources().getDimensionPixelSize(R.dimen.margin_large),
                height);

        return dialog;

    }


}
