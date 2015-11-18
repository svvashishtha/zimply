package com.application.zimplyshop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.AllFilterClassActivity;
import com.application.zimplyshop.activities.ArticleDescriptionActivity;
import com.application.zimplyshop.adapters.ArticlesListRecyclerAdapter;
import com.application.zimplyshop.adapters.ArticlesListRecyclerAdapter.OnItemClickListener;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ArticleListObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.HomeArticleObj;
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

public class ArticleListingFragment extends BaseFragment
        implements RequestTags, GetRequestListener, OnClickListener, AppConstants, UploadManagerCallback {

    String nextUrl;

    RecyclerView articleList;
    //isFilterapplied is used to toogle the visibility of the tick on filter button
    boolean isLoading, isRequestAllowed, isFilterApplied;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    boolean isDestroyed, isRefreshData;

    int categoryId = -1, sortById = 1;

    int clickedPos;

    boolean isProductFilterLoaded = true;
    boolean isArticleFilterLoaded = false;

    boolean showLoading;

    public static ArticleListingFragment newInstance(Bundle bundle) {
        ArticleListingFragment fragment = new ArticleListingFragment();
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

        view = LayoutInflater.from(getActivity()).inflate(R.layout.experts_list_layout, null);

        articleList = (RecyclerView) view.findViewById(R.id.experts_list);

        articleList.setLayoutManager(new LinearLayoutManager(getActivity()));
        articleList.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.margin_small)));
        // setRecyclerPhotosView();
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        setFilterVariables();
        selectfiltersLayout.setVisibility(View.GONE);
        view.findViewById(R.id.separator2).setVisibility(View.GONE);
        setFiltersClick();
        setCategoryFilterText("All");
        setSortByFilterName("Featured");
//		loadData();
//		loadProductCategories();
        GetRequestManager.getInstance().addCallbacks(this);

        UploadManager.getInstance().addCallback(this);

        return view;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && getActivity() != null && !isLoading && articleList.getAdapter() == null) {
            if (AllCategories.getInstance().getPhotoCateogryObjs() == null || AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() == null || (AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() != null && AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category().size() == 0)) {
                showLoading = false;
                loadArticlePhotoCategories();
            } else {
                showLoading = true;
                loadData();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    private void setFiltersClick() {
        filterLayout.setOnClickListener(this);
        categoriesLayout.setOnClickListener(this);
        sortByLayout.setOnClickListener(this);
    }

    private void loadData() {

        String finalUrl;
        if (nextUrl == null) {
            finalUrl = AppApplication.getInstance().getBaseUrl() + ARTICLE_LIST_URL + "?filter=0" + "&page=1" + "&o=" + sortById
                    + (AppPreferences.isUserLogIn(getActivity()) ? "&userid=" + AppPreferences.getUserID(getActivity()) : "");
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }
        GetRequestManager.getInstance().requestHTTPThenCache(finalUrl, ARTICLE_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_ARTICLE_LIST_OBJECT, GetRequestManager.THREE_HOURS);
    }

    /**
     * Add Photos to a recycler view
     */
    private void setAdapterData(ArrayList<HomeArticleObj> objs) {
        if (articleList.getAdapter() == null) {
            int height = (4 * getDisplayMetrics().heightPixels) / 10;
            ArticlesListRecyclerAdapter adapter = new ArticlesListRecyclerAdapter(this, height);
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
                    new ImageLoaderManager(getActivity()).setScrollState(newState);
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

    public void scrollCategoryLayout(int dy) {
        filterLayout.setTranslationY(dy);
    }

    public RecyclerView.ViewHolder getViewHolder(int position) {
        return articleList.findViewHolderForAdapterPosition(position);
    }

    private void startArticleDescriptionActivity(int pos) {
        clickedPos = pos;
        Intent intent = new Intent(getActivity(), ArticleDescriptionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("article_obj", ((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItem(pos));
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(ARTICLE_LIST_REQUEST_TAG)) {
            if (articleList.getAdapter() == null || articleList.getAdapter().getItemCount() == 0 || isRefreshData) {

                if (showLoading) {
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
                    if (AllCategories.getInstance().getPhotoCateogryObjs() == null || AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() == null || (AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() != null && AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category().size() == 0)) {
                        loadArticlePhotoCategories();
                    } else {
                        loadData();
                    }

                }
                break;
            case R.id.cat_filter_layout:
                if (AllCategories.getInstance().getPhotoCateogryObjs() == null) {
                    if (!CommonLib.isNetworkAvailable(getActivity()))
                        showToast("Please connect to internet");
                    else
                        showToast("Something went wrong. Please try again.");

                } else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("is_photos", false);
                    bundle.putInt("selected_pos", categoryId);
                    bundle.putInt("selected_sort", sortById);
                    Intent intent = new Intent(getActivity(), AllFilterClassActivity.class);
                    startActivity(intent);
                    /*dialogFragment.setOnApplyClickListener(new AllFilterClassActivity.OnApplyClickListener() {
                        @Override
						public void onApplyClick(Bundle bundle) {
							categoryId = bundle.getInt("selected_pos") == 0 ? -1 : Integer.parseInt(AllCategories.getInstance().getPhotoCateogryObjs()
									.getArticle_category().get(bundle.getInt("selected_pos") - 1).getId());
							nextUrl = ARTICLE_LIST_URL + "?filter=0" + ((categoryId != -1) ? ("&cat=" + categoryId) : "")
									+ ((sortById != -1) ? ("&o=" + sortById) : "")
									+ (AppPreferences.isUserLogIn(getActivity())
									? "&userid=" + AppPreferences.getUserID(getActivity()) : "");
							categoryId = -1;
							setCategoryFilterText((bundle.getInt("selected_pos") == 0) ? "All" : AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category()
									.get(bundle.getInt("selected_pos") - 1).getName());
							isRefreshData = true;
							loadData();
						}
					});*/


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
    public void onResume() {
        isDestroyed = false;
        super.onResume();
    }


    @Override
    public void onDestroy() {
        isDestroyed = true;
        UploadManager.getInstance().removeCallback(this);
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object respose, boolean status, int parserId) {
        if (requestType == MARK_FAVOURITE_REQUEST_TAG) {
            if (status) {
                if( articleList != null && articleList.getAdapter() != null && ((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItemCount() < clickedPos && ((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItem(clickedPos) != null){
                    ((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItem(clickedPos).setIs_favourite(true);
                    ((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItem(clickedPos)
                            .setFavorite_item_id((String) data);
                }
            }
        } else if (requestType == MARK_UN_FAVOURITE_REQUEST_TAG) {
            if (status) {
                if( articleList != null && articleList.getAdapter() != null && ((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItemCount() < clickedPos && ((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItem(clickedPos) != null)
                    ((ArticlesListRecyclerAdapter) articleList.getAdapter()).getItem(clickedPos).setIs_favourite(false);

            }
        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object object) {

    }

    private void loadArticlePhotoCategories() {

        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.ARTICLE_PHOTO_CATEGORY_REQUEST_URL;

        GetRequestManager.getInstance().requestHTTPThenCache(url, ARTICLE_PHOTO_CAT_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_ARTICLE_PHOTO_CATEGORY_LIST, GetRequestManager.THREE_HOURS);

    }

    public void showFilterLayout() {
        if (AllCategories.getInstance().getPhotoCateogryObjs() == null) {
            if (!CommonLib.isNetworkAvailable(getActivity()))
                showToast("Please connect to internet");
            else
                showToast("Something went wrong. Please try again.");

        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_articles", true);
            bundle.putInt("selected_pos", categoryId);
            bundle.putInt("sort_id", sortById);
            AllFilterClassActivity dialogFragment = AllFilterClassActivity.newInstance(bundle);
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJCustomDialogTheme);
            dialogFragment.setOnApplyClickListener(new AllFilterClassActivity.OnApplyClickListener() {
                @Override
                public void onApplyClick(Bundle bundle) {

                    categoryId = bundle.getInt("selected_pos");
                    sortById = bundle.getInt("sort_id");
                    nextUrl = ARTICLE_LIST_URL + "" + ((categoryId != 0) ? ("?filter=1&cat=" + Integer.parseInt(AllCategories.getInstance().getPhotoCateogryObjs()
                            .getArticle_category().get(categoryId - 1).getId())) : "?filter=0")
                            + ("&o=" + sortById)
                            + (AppPreferences.isUserLogIn(getActivity())
                            ? "&userid=" + AppPreferences.getUserID(getActivity()) : "");

                    isRefreshData = true;
                    if (categoryId != 0) {
                        isFilterApplied = true;
                        getActivity().findViewById(R.id.filter_applied).setVisibility(View.VISIBLE);
                    } else {
                        isFilterApplied = false;
                        getActivity().findViewById(R.id.filter_applied).setVisibility(View.GONE);
                    }
                    loadData();
                }
            });

            dialogFragment.show(getActivity().getSupportFragmentManager(), ARTICLE_PHOTO_CATEGROY_DIALOG_TAG);
        }
    }

    public boolean isFilterApplied() {
        return isFilterApplied;
    }
}
