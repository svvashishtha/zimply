package com.application.zimplyshop.utils;

import android.content.Context;

import com.application.zimplyshop.application.AppApplication;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

public class ZTracker {

    // Google Analytics Event
    public static void logGAEvent(Context ctx, String categoryStr, String actionStr, String labelStr) {

        try {
            // Get tracker.
            if (CommonLib.LogGAEvent) {
                Tracker tracker = ((AppApplication) ctx.getApplicationContext())
                        .getTracker(CommonLib.TrackerName.APPLICATION_TRACKER);

                // Build and send an Event.
                GoogleAnalytics.getInstance(ctx).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
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
            } catch (Exception e) {
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
            GoogleAnalytics.getInstance(ctx).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            tracker.send(new HitBuilders.AppViewBuilder().build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logGAEcommerceProductClickAction(Context context, String screenName, String productID, String productName, String productCategory, String productBrand, String productVariant, int position, double price, String productActionListName, String actionPerformed) {
        try {
            Product product = new Product()
                    .setId(productID)
                    .setName(productName)
                    .setCategory(productCategory)
                    .setBrand(productBrand)
                    .setVariant(productVariant)
                    .setPosition(position)
                    .setPrice(price);
            ProductAction productAction = new ProductAction(actionPerformed)
                    .setProductActionList(productActionListName);
            HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                    .addProduct(product)
                    .setProductAction(productAction);

            Tracker tracker = ((AppApplication) context.getApplicationContext())
                    .getTracker(CommonLib.TrackerName.APPLICATION_TRACKER);
            tracker.setScreenName(screenName);
            tracker.send(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkOutGaEvents(ProductAction productAction, Product product, Context context) {
        if (CommonLib.LogGAEvent) {
            try {
                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                        .addProduct(product)
                        .setProductAction(productAction);

                Tracker t = ((AppApplication) context).getTracker(
                        CommonLib.TrackerName.APPLICATION_TRACKER);
               GoogleAnalytics.getInstance(context).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
                t.send(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}