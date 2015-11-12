package com.application.zimply.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.activities.FilterActivity;
import com.application.zimply.adapters.CitiesAdapter;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.CategoryObject;
import com.application.zimply.baseobjects.ErrorObject;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.objects.AllCities;
import com.application.zimply.objects.ZFilter;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.utils.CommonLib;
import com.application.zimply.utils.UploadManager;
import com.application.zimply.utils.UploadManagerCallback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilterFragmentThird extends BaseFragment
        implements OnClickListener, GetRequestListener, UploadManagerCallback {

    int width;
    ProgressDialog zProgressDialog;
    ArrayList<CategoryObject> cities = new ArrayList<CategoryObject>();
    SimpleListAdapter adapter1;
    String mAddressOutput;
    private View rootView;
    private Activity mActivity;
    private boolean destroyed = false;
    private ListView mSubzoneSearchListView;
    private int citySelected = 0;
    private boolean cityFound = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.photo_filter_form_fourth, container,false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        width = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        mSubzoneSearchListView = (ListView) rootView.findViewById(R.id.subzone_search_list_view);

        UploadManager.getInstance().addCallback(this);
        GetRequestManager.getInstance().addCallbacks(this);
        fixSizes();
        setListeners();
        cities = new ArrayList<>();

        if (AllCities.getInsance().getCities() != null)
            cities.addAll(AllCities.getInsance().getCities());
        else {
            String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_CITY_LIST;
            GetRequestManager.getInstance().makeAyncRequest(url, CommonLib.GET_CITY_LIST + "",
                    ObjectTypes.OBJECT_TYPE_CITY_LIST);
        }
        if (AppPreferences.isUserLogIn(getActivity())) {
            ((EditText) rootView.findViewById(R.id.name_et)).setText(AppPreferences.getUserName(getActivity()));
        }
        mAddressOutput = AppPreferences.getSavedCity(mActivity);
        displayAddressOutput();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_et:
                final Dialog dialog1 = new Dialog(getActivity());
                View view1 = getActivity().getLayoutInflater().inflate(
                        R.layout.z_select_state_dialog_layout, null);
                //dialog1.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.black_trans_seventy));
                ListView list1 = (ListView) view1.findViewById(R.id.hj_common_list);

                ImageView cancelDialog = (ImageView) view1.findViewById(R.id.cancel_dialog);
                cancelDialog.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });
                /*cancelDialog.setImageBitmap(CommonLib.getBitmap(getActivity(),
                        R.drawable.ic_cross, getResources().getDimensionPixelSize(R.dimen.header_item_size),
                        getResources().getDimensionPixelSize(R.dimen.header_item_size)));*/
                CitiesAdapter adapter = new CitiesAdapter(getActivity(), cities);
                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.simple_list_item, R.id.text1);
                for (int i = 0; i < cities.size(); i++) {
                    arrayAdapter.add(cities.get(i).getName());
                }
                list1.setAdapter(arrayAdapter);
                list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        citySelected = Integer.parseInt(cities.get(position).getId());
                        ((TextView) rootView.findViewById(R.id.location_et)).setText(cities.get(position).getName());
                        dialog1.dismiss();
                    }
                });
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.setContentView(view1);
                dialog1.getWindow().setLayout(
                        getDisplayMetrics().widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.margin_large),
                        ((4 * getDisplayMetrics().heightPixels) / 5));
                dialog1.show();

                break;
        }
    }

    public boolean checkValidation() {
        if (((EditText) rootView.findViewById(R.id.name_et)).getText().toString().length() > 0) {
            if (((EditText) rootView.findViewById(R.id.phone_et)).getText().toString().length() > 0) {
                if (((EditText) rootView.findViewById(R.id.phone_et)).getText().toString().length() == 10) {
                    if (((TextView) rootView.findViewById(R.id.location_et)).getText().toString().length() > 0) {
                        return true;
                    } else {
                        showToast("Please select your city");

                        return false;
                    }
                } else {
                    showToast("Please enter 10-digit phone number");

                    return false;
                }
            } else {
                showToast("Please enter phone number");

                return false;
            }
        } else {
            showToast("Please enter name");

            return false;
        }
    }

    private void setListeners() {

        rootView.findViewById(R.id.location_et).setOnClickListener(this);
        rootView.findViewById(R.id.next_bt).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CommonLib.isNetworkAvailable(getActivity())) {
                    if (checkValidation()) {
                        zProgressDialog = ProgressDialog.show(mActivity, null,
                                getResources().getString(R.string.sending_details), true, false);
                        ZFilter filterObj = ((FilterActivity) mActivity).getFilter();
                        filterObj.setName(((EditText) rootView.findViewById(R.id.name_et)).getText().toString());
                        filterObj.setPhone(((EditText) rootView.findViewById(R.id.phone_et)).getText().toString());
                        filterObj.setLocation(((TextView) rootView.findViewById(R.id.location_et)).getText().toString());

                        // form the request and send it to the server
                        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.SAVE_FILTER;
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("name", filterObj.getName()));
                        nameValuePairs.add(new BasicNameValuePair("phone", filterObj.getPhone()));
                        if (AppPreferences.isUserLogIn(getActivity()))
                            nameValuePairs.add(new BasicNameValuePair("userid", "" + AppPreferences.getUserID(getActivity())));
                        nameValuePairs.add(new BasicNameValuePair("location", "" + citySelected));
                        nameValuePairs.add(new BasicNameValuePair("budget", "" + filterObj.getBudget()));
                        nameValuePairs.add(new BasicNameValuePair("category", filterObj.getCategory()));
                        nameValuePairs.add(new BasicNameValuePair("query", filterObj.getQuery()));
                        nameValuePairs.add(new BasicNameValuePair("photo_slug", filterObj.getPhoto_slug()));
                        nameValuePairs.add(new BasicNameValuePair("pro_slug", filterObj.getPro_slug()));
                        nameValuePairs.add(new BasicNameValuePair("area", filterObj.getArea()));
                        nameValuePairs.add(new BasicNameValuePair("area_unit", "" + filterObj.getArea_unit()));
                        nameValuePairs.add(new BasicNameValuePair("received_from","mobile"));


                        if (filterObj.getPhoto_slug() == null && filterObj.getPro_slug() == null) {
                            nameValuePairs.add(new BasicNameValuePair("type", "" + 0));
                        } else {
                            nameValuePairs.add(new BasicNameValuePair("type", "" + filterObj.getType()));// 1
                        }
                        UploadManager.getInstance().makeAyncRequest(url, CommonLib.SAVE_FILTER, "",
                                ObjectTypes.OBJECT_TYPE_RESPONSE_FILTER, null, nameValuePairs, null);
                    }

                }else{
                    showToast("No network connection");
                }
            }
        });
    }

    private void fixSizes() {

        //	rootView.findViewById(R.id.projects_label).setPadding(width / 20, width / 20, width / 20, width / 20);

        rootView.findViewById(R.id.name_et).setPadding(width / 20, width / 80, width / 20, width / 80);

        rootView.findViewById(R.id.phone_et).setPadding(width / 20, width / 80, width / 20, width / 80);

        rootView.findViewById(R.id.location_et).setPadding(width / 20, width / 80, width / 20, width / 80);

        rootView.findViewById(R.id.name_et).setPadding(width / 20, width / 80, width / 20, width / 80);

    }

    @Override
    public void onDestroy() {
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);
        if (zProgressDialog != null)
            zProgressDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object respose, boolean status, int parserId) {
        if (requestType == CommonLib.SAVE_FILTER) {
            if (!destroyed) {
                if (zProgressDialog != null)
                    zProgressDialog.dismiss();
                if (status) {
                    ((FilterActivity) mActivity).setFourthFragment((String) respose);
                } else {
                    showToast(((ErrorObject) respose).getErrorMessage());

                }
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object object) {
    }

    @Override
    public void onRequestStarted(String requestTag) {
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag.equals(CommonLib.GET_CITY_LIST + "")) {
            if (!destroyed && obj != null && obj instanceof ArrayList<?>) {
                cities = (ArrayList<CategoryObject>) obj;
                adapter1 = new SimpleListAdapter(mActivity, R.layout.simple_list_item, cities);
                mSubzoneSearchListView.setAdapter(adapter1);
                rootView.findViewById(R.id.progress_container).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag.equals(CommonLib.GET_CITY_LIST + "")) {
            if (!destroyed) {
                rootView.findViewById(R.id.progress_container).setVisibility(View.GONE);
            }
        }
    }

    private void displayAddressOutput() {
        CommonLib.ZLog("FilterFragmentThird", mAddressOutput);
        if (cities != null) {
            for (int i = 0; i < cities.size(); i++) {
                CommonLib.ZLog("FilterFragmentThird", i);
                if (cities.get(i).getName().contains(mAddressOutput)) {
                    CommonLib.ZLog("FilterFragmentThird", "cityfound");
                    citySelected = Integer.parseInt(cities.get(i).getId());
                    ((TextView) rootView.findViewById(R.id.location_et)).setText(cities.get(i).getName());
                    cityFound = true;
                    return;
                }
            }
        }
    }

    private class SimpleListAdapter extends ArrayAdapter<CategoryObject> {

        private ArrayList<CategoryObject> wishes;
        private ArrayList<CategoryObject> filtered;
        private Activity mContext;
        private int width;
        private Filter filter;

        public SimpleListAdapter(Activity context, int resourceId, ArrayList<CategoryObject> wishes) {
            super(context.getApplicationContext(), resourceId, wishes);
            mContext = context;
            this.wishes = wishes;
            this.filtered = (ArrayList<CategoryObject>) this.wishes.clone();
            width = mContext.getWindowManager().getDefaultDisplay().getWidth();
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new CityFilter();
            }
            return filter;
        }

        @Override
        public int getCount() {
            if (wishes == null) {
                return 0;
            } else {
                return wishes.size();
            }
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final CategoryObject wish = wishes.get(position);
            if (v == null || v.findViewById(R.id.list_root) == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, null);
            }

            ViewHolder viewHolder = (ViewHolder) v.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) v.findViewById(R.id.text1);
                v.setTag(viewHolder);
            }

            viewHolder.text.setText(wish.getName());
            viewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    citySelected = Integer.parseInt(wish.getId());
                    ((TextView) rootView.findViewById(R.id.location_et)).setText(((TextView) v).getText().toString());
                    rootView.findViewById(R.id.subzone_search_list_view_container).setVisibility(View.GONE);
                    cityFound = true;
                }
            });
            return v;
        }

        protected class ViewHolder {
            TextView text;
        }

        private class CityFilter extends Filter {
            @Override
            protected android.widget.Filter.FilterResults performFiltering(CharSequence constraint) {

                constraint = constraint.toString().toLowerCase(Locale.getDefault());

                FilterResults result = new FilterResults();

                if (constraint != null && constraint.toString().length() > 0) {
                    ArrayList<CategoryObject> filt = new ArrayList<CategoryObject>();
                    ArrayList<CategoryObject> first = new ArrayList<CategoryObject>();
                    ArrayList<CategoryObject> lItems = new ArrayList<CategoryObject>();
                    synchronized (this) {
                        lItems.addAll(wishes);
                    }

                    CategoryObject r;
                    String name;
                    int relevent_count = 0;

                    if (constraint.toString().contains(" ")) {

                        for (int i = 0, l = lItems.size(); i < l; i++) {
                            r = lItems.get(i);
                            name = r.getName();

                            if (name.toLowerCase(Locale.getDefault()).startsWith((String) constraint)) {
                                first.add(r);
                            }
                        }

                    } else {
                        for (int i = 0, l = lItems.size(); i < l; i++) {
                            r = lItems.get(i);
                            name = r.getName();

                            String[] nameTag = name.split(" ");
                            if (nameTag != null && nameTag.length > 0) {

                                for (int j = 0; j < nameTag.length; j++) {
                                    if (nameTag[j].toLowerCase(Locale.getDefault()).startsWith((String) constraint)) {
                                        if (j == 0)
                                            first.add(relevent_count++, r);
                                        else
                                            first.add(r);
                                        break;
                                    }
                                }
                            } else {
                                if (name.toLowerCase(Locale.getDefault()).startsWith((String) constraint)) {
                                    first.add(relevent_count++, r);
                                }
                            }
                        }
                    }
                    filt.addAll(first);


                    result.count = filt.size();
                    result.values = filt;


                } else {
                    synchronized (this) {
                        result.values = wishes;
                        result.count = wishes.size();
                    }
                }
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, android.widget.Filter.FilterResults results) {
                wishes = (ArrayList<CategoryObject>) results.values;
                if (constraint.toString().length() == 0 && wishes.size() == 0) {
                    wishes = (filtered);
                }
                if (results.count > 0) {
                    adapter1.notifyDataSetChanged();
                } else {
                    adapter1.notifyDataSetChanged();
                }
            }

        }

    }


}