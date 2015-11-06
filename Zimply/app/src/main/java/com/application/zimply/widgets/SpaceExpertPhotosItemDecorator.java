package com.application.zimply.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceExpertPhotosItemDecorator extends RecyclerView.ItemDecoration {

	private int space;

	public SpaceExpertPhotosItemDecorator(int space) {
		this.space = space;

	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

		// Add top margin only for the first item to avoid double space between
		// items
		if (parent.getChildAdapterPosition(view)==0) {
			outRect.top = space;
		}
			outRect.left = space;
			outRect.bottom = space;
			outRect.right = space;


	}
}
