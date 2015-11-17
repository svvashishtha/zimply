package com.application.zimplyshop.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * @author Umesh Lohani
 *
 */
public class ZObservableListView extends ListView {

	public interface ListViewObserver {
		void onScroll(float deltaY);

		void onScrollIdle();
	}

	private ListViewObserver mObserver;
	private View mTrackedChild;
	private int mTrackedChildPrevPosition;
	private int mTrackedChildPrevTop;

	/**
	 * 
	 */
	public ZObservableListView(Context context) {
		super(context);
	}

	public ZObservableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ZObservableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if (mTrackedChild == null) {
			if (getChildCount() > 0) {
				mTrackedChild = getChildInTheMiddle();
				mTrackedChildPrevTop = mTrackedChild.getTop();
				mTrackedChildPrevPosition = getPositionForView(mTrackedChild);
			}
		} else {
			boolean childIsSafeToTrack = mTrackedChild.getParent() == this
					&& getPositionForView(mTrackedChild) == mTrackedChildPrevPosition;
			if (childIsSafeToTrack) {
				int top = mTrackedChild.getTop();
				if (mObserver != null) {
					float deltaY = top - mTrackedChildPrevTop;
					mObserver.onScroll(deltaY);
				}
				mTrackedChildPrevTop = top;
			} else {
				mTrackedChild = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onScreenStateChanged(int)
	 */
	@Override
	public void onScreenStateChanged(int screenState) {
		if (screenState == SCREEN_STATE_OFF) {
			mObserver.onScrollIdle();
		}
	}

	private View getChildInTheMiddle() {
		return getChildAt(getChildCount() / 2);
	}

	public void setObserver(ListViewObserver observer) {
		mObserver = observer;
	}

}