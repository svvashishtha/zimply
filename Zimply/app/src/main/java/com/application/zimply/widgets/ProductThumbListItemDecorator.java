package com.application.zimply.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Saurabh on 02-11-2015.
 */
public class ProductThumbListItemDecorator extends RecyclerView.ItemDecoration {

    private int space;

    public ProductThumbListItemDecorator(int space) {
        this.space = space;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        // Add top margin only for the first item to avoid double space between
        // items
        if (parent.getChildAdapterPosition(view) == parent.getChildCount() - 1) {
            outRect.right = space;
        }
      /*  outRect.left = space;
        outRect.bottom = space;*/
        outRect.left = space;


    }
}
