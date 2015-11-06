package com.application.zimply.utils;

import com.application.zimply.managers.GetImage;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class AsyncDrawable extends BitmapDrawable {
    // private final SoftReference<GetImage> bitmapWorkerTaskReference;
    private final GetImage bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, GetImage bitmapWorkerTask) {
        super(res, bitmap);
        // bitmapWorkerTaskReference = new
        // SoftReference<GetImage>(bitmapWorkerTask);
        bitmapWorkerTaskReference = bitmapWorkerTask;
    }

    public GetImage getBitmapWorkerTask() {
        return bitmapWorkerTaskReference;
        // return bitmapWorkerTaskReference.get();
    }
}