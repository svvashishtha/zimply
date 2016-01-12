package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.ProductCheckoutActivity;
import com.application.zimplyshop.adapters.NewAdressSelectionAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllUsers;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.ZTracker;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 12/8/2015.
 */
public class NewAddressSelectionFragment extends ZFragment implements GetRequestListener{


    RecyclerView recyclerView;
    boolean billing;
    CartObject cartObject;
    AddressObject billingAddress, shippingAddress;

    boolean isDestroyed;



    public static NewAddressSelectionFragment newInstance(Bundle bundle){
        NewAddressSelectionFragment fragment = new NewAddressSelectionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.products_list_layout,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.category_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_medium)));
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
        setLoadingVariables();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GetRequestManager.getInstance().addCallbacks(this);
        // loadData();
        setAdapterData();
        ZTracker.logGAScreen(getActivity(), "Address Selection");
    }

    public void loadData(){
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_ADDRESSES + "?userid=" + AppPreferences.getUserID(getActivity());
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.GET_ADDRESS_REQUEST_TAG, ObjectTypes.OBJECT_TYPE_GET_ADDRESSES);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ProductCheckoutActivity)getActivity()).setTitleText("Choose Address");
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if(requestTag != null && requestTag.equals(RequestTags.GET_ADDRESS_REQUEST_TAG) && !isDestroyed){
            showLoadingView();
            changeViewVisiblity(recyclerView, View.GONE);
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag != null && requestTag.equals(RequestTags.GET_ADDRESS_REQUEST_TAG) && !isDestroyed) {
            ArrayList<AddressObject> addressObjectArrayList = (ArrayList<AddressObject>) obj;
            if (addressObjectArrayList.size() > 0) {
                showView();
                //   setAdapterData((ArrayList<AddressObject>) obj);

//                changeViewVisiblity(view.findViewById(R.id.listview_container), View.VISIBLE);
                changeViewVisiblity(recyclerView, View.VISIBLE);
            } else {
                //       addressSelected = true;
                nextFragmentAddressEdit(null);
            }
        }
    }
    private void nextFragmentAddressEdit(Bundle bundle) {
        if (getActivity() != null) {
            ((ProductCheckoutActivity) getActivity()).setEditAddressFragmentWithBackstack(bundle);
        }
    }
    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if(  requestTag != null && requestTag.equals(RequestTags.GET_ADDRESS_REQUEST_TAG) && !isDestroyed){
            showNetworkErrorView();
            changeViewVisiblity(recyclerView, View.GONE);
        }
    }

    public void setAdapterData(){
        if(recyclerView.getAdapter()==null){
            //the second argument is for the purpose of reusing adapter class @NewAdressSelectionAdapter.
            // Please read the comments in @NewAdressSelectionAdapter
            final NewAdressSelectionAdapter adapter = new NewAdressSelectionAdapter(getActivity(),1);
            recyclerView.setAdapter(adapter);
            adapter.setOnUseAdressClickListener(new NewAdressSelectionAdapter.OnUseAdressClickListener() {
                @Override
                public void onAddressSelected(int pos) {
                    AllUsers.getInstance().swapAddress(pos);
                    ((ProductCheckoutActivity) getActivity()).popFromBackStack(ProductCheckoutActivity.ORDER_SUMMARY_FRAGMENT);
                }

                @Override
                public void addNewAddress() {
                    Bundle bundle = getArguments();
                    bundle.remove("addressObject");
                    bundle.remove("is_edit_address");
                    ((ProductCheckoutActivity) getActivity()).newEditAddressFragmentWithBackstack(getArguments());
                }

                @Override
                public void editExistingAddress(int pos) {
                    Bundle bundle = getArguments();
                    bundle.putSerializable("addressObject", AllUsers.getInstance().getObjs().get(pos));
                    bundle.putBoolean("is_edit_address", true);
                    bundle.putInt("edit_position", pos);
                    ((ProductCheckoutActivity) getActivity()).newEditAddressFragmentWithBackstack(getArguments());
                }
            });
        }

        ((NewAdressSelectionAdapter)recyclerView.getAdapter()).addData(AllUsers.getInstance().getObjs());
    }

    /**
     * Refresh list
     */
    public void updateAddress(){
        if(recyclerView.getAdapter()!=null){
            ((NewAdressSelectionAdapter)recyclerView.getAdapter()).addData(AllUsers.getInstance().getObjs());
        }
    }

    @Override
    public void onDestroy() {
        isDestroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        ((ProductCheckoutActivity)getActivity()).setTitleText("Order Summary");
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onFragmentResult(Bundle bundle) {
        return false;
    }


}
