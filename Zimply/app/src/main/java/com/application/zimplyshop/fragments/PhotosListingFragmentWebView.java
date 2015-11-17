package com.application.zimplyshop.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimplyshop.activities.AllFilterClassActivity;
import com.application.zimplyshop.activities.BaseLoginSignupActivity;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.objects.AllCategories;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.utils.CommonLib;

public class PhotosListingFragmentWebView extends BaseFragment implements View.OnClickListener, AppConstants {

    static PhotosListingFragmentWebView fragment;
    boolean isLoading, isDataLoadComplete;
    WebView contentContainer;
    boolean isRefreshData, isFilterApplied;
    int categoryId = 0, sizeId = -1, budgetId = -1, styleId = -1;
    private String mUrl;


    public static PhotosListingFragmentWebView newInstance(Bundle bundle) {
        if (fragment == null) {
            fragment = new PhotosListingFragmentWebView();
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.photo_list_layout, null);
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        setFilterVariables();
        setFiltersClick();
        setCategoryFilterText("All");
        setBasicFilterText((sizeId == -1 && styleId == -1 && budgetId == -1) ? "None" : getFilterQuatntityApplied() + " Applied");
        return view;
    }


    private void setBasicFilterText(String text) {
        ((TextView) view.findViewById(R.id.filter_filter_text)).setText(text);
        if (sizeId == -1 && budgetId == -1 && styleId == -1) {
            ((TextView) view.findViewById(R.id.filter_filter_text))
                    .setTextColor(getResources().getColor(R.color.text_color1));
        } else {
            ((TextView) view.findViewById(R.id.filter_filter_text))
                    .setTextColor(getResources().getColor(R.color.green_text_color));
        }
    }

    private void setFiltersClick() {
        filterLayout.setOnClickListener(this);
        categoriesLayout.setOnClickListener(this);
        sortByLayout.setVisibility(View.GONE);
        selectfiltersLayout.setOnClickListener(this);
        view.findViewById(R.id.separator2).setVisibility(View.GONE);
    }

    private void setCategoryFilterText(String text) {
        ((TextView) view.findViewById(R.id.category_name)).setText(text);
        if (categoryId == -1) {
            ((TextView) view.findViewById(R.id.category_name))
                    .setTextColor(getResources().getColor(R.color.text_color1));
        } else {
            ((TextView) view.findViewById(R.id.category_name))
                    .setTextColor(getResources().getColor(R.color.green_text_color));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUrl = AppApplication.getInstance().getWebUrl() + "photos/";

        if (mUrl != null)

            mUrl = mUrl + "?src=mob" + (AppPreferences.isUserLogIn(getActivity()) ? "&userid=" + AppPreferences.getUserID(getActivity()) : "");

        contentContainer = (WebView) view.findViewById(R.id.webView);
        contentContainer.getLayoutParams().height = getDisplayMetrics().heightPixels - (2 * getResources().getDimensionPixelSize(R.dimen.z_item_height_48) + getStatusBarHeight());
        contentContainer.setWebChromeClient(new MyWebChromeClient());
        WebSettings webSettings = contentContainer.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        contentContainer.setWebViewClient(new MyWebViewClient());
        loadUrlPage();
    }

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_layout:
                loadUrlPage();
                break;
            case R.id.cat_filter_layout:
                break;
            case R.id.filter_filter_layout:

                break;
        }
    }

    private void loadUrlPage() {
        if (CommonLib.isNetworkAvailable(getActivity())) {
            contentContainer.loadUrl(mUrl);

        } else {
            showNetworkErrorView();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PHOTO_FILTER_ACTIVITY) {
                int id = data.getIntExtra("id", 1) + 1;
                int type = data.getIntExtra("type", 1);
                if (type == TYPE_PHOTO_FILTER_BUDGET) {
                    budgetId = id;
                } else if (type == TYPE_PHOTO_FILTER_SIZE) {
                    sizeId = id;
                } else if (type == TYPE_PHOTO_FILTER_STYLE) {
                    styleId = id;
                } else {
                    budgetId = -1;
                    sizeId = -1;
                    styleId = -1;
                }

                mUrl = AppApplication.getInstance().getWebUrl() + "/photos/"
                        + ((categoryId != -1) ? ("?filter=1&cat=" + categoryId)
                        : ((sizeId != -1 || budgetId != -1 || styleId != -1) ? "?photo_width=360&filter=1"
                        : "?photo_width=360&filter=0"))
                        + ((sizeId != -1) ? ("&size=" + sizeId) : "")
                        + ((budgetId != -1) ? ("&budget=" + budgetId) : "")
                        + ((styleId != -1) ? ("&style=" + styleId) : "")
                        + (AppPreferences.isUserLogIn(getActivity())
                        ? "&userid=" + AppPreferences.getUserID(getActivity()) : "") + "&src=mob";
                if (styleId == -1 && budgetId == -1 && sizeId == -1) {
                    setBasicFilterText("None");
                } else {

                    setBasicFilterText(getFilterQuatntityApplied() + " Applied");
                }
                isRefreshData = true;
                loadUrlPage();
            }
        }
    }

    public int getFilterQuatntityApplied() {
        int i = 0;
        if (styleId != -1) {
            i++;
        }
        if (budgetId != -1) {
            i++;
        }
        if (sizeId != -1) {
            i++;
        }
        return i;
    }

    public void showFilterLayout() {
        if (AllCategories.getInstance().getPhotoCateogryObjs() == null) {
            if (!CommonLib.isNetworkAvailable(getActivity()))
                showToast("Failed to load.Please check your internet connection.");
            else
                showToast("Something went wrong. Please try again.");
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_photos", true);
            bundle.putInt("selected_pos", categoryId);
            bundle.putInt("budget_id", budgetId);
            bundle.putInt("size_id", sizeId);
            bundle.putInt("style_id", styleId);
            AllFilterClassActivity dialog = AllFilterClassActivity.newInstance(bundle);
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJCustomDialogTheme);
            dialog.setOnApplyClickListener(new AllFilterClassActivity.OnApplyClickListener() {
                @Override
                public void onApplyClick(Bundle bundle) {
                    categoryId = bundle.getInt("selected_pos");
                    styleId = bundle.getInt("style_id");
                    sizeId = bundle.getInt("size_id");
                    budgetId = bundle.getInt("budget_id");
                    mUrl = AppApplication.getInstance().getWebUrl() + "/photos/" + "?photo_width=360&filter=1" + ((categoryId != 0) ? ("&cat=" + Integer.parseInt(AllCategories.getInstance().getPhotoCateogryObjs()
                            .getPhoto_category().get(categoryId - 1).getId())) : "")
                            + ((sizeId != -1) ? ("&size=" + sizeId) : "")
                            + ((budgetId != -1) ? ("&budget=" + budgetId) : "")
                            + ((styleId != -1) ? ("&style=" + styleId) : "")
                            + (AppPreferences.isUserLogIn(getActivity())
                            ? "&userid=" + AppPreferences.getUserID(getActivity()) : "") + "&src=mob";
                    setCategoryFilterText(categoryId == 0 ? "All" : AllCategories.getInstance().getPhotoCateogryObjs().getPhoto_category()
                            .get(categoryId - 1).getName());
                    loadUrlPage();
                    if (categoryId != 0) {
                        isFilterApplied = true;
                        getActivity().findViewById(R.id.filter_applied).setVisibility(View.VISIBLE);
                    } else {
                        isFilterApplied = false;
                        getActivity().findViewById(R.id.filter_applied).setVisibility(View.INVISIBLE);
                    }
                }
            });

            dialog.show(getFragmentManager(), ARTICLE_PHOTO_CATEGROY_DIALOG_TAG);

        }

    }

    public boolean isFilterApplied() {
        return isFilterApplied;
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null && url.contains("blahblah")) {
                Intent intent = new Intent(getActivity(), BaseLoginSignupActivity.class);
                getActivity().startActivity(intent);
                return true;
            } else {
                return false;
            }

        }
    }

    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress > 99) {
                isLoading = false;
                isDataLoadComplete = true;
                showView();
                changeViewVisiblity(view, View.VISIBLE);
            } else {
                if (!isLoading) {
                    isLoading = true;
                    showLoadingView();
                    changeViewVisiblity(view, View.GONE);
                }
            }
        }

    }
}
