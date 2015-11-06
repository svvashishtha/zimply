package com.application.zimply.adapters;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.activities.HomeActivity;
import com.application.zimply.baseobjects.HomeArticleObj;
import com.application.zimply.fragments.ArticleListingFragment;
import com.application.zimply.managers.ImageLoaderManager;
import com.application.zimply.utils.TimeUtils;

import java.util.ArrayList;

public class ArticlesListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	int TYPE_DATA = 0;
	int TYPE_LOADER = 1;

	Context mContext;

	boolean isFooterRemoved;

	int height;

	ArrayList<HomeArticleObj> objs;

	OnItemClickListener mListener;

	int overflowVisiblePos = -1;

	int previousPos = -1;

    Fragment fragment;

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;
	}

	public ArticlesListRecyclerAdapter(Fragment fragment, int height) {
        this.fragment = fragment;
		this.mContext = fragment.getActivity();
		this.height = height;
		this.objs = new ArrayList<HomeArticleObj>();
	}

	public void addData(ArrayList<HomeArticleObj> objs) {
		ArrayList<HomeArticleObj> newObjs = new ArrayList<HomeArticleObj>(objs);
		this.objs.addAll(this.objs.size(), newObjs);
		// notifyItemRangeInserted(this.objs.size() - objs.size(), objs.size());
		notifyDataSetChanged();
	}

	public void removeItem() {
		isFooterRemoved = true;
		notifyItemRemoved(objs.size());
	}

	public HomeArticleObj getItem(int pos) {
		return objs.get(pos);
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
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (getItemViewType(position) == TYPE_DATA) {
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, height);
			((ArticleViewHolder) holder).parentLayout.setLayoutParams(lp);
			((ArticleViewHolder) holder).articleDate.setText(
					TimeUtils.getTimeStampDate(objs.get(position).getCreated_date(), TimeUtils.DATE_TYPE_DD_MON));
			((ArticleViewHolder) holder).articleDesc.setText(objs.get(position).getSubtitle());
			((ArticleViewHolder) holder).articleTitle.setText(objs.get(position).getTitle());
			if (((ArticleViewHolder) holder).aticleImg.getTag() == null
					|| !((String) ((ArticleViewHolder) holder).aticleImg.getTag())
							.equalsIgnoreCase(objs.get(position).getImage())) {
				new ImageLoaderManager((HomeActivity) mContext).setImageFromUrl(objs.get(position).getImage(),
						((ArticleViewHolder) holder).aticleImg, "users",
						((ArticleViewHolder) holder).aticleImg.getWidth(), height, true, false);

				((ArticleViewHolder) holder).aticleImg.setTag(objs.get(position).getImage());
			}

			/*((ArticleViewHolder) holder).moreInfo.setTag(holder);
			((ArticleViewHolder) holder).moreInfo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showHideOverFlowLayout(((RecyclerView.ViewHolder) v.getTag()));
				}

			});*/
			if (position == previousPos) {
				((ArticleViewHolder) holder).dimView.setVisibility(View.VISIBLE);
				((ArticleViewHolder) holder).overFlowLayout.setVisibility(View.VISIBLE);
			} else {
				((ArticleViewHolder) holder).dimView.setVisibility(View.GONE);
				((ArticleViewHolder) holder).overFlowLayout.setVisibility(View.GONE);
			}

		} else {

		}
	}

	private void showHideOverFlowLayout(RecyclerView.ViewHolder holder) {
		int pos = holder.getAdapterPosition();
		if (pos == previousPos) {
			previousPos = -1;
			animateOverFlowLayoutOut(((ArticleViewHolder) holder).dimView, ((ArticleViewHolder) holder).overFlowLayout,
					((ArticleViewHolder) holder).aticleImg.getHeight());

		} else if (previousPos == -1) {
			previousPos = pos;
			animateOverFlowLayoutIn(((ArticleViewHolder) holder).dimView, ((ArticleViewHolder) holder).overFlowLayout,
					((ArticleViewHolder) holder).aticleImg.getHeight());
		} else {
			checkHideShowHolder(previousPos);
			animateOverFlowLayoutIn(((ArticleViewHolder) holder).dimView, ((ArticleViewHolder) holder).overFlowLayout,
					((ArticleViewHolder) holder).aticleImg.getHeight());
			previousPos = pos;
		}
	}

	private void checkHideShowHolder(int pos) {
        RecyclerView.ViewHolder vHolder = ((ArticleListingFragment)fragment).getViewHolder(pos);
		if (vHolder != null) {
			animateOverFlowLayoutOut(((ArticleViewHolder) vHolder).dimView,
					((ArticleViewHolder) vHolder).overFlowLayout, ((ArticleViewHolder) vHolder).aticleImg.getHeight());
		} else {
			notifyItemChanged(previousPos);
		}

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGrp, int itemType) {
		RecyclerView.ViewHolder holder;
		if (itemType == TYPE_DATA) {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.homepage_article_layout, null);
			holder = new ArticleViewHolder(view);
		} else {
			View view = LayoutInflater.from(viewGrp.getContext()).inflate(R.layout.progress_footer_layout, viewGrp,
					false);
			holder = new LoadingViewHolder(view);
		}
		return holder;
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
		//	moreInfo = (ImageView) view.findViewById(R.id.more_info);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mListener.onItemClickListener(getAdapterPosition());
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
		if (objs != null) {
			objs.clear();
			isFooterRemoved = false;
			notifyDataSetChanged();
		}
	}

	public interface OnItemClickListener {
		void onItemClickListener(int pos);
	}

	private void animateOverFlowLayoutOut(final View dimView, final LinearLayout overFlowLayout, int height) {

		AnimatorSet set = new AnimatorSet();
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(overFlowLayout, View.TRANSLATION_Y, 0, height);

		ObjectAnimator anim2 = ObjectAnimator.ofFloat(dimView, View.ALPHA, 1, 0);

		set.playTogether(anim1, anim2);
		set.setDuration(200);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				overFlowLayout.setVisibility(View.GONE);
				dimView.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		set.start();
	}

	private void animateOverFlowLayoutIn(final View dimView, final LinearLayout overFlowLayout, final int height) {
		AnimatorSet set = new AnimatorSet();
		overFlowLayout.setVisibility(View.VISIBLE);
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(overFlowLayout, View.TRANSLATION_Y, height, 0);
		dimView.setVisibility(View.VISIBLE);
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(dimView, View.ALPHA, 0, 1);
		anim2.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				dimView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						animateOverFlowLayoutOut(dimView, overFlowLayout, height);

					}
				});
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		set.playTogether(anim1, anim2);
		set.setDuration(200);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.start();
	}
}
