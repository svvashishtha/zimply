package com.application.zimplyshop.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimplyshop.adapters.UserFavouritesListAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.FavListItemObject;
import com.application.zimplyshop.baseobjects.FavListObject;
import com.application.zimplyshop.baseobjects.HomeArticleObj;
import com.application.zimplyshop.baseobjects.HomePhotoObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

import java.util.ArrayList;

public class UserFavouritesActivity extends BaseActivity
        implements OnClickListener, GetRequestListener, RequestTags, UploadManagerCallback {

    RecyclerView favList;
    UserFavouritesListAdapter mAdapter;
    String nextUrl;

    boolean isDestroyed, isLoading, isRequestAllowed;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    int clickedPos, clickedType;
    private ArrayList<FavListItemObject> objects = new ArrayList<FavListItemObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_favourites_activity_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        favList = (RecyclerView) findViewById(R.id.fav_list);
        favList.setLayoutManager(new LinearLayoutManager(this));
        favList.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.margin_small)));

        setStatusBarColor();
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        GetRequestManager.getInstance().addCallbacks(this);
        UploadManager.getInstance().addCallback(this);
        loadData();
    }

    private void loadData() {
        String finalUrl;
        if (nextUrl == null) {
            finalUrl = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_FAV_LIST_URL + "?userid="
                    + AppPreferences.getUserID(this);
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }

        GetRequestManager.getInstance().makeAyncRequest(finalUrl, FAV_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_FAV_LIST);
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, null);
        TextView titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setText(getString(R.string.favourites_text));
        toolbar.addView(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_layout:
                loadData();
                break;
        }
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(FAV_LIST_REQUEST_TAG)) {

            if (favList.getAdapter() == null || favList.getAdapter().getItemCount() == 0) {
                showLoadingView();
                changeViewVisiblity(favList, View.GONE);

            } else {

            }
            isLoading = true;
        }

    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag.equalsIgnoreCase(FAV_LIST_REQUEST_TAG)) {

            if (((FavListObject) obj).getObjects().size() == 0) {
                if (favList.getAdapter() == null || favList.getAdapter().getItemCount() == 1) {
                    showNullCaseView("No Favourites");
                    changeViewVisiblity(favList, View.GONE);
                } else {
                    ((UserFavouritesListAdapter) favList.getAdapter()).removeItem();
                    showToast("No more Favourites");

                }
                isRequestAllowed = false;
            } else {
                setAdapterData(((FavListObject) obj).getObjects());
                nextUrl = ((FavListObject) obj).getNext_url();
                showView();
                changeViewVisiblity(favList, View.VISIBLE);
                if (((FavListObject) obj).getObjects().size() < 10) {
                    isRequestAllowed = false;
                    ((UserFavouritesListAdapter) favList.getAdapter()).removeItem();
                } else {
                    isRequestAllowed = true;
                }
            }

            isLoading = false;

        }
    }

    private void setAdapterData(ArrayList<FavListItemObject> objects1) {

        this.objects = objects1;
        if (mAdapter == null) {
            int height = (4 * getDisplayMetrics().heightPixels) / 10;

            mAdapter = new UserFavouritesListAdapter(this, height, height);
            favList.setAdapter(mAdapter);
            favList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    visibleItemCount = favList.getLayoutManager().getChildCount();
                    totalItemCount = favList.getLayoutManager().getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) favList.getLayoutManager())
                            .findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLoading && isRequestAllowed) {
                        loadData();
                    }
                    // scrollToolbarAndHeaderBy(-dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    new ImageLoaderManager(UserFavouritesActivity.this).setScrollState(newState);
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });

            mAdapter.setOnItemClickListener(new UserFavouritesListAdapter.OnItemClickListener() {

                @Override
                public void OnItemClick(int pos, int type) {
                    clickedPos = pos;
                    if (type == AppConstants.ITEM_TYPE_ARTICLE) {
                        clickedType = AppConstants.ITEM_TYPE_ARTICLE;
                        Intent intent = new Intent(UserFavouritesActivity.this, ArticleDescriptionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("article_obj",
                                (HomeArticleObj) ((UserFavouritesListAdapter) favList.getAdapter()).getItem(pos));
                        intent.putExtras(bundle);
                        intent.putExtra("position", pos);

                        startActivityForResult(intent, AppConstants.REQUEST_ARTICLE_DETAILS_REQUEST_CODE);
                    } else {
                        clickedType = AppConstants.ITEM_TYPE_PHOTO;
                        Intent intent = new Intent(UserFavouritesActivity.this, PhotoDetailsActivity.class);
                        ArrayList<HomePhotoObj> objs = new ArrayList<HomePhotoObj>();
                        objs.add((HomePhotoObj) ((ArrayList<FavListItemObject>) ((UserFavouritesListAdapter) favList.getAdapter()).getItems()).get(pos).getObj());
                        intent.putExtra("photo_objs", objs);
                        intent.putExtra("position", 0);
                        startActivityForResult(intent, AppConstants.REQUEST_PHOTO_DETAILS_REQUEST_CODE);
                    }
                }
            });

        }

        ((UserFavouritesListAdapter) favList.getAdapter()).addData(objects1);


    }

    @Override
    protected void onResume() {
        /*if(((UserFavouritesListAdapter)favList.getAdapter())!=null &&((UserFavouritesListAdapter)favList.getAdapter()).getItemCount() == 0){
            loadData();
		}*/
        super.onResume();
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(FAV_LIST_REQUEST_TAG)) {

            if (favList.getAdapter() == null || favList.getAdapter().getItemCount() == 0) {
                showNetworkErrorView();
                changeViewVisiblity(favList, View.GONE);

            } else {
                if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");

                } else {
                    showToast(((ErrorObject) obj).getErrorMessage());

                }
                ((UserFavouritesListAdapter) favList.getAdapter()).removeItem();
                isRequestAllowed = false;
            }
            isLoading = false;
        }

    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object respose, boolean status, int parserId) {
        if (isDestroyed)
            return;
        if (requestType == MARK_FAVOURITE_REQUEST_TAG) {
            if (status && data != null) {
                if (data instanceof HomePhotoObj) {
                    boolean itContains = false;
                    for (FavListItemObject object : objects) {
                        if (object.getType() == AppConstants.ITEM_TYPE_ARTICLE) {
                            if (object.getObj() != null && object.getObj() instanceof HomePhotoObj) {
                                if (((HomePhotoObj) object.getObj()).getSlug().equals(objectId)) {
                                    itContains = true;//remove this object
                                    break;
                                }
                            }
                        }
                    }
                    if (itContains)
                        return;
                    FavListItemObject object = new FavListItemObject();
                    object.setType(AppConstants.ITEM_TYPE_PHOTO);
                    object.setObj(data);
                    objects.add(object);
                    favList.getAdapter().notifyDataSetChanged();
                } else if (data instanceof HomeArticleObj) {
                    boolean itContains = false;
                    for (FavListItemObject object : objects) {
                        if (object.getType() == AppConstants.ITEM_TYPE_ARTICLE) {
                            if (object.getObj() != null && object.getObj() instanceof HomeArticleObj) {
                                if (((HomeArticleObj) object.getObj()).getSlug().equals(objectId)) {
                                    itContains = true;//remove this object
                                    break;
                                }
                            }
                        }
                    }
                    if (itContains)
                        return;
                    FavListItemObject object = new FavListItemObject();
                    object.setType(AppConstants.ITEM_TYPE_ARTICLE);
                    object.setObj(data);
                    objects.add(object);
                    favList.getAdapter().notifyDataSetChanged();
                }
            }

            if (favList.getAdapter() == null || favList.getAdapter().getItemCount() == 0) {
                showNetworkErrorView();
                changeViewVisiblity(favList, View.GONE);

            }

        } else if (requestType == MARK_UN_FAVOURITE_REQUEST_TAG) {
            if (status && data != null && favList != null) {
                if (data instanceof HomePhotoObj) {
                    Object itemToRemove = null;
                    for (FavListItemObject object : objects) {
                        if (object.getType() == AppConstants.ITEM_TYPE_ARTICLE) {
                            if (object.getObj() != null && object.getObj() instanceof HomePhotoObj) {
                                if (((HomePhotoObj) object.getObj()).getSlug().equals(objectId)) {
                                    itemToRemove = object;//remove this object
                                    break;
                                }
                            }
                        }
                    }
                    if (itemToRemove != null) {
                        objects.remove(itemToRemove);
                        favList.getAdapter().notifyDataSetChanged();
                    }
                } else if (data instanceof HomeArticleObj) {
                    Object itemToRemove = null;
                    for (FavListItemObject object : objects) {
                        if (object.getType() == AppConstants.ITEM_TYPE_ARTICLE) {
                            if (object.getObj() != null && object.getObj() instanceof HomeArticleObj) {
                                if (((HomeArticleObj) object.getObj()).getSlug().equals(objectId)) {
                                    itemToRemove = object;//remove this object
                                    break;
                                }
                            }
                        }
                    }
                    if (itemToRemove != null) {
                        objects.remove(itemToRemove);
                        favList.getAdapter().notifyDataSetChanged();
                    }
                }
            }
            if (favList.getAdapter() == null || favList.getAdapter().getItemCount() == 0) {
                showNetworkErrorView();
                changeViewVisiblity(favList, View.GONE);
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object object) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.REQUEST_PHOTO_DETAILS_REQUEST_CODE) {

                if (!intent.getBooleanExtra("is_favourite", false)) {
                    ((UserFavouritesListAdapter) favList.getAdapter()).removeItem(clickedPos);
                } else {
                    ((UserFavouritesListAdapter) favList.getAdapter()).getItem(clickedPos);
                }
            } else if (requestCode == AppConstants.REQUEST_ARTICLE_DETAILS_REQUEST_CODE) {
                if (!intent.getBooleanExtra("is_favourite", false)) {
                    ((UserFavouritesListAdapter) favList.getAdapter()).removeItem(clickedPos);
                } else {
                    ((UserFavouritesListAdapter) favList.getAdapter()).getItem(clickedPos);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}
