package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.baseobjects.CartObject;

/**
 * Created by Umesh Lohani on 12/8/2015.
 */
public class NewAddressSelectionFragment extends BaseFragment{


    RecyclerView recyclerView;
    boolean billing;
    CartObject cartObject;
    AddressObject billingAddress, shippingAddress;

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
        View view = inflater.inflate(R.layout.recyclerview_toolbar_layout,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.categories_list);

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


}
