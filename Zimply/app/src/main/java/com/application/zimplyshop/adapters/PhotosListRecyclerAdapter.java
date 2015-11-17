package com.application.zimplyshop.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.application.zimply.R;
import com.application.zimplyshop.activities.HomeActivity;
import com.application.zimplyshop.baseobjects.HomePhotoObj;
import com.application.zimplyshop.managers.ImageLoaderManager;

import java.util.ArrayList;

public class PhotosListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ImageLoaderManager.ImageLoaderCallback {

	int TYPE_DATA = 0;
	int TYPE_LOADER = 1;

	Context mContext;

	int height;
	int width;

	boolean isFooterRemoved;

	ArrayList<HomePhotoObj> objs;

	OnItemClickListener mListener;

	public PhotosListRecyclerAdapter(Context context, int height, int width) {
		this.mContext = context;
		this.height = height;
		this.objs = new ArrayList<HomePhotoObj>();
		this.width = width;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;
	}

	public void addData(ArrayList<HomePhotoObj> objs) {
		ArrayList<HomePhotoObj> newObjs = new ArrayList<HomePhotoObj>(objs);
		this.objs.addAll(this.objs.size(), newObjs);
		notifyDataSetChanged();
	}


	public ArrayList<HomePhotoObj> getObjs() {
		return objs;
	}

	public void removeItem() {
		isFooterRemoved = true;
		notifyItemRemoved(objs.size());
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
	public int getItemViewType(int position) {
		if (position == objs.size()) {
			return TYPE_LOADER;
		} else {
			return TYPE_DATA;
		}

	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
		if (getItemViewType(position) == TYPE_DATA) {

			/*
			 * ((PhotoViewHolder) viewHolder).parentLayout
			 * .setBackgroundDrawable(new BitmapDrawable(CommonLib
			 * .getBitmap(mContext, R.drawable.ic_card_bg, width, height)));
			 */

			((PhotoViewHolder) viewHolder).parentLayout.setBackgroundResource(R.drawable.ic_card_bg);

			if (((PhotoViewHolder) viewHolder).photoImg.getTag() == null
					|| !(((String) ((PhotoViewHolder) viewHolder).photoImg.getTag())
							.equalsIgnoreCase(objs.get(position).getImage2()))) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
						height);
				((PhotoViewHolder) viewHolder).photoImg.setLayoutParams(lp);
				/*
				 * RequestManager.get(mContext).requestImage(mContext,
				 * ((PhotoViewHolder) viewHolder).photoImg,
				 * objs.get(position).getImage(), position);
				 */
				/*
				 * Picasso.with(mContext).load(objs.get(position).getImage())
				 * .into(((PhotoViewHolder) viewHolder).photoImg);
				 */

				new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl
						(objs.get(position).getImage2(), ((PhotoViewHolder) viewHolder).photoImg, "users", width, height, true,
						false);
				((PhotoViewHolder) viewHolder).photoImg.setTag(objs.get(position).getImage2());
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
//							new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl
//									(objs.get(position).getImage(), ((PhotoViewHolder) viewHolder).photoImg, "photo_details", width, height, true,
//											false);
//						((PhotoViewHolder) viewHolder).photoImg.setTag(objs.get(position).getImage());
					}
				}, 1000);
//				new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl(objs.get(position).getImage2(),
//						((PhotoViewHolder) viewHolder).photoImg, "users", height, height, true, false);


				/*
				 * viewHolder.itemView.setOnClickListener(new
				 * View.OnClickListener() {
				 * 
				 * @Override public void onClick(View v) {
				 * 
				 * } });
				 */

			}
		} else {

		}

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGrp, int itemType) {
		RecyclerView.ViewHolder holder;
		if (itemType == TYPE_DATA) {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.photo_list_item_layout, viewGrp,
					false);
			holder = new PhotoViewHolder(view);
		} else {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.progress_footer_layout, viewGrp,
					false);
			holder = new LoadingViewHolder(view);
		}
		return holder;
	}

	@Override
	public void loadingStarted() {

	}

	@Override
	public void loadingFinished(Bitmap bitmap) {

//		new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl
//				(objs.get(position).getImage(), ((PhotoViewHolder) viewHolder).photoImg, "photo_details", width, height, true,
//						false);
//		((PhotoViewHolder) viewHolder).photoImg.setTag(objs.get(position).getImage());
	}

	public class PhotoViewHolder extends RecyclerView.ViewHolder {
		ImageView photoImg;
		FrameLayout parentLayout;

		public PhotoViewHolder(View view) {
			super(view);
			photoImg = (ImageView) view.findViewById(R.id.photo_img);
			parentLayout = (FrameLayout) view.findViewById(R.id.parent_layout);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onItemClick(getAdapterPosition());
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

	public void removePreviousData() {
		objs.clear();
		isFooterRemoved = false;
		notifyDataSetChanged();
	}

	public interface OnItemClickListener {
		void onItemClick(int pos);
	}

	public HomePhotoObj getItem(int pos) {

		return objs.get(pos);
	}
}
