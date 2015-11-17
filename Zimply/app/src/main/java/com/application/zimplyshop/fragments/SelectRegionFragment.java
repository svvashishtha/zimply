package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.application.zimply.R;
import com.application.zimplyshop.adapters.RegionListAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 10/5/2015.
 */
public class SelectRegionFragment extends BaseFragment implements RequestTags,AppConstants,GetRequestListener,View.OnClickListener{


    public static SelectRegionFragment newInstance(Bundle bundle){
        SelectRegionFragment fragment = new SelectRegionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    RecyclerView regionList;
    String cityId;

    OnRegionSelected mListener;

    String cityName;

    public void setOnRegionSelectedListener(OnRegionSelected listener){
        this.mListener = listener;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.select_regions_search_layout,null);
        regionList = (RecyclerView)view.findViewById(R.id.categories_list);
        regionList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments()!= null){
            cityId = getArguments().getString("city_id");
            cityName = getArguments().getString("city_name");
        }

        EditText searchText = (EditText)view.findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int start, int before, int count) {
                if (adapter != null)
                    adapter.getFilter().filter(arg0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        GetRequestManager.getInstance().addCallbacks(this);
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        loadData();
    }

    public void loadData(){
        String url = AppApplication.getInstance().getBaseUrl()+GET_REGION_LIST+"?city_id="+cityId;
        GetRequestManager.getInstance().makeAyncRequest(url, REGIONLIST_REQUESTTAG, ObjectTypes.OBJECT_TYPE_REGIONLIST);
    }
    RegionListAdapter adapter;
    public void setAdapterData(final ArrayList<CategoryObject> objs){
        adapter = new RegionListAdapter(getActivity() , objs);
        regionList.setAdapter(adapter);
        adapter.setOnItemClickListener(new RegionListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(CategoryObject obj) {
                AppPreferences.setIsLocationSaved(getActivity() , true);
                AppPreferences.setSavedLocality(getActivity(), obj.getName());
                AppPreferences.setSavedCity(getActivity(), cityName);
                mListener.onRegionSelected(obj.getName());
            }
        });
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if(requestTag.equalsIgnoreCase(REGIONLIST_REQUESTTAG )){
            showLoadingView();
            changeViewVisiblity(regionList,View.GONE);
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if(requestTag.equalsIgnoreCase(REGIONLIST_REQUESTTAG )) {
            setAdapterData((ArrayList<CategoryObject>)obj);
            showView();
            changeViewVisiblity(regionList, View.VISIBLE);
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if(requestTag.equalsIgnoreCase(REGIONLIST_REQUESTTAG )){
            showNetworkErrorView();
        }
    }
boolean isDestroyed;
    @Override
    public void onDestroy() {
        isDestroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.retry_layout:
                loadData();
                break;
        }
    }

    public interface OnRegionSelected{
        void onRegionSelected(String selectedRegion);
    }
}
