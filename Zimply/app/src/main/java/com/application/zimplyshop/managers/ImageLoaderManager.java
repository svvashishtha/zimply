package com.application.zimplyshop.managers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.utils.AsyncDrawable;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.ImageScrollListener;
import com.application.zimplyshop.utils.ImageUtils;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageLoaderManager {

    private ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    public ArrayList<GetImage> getImageArray = new ArrayList<GetImage>();
    private int mScrollState;
    private ImageScrollListener imgScrollListener;
    private ImageUtils imageUtils;
    private AppApplication zapp = AppApplication.getInstance();
    ImageLoaderCallback mCallback;

    private Activity activity;

    public ImageLoaderManager(Activity activity) {
        if (activity != null) {
            this.activity = activity;
            zapp = (AppApplication) activity.getApplication();
        }
        imageUtils = new ImageUtils();
    }

    public int getScrollState() {
        return mScrollState;
    }

    public interface ImageLoaderCallback {
        void loadingStarted();

        void loadingFinished(Bitmap bitmap);
    }

    public boolean cancelPotentialWork(String data, ImageView imageView) {
        final GetImage bitmapWorkerTask = imageUtils.getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {

            final String bitmapData = bitmapWorkerTask.url;
            if (!bitmapData.equals(data)) {
                if (bitmapWorkerTask.url2.equals("")) {
                    bitmapWorkerTask.url2 = new String(bitmapWorkerTask.url);
                }
                // Cancel previous task
                bitmapWorkerTask.url = "";
                bitmapWorkerTask.cancel(true);
                // getImageArray.clear();
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was
        // cancelled
        return true;
    }

    /*
     * type :
     */
    public void setImageFromUrl(final String url, final ImageView imageView, final String type, final int width,
                                final int height, final boolean toBeRounded, boolean isFastBlurr) {

        if (cancelPotentialWork(url, imageView)) {
            Bitmap bitmap = null;
            if (zapp.cache.get(imageUtils.getImprovisedUrl(url, type)) != null) {
                bitmap = zapp.cache.get(imageUtils.getImprovisedUrl(url, type));
//				imageView.setBackgroundResource(R.drawable.ic_pro_placeholder);

            }

            GetImage task = new GetImage(url, imageView, width, height, true, toBeRounded, isFastBlurr, type, zapp,
                    ImageLoaderManager.this);
            AsyncDrawable asyncDrawable = new AsyncDrawable(zapp.getResources(), bitmap, task);

            imageView.setImageDrawable(asyncDrawable);

            try {
                task.executeOnExecutor(CommonLib.THREAD_POOL_EXECUTOR_IMAGE);
            } catch (Exception e) {
                CommonLib.sPoolWorkQueueImage.clear();
            }
            if (getImageArray.size() == 20) {
                getImageArray.remove(0);
            }
            getImageArray.add(task);

        } else if (((BitmapDrawable) imageView.getDrawable()).getBitmap() != null) {
            imageView.setBackgroundResource(0);
        }

    }

    public void setScrollState(final int scrollState) {

        if (activity != null) {
            mScrollState = scrollState;
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                worker.schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                            activity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                                        ArrayList<GetImage> arr2 = new ArrayList<GetImage>();

                                        boolean urlConflict = false;

                                        for (GetImage task : getImageArray) {
                                            if (!task.url.equals("")) {

                                                if (task.imageViewReference != null
                                                        && task.imageViewReference.get() != null) {
                                                    GetImage getImg = imageUtils
                                                            .getBitmapWorkerTask(task.imageViewReference.get());
                                                    if (getImg != null && getImg.url2.equals(task.url)) {

                                                        GetImage gi = new GetImage(task.url, task.imageViewReference.get(),
                                                                task.width, task.height, task.useDiskCache, false,
                                                                task.isFastBlurr, task.type, zapp, ImageLoaderManager.this);
                                                        try {
                                                            gi.executeOnExecutor(CommonLib.THREAD_POOL_EXECUTOR_IMAGE);
                                                        } catch (RejectedExecutionException e) {
                                                            CommonLib.sPoolWorkQueueImage.clear();
                                                        }
                                                        arr2.add(0, gi);

                                                    } else {
                                                        urlConflict = true;
                                                        // break;
                                                    }
                                                }
                                            } else if (!task.url2.equals("")) {

                                                if (task.imageViewReference != null
                                                        && task.imageViewReference.get() != null) {
                                                    GetImage getImg = imageUtils
                                                            .getBitmapWorkerTask(task.imageViewReference.get());
                                                    if (getImg != null && getImg.url2.equals(task.url2)) {

                                                        GetImage gi = new GetImage(task.url2, task.imageViewReference.get(),
                                                                task.width, task.height, task.useDiskCache, false,
                                                                task.isFastBlurr, task.type, zapp, ImageLoaderManager.this);
                                                        try {
                                                            gi.executeOnExecutor(CommonLib.THREAD_POOL_EXECUTOR_IMAGE);
                                                        } catch (RejectedExecutionException e) {
                                                            CommonLib.sPoolWorkQueueImage.clear();
                                                        }
                                                        arr2.add(0, gi);

                                                    } else {
                                                        urlConflict = true;
                                                    }
                                                }
                                            }
                                        }

                                        getImageArray.clear();

                                        if (urlConflict) {
                                            imgScrollListener.notifyDataSetForImages();
                                        } else {
                                            getImageArray.addAll(arr2);
                                        }

                                    }
                                }
                            });
                        }
                    }
                }, 50, TimeUnit.MILLISECONDS);
            }
        }
    }

    public ImageLoaderCallback getCallbackObject() {
        return mCallback;
    }
}