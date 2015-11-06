package com.application.zimply.fragments;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimply.R;
import com.application.zimply.utils.UiUtils;

public class BaseFragment extends Fragment {

    public View view;
    Toast toast;
    ProgressBar progress;
    TextView nullcaseText;
    TextView quoteText;
    boolean isRequestFailed;
    LinearLayout retryLayout;
    LinearLayout filterLayout;
    LinearLayout categoriesLayout, selectfiltersLayout, sortByLayout;
    int filterLayoutHeight;

    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        return metrics;
    }
    public void showToast(String message){
        if(toast == null){
            toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        }
        toast.setText(message);
        toast.show();
    }

    /**
     * Show Loading for all activities
     */
    public void setLoadingVariables() {
        progress = (ProgressBar) view.findViewById(R.id.progress);
        nullcaseText = (TextView) view.findViewById(R.id.nullcase_text);
        retryLayout = (LinearLayout) view.findViewById(R.id.retry_layout);
        quoteText = (TextView) view.findViewById(R.id.quote);
    }

    public void setFilterVariables() {
        filterLayout = (LinearLayout) view.findViewById(R.id.filter_layout);
        categoriesLayout = (LinearLayout) view.findViewById(R.id.cat_filter_layout);
        selectfiltersLayout = (LinearLayout) view.findViewById(R.id.filter_filter_layout);
        sortByLayout = (LinearLayout) view.findViewById(R.id.sort_filter_layout);
        final ViewTreeObserver vt = filterLayout.getViewTreeObserver();
        vt.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                filterLayoutHeight = filterLayout.getHeight();
                /*if(Build.VERSION.SDK_INT >= 16) {
                    vt.removeOnGlobalLayoutListener(this);
                }else {
                    vt.removeGlobalOnLayoutListener(this);
                }*/
            }
        });
    }

    /**
     * Method shows the listview as soon as the data is successfully received
     * from the server and loaded into the adapter
     */
    public void showView() {
        progress.setVisibility(View.GONE);
        nullcaseText.setVisibility(View.GONE);
        retryLayout.findViewById(R.id.retry_image).setVisibility(View.GONE);

        retryLayout.setVisibility(View.GONE);
        quoteText.setVisibility(View.GONE);

    }

    /**
     * Method shows loading view when a server request is generated
     */
    public void showLoadingView() {
        progress.setVisibility(View.VISIBLE);
        nullcaseText.setVisibility(View.GONE);
        retryLayout.findViewById(R.id.retry_image).setVisibility(View.GONE);
        retryLayout.setVisibility(View.GONE);
        quoteText.setVisibility(View.VISIBLE);
        quoteText.setText(UiUtils.getTextFromRes(getActivity()));
    }

    /**
     * Method shows the Error view when the server request could not be
     * completed
     */
    public void showNetworkErrorView() {
        isRequestFailed = true;
        nullcaseText.setVisibility(View.VISIBLE);
        nullcaseText.setText("");
        //nullcaseText.setBackgroundResource(R.drawable.green_btn_rectangle_bg);
        nullcaseText.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_small), getResources().getDimensionPixelSize(R.dimen.margin_small), getResources().getDimensionPixelSize(R.dimen.margin_small), getResources().getDimensionPixelSize(R.dimen.margin_small));
        nullcaseText.setTypeface(null, Typeface.NORMAL);
        nullcaseText.setTextColor(getResources().getColor(R.color.heading_text_color));
        // changeLeftDrawable(R.drawable.ic_navigation_refresh);
        quoteText.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        retryLayout.setVisibility(View.VISIBLE);
        int width = getDisplayMetrics().widthPixels;
        int height = getDisplayMetrics().heightPixels;

        retryLayout.findViewById(R.id.retry_image).setVisibility(View.VISIBLE);
        retryLayout.findViewById(R.id.nullcase_text).setVisibility(View.GONE);
        //retryLayout.setBackgroundResource(R.drawable.ic_navigation_refresh);
        //retryLayout.setBackgroundDrawable(new BitmapDrawable(CommonLib.getBitmap(getActivity(), R.drawable.ic_navigation_refresh, width, height)));
//        retryLayout.setBackgroundResource(R.drawable.ic_navigation_refresh);

        //retryLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    public void showNullCaseView(String text) {
        nullcaseText.setVisibility(View.VISIBLE);
        nullcaseText.setText(text);
        nullcaseText.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        nullcaseText.setTextColor(getResources().getColor(R.color.heading_text_color));
        changeLeftDrawable(0);
        nullcaseText.setTypeface(null, Typeface.BOLD);
        quoteText.setVisibility(View.GONE);
        //   nullcaseText.setTextColor(Color.WHITE);
        progress.setVisibility(View.GONE);
        retryLayout.setVisibility(View.VISIBLE);
        retryLayout.findViewById(R.id.retry_image).setVisibility(View.GONE);
        retryLayout.findViewById(R.id.nullcase_text).setVisibility(View.VISIBLE);
        retryLayout.setBackgroundResource(R.drawable.ic_null_case);
    }

    /**
     * Method to change the Left drawbale of the nullcase text view
     *
     * @param drawable
     */
    public void changeLeftDrawable(int drawable) {
        if (drawable != 0) {
            Drawable drawableTop = getResources().getDrawable(drawable);
            if(drawableTop !=null )
                drawableTop.setBounds(0, 0, drawableTop.getIntrinsicWidth(), drawableTop.getIntrinsicHeight());

            nullcaseText.setCompoundDrawables(null, drawableTop, null, null);
        } else {
            nullcaseText.setCompoundDrawables(null, null, null, null);
        }
    }
    public void changeViewVisiblity(View view, int visiblity) {
        view.setVisibility(visiblity);
    }

    protected void scrollToolbarAndHeaderBy(int dy) {
        // dy +ve means scrolling upward and -ve mean scrolling downward
        if (dy > 0) {

            float reqTransSearch = filterLayout.getTranslationY() + dy;
            if (reqTransSearch < -filterLayoutHeight) {
                reqTransSearch = -filterLayoutHeight;
            } else if (reqTransSearch > 0)
                reqTransSearch = 0;
            filterLayout.setTranslationY(reqTransSearch);

        } else {
            float reqTrans = filterLayout.getTranslationY() + dy;
            if (reqTrans < -filterLayoutHeight) {
                reqTrans = -filterLayoutHeight;
            } else if (reqTrans > 0)
                reqTrans = 0;
            filterLayout.setTranslationY(reqTrans);

        }
    }
}
