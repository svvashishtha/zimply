package com.application.zimplyshop.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.utils.AsyncDrawable;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.ImageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

public class GetImage extends AsyncTask<Void, Void, Bitmap>implements Cloneable {

	String url = "";
	public WeakReference<ImageView> imageViewReference;
	public int width;
	public int height;
	boolean useDiskCache;
	public String type;
	String url2 = "";
	private AppApplication zapp;
	private ImageUtils imageUtils;
	private ImageLoaderManager imgManager;
	boolean isFastBlurr;
	long timestampInitial;

	public GetImage(String url, ImageView imageView, int width, int height, boolean useDiskCache, boolean toBeRounded,
			boolean isFastBlurr, String type, AppApplication zapp, ImageLoaderManager imgManager) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.width = width;
		this.height = height;
		this.useDiskCache = useDiskCache;
		this.type = type;
		this.zapp = zapp;
		this.imgManager = imgManager;
		this.imageUtils = new ImageUtils();
		this.url = url;
		this.isFastBlurr = isFastBlurr;
		timestampInitial = System.currentTimeMillis();
	}


	@Override
	protected void onPreExecute() {

		/*if(type == "pro")
		((ImageView)this.imageViewReference.get()).setImageBitmap(CommonLib.getBitmap(zapp, R.drawable.ic_pro_placeholder,zapp.getResources().getDimensionPixelSize(R.dimen.pro_image_size),zapp.getResources().getDimensionPixelSize(R.dimen.pro_image_size)));*/
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {

		if (zapp == null)
			return;

		if (isCancelled()) {
			bitmap = null;
		}
		CommonLib.ZLog("bitmap_scaling", "Difference:"+(System.currentTimeMillis() - timestampInitial));
		if (bitmap != null) {

			zapp.cache.put(imageUtils.getImprovisedUrl(url, type), bitmap);

		} else if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			GetImage task = imageUtils.getBitmapWorkerTask(imageView);
			if (task != null) {
				if (task.url2.equals("")) {
					task.url2 = new String(task.url);
				}
				task.url = "";
			}
		}

		if (imageViewReference != null && bitmap != null) {
			if(type != null && type.equals("photo_details") && imageViewReference.get() != null) {
			//	imageViewReference.get().setAlpha(0.5f);
			}

			if (imgManager != null && imgManager.getCallbackObject() != null)
				imgManager.getCallbackObject().loadingFinished(bitmap);
			final ImageView imageView = imageViewReference.get();
			if (imageView != null && imgManager.getScrollState() == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				GetImage currentTask = imageUtils.getBitmapWorkerTask(imageView);
				if ((!url.equals("")) && currentTask != null
						&& (currentTask.url.equals(url) || currentTask.url2.equals(url))) {
					GetImage task = new GetImage(url, imageView, width, height, true, false, isFastBlurr, type, zapp,
							imgManager);
					final AsyncDrawable asyncDrawable = new AsyncDrawable(zapp.getResources(), bitmap, task);
					imageView.setImageDrawable(asyncDrawable);
					// imageView.setBackgroundResource(0);
				} else {
					CommonLib.ZLog("getimagearray-imageview", "wrong bitmap");
				}
				imgManager.getImageArray.remove(this);

			} else if (imageView != null) {
				GetImage task = imageUtils.getBitmapWorkerTask(imageView);
				if (task != null) {
					// if(task.url2.equals("")) {
					task.url2 = new String(task.url);
					// }
					task.url = "";
				}
			} else if (imageView == null) {
				CommonLib.ZLog("getimagearray-imageview", "null");
			}
			if(type != null && type.equals("photo_details") && imageViewReference.get() != null) {
					imageViewReference.get().animate().setDuration(200);
                //imageViewReference.get().animate().alpha(1f).setDuration(200);
			}
		}

	}

	@Override
	protected Bitmap doInBackground(Void... params) {

		Bitmap bitmap = null;

		try {
			if (imgManager.getScrollState() == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

				if (useDiskCache)
					bitmap = CommonLib.getBitmapFromDisk(imageUtils.getImprovisedUrl(url, type), zapp);

				InputStream i = null;
				if (bitmap == null) {
					try {

						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = true;
						opts.inPreferredConfig = Bitmap.Config.RGB_565;
						BitmapFactory.decodeStream((InputStream) new URL(url).getContent(), null, opts);

						opts.inJustDecodeBounds = false;
						opts.inSampleSize = CommonLib.calculateInSampleSize(opts, width, height);
						opts.inPreferredConfig = Bitmap.Config.RGB_565;
						bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent(), null, opts);

						if (useDiskCache) {
							if(CommonLib.shouldScaleDownBitmap(zapp, bitmap))
								bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
							CommonLib.writeBitmapToDisk(imageUtils.getImprovisedUrl(url, type), bitmap, zapp,
									Bitmap.CompressFormat.JPEG);
						}

					} catch (Exception e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) {

						zapp.cache.clear();

						e.printStackTrace();
					} catch (Error r) {
						r.printStackTrace();
					} finally {
						if (i != null) {
							try {
								i.close();
							} catch (IOException ex) {
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
		if (bitmap != null && isFastBlurr) {
			bitmap = ImageUtils.fastblur(bitmap, 20);
		}
		return bitmap;
	}
}