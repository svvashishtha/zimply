package com.application.zimplyshop.fragments;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.ProductCheckoutActivity;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;

import java.util.ArrayList;
import java.util.List;

public class AddressSelectionFragment extends BaseFragment implements GetRequestListener, View.OnClickListener {

    private static AddressSelectionFragment fragment;
    boolean addressSelected = false;
    boolean billing;
    CartObject cartObject;
    AddressObject billingAddress, shippingAddress;
    private ListView mListView;
    private Activity mActivity;
    private boolean destroyed;

    public static AddressSelectionFragment newInstance(Bundle bundle) {
        AddressSelectionFragment fragment = new AddressSelectionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CommonLib.ZLog("onCreateView", toString());
        view = inflater.inflate(R.layout.address_selection_fragment, container, false);;
        return view;
    }

    @Override
    public void onDestroyView() {
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        if (getArguments() != null) {
            billing = getArguments().getBoolean("isBilling");
            cartObject = (CartObject) getArguments().getSerializable("cartObject");
            billingAddress = (AddressObject) getArguments().getSerializable("billingAddress");
            shippingAddress = (AddressObject) getArguments().getSerializable("shippingAddress");
        } else if (savedInstanceState != null) {
            billing = savedInstanceState.getBoolean("isBilling");
            cartObject = (CartObject) savedInstanceState.getSerializable("cartObject");
            billingAddress = (AddressObject) savedInstanceState.getSerializable("billingAddress");
            shippingAddress = (AddressObject) savedInstanceState.getSerializable("shippingAddress");
        }
        destroyed = false;

        // mListView = (ListView) view.findViewById(R.id.address_listview);
        mListView.setDivider(null);
        view.findViewById(R.id.add_new_address).setOnClickListener(this);
        GetRequestManager.getInstance().addCallbacks(this);
        setLoadingVariables();
        refreshView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private void refreshView() {
        //Get the list of addresses
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_ADDRESSES + "?userid=" + AppPreferences.getUserID(mActivity);
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.GET_ADDRESS_REQUEST_TAG, ObjectTypes.OBJECT_TYPE_GET_ADDRESSES);
    }


    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag != null && requestTag.equals(RequestTags.GET_ADDRESS_REQUEST_TAG) && !destroyed) {
            //view.findViewById(R.id.progress_container).setVisibility(View.VISIBLE);
            //  changeViewVisiblity(view.findViewById(R.id.add_new_address_view), View.GONE);
            //   changeViewVisiblity(view.findViewById(R.id.listview_container), View.GONE);
            showLoadingView();
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag != null && requestTag.equals(RequestTags.GET_ADDRESS_REQUEST_TAG) && !destroyed) {
            ArrayList<AddressObject> addressObjectArrayList = (ArrayList<AddressObject>) obj;
            if (addressObjectArrayList.size() > 0) {
                AddressAdapter mAdapter = new AddressAdapter(mActivity, R.layout.address_snippet, addressObjectArrayList);
                mListView.setAdapter(mAdapter);
                //  view.findViewById(R.id.progress_container).setVisibility(View.GONE);
                showView();
//                changeViewVisiblity(view.findViewById(R.id.listview_container), View.VISIBLE);
                //  changeViewVisiblity(view.findViewById(R.id.add_new_address_view), View.VISIBLE);
            } else {
                addressSelected = true;
                nextFragmentAddressEdit(null);
            }
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag != null && requestTag.equals(RequestTags.GET_ADDRESS_REQUEST_TAG) && !destroyed) {
            Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_new_address:
                nextFragmentAddressEdit(null);
        }
    }

    private void nextFragmentAddressEdit(Bundle bundle) {
        if (mActivity != null) {
            ((ProductCheckoutActivity) mActivity).setEditAddressFragmentWithBackstack(bundle);
        }
    }

    @Override
    public void onResume() {
        if (getActivity() != null) {
            ((ProductCheckoutActivity) getActivity()).setTitleText("Address Selection");
        }

        super.onResume();
    }

    public class AddressAdapter extends ArrayAdapter<AddressObject> {

        private List<AddressObject> messageItems;
        private Activity mContext;
        private int width;

        public AddressAdapter(Activity context, int resourceId, List<AddressObject> feedItems) {
            super(context.getApplicationContext(), resourceId, feedItems);
            mContext = context;
            this.messageItems = feedItems;
            width = mContext.getWindowManager().getDefaultDisplay().getWidth();
        }

        @Override
        public int getCount() {
            if (messageItems == null) {
                return 0;
            } else {
                return messageItems.size();
            }
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final AddressObject feedItem = messageItems.get(position);

            if (v == null || v.findViewById(R.id.address_snippet_root) == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.address_snippet, null);
                v.findViewById(R.id.address_snippet_root).setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.white)));
            }

            ViewHolder viewHolder = (ViewHolder) v.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.address = (TextView) v.findViewById(R.id.address);
                viewHolder.phone = (TextView) v.findViewById(R.id.phone);
                viewHolder.edit = (ImageView) v.findViewById(R.id.edit);
                viewHolder.useThisAddress = (TextView) v.findViewById(R.id.use_this_address);
                v.setTag(viewHolder);
            }
            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("addressObject", feedItem);
                    nextFragmentAddressEdit(bundle);
                }
            });
            viewHolder.useThisAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    if (billing) {
                        bundle.putSerializable("addressBilling", feedItem);
                        bundle.putSerializable("addressShipping", shippingAddress);
                    } else {
                        bundle.putSerializable("addressBilling", billingAddress);
                        bundle.putSerializable("addressShipping", feedItem);
                    }

                    bundle.putBoolean("isBillig", billing);
                    bundle.putBoolean("from_selection", true);
                    if (cartObject != null)
                        bundle.putSerializable("cartObject", cartObject);
                    //onFragmentResult(bundle);
                    ((ProductCheckoutActivity) mActivity).setOrderSummaryFragment(bundle);
                }
            });
            String addressString = feedItem.getName() + ", " + feedItem.getLine1() +
                    (feedItem.getLine2().trim().length()>0?", "+feedItem.getLine2():"") +  ", " + feedItem.getCity() + ", " + feedItem.getPincode();
            viewHolder.address.setText(addressString);
            viewHolder.phone.setText(feedItem.getPhone());
            return v;
        }

        protected class ViewHolder {
            TextView address, phone, useThisAddress;
            ImageView edit, delete;
        }
    }
}

