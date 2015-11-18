package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.UserFavouritesActivity;
import com.application.zimplyshop.baseobjects.FavListItemObject;
import com.application.zimplyshop.baseobjects.HomeArticleObj;
import com.application.zimplyshop.baseobjects.HomePhotoObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.utils.TimeUtils;

import java.util.ArrayList;

public class UserFavouritesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	ArrayList<FavListItemObject> objs;

	int TYPE_ARTILCLE = 0;
	int TYPE_PHOTO = 1;

	int TYPE_LOADER = 2;

	int articleHeight, photoHeight;

	Context mContext;

	boolean isFooterRemoved;

	OnItemClickListener mListener;

	public UserFavouritesListAdapter(Context context, int articleHeight, int photoHeight) {
		this.mContext = context;
		this.articleHeight = articleHeight;
		this.photoHeight = photoHeight;
		objs =  new ArrayList<FavListItemObject>();
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

    public void addData(ArrayList<FavListItemObject> objs){
        ArrayList<FavListItemObject> newObjs = new ArrayList<FavListItemObject>(objs);
        this.objs.addAll(this.objs.size(), newObjs);
        notifyDataSetChanged();
    }

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (getItemViewType(position) == TYPE_ARTILCLE) {
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, articleHeight);
			((ArticleViewHolder) holder).parentLayout.setLayoutParams(lp);
			((ArticleViewHolder) holder).articleDate.setText(TimeUtils.getTimeStampDate(
					((HomeArticleObj) objs.get(position).getObj()).getCreated_date(), TimeUtils.DATE_TYPE_DD_MON));
			((ArticleViewHolder) holder).articleDesc
					.setText(((HomeArticleObj) objs.get(position).getObj()).getSubtitle());
			((ArticleViewHolder) holder).articleTitle
					.setText(((HomeArticleObj) objs.get(position).getObj()).getTitle());
			if (((ArticleViewHolder) holder).aticleImg.getTag() == null
					|| !((String) ((ArticleViewHolder) holder).aticleImg.getTag())
							.equalsIgnoreCase(((HomeArticleObj) objs.get(position).getObj()).getImage())) {
				new ImageLoaderManager((UserFavouritesActivity) mContext).setImageFromUrl(
						((HomeArticleObj) objs.get(position).getObj()).getImage(),
						((ArticleViewHolder) holder).aticleImg, "users",
						((ArticleViewHolder) holder).aticleImg.getWidth(), articleHeight, true, false);

				((ArticleViewHolder) holder).aticleImg
						.setTag(((HomeArticleObj) objs.get(position).getObj()).getImage());
			}
		} else if (getItemViewType(position) == TYPE_PHOTO) {
			((PhotoViewHolder) holder).parentLayout.setBackgroundResource(R.drawable.ic_card_bg);

			if (((PhotoViewHolder) holder).photoImg.getTag() == null
					|| !(((String) ((PhotoViewHolder) holder).photoImg.getTag())
							.equalsIgnoreCase(((HomePhotoObj) objs.get(position).getObj()).getImage()))) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
						photoHeight);
				((PhotoViewHolder) holder).photoImg.setLayoutParams(lp);

				new ImageLoaderManager((UserFavouritesActivity) mContext).setImageFromUrl(
						((HomePhotoObj) objs.get(position).getObj()).getImage(), ((PhotoViewHolder) holder).photoImg,
						"users", photoHeight, photoHeight, true, false);
				((PhotoViewHolder) holder).photoImg.setTag(((HomePhotoObj) objs.get(position).getObj()).getImage());
				((PhotoViewHolder) holder).styleCatText.setText(((HomePhotoObj) objs.get(position).getObj()).getCat() + " - " + ((HomePhotoObj) objs.get(position).getObj()).getStyle());
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});

			}
		} else {

		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGrp, int itemType) {
		RecyclerView.ViewHolder holder;
		if (itemType == TYPE_LOADER) {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.progress_footer_layout, viewGrp,
					false);
			holder = new LoadingViewHolder(view);
		} else if (itemType == TYPE_ARTILCLE) {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.homepage_article_layout, null);
			holder = new ArticleViewHolder(view);
		} else {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.fav_photo_item_layout, viewGrp,
					false);
			holder = new PhotoViewHolder(view);
		}
		return holder;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == objs.size()) {
			return TYPE_LOADER;

		} else if (objs.get(position).getType() == AppConstants.ITEM_TYPE_ARTICLE) {
			return TYPE_ARTILCLE;
		} else {
			return TYPE_PHOTO;
		}

	}

	public class PhotoViewHolder extends RecyclerView.ViewHolder {
		ImageView photoImg;
		LinearLayout parentLayout;
		TextView styleCatText;

		public PhotoViewHolder(View view) {
			super(view);
			photoImg = (ImageView) view.findViewById(R.id.photo_img);
			parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout);
			styleCatText = (TextView)view.findViewById(R.id.style_cat_text);
			photoImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.OnItemClick(getAdapterPosition(), objs.get(getAdapterPosition()).getType());
					}
				}
			});
		}

	}

	public class ArticleViewHolder extends RecyclerView.ViewHolder {

		LinearLayout parentLayout;

		TextView articleTitle, articleDesc, articleDate;

		ImageView aticleImg, moreInfo;
		LinearLayout overFlowLayout;
		View dimView;

		public ArticleViewHolder(View view) {
			super(view);
			parentLayout = (LinearLayout) view.findViewById(R.id.parent);
			articleTitle = (TextView) view.findViewById(R.id.article_title);
			articleDesc = (TextView) view.findViewById(R.id.article_desc);
			articleDate = (TextView) view.findViewById(R.id.article_date);
			aticleImg = (ImageView) view.findViewById(R.id.aticle_img);
			overFlowLayout = (LinearLayout) view.findViewById(R.id.overflow_layout);
			dimView = view.findViewById(R.id.dim_view);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.OnItemClick(getAdapterPosition(), objs.get(getAdapterPosition()).getType());
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

	public void removeItem() {
		isFooterRemoved = true;
		notifyItemRemoved(objs.size());
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}

	public interface OnItemClickListener {
		void OnItemClick(int pos, int type);
	}

	public Object getItem(int pos) {
		return objs.get(pos).getObj();
	}

	public Object getItems() {
		return objs;
	}

	public int getType(int pos) {
		return objs.get(pos).getType();
	}

	public void removeItem(int clickedPos) {
		if (objs != null && objs.size() >= clickedPos) {
			objs.remove(clickedPos);
			notifyItemRemoved(clickedPos);
		}
	}

}
