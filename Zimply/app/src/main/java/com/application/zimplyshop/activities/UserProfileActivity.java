package com.application.zimplyshop.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimplyshop.adapters.UserProfileAdapter;

public class UserProfileActivity extends BaseActivity {

	RecyclerView userprofilelist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.expert_detail_layout);
		userprofilelist = (RecyclerView) findViewById(R.id.experts_data_list);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, null);
		((TextView) view.findViewById(R.id.title_textview)).setText(getString(R.string.profile_text));
		toolbar.addView(view);
		findViewById(R.id.hire_designer).setVisibility(View.GONE);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		userprofilelist.setLayoutManager(new LinearLayoutManager(this));
		UserProfileAdapter adapter = new UserProfileAdapter(this);

		userprofilelist.setAdapter(adapter);
		adapter.setOnItemClickListener(new UserProfileAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(int pos) {

			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
