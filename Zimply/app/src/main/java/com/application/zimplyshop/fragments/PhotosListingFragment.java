package com.application.zimplyshop.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.PhotoDetailsActivity;
import com.application.zimplyshop.adapters.PhotosListRecyclerAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.HomePhotoObj;
import com.application.zimplyshop.baseobjects.PhotoListObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.objects.AllCategories;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

import java.util.ArrayList;

public class PhotosListingFragment extends BaseFragment
		implements GetRequestListener, RequestTags, OnClickListener, AppConstants, UploadManagerCallback, ImageLoaderManager.ImageLoaderCallback {

	RecyclerView photosList;

	boolean isLoading, isRequestAllowed;

	String nextUrl;

	int pastVisiblesItems, visibleItemCount, totalItemCount;

	int width;

	boolean isDestroyed;

	int sortById = -1, categoryId = -1, sizeId = -1, budgetId = -1, styleId = -1;

	boolean isRefreshData;

	int clickedPos = -1;

	boolean isProductFilterLoaded = true;
	boolean isArticleFilterLoaded = false;
	boolean showLoading;
	static PhotosListingFragment fragment;

    public static PhotosListingFragment newInstance(Bundle bundle){
		if(fragment == null) {
			fragment = new PhotosListingFragment();
			fragment.setArguments(bundle);
		}
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

		photosList = (RecyclerView) view.findViewById(R.id.experts_list);
		photosList.setLayoutManager(new LinearLayoutManager(getActivity()));
		photosList
				.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));

		setLoadingVariables();
		setFilterVariables();
		setFiltersClick();
		width = (3 * getDisplayMetrics().widthPixels - (2 * getResources().getDimensionPixelSize(R.dimen.margin_small)))
				/ 4;
		setCategoryFilterText("All");
		setSortByFilterName("None");
		setBasicFilterText("None");
		GetRequestManager.getInstance().addCallbacks(this);
//		loadData();

		retryLayout.setOnClickListener(this);
		UploadManager.getInstance().addCallback(this);
        return view;
	}


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
		if(isVisibleToUser && getActivity()!=null && photosList.getAdapter() == null && !isLoading){
            if(AllCategories.getInstance().getPhotoCateogryObjs() == null ||AllCategories.getInstance().getPhotoCateogryObjs().getPhoto_category() == null || (AllCategories.getInstance().getPhotoCateogryObjs().getPhoto_category()!=null && AllCategories.getInstance().getPhotoCateogryObjs().getPhoto_category().size() == 0))
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
		filterLayout.setOnClickListener(this);
		categoriesLayout.setOnClickListener(this);
		sortByLayout.setVisibility(View.GONE);
		selectfiltersLayout.setOnClickListener(this);
		view.findViewById(R.id.separator2).setVisibility(View.GONE);
	}

	private void loadData() {
        if(CommonLib.isNetworkAvailable(getActivity())) {
            String finalUrl;
            if (nextUrl == null) {
				CommonLib.ZLog("photo_width", width / 3);
                finalUrl = AppApplication.getInstance().getBaseUrl() + PHOTOS_LISTING_URL + "?photo_width="+width
                        + "&filter=0"
						+ "&thumb_width=" + width / 10
                        + (AppPreferences.isUserLogIn(getActivity()) ? "&userid=" + AppPreferences.getUserID(getActivity()) : "");
            } else {
                finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
            }

            GetRequestManager.getInstance().makeAyncRequest(finalUrl, PHOTOS_LIST_REQUEST_TAG,
                    ObjectTypes.OBJECT_TYPE_PHOTO_LIST_OBJECT);
        }else{
            showToast("Failed to load.Please check your internet connection.");
        }
	}

	/**
	 * Add Photos to a recycler view
	 */
	private void setAdapterData(final ArrayList<HomePhotoObj> objs) {

		if (photosList.getAdapter() == null) {
			int height = (4 * getDisplayMetrics().heightPixels) / 10;
			int width = getDisplayMetrics().widthPixels
					- (2 * getResources().getDimensionPixelSize(R.dimen.margin_medium));
			final PhotosListRecyclerAdapter adapter = new PhotosListRecyclerAdapter(getActivity(), height, width);
			photosList.setAdapter(adapter);
			photosList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

					visibleItemCount = photosList.getLayoutManager().getChildCount();
					totalItemCount = photosList.getLayoutManager().getItemCount();
					pastVisiblesItems = ((LinearLayoutManager) photosList.getLayoutManager())
							.findFirstVisibleItemPosition();

					if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLoading && isRequestAllowed && photosList.getAdapter().getItemCount()>0) {
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
			adapter.setOnItemClickListener(new PhotosListRecyclerAdapter.OnItemClickListener() {

				@Override
				public void onItemClick(int pos) {
					clickedPos = pos;
					Intent intent = new Intent(getActivity(), PhotoDetailsActivity.class);
//					intent.putExtra("photo_obj", ((PhotosListRecyclerAdapter) photosList.getAdapter()).getItem(pos));
					intent.putExtra("photo_objs", adapter.getObjs());
					intent.putExtra("position", pos);
					intent.putExtra("nextUrl", nextUrl);
					startActivity(intent);
				}
			});
		}
		((PhotosListRecyclerAdapter) photosList.getAdapter()).addData(objs);
	}

	private void addToolbarView(Toolbar toolbar) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.common_toolbar_text_layout, null);
		TextView titleText = (TextView) view.findViewById(R.id.title_textview);
		titleText.setText(getString(R.string.photos_text));
		toolbar.addView(view);
	}


	@Override
	public void onRequestStarted(String requestTag) {
		if (!isDestroyed && requestTag.equalsIgnoreCase(PHOTOS_LIST_REQUEST_TAG)) {
			if (photosList.getAdapter() == null || photosList.getAdapter().getItemCount() == 0 || isRefreshData) {
				if(showLoading){
					showLoadingView();
					changeViewVisiblity(photosList, View.GONE);
				}
				if (isRefreshData && (photosList.getAdapter() != null)) {
					((PhotosListRecyclerAdapter) photosList.getAdapter()).removePreviousData();
				}
			} else {

			}
			isLoading = true;
		} else if (!isDestroyed && (requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)
				|| requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG))) {
			showLoadingView();
			changeViewVisiblity(photosList, View.GONE);
		}
	}

	@Override
	public void onRequestCompleted(String requestTag, Object obj) {
		if (!isDestroyed && requestTag.equalsIgnoreCase(PHOTOS_LIST_REQUEST_TAG)) {
			if (((PhotoListObject) obj).getOutput().size() == 0) {
				if (photosList.getAdapter() == null || photosList.getAdapter().getItemCount() == 1) {
					showNullCaseView("No Photos");

				} else {
					((PhotosListRecyclerAdapter) photosList.getAdapter()).removeItem();
                    showToast("No more Photos");

				}
				isRequestAllowed = false;
			} else {
				setAdapterData(((PhotoListObject) obj).getOutput());
				nextUrl = ((PhotoListObject) obj).getNext_url();
				showView();
				changeViewVisiblity(photosList, View.VISIBLE);
				if (((PhotoListObject) obj).getOutput().size() < 10) {
					isRequestAllowed = false;
					((PhotosListRecyclerAdapter) photosList.getAdapter()).removeItem();
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
		if (!isDestroyed && requestTag.equalsIgnoreCase(PHOTOS_LIST_REQUEST_TAG)) {
			if (photosList.getAdapter() == null || photosList.getAdapter().getItemCount() == 0 || isRefreshData) {
				showNetworkErrorView();
				changeViewVisiblity(photosList, View.GONE);
				isRefreshData = false;
			} else {
				if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");
				} else {
                    showToast(((ErrorObject) obj).getErrorMessage());
				}

				((PhotosListRecyclerAdapter) photosList.getAdapter()).removeItem();
				isRequestAllowed = false;
			}
			isLoading = false;
		}  else if (!isDestroyed && requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)) {
			isProductFilterLoaded = false;
			//loadProductCategories();
		} else if (!isDestroyed && requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG)) {
			isArticleFilterLoaded = false;
			showNetworkErrorView();
			changeViewVisiblity(photosList, View.GONE);
		}
	}

	@Override
    public void onDestroy() {
		isDestroyed = true;
		GetRequestManager.getInstance().removeCallbacks(this);
		UploadManager.getInstance().removeCallback(this);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.retry_layout:
				if(isRequestFailed){
					if(AllCategories.getInstance().getPhotoCateogryObjs() == null ||AllCategories.getInstance().getPhotoCateogryObjs().getPhoto_category() == null || (AllCategories.getInstance().getPhotoCateogryObjs().getPhoto_category()!=null && AllCategories.getInstance().getPhotoCateogryObjs().getPhoto_category().size() == 0))
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
					showToast("Failed to load.Please check your internet connection.");
				else
					showToast("Something went wrong. Please try again.");
			}else {

					Bundle bundle = new Bundle();
					bundle.putBoolean("is_photos", true);
					bundle.putInt("selected_pos", categoryId);
					ArticleCategoryDialogFragment dialogFragment = ArticleCategoryDialogFragment.newInstance(bundle);
					dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJCustomDialogTheme);
					dialogFragment.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(int pos) {
							if (pos == 0) {
								nextUrl = PHOTOS_LISTING_URL + "?photo_width=360&filter=0"
										+ ((sizeId != -1) ? ("&size=" + sizeId) : "")
										+ ((budgetId != -1) ? ("&budget=" + budgetId) : "")
										+ ((styleId != -1) ? ("&style=" + styleId) : "")
										+ (AppPreferences.isUserLogIn(getActivity())
										? "&userid=" + AppPreferences.getUserID(getActivity()) : "");
								categoryId = -1;
								setCategoryFilterText("All");
							} else {
								categoryId = Integer.parseInt(AllCategories.getInstance().getPhotoCateogryObjs()
										.getPhoto_category().get(pos - 1).getId());
								nextUrl = PHOTOS_LISTING_URL + "?photo_width=360&filter=1" + "&cat=" + categoryId
										+ ((sizeId != -1) ? ("&size=" + sizeId) : "")
										+ ((budgetId != -1) ? ("&budget=" + budgetId) : "")
										+ ((styleId != -1) ? ("&style=" + styleId) : "")
										+ (AppPreferences.isUserLogIn(getActivity())
										? "&userid=" + AppPreferences.getUserID(getActivity()) : "");
								setCategoryFilterText(AllCategories.getInstance().getPhotoCateogryObjs().getPhoto_category()
										.get(pos - 1).getName());
							}
							isRefreshData = true;
							loadData();
						}
					});
					dialogFragment.show(getActivity().getSupportFragmentManager(), ARTICLE_PHOTO_CATEGROY_DIALOG_TAG);

			}
			break;
		case R.id.sort_filter_layout:
			if(AllCategories.getInstance().getPhotoCateogryObjs() == null){
				if(!CommonLib.isNetworkAvailable(getActivity()))
					showToast("Failed to load.Please check your internet connection.");
				else
					showToast("Something went wrong. Please try again.");

			}else {

                    Bundle photoBundle = new Bundle();
                    photoBundle.putBoolean("is_photos", true);
                    SortDialogFragment dialog = SortDialogFragment.newInstance(photoBundle);
                    dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJCustomDialogTheme);
                    dialog.show(getActivity().getSupportFragmentManager(), ARTICLE_SORT_CATEGROY_DIALOG_TAG);
                    dialog.setOnSortItemClickListener(new OnSortItemClickListener() {

                        @Override
                        public void onOptionsSelected(int id) {
                            if (id == 1) {
                                setSortByFilterName("Latest");
                            } else {
                                setSortByFilterName("None");
                            }
                            if (id == -1) {
                                sortById = -1;
                                nextUrl = PHOTOS_LISTING_URL
                                        + ((categoryId != -1) ? ("?photo_width=360&filter=1&cat=" + categoryId)
                                        : "?photo_width=360&filter=0")
                                        + (AppPreferences.isUserLogIn(getActivity())
                                        ? "&userid=" + AppPreferences.getUserID(getActivity()) : "");
                            } else {
                                sortById = id;
                                nextUrl = PHOTOS_LISTING_URL + "?o=" + sortById
                                        + ((categoryId != -1) ? ("&photo_width=360&filter=1&cat=" + categoryId) : "&filter=0")
                                        + (AppPreferences.isUserLogIn(getActivity())
                                        ? "&userid=" + AppPreferences.getUserID(getActivity()) : "");
                            }
                            isRefreshData = true;
                            loadData();
                        }
                    });
            }

			break;
			*/
		case R.id.filter_filter_layout:
			if(AllCategories.getInstance().getPhotoCateogryObjs() == null) {
				if(!CommonLib.isNetworkAvailable(getActivity()))
					showToast("Failed to load.Please check your internet connection.");
				else
					showToast("Something went wrong. Please try again.");

			}else {

                    Intent intent = new Intent(getActivity(), PhotoFilterFragment.class);

                    intent.putExtra("budget_id", budgetId);
                    intent.putExtra("size_id", sizeId);
                    intent.putExtra("style_id", styleId);
                    getActivity().startActivityForResult(intent, AppConstants.REQUEST_PHOTO_FILTER_ACTIVITY);

			}
			break;
		}
    }

	private void setSortByFilterName(String text) {

		((TextView) view.findViewById(R.id.sortby_name)).setText(text);
		if (sortById == -1) {
			((TextView) view.findViewById(R.id.sortby_name))
					.setTextColor(getResources().getColor(R.color.text_color1));
		} else {
			((TextView) view.findViewById(R.id.sortby_name))
					.setTextColor(getResources().getColor(R.color.green_text_color));
		}
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_PHOTO_FILTER_ACTIVITY) {
				int id = data.getIntExtra("id", 1) + 1;
				int type = data.getIntExtra("type", 1);
				if (type == TYPE_PHOTO_FILTER_BUDGET) {
					budgetId = id;
				} else if (type == TYPE_PHOTO_FILTER_SIZE) {
					sizeId = id;
				} else if (type == TYPE_PHOTO_FILTER_STYLE) {
					styleId = id;
				} else {
					budgetId = -1;
					sizeId = -1;
					styleId = -1;
				}
				sortById = -1;
                nextUrl = PHOTOS_LISTING_URL
						+ ((categoryId != -1) ? ("?filter=1&cat=" + categoryId)
								: ((sizeId != -1 || budgetId != -1 || styleId != -1) ? "?photo_width=360&filter=1"
										: "?photo_width=360&filter=0"))
						+ ((sizeId != -1) ? ("&size=" + sizeId) : "")
						+ ((budgetId != -1) ? ("&budget=" + budgetId) : "")
						+ ((styleId != -1) ? ("&style=" + styleId) : "")
						+ (AppPreferences.isUserLogIn(getActivity())
								? "&userid=" + AppPreferences.getUserID(getActivity()) : "");
				if (styleId == -1 && budgetId == -1 && sizeId == -1) {
					setBasicFilterText("None");
				} else {
					int i = 0;
					if (styleId != -1) {
						i++;
					}
					if (budgetId != -1) {
						i++;
					}
					if (sizeId != -1) {
						i++;
					}
					setBasicFilterText(i + " Applied");
				}
				isRefreshData = true;
				loadData();
			}
		}
	}

	private void setBasicFilterText(String text) {
		((TextView) view.findViewById(R.id.filter_filter_text)).setText(text);
		if (sizeId == -1 && budgetId == -1 && styleId == -1) {
			((TextView) view.findViewById(R.id.filter_filter_text))
					.setTextColor(getResources().getColor(R.color.text_color1));
		} else {
            ((TextView) view.findViewById(R.id.filter_filter_text))
					.setTextColor(getResources().getColor(R.color.green_text_color));
		}
	}

	@Override
	public void uploadFinished(int requestType, String objectId, Object data, Object respose, boolean status, int parserId) {
		if (requestType == MARK_FAVOURITE_REQUEST_TAG) {
			if (status) {
				((PhotosListRecyclerAdapter) photosList.getAdapter()).getItem(clickedPos).setIs_favourite(true);
				((PhotosListRecyclerAdapter) photosList.getAdapter()).getItem(clickedPos)
						.setFavorite_item_id((String) data);
			}
		} else if (requestType == MARK_UN_FAVOURITE_REQUEST_TAG) {
			if (status) {
				((PhotosListRecyclerAdapter) photosList.getAdapter()).getItem(clickedPos).setIs_favourite(false);
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

	@Override
	public void loadingStarted() {

	}

	@Override
	public void loadingFinished(Bitmap bitmap) {

	}
}
