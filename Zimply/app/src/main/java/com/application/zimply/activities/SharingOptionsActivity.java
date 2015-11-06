package com.application.zimply.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.application.zimply.R;
import com.application.zimply.adapters.ShareAppGridAdapter;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.utils.CommonLib;
import com.application.zimply.widgets.SlidingUpPanelLayout;
import com.application.zimply.widgets.SlidingUpPanelLayout.PanelSlideListener;
import com.application.zimply.widgets.SlidingUpPanelLayout.PanelState;
import com.application.zimply.widgets.ZHeaderGridView;
import com.facebook.widget.FacebookDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SharingOptionsActivity extends BaseActivity {

	SlidingUpPanelLayout mLayout;
	ZHeaderGridView grid;

	String TAG = "SlidingUpPanel Tag";

	int firstVisibleItem;

	ShareAppGridAdapter adapter;

//	CallbackManager callbackManager;

//	ShareDialog shareDialog;

	String shareTitle;
	String shareSlug;
	int shareTypeName;
	String shareItemName;
	String imageUrl;

	//ArrayList<String> tags;
	String shortUrl;

	public static float getPixels(int unit, float size) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return TypedValue.applyDimension(unit, size, metrics);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_app_grid_layout);
		mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		FrameLayout dimLayout = (FrameLayout) findViewById(R.id.frameLayout);

		shareTitle = getIntent().getStringExtra("title");

		shareSlug = getIntent().getStringExtra("slug");
		shareTypeName = getIntent().getIntExtra("type_name", 0);
		shareItemName = getIntent().getStringExtra("item_name");
		if (shareItemName == null || (shareItemName != null && shareItemName.equalsIgnoreCase("null"))) {
			shareItemName = "";
		}
		imageUrl = getIntent().getStringExtra("image_url");

		dimLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mLayout.setPanelSlideListener(new PanelSlideListener() {
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				if (panel.getPaddingBottom() != 0) {
					panel.setPadding(panel.getPaddingLeft(), panel.getPaddingTop(), panel.getPaddingRight(), 0);
				}
			}

			@Override
			public void onPanelExpanded(View panel) {
				CommonLib.ZLog(TAG, "onPanelExpanded");

			}

			@Override
			public void onPanelCollapsed(View panel) {
				CommonLib.ZLog(TAG, "onPanelCollapsed");
			}

			@Override
			public void onPanelAnchored(View panel) {
				int paddingPx = (int) getPixels(TypedValue.COMPLEX_UNIT_DIP, 48);
				panel.setPadding(panel.getPaddingLeft(), panel.getPaddingTop(), panel.getPaddingRight(), paddingPx);
			}

			@Override
			public void onPanelHidden(View panel) {
				CommonLib.ZLog(TAG, "onPanelHidden");
			}
		});

		adapter = new ShareAppGridAdapter(getPackageManager(), this, R.layout.z_share_app_grid_item_layout,
				getListOfPackages());

		grid = (ZHeaderGridView) findViewById(R.id.collection_images_grid);
//		FacebookSdk.sdkInitialize(getApplicationContext());
//		callbackManager = CallbackManager.Factory.create();
//		shareDialog = new ShareDialog(this);
		grid.setNumColumns(3);
		grid.setOnTouchListener(new OnTouchListener() {
			float before = -1;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					before = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					if (mLayout.getPanelState() == PanelState.COLLAPSED) {
						mLayout.requestDisallowInterceptTouchEvent(false);
						return true;
					} else if (mLayout.getPanelState() == PanelState.EXPANDED) {
						if (firstVisibleItem == 0 && grid.getChildAt(0).getTop() >= 0) {
							float now = event.getY();
							if (now > before) {
								mLayout.requestDisallowInterceptTouchEvent(false);
								return true;
							}
						}

					}

				}
				mLayout.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}

		});
		grid.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisible, int visibleItemCount, int totalItemCount) {
				firstVisibleItem = firstVisible;
			}
		});
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				ResolveInfo launchable = adapter.getItem(position);
				final ActivityInfo activity = launchable.activityInfo;

				if (activity.applicationInfo.packageName.equalsIgnoreCase("com.facebook.katana")) {
					shareOnFacebook();
				} else {
					ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
					Intent i = new Intent(Intent.ACTION_SEND/*_MULTIPLE*/);
					i.setType("text/plain");
					String shareText = shareTitle + shareItemName + " " + shortUrl;
					i.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
					if (shareTypeName == AppConstants.ITEM_TYPE_ARTICLE)
						i.putExtra(Intent.EXTRA_SUBJECT, "New story on zimply- " + shareItemName);
					else if (shareTypeName == AppConstants.ITEM_TYPE_PHOTO)
						i.putExtra(Intent.EXTRA_SUBJECT, "New photograph on zimply " + shareItemName);
					else if (shareTypeName == AppConstants.ITEM_TYPE_PRODUCT)
						i.putExtra(Intent.EXTRA_SUBJECT, "New product on zimply-" + shareItemName);

					i.setComponent(name);

					finish();
					startActivity(i);
					/*
					 * JSONObject obj = new JSONObject(); try { obj.put("type",
					 * shareTypeName); obj.put("slug", shareSlug); } catch
					 * (JSONException e) { e.printStackTrace(); }
					 *
					 * branch.getShortUrl( tags, activity.applicationInfo
					 * .loadLabel(getPackageManager()) + "",
					 * Branch.FEATURE_TAG_SHARE, null, obj, new
					 * BranchLinkCreateListener() {
					 *
					 * @Override public void onLinkCreate(String url,
					 * BranchError error) { ComponentName name = new
					 * ComponentName( activity.applicationInfo.packageName,
					 * activity.name); Intent i = new Intent(
					 * Intent.ACTION_SEND_MULTIPLE); i.setType("text/plain");
					 * String shareText = shareTitle + shareItemName + " " +
					 * url; i.putExtra( android.content.Intent.EXTRA_TEXT,
					 * shareText);
					 *
					 * i.setComponent(name);
					 *
					 * finish(); startActivity(i);
					 *
					 *
					 * });}
					 */

				}

			}

		});

		JSONObject obj = new JSONObject();
		try {
			obj.put("type", shareTypeName);
			obj.put("slug", shareSlug);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//Enter the url here
		shortUrl = "Some url";
		if(getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("short_url"))
			shortUrl = getIntent().getExtras().getString("short_url");

		if(shortUrl.equals("Some url")) {
			if(shareTypeName == AppConstants.ITEM_TYPE_PHOTO) {
				shortUrl = "http://www.zimply.in/p/shareSlug/";
			} else if(shareTypeName == AppConstants.ITEM_TYPE_ARTICLE) {
				shortUrl = "http://www.zimply.in/home/";
			}
		}
	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	private void shareOnFacebook() {
		if (shortUrl != null) {
			if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
					FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
					    // Publish the post using the Share Dialog
					    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
					            .setLink(shortUrl)
					            .setName("Zimply")
					            .setDescription(shareTitle + shareItemName + " ")
					            .build();
					    shareDialog.present();
//					    uiHelper.trackPendingDialogCall(shareDialog.present());
			}

//			ShareLinkContent content = new ShareLinkContent.Builder().setContentTitle("Zimply")
//					.setContentUrl(Uri.parse(shortUrl)).setContentDescription(shareTitle + shareItemName + " ")
//					.setImageUrl(Uri.parse(imageUrl)).build();
//
//			shareDialog.show(content);
			finish();
		} else {

			Toast.makeText(this, "Cannot share on facebook. Try again later.", Toast.LENGTH_SHORT).show();
		}
		/*
		 * JSONObject obj = new JSONObject(); try {
		 *
		 * obj.put("type", shareTypeName); obj.put("slug", shareSlug); } catch
		 * (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } branch.getShortUrl(tags, "Facebook",
		 * Branch.FEATURE_TAG_SHARE, null, obj, new BranchLinkCreateListener() {
		 *
		 * @Override public void onLinkCreate(String url, BranchError error) {
		 * Log.i("link", "Ready to share my link = " + url); ShareLinkContent
		 * content = new ShareLinkContent.Builder() .setContentTitle("Zimply")
		 * .setContentUrl(Uri.parse(url)) .setContentDescription( shareTitle +
		 * shareItemName + " ") .setImageUrl(Uri.parse(imageUrl)).build();
		 *
		 * shareDialog.show(content); finish(); } });
		 */
	}

	private List<ResolveInfo> getListOfPackages() {

		PackageManager pm = getPackageManager();
		Intent main = new Intent(Intent.ACTION_SEND);
		main.setType("text/plain");
		List<ResolveInfo> launchables = pm.queryIntentActivities(main, PackageManager.MATCH_DEFAULT_ONLY);
		sortList(launchables);
		return launchables;
	}

	private void sortList(List<ResolveInfo> launchables) {
		int counterValue = 0;
		for (int i = 0; i < launchables.size(); i++) {
			if (launchables.get(i).activityInfo.applicationInfo.packageName.equalsIgnoreCase("com.facebook.katana")
					|| launchables.get(i).activityInfo.applicationInfo.packageName.equalsIgnoreCase("com.whatsapp")
					|| launchables.get(i).activityInfo.applicationInfo.packageName
							.equalsIgnoreCase("com.google.android.gm")) {
				launchables.add(counterValue, launchables.get(i));
				launchables.remove(i + 1);
				counterValue++;
			}
			if (counterValue > 2)
				break;
		}
	}

}
