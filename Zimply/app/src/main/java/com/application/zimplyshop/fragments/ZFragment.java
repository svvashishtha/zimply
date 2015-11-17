package com.application.zimplyshop.fragments;

import android.os.Bundle;

public abstract class ZFragment extends BaseFragment {
    //return true - if consumed
    //false - if activity should handle (popfragment)
    public abstract boolean onBackPressed();
    public abstract boolean onFragmentResult(Bundle bundle);

}