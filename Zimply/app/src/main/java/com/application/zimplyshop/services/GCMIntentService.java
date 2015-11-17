package com.application.zimplyshop.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.managers.GcmNotificationManager;
import com.application.zimplyshop.receivers.GcmBroadcastReceiver;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.JSONUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

public class GCMIntentService extends IntentService implements AppConstants {

    public Context context;

    public GCMIntentService() {
        super("GcmIntentService");
        context = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {
            /*
             * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                CommonLib.ZLog("Send error:", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                CommonLib.ZLog("Deleted messages on server:", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                {
                    sendNotification(extras);
                    try {
                        JSONObject jsonObject = new JSONObject(intent.getStringExtra("com.parse.Data"));
                        String gcmAction = jsonObject.optString("action");
                        if (gcmAction.equals("sync")) {
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else if (intent.getStringExtra("is_notiphi") != null) {
                return;
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle bundle) {
        GcmNotificationManager gcmNotificationManager = null;//.showUpdatedCustomNotification(notification_id);

        JSONObject obj = JSONUtils.getJSONObject(bundle.getString("message"));
        if (obj != null) {
            String message = JSONUtils.getStringfromJSON(obj, "message");
            String imageUrl = JSONUtils.getStringfromJSON(obj, "img");
            System.out.println("GcmObject" + obj.toString());
            int type = JSONUtils.getIntegerfromJSON(obj, "type");
            int notificationType = JSONUtils.getIntegerfromJSON(obj, "expand");
            String slug = JSONUtils.getStringfromJSON(obj, "slug");
            if (gcmNotificationManager == null) {
                gcmNotificationManager = new GcmNotificationManager(context);
            }
            if (notificationType == 1) {
                gcmNotificationManager.showCustomNotification(message,
                        type, slug);
            } else {
                gcmNotificationManager.showBigImageNotification(imageUrl,
                        message, type, slug);
            }

        }


    }

}