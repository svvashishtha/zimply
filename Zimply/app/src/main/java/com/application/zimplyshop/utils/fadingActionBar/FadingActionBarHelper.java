package com.application.zimplyshop.utils.fadingActionBar;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.application.zimply.R;

public class FadingActionBarHelper extends FadingActionBarHelperBase {

    private ActionBar mActionBar;

    @SuppressLint("NewApi")
    @Override
    public void initActionBar(ActionBarActivity activity) {
        mActionBar = activity.getSupportActionBar();
        super.initActionBar(activity);
    }

    @SuppressLint("NewApi")
    @Override
    protected int getActionBarHeight() {
        return mActionBar.getHeight();
    }

    @Override
    protected boolean isActionBarNull() {
        return mActionBar == null;
    }

    @SuppressLint("NewApi")
    @Override
    protected void setActionBarBackgroundDrawable(Drawable drawable) {
        mActionBar.setBackgroundDrawable(drawable);
    }

    @Override
    public void setActionBarTitleContainerAlpha(float alpha) {
        View customView = mActionBar.getCustomView();
        // try catch incase the view ids are changed in future
        try {
            if (alpha < 0.3) {
                customView.findViewById(R.id.share_product).setClickable(true);
            }
            else {
                customView.findViewById(R.id.share_product).setClickable(true);
            }

            customView.findViewById(R.id.title).setAlpha(alpha);
            customView.getBackground().setAlpha((int)(255 * alpha));
        } catch (Exception e){

        }
    }
}
