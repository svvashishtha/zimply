package com.application.zimplyshop.utils;

import android.content.Context;

import com.application.zimplyshop.application.AppApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ZTracker {

    // Google Analytics Event
    public static void logGAEvent(Context ctx, String categoryStr, String actionStr, String labelStr) {

        try {
            // Get tracker.
            if (CommonLib.LogGAEvent) {
                Tracker tracker = ((AppApplication) ctx.getApplicationContext())
                        .getTracker(CommonLib.TrackerName.APPLICATION_TRACKER);

                // Build and send an Event.
                tracker.send(new HitBuilders.EventBuilder().setCategory(categoryStr).setAction(actionStr).setLabel(labelStr)
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void logGaCustomEvent(Context ctx, String pageType, String name, String category, String sku) {
        if (CommonLib.LogGAEvent) {
        try {
            Tracker t = ((AppApplication) ctx.getApplicationContext()).getTracker(
                    CommonLib.TrackerName.APPLICATION_TRACKER);
               t.send(new HitBuilders.EventBuilder()
                       .setCategory(pageType + " Views")
                       .setAction(name + category)
                       .setLabel(sku)
                       .build());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
          }
    }

    // Google Analytics Screen View
    public static void logGAScreen(Context ctx, String screenName) {

        try {
            // Get tracker.
            Tracker tracker = ((AppApplication) ctx.getApplicationContext())
                    .getTracker(CommonLib.TrackerName.APPLICATION_TRACKER);

            // Set screen name.
            tracker.setScreenName(screenName);

            // Send a screen view.
            tracker.send(new HitBuilders.AppViewBuilder().build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}