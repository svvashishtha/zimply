package com.application.zimplyshop.utils;

import com.google.android.gms.tagmanager.ContainerHolder;

/**
 * Created by apoorvarora on 26/11/15.
 * Singleton to hold the GTM Container (since it should be only created once
 * per run of the app).
 */
public class ZContainerHolder {
    private static ContainerHolder containerHolder;

    /**
     * Utility class; don't instantiate.
     */
    private ZContainerHolder() {
    }

    public static ContainerHolder getContainerHolder() {
        return containerHolder;
    }

    public static void setContainerHolder(ContainerHolder c) {
        containerHolder = c;
    }
}
