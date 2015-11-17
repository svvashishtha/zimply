package com.application.zimplyshop.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * SmartViewPager class for proper swiping of viewpager inside a scrollview
 * 
 * @author Umesh
 *
 */
public class SmartViewPager extends ViewPager {

	public SmartViewPager(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(context, new XScrollDetector());
	}

	public SmartViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(context, new XScrollDetector());
	}
	
	private GestureDetector mGestureDetector;
	private boolean mIsLockOnHorizontalAxis = false;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// decide if horizontal axis is locked already or we need to check the
		// scrolling direction
		if (!mIsLockOnHorizontalAxis)
			mIsLockOnHorizontalAxis = mGestureDetector.onTouchEvent(event);

		// release the lock when finger is up
		if (event.getAction() == MotionEvent.ACTION_UP)
			mIsLockOnHorizontalAxis = false;

		getParent().requestDisallowInterceptTouchEvent(mIsLockOnHorizontalAxis);
		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int height = 0;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int h = child.getMeasuredHeight();
			if (h > height)
				height = h;
		}

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
				MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private class XScrollDetector extends SimpleOnGestureListener {

		/**
		 * @return true - if we're scrolling in X direction, false - in Y
		 *         direction.
		 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return Math.abs(distanceX) > Math.abs(distanceY);
		}

	}
}