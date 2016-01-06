package com.application.zimplyshop.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.application.zimplyshop.utils.CommonLib;

/**
 * Created by Umesh Lohani on 12/8/2015.
 */
public class CustomRadioButton extends AppCompatRadioButton {

    public CustomRadioButton(Context context) {
        super(context);
        changeText(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        changeText(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        changeText(context);
    }

    public void changeText(Context context) {
        setTypeface(CommonLib.getTypeface(context, CommonLib.REGULAR_FONT));
    }
}
