package com.application.zimply.activities;

import java.util.ArrayList;

import com.application.zimply.R;
import com.application.zimply.adapters.ShopSubCategoriesAdapter;
import com.application.zimply.adapters.ShopSubCategoriesAdapter.OnItemClickListener;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.ShopSubCategoryObj;
import com.application.zimply.baseobjects.ShopSubCategoryObjectList;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.UiUtils;
import com.application.zimply.widgets.SpaceItemDecoration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ShopSubCategoriesActivity extends BaseActivity implements
		OnClickListener, GetRequestListener, RequestTags {

	RecyclerView productList;

	boolean isDestroyed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recyclerview_toolbar_layout);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		addToolbarView(toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		productList = (RecyclerView) findViewById(R.id.categories_list);
		productList.setLayoutManager(new LinearLayoutManager(this));
		productList.addItemDecoration(new SpaceItemDecoration(
				(int) getResources().getDimension(R.dimen.margin_small)));

		setStatusBarColor();
		setLoadingVariables();
		setToolbarHeight();
		loadData();
	}

	private void loadData() {
		String id = getIntent().getStringExtra("url");
		String url = AppApplication.getInstance().getBaseUrl()
				+ AppConstants.SUB_CATEGORY_LIST_URL + id + "/";
		GetRequestManager.getInstance().addCallbacks(this);
		GetRequestManager.getInstance().makeAyncRequest(url,
				SUB_CATEGORY_LIST_REQUEST_TAG,
				ObjectTypes.OBJECT_TYPE_SHOP_SUB_CATEGORY_OBJECT);
	}

	private void addToolbarView(Toolbar toolbar) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.common_toolbar_text_layout, null);
		TextView titleText = (TextView) view.findViewById(R.id.title_textview);
		titleText.setText(getString(R.string.sub_categories));
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

	private void setAdapterData(ArrayList<ShopSubCategoryObj> objs) {

		ShopSubCategoriesAdapter adapter = new ShopSubCategoriesAdapter(this,
				objs);
		productList.setAdapter(adapter);
		productList.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				scrollToolbarBy(-dy);
				super.onScrolled(recyclerView, dx, dy);
			}

		});

		adapter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClicked(int pos) {
				Intent intent = new Intent(ShopSubCategoriesActivity.this,
						ProductListingActivity.class);
				intent.putExtra(
						"url",
						AppConstants.SUB_CATEGORY_PRODUCT_LIST_URL
								+ UiUtils
										.getId(((ShopSubCategoriesAdapter) productList
												.getAdapter()).getItem(pos)
												.getSlug()) + "/");
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.retry_layout:
			if (isRequestFailed) {
				loadData();
			}
			break;
		}
	}

	@Override
	public void onRequestStarted(String requestTag) {
		if (!isDestroyed
				&& requestTag.equalsIgnoreCase(SUB_CATEGORY_LIST_REQUEST_TAG)) {
			showLoadingView();
			changeViewVisiblity(productList, View.GONE);
		}
	}

	@Override
	public void onRequestCompleted(String requestTag, Object obj) {
		if (!isDestroyed
				&& requestTag.equalsIgnoreCase(SUB_CATEGORY_LIST_REQUEST_TAG)) {
			setAdapterData(((ShopSubCategoryObjectList) obj).getSubcategory());

			showView();
			changeViewVisiblity(productList, View.VISIBLE);
		}
	}

	@Override
	public void onRequestFailed(String requestTag, Object obj) {
		if (!isDestroyed
				&& requestTag.equalsIgnoreCase(SUB_CATEGORY_LIST_REQUEST_TAG)) {
			showNetworkErrorView();
			changeViewVisiblity(productList, View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		isDestroyed = true;
		GetRequestManager.getInstance().removeCallbacks(this);
		super.onDestroy();
	}
}
