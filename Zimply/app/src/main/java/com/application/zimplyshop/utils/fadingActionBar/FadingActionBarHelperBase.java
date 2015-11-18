package com.application.zimplyshop.utils.fadingActionBar;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.ProductDetailsActivity;
import com.application.zimplyshop.utils.CommonLib;

@SuppressWarnings("unchecked")
public abstract class FadingActionBarHelperBase {
    protected static final String TAG = "FadingActionBarHelper";
    private Drawable mActionBarBackgroundDrawable;
    private FrameLayout mHeaderContainer;
    private int mActionBarBackgroundResId;
    private int mHeaderLayoutResId;
    private View mHeaderView;
    private int mHeaderOverlayLayoutResId;
    private View mHeaderOverlayView;
    private int mContentLayoutResId;
    private View mContentView;
    private LayoutInflater mInflater;
    private boolean mLightActionBar;
    private boolean mUseParallax = true;
    private int mLastDampedScroll;
    private int mLastHeaderHeight = -1;
    private boolean mFirstGlobalLayoutPerformed;
    private FrameLayout mMarginView;
    private View mListViewBackgroundView;

    public final <T extends FadingActionBarHelperBase> T actionBarBackground(int drawableResId) {
        mActionBarBackgroundResId = drawableResId;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T actionBarBackground(Drawable drawable) {
        mActionBarBackgroundDrawable = drawable;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T headerLayout(int layoutResId) {
        mHeaderLayoutResId = layoutResId;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T headerView(View view) {
        mHeaderView = view;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T headerOverlayLayout(int layoutResId) {
        mHeaderOverlayLayoutResId = layoutResId;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T headerOverlayView(View view) {
        mHeaderOverlayView = view;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T contentLayout(int layoutResId) {
        mContentLayoutResId = layoutResId;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T  contentView(View view) {
        mContentView = view;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T lightActionBar(boolean value) {
        mLightActionBar = value;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T  parallax(boolean value) {
        mUseParallax = value;
        return (T)this;
    }

    int HEADER_HEIGHT = 0;
    int HEADER_WIDTH = 0;

    public final View createView(ActionBarActivity context) {
        HEADER_HEIGHT = ((ProductDetailsActivity)context).IMAGE_HEIGHT;
        HEADER_WIDTH = ((ProductDetailsActivity)context).IMAGE_HEIGHT;
        return createView(LayoutInflater.from(context));
    }

    public final View createView(LayoutInflater inflater) {
        //
        // Prepare everything


        mInflater = inflater;
        if (mContentView == null) {
            mContentView = inflater.inflate(mContentLayoutResId, null);
        }
        if (mHeaderView == null) {
            mHeaderView = inflater.inflate(mHeaderLayoutResId, null, false);
        }

        View root;
        root = createScrollView();

        if (mHeaderOverlayView == null && mHeaderOverlayLayoutResId != 0) {
            mHeaderOverlayView = inflater.inflate(mHeaderOverlayLayoutResId, mMarginView, false);
        }
        if (mHeaderOverlayView != null) {
            mMarginView.addView(mHeaderOverlayView);
        }

        // Use measured height here as an estimate of the header height, later on after the layout is complete
        // we'll use the actual height
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(LayoutParams.MATCH_PARENT, MeasureSpec.EXACTLY);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.EXACTLY);
        mHeaderView.measure(widthMeasureSpec, heightMeasureSpec);
        updateHeaderHeight(mHeaderView.getMeasuredHeight());

        root.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CommonLib.ZLog("CreateView", "GlobalLayout");
                int headerHeight = mHeaderContainer.getHeight();
                if (!mFirstGlobalLayoutPerformed && headerHeight != 0) {
                    CommonLib.ZLog("CreateView", "GlobalLayout " + headerHeight);
                    updateHeaderHeight(headerHeight);
                    mFirstGlobalLayoutPerformed = true;
                }
            }
        });
        return root;
    }

    public void initActionBar(ActionBarActivity activity) {
        if (mActionBarBackgroundDrawable == null) {
            mActionBarBackgroundDrawable = activity.getResources().getDrawable(mActionBarBackgroundResId);
        }
        setActionBarBackgroundDrawable(mActionBarBackgroundDrawable);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
        mActionBarBackgroundDrawable.setAlpha(0);
        setActionBarTitleContainerAlpha(0);
    }

    protected abstract int getActionBarHeight();
    protected abstract boolean isActionBarNull();
    protected abstract void setActionBarBackgroundDrawable(Drawable drawable);
    protected abstract void setActionBarTitleContainerAlpha(float alpha);

    protected <T> T getActionBarWithReflection(Activity activity, String methodName) {
        try {
            Method method = activity.getClass().getMethod(methodName);
            return (T)method.invoke(activity);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            setActionBarBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };


    private View createScrollView() {
        ViewGroup scrollViewContainer = (ViewGroup) mInflater.inflate(R.layout.parallax_scrollview_container, null);

        ObservableScrollView scrollView = (ObservableScrollView) scrollViewContainer.findViewById(R.id.parallax_scroll_view);
        scrollView.setOnScrollChangedCallback(mOnScrollChangedListener);

        ViewGroup contentContainer = (ViewGroup) scrollViewContainer.findViewById(R.id.parallax_container);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(layoutParams);
        contentContainer.addView(mContentView);
        mHeaderContainer = (FrameLayout) scrollViewContainer.findViewById(R.id.parallax_header_container);
        initializeGradient(mHeaderContainer);
        mHeaderContainer.addView(mHeaderView, 0);
        mMarginView = (FrameLayout) contentContainer.findViewById(R.id.parallax_content_top_margin);

        return scrollViewContainer;
    }

    private OnScrollChangedCallback mOnScrollChangedListener = new OnScrollChangedCallback() {
        public void onScroll(int l, int t) {
            onNewScroll(t);
        }
    };


    private int mLastScrollPosition;

    private void onNewScroll(int scrollPosition) {

        if (isActionBarNull()) {
            return;
        }

        int currentHeaderHeight = mHeaderContainer.getHeight();

        if (currentHeaderHeight != mLastHeaderHeight) {
            updateHeaderHeight(currentHeaderHeight);
        }

        int headerHeight = currentHeaderHeight - getActionBarHeight();
        float ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;
        int newAlpha = (int) (ratio * 255);
        mActionBarBackgroundDrawable.setAlpha(newAlpha);
        float x = ratio * 2;
        setActionBarTitleContainerAlpha(x > 1.0f ? 1.0f : x);
        addParallaxEffect(scrollPosition);
    }

    private void addParallaxEffect(int scrollPosition) {
        float damping = mUseParallax ? 0.3f : 1.0f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mHeaderContainer.offsetTopAndBottom(offset);

        if (mListViewBackgroundView != null) {
            offset = mLastScrollPosition - scrollPosition;
            mListViewBackgroundView.offsetTopAndBottom(offset);
        }

        if (mFirstGlobalLayoutPerformed) {
            mLastScrollPosition = scrollPosition;
            mLastDampedScroll = dampedScroll;
        }
    }

    private void updateHeaderHeight(int headerHeight) {
        CommonLib.ZLog("CreateView", "Step 7");

        ViewGroup.LayoutParams params = mMarginView.getLayoutParams();
        params.height = headerHeight;
        mMarginView.setLayoutParams(params);
        if (mListViewBackgroundView != null) {
            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) mListViewBackgroundView.getLayoutParams();
            params2.topMargin = headerHeight;
            mListViewBackgroundView.setLayoutParams(params2);
        }
        mLastHeaderHeight = headerHeight;
    }

    private void initializeGradient(ViewGroup headerContainer) {
        View gradientView = headerContainer.findViewById(R.id.parallax_gradient);
        int gradient = R.drawable.parallax_gradient;
        if (mLightActionBar) {
            gradient = R.drawable.parallax_gradient_light;
        }
        gradientView.setBackgroundResource(gradient);
    }
}
