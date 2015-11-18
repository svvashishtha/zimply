package com.application.zimplyshop.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.BannerObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.utils.ZWebView;

import java.util.List;

/**
 * Created by Umesh Lohani on 11/4/2015.
 */
public class BannerDialogFragment extends BaseDialogFragment{
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

        final ImageView bannerImage = (ImageView) view
                .findViewById(R.id.banner_img);
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
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);


        dialog.setCanceledOnTouchOutside(true);

        new ImageLoaderManager(getActivity()).setImageFromUrl(obj.getBanner(), bannerImage, "users", metrics.widthPixels,metrics.heightPixels,false,false);

        dialog.setContentView(view);
        // parent.setVisibility(View.GONE);
        dialog.getWindow().setLayout(metrics.widthPixels,
                LayoutParams.WRAP_CONTENT);

        return dialog;

    }


}
