package com.application.zimplyshop.adapters;

import java.util.List;

import com.application.zimplyshop.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareAppGridAdapter extends ArrayAdapter<ResolveInfo> {

	PackageManager pm;

	Context context;

	int resource;

	public ShareAppGridAdapter(PackageManager pm, Context context, int resource, List<ResolveInfo> apps) {
		super(context, resource, apps);
		this.pm = pm;
		this.context = context;
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = newView(parent);
		}

		bindView(position, convertView);

		return (convertView);
	}

	private View newView(ViewGroup parent) {
		return (LayoutInflater.from(context).inflate(resource, parent, false));
	}

	private void bindView(int position, View row) {
		TextView label = (TextView) row.findViewById(R.id.app_label);

		label.setText(getItem(position).loadLabel(pm));

		ImageView icon = (ImageView) row.findViewById(R.id.app_image);

		icon.setImageDrawable(getItem(position).loadIcon(pm));
	}
}