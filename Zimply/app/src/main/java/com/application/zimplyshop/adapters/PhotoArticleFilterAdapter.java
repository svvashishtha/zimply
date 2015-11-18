package com.application.zimplyshop.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.CategoryObject;

import java.util.ArrayList;

public class PhotoArticleFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	Context mContext;

	ArrayList<CategoryObject> objs;

	int selectedPos;

	public PhotoArticleFilterAdapter(Context context, ArrayList<CategoryObject> objs, int selectedPos) {
		this.mContext = context;
		this.objs = new ArrayList<CategoryObject>(objs);
		this.selectedPos = selectedPos;
	}

	@Override
	public int getItemCount() {
		if (objs != null)
			return objs.size() + 1;
		return 0;
	}

	public void setSelectedPos(int selectedPos) {
		this.selectedPos = selectedPos;
		notifyDataSetChanged();
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (position == 0) {
			((ViewHolder) holder).catName.setText("All");
		} else {
			((ViewHolder) holder).catName.setText(objs.get(position - 1).getName());
		}
		((ViewHolder) holder).forwardArrow.setVisibility(View.GONE);
		if ((selectedPos == -1 && position == 0)||selectedPos == position) {
			//  setRightDrawable(((ViewHolder) holder).catName, R.drawable.ic_tick);
            ((ViewHolder) holder).parent.setSelected(true);
		} else {
            ((ViewHolder) holder).parent.setSelected(false);

		}
	}

	private void setRightDrawable(TextView catName, int icTick) {
		if (icTick == 0) {
			catName.setCompoundDrawables(null, null, null, null);
		} else {
			Drawable d = mContext.getResources().getDrawable(icTick);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			catName.setCompoundDrawables(null, null, d, null);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGrp, int itemtype) {
		View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.filter_list_item_layout, viewGrp, false);
		ViewHolder holder = new ViewHolder(view);
		return holder;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		TextView catName;
		ImageView forwardArrow;
		LinearLayout parent;

		public ViewHolder(View itemView) {
			super(itemView);
			catName = (TextView) itemView.findViewById(R.id.name);
			forwardArrow = (ImageView) itemView.findViewById(R.id.forward_arrow);
			parent  = (LinearLayout)itemView.findViewById(R.id.parent);
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mListener.onItemClick(getAdapterPosition());
				}
			});
		}
	}

	OnItemClickListener mListener;

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;
	}

	public interface OnItemClickListener {
		void onItemClick(int pos);
	}
}
