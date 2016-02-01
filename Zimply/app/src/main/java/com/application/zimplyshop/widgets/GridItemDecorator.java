package com.application.zimplyshop.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Saurabh on 25-01-2016.
 */
public class GridItemDecorator  extends RecyclerView.ItemDecoration{
    private int space;
    private int space2;
    boolean isLinear;
    public GridItemDecorator(int space, int space2,boolean isLinear) {
        this.space = space;
        this.space2 = space2;
        this.isLinear = isLinear;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        // Add top margin only for the first item to avoid double space between
        // items
        if (parent.getChildAdapterPosition(view) == 0) {

            outRect.left = space;
            outRect.bottom = space2;
            outRect.right = space;
        }  else if (!isLinear) {
            if (parent.getChildAdapterPosition(view) % 2 == 1) {
                outRect.left = space;
                outRect.bottom = space2;
                outRect.top = space2;
                outRect.right = space2;
            } else {
                outRect.left = space2;
                outRect.right = space;
                outRect.bottom = space2;
                outRect.top = space2;
            }
        }else{
            outRect.left = space2;
            outRect.right = space;
            outRect.bottom = space2;
            outRect.top = space2;
        }

    }

}
