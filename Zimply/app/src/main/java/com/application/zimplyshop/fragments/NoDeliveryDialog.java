package com.application.zimplyshop.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.utils.CommonLib;

/**
 * Created by Umesh Lohani on 11/18/2015.
 */
public class NoDeliveryDialog extends DialogFragment {

    public static NoDeliveryDialog newInstance(Bundle bundle) {
        NoDeliveryDialog fragment = new NoDeliveryDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(),
                R.style.HJCustomDialogTheme);
        final View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.no_delivery_dialog, null);

        ((TextView)view.findViewById(R.id.use_address_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        int width = metrics.widthPixels-(2*getResources().getDimensionPixelSize(R.dimen.margin_xxlarge));
        ImageView img = (ImageView)view.findViewById(R.id.sad_img);
       /* LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width/2 , width/2);
        img.setLayoutParams(lp);*/
        img.setImageBitmap(CommonLib.getBitmap(getActivity(),R.drawable.ic_sad_face,width/2,width/2));
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(view);
        // parent.setVisibility(View.GONE);
        dialog.getWindow().setLayout(width,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        return dialog;

    }


}
