package com.application.zimplyshop.activities;

import java.util.ArrayList;

import com.application.zimply.R;
import com.application.zimplyshop.adapters.CategoriesGridRecyclerAdapter;
import com.application.zimplyshop.adapters.CategoriesGridRecyclerAdapter.OnItemClickListener;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ShopCategoryListObject;
import com.application.zimplyshop.baseobjects.ShopCategoryObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.widgets.SpaceGridItemDecorator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ShopCategoriesActivity extends BaseActivity implements
		OnClickListener, GetRequestListener, RequestTags {

	RecyclerView categoriesGrid;
	boolean isDestroyed;
	int itemWidth, itemHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recyclerview_toolbar_layout);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		addToolbarView(toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		categoriesGrid = (RecyclerView) findViewById(R.id.categories_list);

		categoriesGrid.setLayoutManager(new GridLayoutManager(this, 2));
		categoriesGrid.addItemDecoration(new SpaceGridItemDecorator(
				(int) getResources().getDimension(R.dimen.margin_small),
				(int) getResources().getDimension(R.dimen.margin_mini)));

		itemWidth = (getDisplayMetrics().widthPixels - getResources()
				.getDimensionPixelSize(R.dimen.margin_small)) / 2;
		setStatusBarColor();
		setLoadingVariables();
		loadData();
	}

	public void loadData() {

		GetRequestManager.getInstance().addCallbacks(this);
		String url = AppApplication.getInstance().getBaseUrl()
				+ AppConstants.CATEGORY_LIST_URL;
		GetRequestManager.getInstance().makeAyncRequest(url,
				CATEGORY_LIST_REQUEST_TAG,
				ObjectTypes.OBJECT_TYPE_SHOP_CATEGORY_OBJECT);
	}

	private void setAdapterData(ArrayList<ShopCategoryObject> objs) {
		if (categoriesGrid.getAdapter() == null) {

			CategoriesGridRecyclerAdapter adapter = new CategoriesGridRecyclerAdapter(
					this, objs, itemWidth, itemWidth);
			categoriesGrid.setAdapter(adapter);
		}

		((CategoriesGridRecyclerAdapter) categoriesGrid.getAdapter())
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(int pos) {
						startSubCategoryActivity((ShopCategoryObject) ((CategoriesGridRecyclerAdapter) categoriesGrid
								.getAdapter()).getItem(pos));
					}

				});
	}

	private void startSubCategoryActivity(ShopCategoryObject shopCategory) {
		Intent intent = new Intent(this, ProductListingActivity.class);
		intent.putExtra("url", shopCategory.getSlug());
		startActivity(intent);
	}

	private void addToolbarView(Toolbar toolbar) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.common_toolbar_text_layout, null);
		TextView titleText = (TextView) view.findViewById(R.id.title_textview);
		titleText.setText(getString(R.string.shop_categories_text));
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
				&& requestTag.equalsIgnoreCase(CATEGORY_LIST_REQUEST_TAG)) {
			showLoadingView();
			changeViewVisiblity(categoriesGrid, View.GONE);
		}
	}

	@Override
	public void onRequestCompleted(String requestTag, Object obj) {
		if (!isDestroyed
				&& requestTag.equalsIgnoreCase(CATEGORY_LIST_REQUEST_TAG)) {
			setAdapterData(((ShopCategoryListObject) obj).getCategory());
			showView();
			changeViewVisiblity(categoriesGrid, View.VISIBLE);
		}

	}

	@Override
	public void onRequestFailed(String requestTag, Object obj) {
		if (!isDestroyed
				&& requestTag.equalsIgnoreCase(CATEGORY_LIST_REQUEST_TAG)) {
			showNetworkErrorView();
			changeViewVisiblity(categoriesGrid, View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		isDestroyed = true;
		GetRequestManager.getInstance().removeCallbacks(this);
		super.onDestroy();
	}
}
