package com.application.zimplyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimplyshop.adapters.ExpertDetailListAdapter;
import com.application.zimplyshop.adapters.ExpertDetailListAdapter.OnItemClickListener;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.HomeExpertObj;
import com.application.zimplyshop.baseobjects.HomePhotoObj;
import com.application.zimplyshop.baseobjects.PhotoListObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.ZTracker;
import com.application.zimplyshop.widgets.SpaceExpertPhotosItemDecorator;

import java.util.ArrayList;

public class ExpertProfileActivity extends BaseActivity
		implements GetRequestListener, AppConstants, RequestTags, OnClickListener {

	RecyclerView expertDataList;

	int visibleItemCount, totalItemCount, pastVisiblesItems;

	boolean isLoading, isRequestAllowed = true;

	String nextUrl;
	HomeExpertObj obj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expert_detail_layout);
		expertDataList = (RecyclerView) findViewById(R.id.experts_data_list);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		obj = getIntent().getExtras().getParcelable("expert_obj");
		addToolbarView(toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		findViewById(R.id.hire_designer).setOnClickListener(this);

		expertDataList.setLayoutManager(new LinearLayoutManager(this));
		expertDataList.addItemDecoration(
				new SpaceExpertPhotosItemDecorator(getResources().getDimensionPixelSize(R.dimen.margin_small)));
		int width = getDisplayMetrics().widthPixels - getResources().getDimensionPixelSize(R.dimen.margin_small);
		int height = getDisplayMetrics().widthPixels / 3;

		int photoHeight = (4 * getDisplayMetrics().heightPixels) / 10;

		final ExpertDetailListAdapter adapter = new ExpertDetailListAdapter(this, obj, width, height, photoHeight);
		expertDataList.setAdapter(adapter);
		expertDataList.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				visibleItemCount = expertDataList.getLayoutManager().getChildCount();
				totalItemCount = expertDataList.getLayoutManager().getItemCount();
				pastVisiblesItems = ((LinearLayoutManager) expertDataList.getLayoutManager())
						.findFirstVisibleItemPosition();

				if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLoading && isRequestAllowed) {
					loadData();
				}
				super.onScrolled(recyclerView, dx, dy);
			}

		});
		adapter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(int pos) {
				startPhotoDetailActivity(adapter.getItem(pos));

			}

		});
		GetRequestManager.getInstance().addCallbacks(this);
		ZTracker.logGAEvent(this, "Expert", obj.getTitle(), "");
		loadData();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		return super.onCreateOptionsMenu(menu);
	}

	private void addToolbarView(Toolbar toolbar) {
		View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, null);
		TextView titleText = (TextView) view.findViewById(R.id.title_textview);
		titleText.setText(obj.getTitle());
		toolbar.addView(view);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			supportFinishAfterTransition();
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void startPhotoDetailActivity(HomePhotoObj obj) {
		Intent intent = new Intent(this, PhotoDetailsActivity.class);
		ArrayList<HomePhotoObj> objects = new ArrayList<HomePhotoObj>();
		objects.add(obj);
		intent.putExtra("photo_objs", objects);
		intent.putExtra("position", 0);
		startActivity(intent);
	}

	private void loadData() {
		String finalUrl;
		if (nextUrl == null) {
			finalUrl = AppApplication.getInstance().getBaseUrl() + EXPERT_PHOTO_REQUETS_URL + obj.getSlug()
					+ "/?photo_width=300&page_size=5";
		} else {
			finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
		}
		GetRequestManager.getInstance().makeAyncRequest(finalUrl, EXPERT_DETAIL_REQUEST_TAG,
				ObjectTypes.OBJECT_TYPE_PHOTO_LIST_OBJECT);
	}

	@Override
	public void onRequestStarted(String requestTag) {
		if (requestTag.equalsIgnoreCase(EXPERT_DETAIL_REQUEST_TAG)) {
			isLoading = true;
		}
	}

	@Override
	public void onRequestCompleted(String requestTag, Object obj) {
		if (requestTag.equalsIgnoreCase(EXPERT_DETAIL_REQUEST_TAG)) {
			((ExpertDetailListAdapter) expertDataList.getAdapter()).addData((((PhotoListObject) obj).getOutput()));
			nextUrl = ((PhotoListObject) obj).getNext_url();
			if ((((PhotoListObject) obj).getOutput()).size() < 5) {
				isRequestAllowed = false;
				((ExpertDetailListAdapter) expertDataList.getAdapter()).removeItem();
			}
		}
		isLoading = false;
	}

	@Override
	public void onRequestFailed(String requestTag, Object obj) {
		if (requestTag.equalsIgnoreCase(EXPERT_DETAIL_REQUEST_TAG)) {
			if (((ErrorObject) obj).getErrorCode() == 500) {
				showToast("Could not load photos");

			} else {
				showToast( ((ErrorObject) obj).getErrorMessage());

			}
			((ExpertDetailListAdapter) expertDataList.getAdapter()).removeItem();
			isRequestAllowed = false;
			isLoading = false;
		}
	}

	@Override
	protected void onDestroy() {
		GetRequestManager.getInstance().removeCallbacks(this);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hire_designer:
			Intent intent = new Intent(ExpertProfileActivity.this, FilterActivity.class);
			intent.putExtra("pro_slug", obj.getSlug());
			intent.putExtra("type", AppConstants.ITEM_TYPE_EXPERT);
			startActivity(intent);
			overridePendingTransition(R.anim.animate_bottom_in, R.anim.animate_bottom_out);

			break;
		}
	}

}
