package com.application.zimplyshop.widgets;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by Umesh Lohani on 12/12/2015.
 */
public class ScrollCustomizedLayoutManager extends LinearLayoutManager {

    boolean isScrollEnabled;

    public ScrollCustomizedLayoutManager(Context context) {
        super(context);
    }

    public ScrollCustomizedLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ScrollCustomizedLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
