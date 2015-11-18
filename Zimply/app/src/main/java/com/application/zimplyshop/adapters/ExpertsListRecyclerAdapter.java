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
import com.application.zimplyshop.activities.ExpertsListActivity;
import com.application.zimplyshop.baseobjects.HomeExpertObj;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.utils.CommonLib;

import java.util.ArrayList;

public class ExpertsListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	int TYPE_DATA = 0;
	int TYPE_LOADER = 1;

	Context mContext;

	int itemCount;

	boolean isFooterRemoved;

	int width;

	int height;

	OnItemClickListener mListener;

	public ExpertsListRecyclerAdapter(Context context, int width, int height) {
		this.mContext = context;
		this.objs = new ArrayList<HomeExpertObj>();
		this.width = width;
		this.height = height;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;

	}

	ArrayList<HomeExpertObj> objs;

	public void addData(ArrayList<HomeExpertObj> objs) {
		ArrayList<HomeExpertObj> newObjs = new ArrayList<HomeExpertObj>(objs);
		this.objs.addAll(this.objs.size(), newObjs);
		// notifyItemRangeInserted(this.objs.size() - objs.size(), objs.size());
		notifyDataSetChanged();
	}

	public void removeItem() {
		isFooterRemoved = true;
		notifyItemRemoved(objs.size());
	}

	public HomeExpertObj getItem(int pos) {
		return objs.get(pos);
	}

	@Override
	public long getItemId(int position) {

		return super.getItemId(position);
	}

	@Override
	public int getItemCount() {
		if (objs != null) {
			if (isFooterRemoved) {
				return objs.size();
			} else {
				return objs.size() + 1;
			}
		}
		return 0;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

		if (getItemViewType(position) == TYPE_DATA) {

			if (((ExpertsViewHolder) holder).coverImg.getTag() == null
					|| !(((String) ((ExpertsViewHolder) holder).coverImg.getTag())
							.equalsIgnoreCase(objs.get(position).getCover()))) {
				new ImageLoaderManager((ExpertsListActivity) mContext).setImageFromUrl(objs.get(position).getCover(),
						((ExpertsViewHolder) holder).coverImg, "users", width, height, true, false);

				((ExpertsViewHolder) holder).coverImg.setTag(objs.get(position).getCover());
			}

			if (((ExpertsViewHolder) holder).expertPic.getTag() == null
					|| !(((String) ((ExpertsViewHolder) holder).expertPic.getTag())
							.equalsIgnoreCase(objs.get(position).getLogo()))) {
				//((ExpertsViewHolder) holder).expertPic.setBorderWidth(5);
				((ExpertsViewHolder) holder).expertPic
						.setImageBitmap(CommonLib.getBitmap(mContext, R.drawable.ic_pro_placeholder,
								mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size),
								mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size)));
				//((ExpertsViewHolder) holder).expertPic.setImageBitmap(CommonLib.getBitmap(mContext , R.drawable.ic_pro_placeholder,mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size),mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size)));
				new ImageLoaderManager((ExpertsListActivity) mContext).setImageFromUrl(objs.get(position).getLogo(),
						((ExpertsViewHolder) holder).expertPic, "pro",
						mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size),
						mContext.getResources().getDimensionPixelSize(R.dimen.pro_image_size), true, false);

				((ExpertsViewHolder) holder).expertPic.setTag(objs.get(position).getLogo());
			}

			((ExpertsViewHolder) holder).expertName.setText(objs.get(position).getTitle());

			((ExpertsViewHolder) holder).expertCategory
					.setText(getExpertCategoryString(objs.get(position).getCategory()));
		} else {

		}
	}

	private String getExpertCategoryString(ArrayList<String> category) {
		String text = "";
		for (int i = 0; i < category.size(); i++) {
			text += category.get(i);
			if (i < category.size() - 1) {
				text += ", ";
			}
		}
		return text;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGrp, int viewType) {
		RecyclerView.ViewHolder holder;
		if (viewType == TYPE_DATA) {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.homepage_experts_layout, viewGrp,
					false);
			holder = new ExpertsViewHolder(view);
		} else {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.progress_footer_layout, viewGrp,
					false);
			holder = new LoadingViewHolder(view);
		}
		return holder;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == objs.size()) {
			return TYPE_LOADER;
		} else {
			return TYPE_DATA;
		}

	}

	public class ExpertsViewHolder extends RecyclerView.ViewHolder {
		ImageView coverImg;
		ImageView expertPic;
		TextView expertName;
		TextView expertCategory;

		public ExpertsViewHolder(View view) {
			super(view);
			coverImg = (ImageView) view.findViewById(R.id.cover_img);
			expertPic = (ImageView) view.findViewById(R.id.expert_pic);
			expertName = (TextView) view.findViewById(R.id.expert_name);
			expertCategory = (TextView) view.findViewById(R.id.expert_category);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onItemClick(expertPic,getAdapterPosition());
                    }
				}
			});
		}

	}

	public class LoadingViewHolder extends RecyclerView.ViewHolder {

		public LoadingViewHolder(View view) {
			super(view);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(View view,int pos);
	}

	public void removePreviousData() {
		objs.clear();
		isFooterRemoved = false;
		notifyDataSetChanged();
	}
}
