package com.application.zimply.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.adapters.ArticlesListRecyclerAdapter;
import com.application.zimply.adapters.ArticlesListRecyclerAdapter.OnItemClickListener;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.ArticleListObject;
import com.application.zimply.baseobjects.ErrorObject;
import com.application.zimply.baseobjects.HomeArticleObj;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.managers.ImageLoaderManager;
import com.application.zimply.objects.AllCategories;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.UploadManager;
import com.application.zimply.utils.UploadManagerCallback;
import com.application.zimply.widgets.SpaceItemDecoration;

import java.util.ArrayList;

public class ArticleListingActivity extends BaseActivity
		implements RequestTags, GetRequestListener, OnClickListener, AppConstants, UploadManagerCallback {

	String nextUrl;

	RecyclerView articleList;

	boolean isLoading, isRequestAllowed;

	int pastVisiblesItems, visibleItemCount, totalItemCount;

	boolean isDestroyed, isRefreshData;

	int categoryId = -1, sortById = 1;

	int clickedPos;

	boolean isProductFilterLoaded = true;
	boolean isArticleFilterLoaded = false;

	boolean showLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.experts_list_layout);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		addToolbarView(toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		articleList = (RecyclerView) findViewById(R.id.experts_list);

		articleList.setLayoutManager(new LinearLayoutManager(this));
		articleList.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.margin_small)));
		// setRecyclerPhotosView();
		setStatusBarColor();

		setLoadingVariables();
		retryLayout.setOnClickListener(this);
		setFilterVariables();
		selectfiltersLayout.setVisibility(View.GONE);
		findViewById(R.id.separator2).setVisibility(View.GONE);
		setFiltersClick();
		setCategoryFilterText("All");
		setSortByFilterName("Featured");
//		loadData();
//		loadProductCategories();
		GetRequestManager.getInstance().addCallbacks(this);
		if(AllCategories.getInstance().getPhotoCateogryObjs() == null ||AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() == null || (AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category()!=null && AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category().size() == 0))
		{	showLoading = false;
			loadArticlePhotoCategories();
		}else{
			showLoading = true;
			loadData();
		}
		UploadManager.getInstance().addCallback(this);


	}

	private void setFiltersClick() {
		filterLayout.setOnClickListener(this);
		categoriesLayout.setOnClickListener(this);
		sortByLayout.setOnClickListener(this);
	}

	private void loadData() {

		String finalUrl;
		if (nextUrl == null) {
			finalUrl = AppApplication.getInstance().getBaseUrl() + ARTICLE_LIST_URL + "?filter=0"
					+ (AppPreferences.isUserLogIn(this) ? "&userid=" + AppPreferences.getUserID(this) : "");
		} else {
			finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
		}
		GetRequestManager.getInstance().makeAyncRequest(finalUrl, ARTICLE_LIST_REQUEST_TAG,
				ObjectTypes.OBJECT_TYPE_ARTICLE_LIST_OBJECT);
	}

	/**
	 * Add Photos to a recycler view
	 */
	private void setAdapterData(ArrayList<HomeArticleObj> objs) {
		if (articleList.getAdapter() == null) {
			int height = (4 * getDisplayMetrics().heightPixels) / 10;
			ArticlesListRecyclerAdapter adapter = new ArticlesListRecyclerAdapter(new Fragment(), height);
			articleList.setAdapter(adapter);
			articleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

					visibleItemCount = articleList.getLayoutManager().getChildCount();
					totalItemCount = articleList.getLayoutManager().getItemCount();
					pastVisiblesItems = ((LinearLayoutManager) articleList.getLayoutManager())
							.findFirstVisibleItemPosition();

					if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLoading && isRequestAllowed) {
						loadData();
					}
					scrollToolbarAndHeaderBy(-dy);
				}

				@Override
				public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
					new ImageLoaderManager(ArticleListingActivity.this).setScrollState(newState);
					super.onScrollStateChanged(recyclerView, newState);
				}
			});
			((ArticlesListRecyclerAdapter) articleList.getAdapter()).setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClickListener(int pos) {

					startArticleDescriptionActivity(pos);
				}

			});

		}
		((ArticlesListRecyclerAdapter) articleList.getAdapter()).addData(objs);

	}

	public RecyclerView.ViewHolder getViewHolder(int position) {
		return articleList.findViewHolderForAdapterPosition(position);
	}

	private void startArticleDescriptionActivity(int pos) {
		clickedPos = pos;
		Intent intent = new Intent(this, ArticleDescriptionActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("article_obj",((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItem(pos));
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void addToolbarView(Toolbar toolbar) {
		View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, null);
		TextView titleText = (TextView) view.findViewById(R.id.title_textview);
		titleText.setText(getString(R.string.articles_text));
		toolbar.addView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRequestStarted(String requestTag) {
		if (!isDestroyed && requestTag.equalsIgnoreCase(ARTICLE_LIST_REQUEST_TAG)) {
			if (articleList.getAdapter() == null || articleList.getAdapter().getItemCount() == 0 || isRefreshData) {

				if(showLoading){
					showLoadingView();
					changeViewVisiblity(articleList, View.GONE);
				}
				if (isRefreshData) {
					if (articleList.getAdapter() != null)
						((ArticlesListRecyclerAdapter) articleList.getAdapter()).removePreviousData();
				}
			} else {

			}
			isLoading = true;
		} else if (!isDestroyed && (requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)
				|| requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG))) {
			showLoadingView();
			changeViewVisiblity(articleList, View.GONE);
		}

	}

	@Override
	public void onRequestCompleted(String requestTag, Object obj) {
		if (!isDestroyed && requestTag.equalsIgnoreCase(ARTICLE_LIST_REQUEST_TAG)) {
			if (((ArticleListObject) obj).getOutput().size() == 0) {
				if (articleList.getAdapter() == null || articleList.getAdapter().getItemCount() == 1) {
					showNullCaseView("No Articles");
					changeViewVisiblity(articleList, View.GONE);
				} else {
					((ArticlesListRecyclerAdapter) articleList.getAdapter()).removeItem();
                    showToast("No more Articles");

				}
				isRequestAllowed = false;
			} else {
				setAdapterData(((ArticleListObject) obj).getOutput());
				nextUrl = ((ArticleListObject) obj).getNext_url();
				showView();
				changeViewVisiblity(articleList, View.VISIBLE);
				if (((ArticleListObject) obj).getOutput().size() < 10) {
					isRequestAllowed = false;
					((ArticlesListRecyclerAdapter) articleList.getAdapter()).removeItem();
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
		if (!isDestroyed && requestTag.equalsIgnoreCase(ARTICLE_LIST_REQUEST_TAG)) {
			if (articleList.getAdapter() == null || articleList.getAdapter().getItemCount() == 0 || isRefreshData) {
				showNetworkErrorView();
				changeViewVisiblity(articleList, View.GONE);
				isRefreshData = false;
			} else {
				if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");

				} else {
                    showToast(((ErrorObject) obj).getErrorMessage());

				}
				((ArticlesListRecyclerAdapter) articleList.getAdapter()).removeItem();
				isRequestAllowed = false;
			}
			isLoading = false;
		} else if (!isDestroyed && requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)) {
			isProductFilterLoaded = false;
			//loadProductCategories();
		} else if (!isDestroyed && requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG)) {
			isArticleFilterLoaded = false;
			//loadArticlePhotoCategories();
			showNetworkErrorView();
			changeViewVisiblity(articleList, View.GONE);
		}

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
			case R.id.cat_filter_layout:
				/*if(AllCategories.getInstance().getPhotoCateogryObjs() == null){
                    showToast("Please connect to internet");

				}else {
					Bundle bundle = new Bundle();
					bundle.putBoolean("is_photos", false);
					bundle.putInt("selected_pos", categoryId);
					ArticleCategoryDialogFragment dialogFragment = ArticleCategoryDialogFragment.newInstance(bundle);
					dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJCustomDialogTheme);
					dialogFragment.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(int pos) {
							if (pos == 0) {
								nextUrl = ARTICLE_LIST_URL + "?filter=0"
										+ ((sortById != -1) ? ("&o=" + sortById) : "")
										+ (AppPreferences.isUserLogIn(ArticleListingActivity.this)
										? "&userid=" + AppPreferences.getUserID(ArticleListingActivity.this) : "");
								categoryId = -1;
								setCategoryFilterText("All");
							} else {
								categoryId = Integer.parseInt(AllCategories.getInstance().getPhotoCateogryObjs()
										.getArticle_category().get(pos - 1).getId());
								nextUrl = ARTICLE_LIST_URL + "?filter=1" + "&cat=" + categoryId
										+ ((sortById != -1) ? ("&o=" + sortById) : "")
										+ (AppPreferences.isUserLogIn(ArticleListingActivity.this)
										? "&userid=" + AppPreferences.getUserID(ArticleListingActivity.this) : "");
								setCategoryFilterText(AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category()
										.get(pos - 1).getName());
							}
							isRefreshData = true;
							loadData();
						}
					});
					dialogFragment.show(getSupportFragmentManager(), ARTICLE_PHOTO_CATEGROY_DIALOG_TAG);
				}
				break;
			case R.id.sort_filter_layout:
				if(AllCategories.getInstance().getPhotoCateogryObjs() == null){
                    showToast("Please connect to internet");
				}else {
					Bundle sortBunddle = new Bundle();
					sortBunddle.putInt("selected_pos", sortById);
					SortDialogFragment dialog = SortDialogFragment.newInstance(sortBunddle);
					dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJCustomDialogTheme);
					dialog.show(getSupportFragmentManager(), ARTICLE_SORT_CATEGROY_DIALOG_TAG);
					dialog.setOnSortItemClickListener(new OnSortItemClickListener() {

						@Override
						public void onOptionsSelected(int id) {
							if (id == -1) {
								sortById = -1;
								nextUrl = ARTICLE_LIST_URL
										+ ((categoryId != -1) ? ("?filter=1&cat=" + categoryId) : "?filter=0")
										+ (AppPreferences.isUserLogIn(ArticleListingActivity.this)
										? "&userid=" + AppPreferences.getUserID(ArticleListingActivity.this) : "");
							} else {
								sortById = id;
								nextUrl = ARTICLE_LIST_URL + "?o=" + sortById
										+ ((categoryId != -1) ? ("&filter=1&cat=" + categoryId) : "&filter=0")
										+ (AppPreferences.isUserLogIn(ArticleListingActivity.this)
										? "&userid=" + AppPreferences.getUserID(ArticleListingActivity.this) : "");
							}
							if (id == 1) {
								setSortByFilterName("Featured");
							} else if (id == 2) {
								setSortByFilterName("Latest");
							} else {
								setSortByFilterName("None");
							}

							isRefreshData = true;
							loadData();
						}
					});
				}
				break;
*/
		}
	}

	private void setSortByFilterName(String text) {

		((TextView) findViewById(R.id.sortby_name)).setText(text);
		if (sortById == -1) {
			((TextView) findViewById(R.id.sortby_name))
					.setTextColor(getResources().getColor(R.color.text_color1));
		} else {
			((TextView) findViewById(R.id.sortby_name))
					.setTextColor(getResources().getColor(R.color.green_text_color));
		}
	}

	private void setCategoryFilterText(String text) {
		((TextView) findViewById(R.id.category_name)).setText(text);
		if (categoryId == -1) {
			((TextView) findViewById(R.id.category_name))
					.setTextColor(getResources().getColor(R.color.text_color1));
		} else {
			((TextView) findViewById(R.id.category_name))
					.setTextColor(getResources().getColor(R.color.green_text_color));
		}
	}

	@Override
	protected void onResume() {
		isDestroyed = false;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		isDestroyed = true;
		UploadManager.getInstance().removeCallback(this);
		GetRequestManager.getInstance().removeCallbacks(this);
		super.onDestroy();
	}

	@Override
	public void uploadFinished(int requestType, String objectId, Object data, Object respose, boolean status, int parserId) {
		if (requestType == MARK_FAVOURITE_REQUEST_TAG) {
			if (status) {
				((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItem(clickedPos).setIs_favourite(true);
				((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItem(clickedPos)
						.setFavorite_item_id((String) data);
			}
		} else if (requestType == MARK_UN_FAVOURITE_REQUEST_TAG) {
			if (status) {
				((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItem(clickedPos).setIs_favourite(false);

			}
		}
	}

	@Override
	public void uploadStarted(int requestType, String objectId, int parserId, Object object) {

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
