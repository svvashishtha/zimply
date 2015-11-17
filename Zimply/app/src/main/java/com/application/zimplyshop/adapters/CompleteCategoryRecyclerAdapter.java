package com.application.zimplyshop.adapters;

import java.util.ArrayList;

import com.application.zimply.R;
import com.application.zimplyshop.baseobjects.CompleteCategoryObject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class CompleteCategoryRecyclerAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {

	ArrayList<CompleteCategoryObject> objs;

	Context mContext;

	public CompleteCategoryRecyclerAdapter(Context context,
			ArrayList<CompleteCategoryObject> objs) {
		this.mContext = context;
		this.objs = new ArrayList<CompleteCategoryObject>(objs);
	}

	@Override
	public int getItemCount() {
		if (objs != null) {
			return objs.size();
		}
		return 0;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		((CategoryViewHolder) holder).name
				.setText(objs.get(position).getName());
		if (objs.get(position).getSubcategory() != null
				&& objs.get(position).getSubcategory().size() > 0) {
			((CategoryViewHolder) holder).forwardArrow
					.setVisibility(View.VISIBLE);
		} else {
			((CategoryViewHolder) holder).forwardArrow.setVisibility(View.GONE);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGrp, int type) {
		View view = LayoutInflater.from(viewGrp.getContext()).inflate(
				R.layout.filter_list_item_layout, viewGrp, false);
		CategoryViewHolder holder = new CategoryViewHolder(view);
		return holder;
	}

	public class CategoryViewHolder extends RecyclerView.ViewHolder {

		TextView name, forwardArrow;

		public CategoryViewHolder(View view) {
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

	OnItemClickListener mListener;

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;
	}

	public interface OnItemClickListener {
		void onItemClick(int pos);
	}

}
