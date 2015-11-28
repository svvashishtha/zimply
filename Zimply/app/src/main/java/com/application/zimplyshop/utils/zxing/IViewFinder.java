package com.application.zimplyshop.utils.zxing;

import android.graphics.Rect;

public interface IViewFinder {
    void setupViewFinder();

    Rect getFramingRect();

    int getWidth();

    int getHeight();
}
