package com.application.zimplyshop.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.application.zimplyshop.utils.CommonLib;

/**
 * Custom Edittext with a changed typeface
 * 
 * @author Umesh
 *
 */
public class CustomEdittext extends EditText {

	public CustomEdittext(Context context) {
		super(context);
		setTypeface(CommonLib.getTypeface(context, CommonLib.REGULAR_FONT));
	}

	public CustomEdittext(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(CommonLib.getTypeface(context, CommonLib.REGULAR_FONT));
	}

	public CustomEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setTypeface(CommonLib.getTypeface(context, CommonLib.REGULAR_FONT));
	}

}
