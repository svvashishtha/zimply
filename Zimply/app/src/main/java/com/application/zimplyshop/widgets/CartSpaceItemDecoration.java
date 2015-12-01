package com.application.zimplyshop.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CartSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private boolean removeSpaceFirstItem;

    public CartSpaceItemDecoration(int space ) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;


    }


}