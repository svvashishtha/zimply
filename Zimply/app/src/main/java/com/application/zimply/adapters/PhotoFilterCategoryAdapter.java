package com.application.zimply.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimply.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PhotoFilterCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	ArrayList<String> names;
	Context mContext;
	boolean isExpanded;

	int selectedItem = -1;
	HashMap<Integer, String> subtitleArray;

	public PhotoFilterCategoryAdapter(Context context, ArrayList<String> names, boolean isExpanded) {
		this.mContext = context;
		this.names = new ArrayList<String>(names);
		this.isExpanded = isExpanded;
	}

	@Override
	public int getItemCount() {
		if (names != null) {
			return names.size();

		}
		return 0;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		if (isExpanded) {
			((PhotoFilterCategoryHolder) holder).forwardArrow.setVisibility(View.VISIBLE);
		} else {
			((PhotoFilterCategoryHolder) holder).forwardArrow.setVisibility(View.GONE);
		}

		if (subtitleArray != null && subtitleArray.get(position) != null) {
			((PhotoFilterCategoryHolder) holder).catName.setText(Html.fromHtml(names.get(position) + " "
					+ "<font color=#76b082>" + "( " + subtitleArray.get(position) + " )" + "</font>"));
		} else {
			((PhotoFilterCategoryHolder) holder).catName.setText(names.get(position));
		}

		if (selectedItem == position) {
			setRightDrawable(((PhotoFilterCategoryHolder) holder).catName, R.drawable.ic_tick);
		} else {
			setRightDrawable(((PhotoFilterCategoryHolder) holder).catName, 0);
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
	public ViewHolder onCreateViewHolder(ViewGroup viewGrp, int itemType) {
		View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.filter_list_item_layout, viewGrp, false);
		RecyclerView.ViewHolder holder = new PhotoFilterCategoryHolder(view);
		return holder;
	}

	public class PhotoFilterCategoryHolder extends RecyclerView.ViewHolder {

		TextView catName;
		ImageView forwardArrow;

		public PhotoFilterCategoryHolder(View itemView) {
			super(itemView);
			catName = (TextView) itemView.findViewById(R.id.name);
			forwardArrow = (ImageView) itemView.findViewById(R.id.forward_arrow);
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onItemClick(getAdapterPosition());
					}
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

	public void setSelectedItem(int budgetId) {
		selectedItem = budgetId;
		notifyDataSetChanged();
	}

	public void addSubTitles(HashMap<Integer, String> subtitleArray) {
		this.subtitleArray = new HashMap<Integer, String>(subtitleArray);
	}
}
