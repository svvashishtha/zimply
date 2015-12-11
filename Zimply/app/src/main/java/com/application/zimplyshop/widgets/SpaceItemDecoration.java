package com.application.zimplyshop.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
	private int space;
	private boolean removeSpaceFirstItem;


	public SpaceItemDecoration(int space ) {
		this.space = space;
	}
    public SpaceItemDecoration(int space ,boolean removeSpaceFirstItem) {
        this.space = space;
        this.removeSpaceFirstItem = removeSpaceFirstItem;
	}



	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) == 0 && !removeSpaceFirstItem)
			outRect.top = space;
		outRect.left = space;
		outRect.right = space;
		outRect.bottom = space;

	}


}