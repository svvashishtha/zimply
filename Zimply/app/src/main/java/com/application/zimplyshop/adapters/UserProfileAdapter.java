package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimplyshop.R;

public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	int TYPE_HEADER = 0;
	int TYPE_ITEM = 1;

	String[] array = { "My Likes", "My Orders", "Logout" };

	OnItemClickListener mListener;

	Context mContext;

	public UserProfileAdapter(Context context) {
		this.mContext = context;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;
	}

	@Override
	public int getItemCount() {
		return 4;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (getItemViewType(position) == TYPE_HEADER) {
			/*((ProfileHeaderViewHolder) holder).imageView.setImageBitmap(ImageUtils
					.fastblur(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_user_pic), 20));*/
		} else {
			((ProfileItemViewHolder) holder).textView.setText(array[position - 1]);

		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGrp, int itemType) {
		RecyclerView.ViewHolder holder;
		if (itemType == TYPE_HEADER) {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.user_profile_header_layout, viewGrp,
					false);
			holder = new ProfileHeaderViewHolder(view);
		} else {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.user_profile_item_layout, viewGrp,
					false);
			holder = new ProfileItemViewHolder(view);
		}
		return holder;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_HEADER;
		} else {
			return TYPE_ITEM;
		}

	}

	public class ProfileHeaderViewHolder extends RecyclerView.ViewHolder {
		ImageView imageView;

		public ProfileHeaderViewHolder(View itemView) {
			super(itemView);
			imageView = (ImageView) itemView.findViewById(R.id.user_pic_big);
		}

	}

	public class ProfileItemViewHolder extends RecyclerView.ViewHolder {
		TextView textView;

		public ProfileItemViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.item_text);
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

	public interface OnItemClickListener {
		void onItemClick(int pos);
	}
}
