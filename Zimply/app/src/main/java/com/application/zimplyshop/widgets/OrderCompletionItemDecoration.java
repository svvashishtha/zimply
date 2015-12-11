package com.application.zimplyshop.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Umesh Lohani on 12/10/2015.
 */
public class OrderCompletionItemDecoration extends RecyclerView.ItemDecoration {

    private int space;


    public OrderCompletionItemDecoration(int space ) {
        this.space = space;
    }



    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        outRect.top = space;
        outRect.left = space;
        outRect.right = space;
       /* if(parent.getChildAdapterPosition(view) == parent.getChildCount()-1)
            outRect.bottom = space;*/

    }



}
