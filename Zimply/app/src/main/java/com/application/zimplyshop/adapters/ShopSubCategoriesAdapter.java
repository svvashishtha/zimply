package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.ShopSubCategoryObj;

import java.util.ArrayList;

public class ShopSubCategoriesAdapter extends RecyclerView.Adapter<ShopSubCategoriesAdapter.SubCategoriesHolder> {

	ArrayList<ShopSubCategoryObj> objs;
	Context mContext;

	OnItemClickListener mListener;

	public ShopSubCategoriesAdapter(Context context, ArrayList<ShopSubCategoryObj> objs) {
		this.mContext = context;
		this.objs = new ArrayList<ShopSubCategoryObj>();
		this.objs.addAll(objs);
	}

	@Override
	public int getItemCount() {
		return objs.size();
	}


	public ShopSubCategoryObj getItem(int pos) {
		return objs.get(pos);
	}

	@Override
	public void onBindViewHolder(SubCategoriesHolder holder, int position) {
		/*if (holder.img.getTag() == null
				|| !((String) holder.img.getTag()).equalsIgnoreCase(objs.get(position).getSlug())) {
			new ImageLoaderManager((ShopSubCategoriesActivity) mContext).setImageFromUrl(objs.get(position).getName(),
					holder.img, "users", holder.img.getWidth(), holder.img.getHeight(), true, false);

			holder.img.setTag(objs.get(position).getSlug());
		}*/
		holder.name.setText(objs.get(position).getName());
	}

	@Override
	public SubCategoriesHolder onCreateViewHolder(ViewGroup viewGrp, int itemtype) {
		View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.subcategory_list_item_layout, viewGrp,
				false);
		SubCategoriesHolder holder = new SubCategoriesHolder(view);
		return holder;
	}

	public class SubCategoriesHolder extends RecyclerView.ViewHolder {

		TextView name;
		ImageView img;

		public SubCategoriesHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.sub_cat_name);
			img = (ImageView) view.findViewById(R.id.sub_cat_img);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onItemClicked(getAdapterPosition());
					}
				}
			});
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}

	public interface OnItemClickListener {
		void onItemClicked(int pos);
	}
}
