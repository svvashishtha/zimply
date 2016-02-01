package com.application.zimplyshop.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceGridItemDecorator extends RecyclerView.ItemDecoration {
	private int space;
	private int space2;
	private boolean isStoreListing;
	public SpaceGridItemDecorator(int space, int space2) {
		this.space = space;
		this.space2 = space2;
	}
	public SpaceGridItemDecorator(int space, int space2,boolean isStoreListing) {
		this.space = space;
		this.space2 = space2;
		this.isStoreListing = isStoreListing;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

		// Add top margin only for the first item to avoid double space between
		// items
		if (parent.getChildAdapterPosition(view) == 0 || parent.getChildAdapterPosition(view) == 1||(isStoreListing && parent.getChildAdapterPosition(view) == 2)) {
			outRect.left = space;
			outRect.bottom = space2;
			outRect.right = space2;
		}  else if (parent.getChildAdapterPosition(view) % 2 == 0) {
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

	}
}