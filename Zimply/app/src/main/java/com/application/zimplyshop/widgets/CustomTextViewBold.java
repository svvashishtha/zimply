package com.application.zimplyshop.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.application.zimplyshop.utils.CommonLib;

/**
 * Created by apoorvarora on 29/10/15.
 */
public class CustomTextViewBold extends TextView {

    public CustomTextViewBold(Context context) {
        super(context);
        setTypeface(CommonLib.getTypeface(context, CommonLib.BOLD_FONT));
    }

    public CustomTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(CommonLib.getTypeface(context, CommonLib.BOLD_FONT));
    }

    public CustomTextViewBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(CommonLib.getTypeface(context, CommonLib.BOLD_FONT));
    }

}
