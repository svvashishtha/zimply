package com.application.zimplyshop.adapters;

import java.util.ArrayList;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.ShopCategoriesActivity;
import com.application.zimplyshop.baseobjects.ShopCategoryObject;
import com.application.zimplyshop.managers.ImageLoaderManager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoriesGridRecyclerAdapter
		extends
		RecyclerView.Adapter<CategoriesGridRecyclerAdapter.CategoriesViewHolder> {

	ArrayList<ShopCategoryObject> objs;
	Context mContext;

	int width;

	int height;

	public CategoriesGridRecyclerAdapter(Context context,
			ArrayList<ShopCategoryObject> objs, int width, int height) {
		this.mContext = context;
		this.objs = new ArrayList<ShopCategoryObject>();
		this.objs.addAll(objs);
		this.width = width;
		this.height = height;
	}

	public class CategoriesViewHolder extends RecyclerView.ViewHolder implements
			OnClickListener {

		ImageView img;
		TextView name;

		public CategoriesViewHolder(View view) {
			super(view);
			img = (ImageView) view.findViewById(R.id.cat_img);
			name = (TextView) view.findViewById(R.id.cat_name);
			view.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mListener != null) {
				mListener.onItemClick(getAdapterPosition());
			}
		}

	}

	public Object getItem(int pos) {
		return objs.get(pos);
	}

	@Override
	public int getItemCount() {
		if (objs != null)
			return objs.size();
		return 0;
	}

	@Override
	public void onBindViewHolder(CategoriesViewHolder holder, int position) {
		holder.name.setText(objs.get(position).getName());
		if (objs.get(position).getImage2() != null) {
			if (holder.img.getTag() == null
					|| !((String) holder.img.getTag()).equalsIgnoreCase(objs
							.get(position).getImage2())) {
				new ImageLoaderManager((ShopCategoriesActivity) mContext)
						.setImageFromUrl(objs.get(position).getImage2(),
								holder.img, "users", width, height, true,false);

				holder.img.setTag(objs.get(position).getImage2());
			}
		}
	}

	@Override
	public CategoriesViewHolder onCreateViewHolder(ViewGroup viewGrp,
			int position) {
		View view = LayoutInflater.from(viewGrp.getContext()).inflate(
				R.layout.category_item_layout, null);
		CategoriesViewHolder holder = new CategoriesViewHolder(view);
		return holder;
	}

	OnItemClickListener mListener;

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;
	}

	public interface OnItemClickListener {
		void onItemClick(int pos);
	}
}
