package com.application.zimplyshop.adapters;

import java.util.ArrayList;

import com.application.zimply.R;
import com.application.zimplyshop.baseobjects.CompleteSubCategoryObject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class CompleteSubCategoryRecyclerGridAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {

	ArrayList<CompleteSubCategoryObject> objs;

	Context mContext;

	public CompleteSubCategoryRecyclerGridAdapter(Context context,
			ArrayList<CompleteSubCategoryObject> objs) {
		this.mContext = context;
		this.objs = new ArrayList<CompleteSubCategoryObject>(objs);
	}

	@Override
	public int getItemCount() {
		if (objs != null)
			return objs.size();
		return 0;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		((SubCategoryViewHolder) holder).name.setText(objs.get(position)
				.getName());
		if (objs.get(position).getSubsubcategory() != null
				&& objs.get(position).getSubsubcategory().size() > 0) {
			((SubCategoryViewHolder) holder).forwardArrow
					.setVisibility(View.VISIBLE);
		} else {
			((SubCategoryViewHolder) holder).forwardArrow
					.setVisibility(View.GONE);
		}

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGrp, int type) {
		View view = LayoutInflater.from(viewGrp.getContext()).inflate(
				R.layout.filter_list_item_layout, viewGrp, false);
		SubCategoryViewHolder holder = new SubCategoryViewHolder(view);
		return holder;
	}

	public class SubCategoryViewHolder extends RecyclerView.ViewHolder {

		TextView name, forwardArrow;

		public SubCategoryViewHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			forwardArrow = (TextView) view
					.findViewById(R.id.forward_arrow);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mListener.onItemClick(getAdapterPosition());
				}
			});
		}
	}

	SubCatOnItemClickListener mListener;

	public void setOnItemClickListener(SubCatOnItemClickListener listener) {
		this.mListener = listener;
	}

	public interface SubCatOnItemClickListener {
		void onItemClick(int pos);
	}
}
