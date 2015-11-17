package com.application.zimplyshop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimplyshop.activities.ExpertProfileActivity;
import com.application.zimplyshop.adapters.ExpertsListRecyclerAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.ExpertListObject;
import com.application.zimplyshop.baseobjects.HomeExpertObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.objects.AllCategories;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

import java.util.ArrayList;

public class ExpertsListFragment extends BaseFragment
		implements OnClickListener, GetRequestListener, RequestTags, AppConstants {

	RecyclerView expertsList;

	String nextUrl;

	boolean isLoading;

	boolean isRequestAllowed;

	int pastVisiblesItems, visibleItemCount, totalItemCount;

	boolean isDestroyed;

	int categoryId = -1;

	boolean isRefreshData;

	boolean isProductFilterLoaded = true;
	boolean isArticleFilterLoaded = false;

	boolean showLoading;


    public static ExpertsListFragment newInstance(Bundle bundle){
        ExpertsListFragment fragment = new ExpertsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    view = LayoutInflater.from(getActivity()).inflate(R.layout.experts_list_layout,null);


		expertsList = (RecyclerView) view.findViewById(R.id.experts_list);
		expertsList.setLayoutManager(new LinearLayoutManager(getActivity()));
		expertsList.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.margin_small)));

		setLoadingVariables();
		retryLayout.setOnClickListener(this);
		setFilterVariables();
		setFiltersClick();
		setCategoryFilterText("All");
//		loadData();
		GetRequestManager.getInstance().addCallbacks(this);

		//loadArticlePhotoCategories();
        return  view;
	}

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
		if(isVisibleToUser && getActivity()!=null && expertsList.getAdapter() == null && !isLoading){
            if(AllCategories.getInstance().getPhotoCateogryObjs() == null ||AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() == null || (AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category()!=null && AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category().size() == 0))
            {	showLoading = false;
                loadArticlePhotoCategories();
            }else{
                showLoading = true;
                loadData();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void setFiltersClick() {
		filterLayout.setVisibility(View.VISIBLE);
		categoriesLayout.setOnClickListener(this);
		sortByLayout.setVisibility(View.GONE);
		selectfiltersLayout.setVisibility(View.GONE);
		view.findViewById(R.id.separator2).setVisibility(View.GONE);
		view.findViewById(R.id.separator1).setVisibility(View.GONE);
	}

	public void loadData() {

		String finalUrl;
		if (nextUrl == null) {
			finalUrl = AppApplication.getInstance().getBaseUrl() + EXPERT_LIST_URL + "?filter=0&width="+width/2;
		} else {
			finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
		}
		GetRequestManager.getInstance().makeAyncRequest(finalUrl, EXPERT_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_EXPERT_LIST_OBJECT);
	}

	private void addToolbarView(Toolbar toolbar) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.common_toolbar_text_layout, null);
		TextView titleText = (TextView) view.findViewById(R.id.title_textview);
		titleText.setText(getString(R.string.experts_text));
		toolbar.addView(view);
	}

	int width;

	private void setAdapterData(ArrayList<HomeExpertObj> objs) {
		if (expertsList.getAdapter() == null) {
			width = getDisplayMetrics().widthPixels - getResources().getDimensionPixelSize(R.dimen.margin_small);
			int height = getDisplayMetrics().widthPixels / 3;

			ExpertsListRecyclerAdapter adapter = new ExpertsListRecyclerAdapter(getActivity(), width, height);
			expertsList.setAdapter(adapter);
			expertsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

					visibleItemCount = expertsList.getLayoutManager().getChildCount();
					totalItemCount = expertsList.getLayoutManager().getItemCount();
					pastVisiblesItems = ((LinearLayoutManager) expertsList.getLayoutManager())
							.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLoading && isRequestAllowed && expertsList.getAdapter().getItemCount()>0) {
                        loadData();
					}
                    scrollToolbarAndHeaderBy(-dy);
				}

				@Override
				public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
					new ImageLoaderManager(getActivity()).setScrollState(newState);
					super.onScrollStateChanged(recyclerView, newState);
				}
			});

			((ExpertsListRecyclerAdapter) expertsList.getAdapter()).setOnItemClickListener(new ExpertsListRecyclerAdapter.OnItemClickListener() {

				@Override
				public void onItemClick(View view, int pos) {
					startExpertActivity(pos);
				}

			});
		}

		((ExpertsListRecyclerAdapter) expertsList.getAdapter()).addData(objs);

	}

	private void startExpertActivity(int pos) {
		Intent intent = new Intent(getActivity(), ExpertProfileActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("expert_obj", ((ExpertsListRecyclerAdapter) expertsList.getAdapter()).getItem(pos));
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.retry_layout:
			if (isRequestFailed) {
				if(AllCategories.getInstance().getPhotoCateogryObjs() == null ||AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() == null || (AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category()!=null && AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category().size() == 0))
				{
					loadArticlePhotoCategories();
				}else{
					loadData();
				}
			}
			break;
		/*case R.id.cat_filter_layout:

			if(AllCategories.getInstance().getPhotoCateogryObjs() == null){
				if(!CommonLib.isNetworkAvailable(getActivity()))
					showToast("Please connect to internet");
				else
                	showToast("Something went wrong. Please try again.");

			}else {
				Bundle bundle = new Bundle();
				bundle.putBoolean("is_expert", true);
				bundle.putInt("selected_pos", categoryId);
				ArticleCategoryDialogFragment dialogFragment = ArticleCategoryDialogFragment.newInstance(bundle);
				dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJCustomDialogTheme);
				dialogFragment.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(int pos) {
						if (pos == 0) {
							nextUrl = EXPERT_LIST_URL + "?filter=0";
							categoryId = -1;
							setCategoryFilterText("All");
						} else {
							categoryId = Integer.parseInt(AllCategories.getInstance().getPhotoCateogryObjs()
									.getExpert_category().get(pos - 1).getId());
							nextUrl = EXPERT_LIST_URL + "?filter=1" + "&cat=" + categoryId;
							setCategoryFilterText(AllCategories.getInstance().getPhotoCateogryObjs().getExpert_category()
									.get(pos - 1).getName());
						}
						isRefreshData = true;
						loadData();
					}
				});
				dialogFragment.show(getActivity().getSupportFragmentManager(), ARTICLE_PHOTO_CATEGROY_DIALOG_TAG);
			}
			break;
		*/}
	}

	private void setCategoryFilterText(String text) {
		((TextView) view.findViewById(R.id.category_name)).setText(text);
		if (categoryId == -1) {
			((TextView) view.findViewById(R.id.category_name))
					.setTextColor(getResources().getColor(R.color.text_color1));
		} else {
			((TextView) view.findViewById(R.id.category_name))
					.setTextColor(getResources().getColor(R.color.green_text_color));
		}
	}

	@Override
	public void onRequestStarted(String requestTag) {
		if (!isDestroyed && requestTag.equalsIgnoreCase(EXPERT_LIST_REQUEST_TAG)) {
			if (expertsList.getAdapter() == null || expertsList.getAdapter().getItemCount() == 0 || isRefreshData) {
				if(showLoading){
					showLoadingView();
					changeViewVisiblity(expertsList, View.GONE);
				}
				if (isRefreshData) {
					if (expertsList.getAdapter() != null)
						((ExpertsListRecyclerAdapter) expertsList.getAdapter()).removePreviousData();
				}
			} else {

			}
			isLoading = true;
		} else if (!isDestroyed && (requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)
				|| requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG))) {
			showLoadingView();
			changeViewVisiblity(expertsList, View.GONE);
		}
	}

	@Override
	public void onRequestCompleted(String requestTag, Object obj) {
		if (!isDestroyed && requestTag.equalsIgnoreCase(EXPERT_LIST_REQUEST_TAG)) {
			if (((ExpertListObject) obj).getOutput().size() == 0) {
				if (expertsList.getAdapter() == null || expertsList.getAdapter().getItemCount() == 1) {
					showNullCaseView("No Experts");

				} else {
                    showToast("No more Experts");

				}
				isRequestAllowed = false;
			} else {
				setAdapterData(((ExpertListObject) obj).getOutput());
				nextUrl = ((ExpertListObject) obj).getNext_url();
				showView();
				changeViewVisiblity(expertsList, View.VISIBLE);
				if (((ExpertListObject) obj).getOutput().size() < 10) {
					isRequestAllowed = false;
					if (expertsList.getAdapter() != null) {
						((ExpertsListRecyclerAdapter) expertsList.getAdapter()).removeItem();
					}
				} else {
					isRequestAllowed = true;
				}
			}
			if (isRefreshData) {
				isRefreshData = false;
			}
			isLoading = false;
		} else if (!isDestroyed && requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)) {
			isProductFilterLoaded = true;
			if (isProductFilterLoaded && isArticleFilterLoaded)
				loadData();
		} else if (requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG)) {
			isArticleFilterLoaded = true;
			if (isProductFilterLoaded && isArticleFilterLoaded)
				loadData();
		}
	}

	@Override
	public void onRequestFailed(String requestTag, Object obj) {
		if (!isDestroyed && requestTag.equalsIgnoreCase(EXPERT_LIST_REQUEST_TAG)) {
			if (expertsList.getAdapter() == null || expertsList.getAdapter().getItemCount() == 0) {
				showNetworkErrorView();
				changeViewVisiblity(expertsList, View.GONE);
				isRefreshData = false;
			} else {
				if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");

					((ExpertsListRecyclerAdapter) expertsList.getAdapter()).removeItem();
				} else {
                    showToast(((ErrorObject) obj).getErrorMessage());

					((ExpertsListRecyclerAdapter) expertsList.getAdapter()).removeItem();
				}
				isRequestAllowed = false;
			}
			isLoading = false;
		}   else if (!isDestroyed && requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)) {
			isProductFilterLoaded = false;
			//loadProductCategories();
		} else if (!isDestroyed && requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG)) {
			isArticleFilterLoaded = false;
			showNetworkErrorView();
			changeViewVisiblity(expertsList, View.GONE);
		}

	}

	@Override
	public void onResume() {
		isDestroyed = false;
		super.onResume();
	}

	@Override
    public  void onDestroy() {
		isDestroyed = true;
		GetRequestManager.getInstance().removeCallbacks(this);
		super.onDestroy();
	}

	private void loadArticlePhotoCategories() {

		String url = AppApplication.getInstance().getBaseUrl() + AppConstants.ARTICLE_PHOTO_CATEGORY_REQUEST_URL;

		GetRequestManager.getInstance().makeAyncRequest(url, ARTICLE_PHOTO_CAT_REQUEST_TAG,
				ObjectTypes.OBJECT_TYPE_ARTICLE_PHOTO_CATEGORY_LIST);

	}

	private void loadProductCategories() {
		String productCatUrl = AppApplication.getInstance().getBaseUrl() + AppConstants.COMPLETE_CATEGORY_LISTING_URL;

		GetRequestManager.getInstance().makeAyncRequest(productCatUrl, COMPLETE_CATEGORY_LIST_REQUEST_TAG,
				ObjectTypes.OBJECT_TYPE_COMPLETE_CATEGORY_LIST);

	}
}
