package com.application.zimply.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimply.R;

import java.util.ArrayList;

public class CompleteSubSubCategoryAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {

	ArrayList<String> objs;

	Context mContext;

	public CompleteSubSubCategoryAdapter(Context context, ArrayList<String> objs) {
		this.mContext = context;
		this.objs = new ArrayList<String>(objs);
	}

	@Override
	public int getItemCount() {
		if (objs != null)
			return objs.size();
		return 0;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		((SubSubCategoryViewHolder) holder).name.setText(objs.get(position));

		((SubSubCategoryViewHolder) holder).forwardArrow.setVisibility(View.GONE);

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGrp, int type) {
		View view = LayoutInflater.from(viewGrp.getContext()).inflate(
				R.layout.filter_list_item_layout, viewGrp, false);
		SubSubCategoryViewHolder holder = new SubSubCategoryViewHolder(view);
		return holder;
	}

	public class SubSubCategoryViewHolder extends RecyclerView.ViewHolder {

		TextView name;
		ImageView forwardArrow;

		public SubSubCategoryViewHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			forwardArrow = (ImageView) view
					.findViewById(R.id.forward_arrow);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mListener.onItemClick(getAdapterPosition());
				}
			});
		}
	}

	SubSubCatOnItemClickListener mListener;

	public void setOnItemClickListener(SubSubCatOnItemClickListener listener) {
		this.mListener = listener;
	}

	public interface SubSubCatOnItemClickListener {
		void onItemClick(int pos);
	}

}
