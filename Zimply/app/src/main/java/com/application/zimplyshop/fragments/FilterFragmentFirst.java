package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.application.zimply.R;
import com.application.zimplyshop.activities.BaseActivity;
import com.application.zimplyshop.activities.FilterActivity;
import com.application.zimplyshop.adapters.ExpandableListAdapter;
import com.application.zimplyshop.baseobjects.CategoriesObject;
import com.application.zimplyshop.objects.ZFilter;

import java.util.ArrayList;

public class FilterFragmentFirst extends BaseFragment implements OnClickListener {

	private View rootView;
	private BaseActivity mActivity;
	ExpandableListAdapter expandableListAdapter;
	String selectedCategoryName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.photo_filter_form_first, container,false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = (BaseActivity) getActivity();

		ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.expandable_list);
		ArrayList<CategoriesObject> categoryObjectArrayList = new ArrayList<CategoriesObject>();
		addTempData(categoryObjectArrayList);
		expandableListAdapter = new ExpandableListAdapter(mActivity, categoryObjectArrayList);
		listView.setAdapter(expandableListAdapter);
		expandableListAdapter.setSelectedCategoryListener(new ExpandableListAdapter.selectedCategoryListener() {
			@Override
			public void setSelectedCategory(int groupPos ,int pos,String categoryName) {
				expandableListAdapter.setSelectedChildPosition(pos,groupPos);
				selectedCategoryName = categoryName;
				moveToNextFragment();
			}
		});
		listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				if(groupPosition ==expandableListAdapter.getGroupCount()-2 || groupPosition ==expandableListAdapter.getGroupCount()-7) {
					return false;
				}
				else{
					selectedCategoryName = expandableListAdapter.getCategoryName(groupPosition);
					expandableListAdapter.setSelectedGrpPosition(groupPosition);
                    moveToNextFragment();
					return true;
				}
			}
		});

	/*	for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
			listView.expandGroup(i);k
		}*/
	//	fixSizes();
		setListeners();
	}


	public void moveToNextFragment(){
		ZFilter filterObj = ((FilterActivity) mActivity).getFilter();
		filterObj.setCategory(selectedCategoryName);
		((FilterActivity) mActivity).setSecondFragment();
	}
	private void addTempData(ArrayList<CategoriesObject> categoryObjectArrayList) {
		ArrayList<String> subs = new ArrayList<String>();

		categoryObjectArrayList.add(new CategoriesObject("Architect", new ArrayList<String>()));
		subs.add("Commercial/Office Space Designer");
		subs.add("Hotel & Restaurant Designer");
		subs.add("Kitchen, Dining & Bathroom Designer");
		subs.add("Living & Bedroom Designer");
		subs.add("Retail Space Designer");
		categoryObjectArrayList.add(new CategoriesObject("Interior Designer And Decorator", subs));
		categoryObjectArrayList.add(new CategoriesObject("General Contractor", new ArrayList<String>()));
		categoryObjectArrayList.add(new CategoriesObject("Home Builder", new ArrayList<String>()));
		categoryObjectArrayList.add(new CategoriesObject("Landscape Architect/Contractor", new ArrayList<String>()));
		categoryObjectArrayList.add(new CategoriesObject("3D Rendering Service", new ArrayList<String>()));
		ArrayList<String> subs1 = new ArrayList<String>();
		subs1.add("Bathroom Fixtures & Plumbing Contractor");
		subs1.add("Carpenter & Furniture Contractor");
		subs1.add("Doors, Windows & Fixtures Contractor");
		subs1.add("Electrical Contractor");
		subs1.add("Fencing Contractor");
		subs1.add("Flooring Contractor");
		subs1.add("Home Cleaner Contractor");
		subs1.add("Home Automation Contractor");
		subs1.add(" Hvac Contractor");
		subs1.add("Lighting Contractor");
		subs1.add("Painting Contractor");
		subs1.add("Pest Control Contractor");
		subs1.add("Remodeling Contractor");
		subs1.add("Roofing Contractor");
		categoryObjectArrayList.add(new CategoriesObject("Home Service Contractor", subs1));
		categoryObjectArrayList.add(new CategoriesObject("Others", new ArrayList<String>()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*case R.id.top_img_1:
			((FilterActivity) mActivity).getSupportFragmentManager().popBackStack();
			break;
		case R.id.top_img_2:
			break;*/
		}
	}

	private void fixSizes() {
		//rootView.findViewById(R.id.projects_label).setPadding(width / 20, width / 20, width / 20, width / 20);
		
	}
	
	private void setListeners() {
		rootView.findViewById(R.id.next_bt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				moveToNextFragment();
			//	((FilterActivity) mActivity).setSecondFragment();
			}
		});
	}


}
