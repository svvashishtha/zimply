package com.application.zimply.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.application.zimply.R;
import com.application.zimply.baseobjects.BannerObject;
import com.application.zimply.managers.ImageLoaderManager;
import com.application.zimply.utils.ZWebView;

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
                Intent intent = new Intent(getActivity(), ZWebView.class);
                intent.putExtra("title", obj.getName());
                intent.putExtra("url", obj.getSlug());
                getActivity().startActivity(intent);
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

        new ImageLoaderManager(getActivity()).setImageFromUrl(obj.getBanner(), bannerImage, "users", metrics.widthPixels,metrics.heightPixels/3,false,false);

        dialog.setContentView(view);
        // parent.setVisibility(View.GONE);
        dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        return dialog;

    }


}
