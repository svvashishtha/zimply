package com.application.zimplyshop.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.application.zimplyshop.utils.CommonLib;

/**
 * Button with a custom typeface
 * 
 * @author Umesh
 *
 */
public class CustomButton extends Button {

	public CustomButton(Context context) {
		super(context);
		init();
	}

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	/**
	 * Change typeface
	 */
	private void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				CommonLib.REGULAR_FONT);
		setTypeface(tf);
	}

}
