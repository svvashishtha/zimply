package com.application.zimplyshop.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.HomeActivity;
import com.application.zimplyshop.activities.NewProductDetailActivity;
import com.application.zimplyshop.activities.ProductCheckoutActivity;
import com.application.zimplyshop.activities.ProductListingActivity;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.JSONUtils;
import com.application.zimplyshop.utils.TimeUtils;
import com.application.zimplyshop.utils.ZWebView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GcmNotificationManager implements AppConstants {
    static int mId = 0;
    static Context mContext;
    NotificationManager mNotificationManager;

    public GcmNotificationManager(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showBigImageNotification(String imageUrl, String message, int type, String slug, String title,String subtext) {
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).
                setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_ticker).setTicker(message)
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true).setColor(Color.parseColor("#bbbbbb"));
        BitmapDrawable bitmapDrawable_large_logo = (BitmapDrawable) mContext.getResources()
                .getDrawable(R.drawable.ic_app_launcher_icon);
        Bitmap bitmap_large_logo = bitmapDrawable_large_logo.getBitmap();
        mBuilder.setLargeIcon(bitmap_large_logo);

        // BigPictureStyle

        final NotificationCompat.BigPictureStyle mBigPictureStyle = new NotificationCompat.BigPictureStyle();
        if (title != null && title.trim().length() > 0)
            mBigPictureStyle.setBigContentTitle(title);
        else
            mBigPictureStyle.setBigContentTitle("Zimply");
        mBigPictureStyle.setSummaryText(new SpannableString(message));

        CommonLib.ZLog("GcmNotificationManager", imageUrl);
        Bitmap bitmap = getBitmapFromURL(imageUrl);
        mBigPictureStyle.bigPicture(bitmap);
        mBuilder.setStyle(mBigPictureStyle);
        Intent notificationIntent = new Intent();
        // TaskBuilder
        if (type == NOTIFICATION_TYPE_SHOP_LISTING) {
            notificationIntent = new Intent(mContext, ProductListingActivity.class);
            notificationIntent.putExtra("category_id", "0");
            notificationIntent.putExtra("hide_filter", false);
            notificationIntent.putExtra("category_name", message);
            notificationIntent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
            notificationIntent.putExtra("discount_id", Integer.parseInt(slug));
            notificationIntent.putExtra("nType", NOTIFICATION_TYPE_SHOP_LISTING);
            notificationIntent.putExtra("is_notification", true);
            notificationIntent.setAction("Big Offer Notification");
        } else if (type == NOTIFICATION_TYPE_WEBVIEW) {
            notificationIntent = new Intent(mContext, ZWebView.class);
            notificationIntent.putExtra("nType", NOTIFICATION_TYPE_WEBVIEW);
            notificationIntent.putExtra("is_notification", true);
            notificationIntent.putExtra("title", message);
            notificationIntent.putExtra("url", slug);
            notificationIntent.setAction("Big Offer Notification");
        } else if (type == NOTIFICATION_TYPE_PRODUCT_DETAIL) {
            notificationIntent = new Intent(mContext, NewProductDetailActivity.class);
            notificationIntent.putExtra("slug", slug);
            notificationIntent.putExtra("nType", NOTIFICATION_TYPE_PRODUCT_DETAIL);
            notificationIntent.putExtra("message", message);
            notificationIntent.putExtra("is_shared", true);
            notificationIntent.putExtra("slug", JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(slug), "slug"));
            notificationIntent.putExtra("id", Integer.parseInt(JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(slug), "id")));
            notificationIntent.setAction("Big Offer Notification");
        }else if(type == NOTIFICATION_TYPE_CART_PAGE){
            notificationIntent = new Intent(mContext, ProductCheckoutActivity.class);
            notificationIntent.putExtra("OrderSummaryFragment", false);
            notificationIntent.putExtra("is_notification", true);
        } else {
            notificationIntent = new Intent(mContext, HomeActivity.class);
            notificationIntent.putExtra("slug", slug);
            notificationIntent.putExtra("message", message);
            notificationIntent.putExtra("is_notification", true);
            notificationIntent.setAction("Big Offer Notification");
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationmanager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(mId, mBuilder.build());
        mId++;
    }

    public void showCustomBigStyleNotification(String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic_ticker)
                .setTicker(message).setContentTitle(mContext.getString(R.string.app_name)).setContentText(message)
                .setLights(0xff0000ff, 500, 500).setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        Intent notificationIntent = new Intent(mContext, HomeActivity.class);
        notificationIntent.setAction("General Notification");
        notificationIntent.putExtra("is_general", true);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        builder.setContentIntent(pendingNotificationIntent);
        // On tap heart

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(mId, builder.build());
        mId++;
    }

    public void showCustomNotification(String message, int type, String slug, String title,String imageUrl,String subText) {

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext).
                setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_ticker).setTicker(message).setStyle(new NotificationCompat.BigTextStyle())
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true).setColor(Color.parseColor("#bbbbbb"));
        BitmapDrawable bitmapDrawable_large_logo = (BitmapDrawable) mContext.getResources()
                .getDrawable(R.drawable.ic_app_launcher_icon);
        Bitmap bitmap_large_logo = bitmapDrawable_large_logo.getBitmap();
        builder.setLargeIcon(bitmap_large_logo);
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.gcm_custom_notification);
        remoteViews.setImageViewResource(R.id.iv_notification_logo, R.drawable.ic_app_launcher_icon);
        //  remoteViews.setImageViewResource(R.id.iv_notification_right_small_logo, R.drawable.ic_action_favorite_red);
        if(imageUrl!=null && imageUrl.length()>0) {
            Bitmap bitmap = getBitmapFromURL(imageUrl);
            remoteViews.setImageViewBitmap(R.id.notif_image, bitmap);
        }else{
            remoteViews.setImageViewResource(R.id.notif_image, R.drawable.notif_search_image);
        }
        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.tv_notification_title, title);

        remoteViews.setTextViewText(R.id.tv_notification_text, message);
        remoteViews.setTextViewText(R.id.tv_notification_time, TimeUtils.getNotificationDateTime());

        // on tapping notification
        Intent notificationIntent;

        if (type == NOTIFICATION_TYPE_SHOP_LISTING) {
            notificationIntent = new Intent(mContext, ProductListingActivity.class);
            notificationIntent.putExtra("category_id", "0");
            notificationIntent.putExtra("hide_filter", false);
            notificationIntent.putExtra("category_name", message);
            notificationIntent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
            notificationIntent.putExtra("discount_id", Integer.parseInt(slug));
            notificationIntent.putExtra("nType", NOTIFICATION_TYPE_SHOP_LISTING);
            notificationIntent.putExtra("is_notification", true);
            notificationIntent.setAction("Big Offer Notification");
        } else if (type == NOTIFICATION_TYPE_WEBVIEW) {
            notificationIntent = new Intent(mContext, ZWebView.class);
            notificationIntent.putExtra("nType", NOTIFICATION_TYPE_WEBVIEW);
            notificationIntent.putExtra("is_notification", true);
            notificationIntent.putExtra("title", message);
            notificationIntent.putExtra("url", slug);
            notificationIntent.setAction("Big Offer Notification");
        } else if (type == NOTIFICATION_TYPE_PRODUCT_DETAIL) {
            notificationIntent = new Intent(mContext, NewProductDetailActivity.class);
            notificationIntent.putExtra("slug", slug);
            notificationIntent.putExtra("nType", NOTIFICATION_TYPE_PRODUCT_DETAIL);
            notificationIntent.putExtra("message", message);
            notificationIntent.putExtra("is_shared", true);
            notificationIntent.putExtra("slug", JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(slug), "slug"));
            notificationIntent.putExtra("id", Integer.parseInt(JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(slug), "id")));
            notificationIntent.setAction("Big Offer Notification");
        }else if(type == NOTIFICATION_TYPE_CART_PAGE){
            notificationIntent = new Intent(mContext, ProductCheckoutActivity.class);
            notificationIntent.putExtra("OrderSummaryFragment", false);
            notificationIntent.putExtra("is_notification", true);
        } else {
            notificationIntent = new Intent(mContext, HomeActivity.class);
            notificationIntent.putExtra("slug", slug);
            notificationIntent.putExtra("message", message);
            notificationIntent.putExtra("is_notification", true);
            notificationIntent.setAction("Big Offer Notification");
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        builder.setContentIntent(pendingNotificationIntent);

        //builder.setContent(remoteViews);

        builder.setContent(remoteViews);
        NotificationManager notificationmanager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(mId, builder.build());

        mId++;
    }

    public void showCustomNotificationSmall(String message, int type, String slug, String title,String imageUrl,String subtext) {

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext).
                setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_ticker).setTicker(message).setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true).setColor(Color.parseColor("#bbbbbb"));
        BitmapDrawable bitmapDrawable_large_logo = (BitmapDrawable) mContext.getResources()
                .getDrawable(R.drawable.ic_app_launcher_icon);
        Bitmap bitmap_large_logo = bitmapDrawable_large_logo.getBitmap();
        builder.setLargeIcon(bitmap_large_logo);
        // Using RemoteViews to bind custom layouts into Notification

        // on tapping notification
        Intent notificationIntent;

        if (type == NOTIFICATION_TYPE_SHOP_LISTING) {
            notificationIntent = new Intent(mContext, ProductListingActivity.class);
            notificationIntent.putExtra("category_id", "0");
            notificationIntent.putExtra("hide_filter", false);
            notificationIntent.putExtra("category_name", message);
            notificationIntent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
            notificationIntent.putExtra("discount_id", Integer.parseInt(slug));
            notificationIntent.putExtra("nType", NOTIFICATION_TYPE_SHOP_LISTING);
            notificationIntent.putExtra("is_notification", true);
            notificationIntent.setAction("Big Offer Notification");
        } else if (type == NOTIFICATION_TYPE_WEBVIEW) {
            notificationIntent = new Intent(mContext, ZWebView.class);
            notificationIntent.putExtra("nType", NOTIFICATION_TYPE_WEBVIEW);
            notificationIntent.putExtra("is_notification", true);
            notificationIntent.putExtra("title", message);
            notificationIntent.putExtra("url", slug);
            notificationIntent.setAction("Big Offer Notification");
        } else if (type == NOTIFICATION_TYPE_PRODUCT_DETAIL) {
            notificationIntent = new Intent(mContext, NewProductDetailActivity.class);
            notificationIntent.putExtra("slug", slug);
            notificationIntent.putExtra("nType", NOTIFICATION_TYPE_PRODUCT_DETAIL);
            notificationIntent.putExtra("message", message);
            notificationIntent.putExtra("is_shared", true);
            notificationIntent.putExtra("slug", JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(slug), "slug"));
            notificationIntent.putExtra("id", Integer.parseInt(JSONUtils.getStringfromJSON(JSONUtils.getJSONObject(slug), "id")));
            notificationIntent.setAction("Big Offer Notification");
        } else if(type == NOTIFICATION_TYPE_CART_PAGE){
            notificationIntent = new Intent(mContext, ProductCheckoutActivity.class);
            notificationIntent.putExtra("OrderSummaryFragment", false);
            notificationIntent.putExtra("is_notification", true);
        }else{
            notificationIntent = new Intent(mContext, HomeActivity.class);
            notificationIntent.putExtra("slug", slug);
            notificationIntent.putExtra("message", message);
            notificationIntent.putExtra("is_notification", true);
            notificationIntent.setAction("Big Offer Notification");
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        builder.setContentIntent(pendingNotificationIntent);

        //builder.setContent(remoteViews);

        NotificationManager notificationmanager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(mId, builder.build());

        mId++;
    }


    private void setImageFromUrlOrDisk(final String url, final ImageView imageView, final String type, int width,
                                       int height, boolean useDiskCache) {

        if (cancelPotentialWork(url, imageView)) {

            GetImage task = new GetImage(url, imageView, width, height, useDiskCache, type);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), null, task);
            imageView.setImageDrawable(asyncDrawable);
            if (imageView.getParent() != null && imageView.getParent() instanceof ViewGroup
                    && ((ViewGroup) imageView.getParent()).getChildAt(2) != null
                    && ((ViewGroup) imageView.getParent()).getChildAt(2) instanceof ProgressBar) {
                ((ViewGroup) imageView.getParent()).getChildAt(2).setVisibility(View.GONE);
            }
            // if (zapp.cache.get(url + type) == null) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1L);
            // } else if (imageView != null && imageView.getDrawable() != null
            // && ((BitmapDrawable) imageView.getDrawable()).getBitmap() !=
            // null) {
            // imageView.setBackgroundResource(0);
            // if (imageView.getParent() != null && imageView.getParent()
            // instanceof ViewGroup
            // && ((ViewGroup) imageView.getParent()).getChildAt(2) != null
            // && ((ViewGroup) imageView.getParent()).getChildAt(2) instanceof
            // ProgressBar) {
            // ((ViewGroup)
            // imageView.getParent()).getChildAt(2).setVisibility(View.GONE);
            // }
            // }
        }
    }

    private class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<GetImage> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, GetImage bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<GetImage>(bitmapWorkerTask);
        }

        public GetImage getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public boolean cancelPotentialWork(String data, ImageView imageView) {
        final GetImage bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.url;
            if (!bitmapData.equals(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was
        // cancelled
        return true;
    }

    private GetImage getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class GetImage extends AsyncTask<Object, Void, Bitmap> {

        String url = "";
        private WeakReference<ImageView> imageViewReference;
        private int width;
        private int height;
        boolean useDiskCache;
        String type;

        public GetImage(String url, ImageView imageView, int width, int height, boolean useDiskCache, String type) {
            this.url = url;
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.width = width;
            this.height = height;
            this.useDiskCache = true;// useDiskCache;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null && imageView.getParent() != null && imageView.getParent() instanceof ViewGroup
                        && ((ViewGroup) imageView.getParent()).getChildAt(2) != null
                        && ((ViewGroup) imageView.getParent()).getChildAt(2) instanceof ProgressBar)
                    ((ViewGroup) imageView.getParent()).getChildAt(2).setVisibility(View.VISIBLE);
            }
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap bitmap = null;
            try {

                String url2 = url + type;

                if (useDiskCache) {
                    bitmap = CommonLib.getBitmapFromDisk(url2, mContext.getApplicationContext());
                }

                if (bitmap == null) {
                    try {
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream((InputStream) new URL(url).getContent(), null, opts);

                        opts.inSampleSize = CommonLib.calculateInSampleSize(opts, width, height);
                        opts.inJustDecodeBounds = false;

                        bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent(), null, opts);

                        if (useDiskCache) {
                            CommonLib.writeBitmapToDisk(url2, bitmap, mContext.getApplicationContext(),
                                    Bitmap.CompressFormat.JPEG);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (Error e) {

                    }
                }

                if (bitmap != null) {

                    if (type.equals("user")) {
                        bitmap = CommonLib.getRoundedCornerBitmap(bitmap, width);
                    } else if (type.equals("restaurant")) {
                        bitmap = CommonLib.getRoundedCornerBitmap(bitmap,
                                mContext.getResources().getDimension(R.dimen.corner_radius));
                    } else if (type.equals("userlarge")) {
                        bitmap = CommonLib.getRoundedCornerBitmap(bitmap, width);
                    }
                }
            } catch (Exception e) {
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    if (imageView.getParent() != null && imageView.getParent() instanceof ViewGroup
                            && ((ViewGroup) imageView.getParent()).getChildAt(2) != null
                            && ((ViewGroup) imageView.getParent()).getChildAt(2) instanceof ProgressBar) {
                        ((ViewGroup) imageView.getParent()).getChildAt(2).setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
