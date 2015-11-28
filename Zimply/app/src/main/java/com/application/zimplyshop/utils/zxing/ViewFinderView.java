package com.application.zimplyshop.utils.zxing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.application.zimplyshop.R;


public class ViewFinderView extends View implements IViewFinder {
    private static final String TAG = "ViewFinderView";
    private Rect mFramingRect;
    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final float LANDSCAPE_WIDTH_RATIO = 0.625F;
    private static final float LANDSCAPE_HEIGHT_RATIO = 0.625F;
    private static final int LANDSCAPE_MAX_FRAME_WIDTH = 1200;
    private static final int LANDSCAPE_MAX_FRAME_HEIGHT = 675;
    private static final float PORTRAIT_WIDTH_RATIO = 0.875F;
    private static final float PORTRAIT_HEIGHT_RATIO = 0.375F;
    private static final int PORTRAIT_MAX_FRAME_WIDTH = 945;
    private static final int PORTRAIT_MAX_FRAME_HEIGHT = 720;
    private static final int[] SCANNER_ALPHA = new int[]{0, 64, 128, 192, 255, 192, 128, 64};
    private int scannerAlpha;
    private static final int POINT_SIZE = 10;
    private static final long ANIMATION_DELAY = 80L;
    private final int mDefaultLaserColor;
    private final int mDefaultMaskColor;
    private final int mDefaultBorderColor;
    private final int mDefaultBorderStrokeWidth;
    private final int mDefaultBorderLineLength;
    protected Paint mLaserPaint;
    protected Paint mFinderMaskPaint;
    protected Paint mBorderPaint;
    protected int mBorderLineLength;

    public ViewFinderView(Context context) {
        super(context);
        this.mDefaultLaserColor = this.getResources().getColor(R.color.viewfinder_laser);
        this.mDefaultMaskColor = this.getResources().getColor(R.color.viewfinder_mask);
        this.mDefaultBorderColor = this.getResources().getColor(R.color.viewfinder_border);
        this.mDefaultBorderStrokeWidth = this.getResources().getInteger(R.integer.viewfinder_border_width);
        this.mDefaultBorderLineLength = this.getResources().getInteger(R.integer.viewfinder_border_length);
        this.init();
    }

    public ViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDefaultLaserColor = this.getResources().getColor(R.color.viewfinder_laser);
        this.mDefaultMaskColor = this.getResources().getColor(R.color.viewfinder_mask);
        this.mDefaultBorderColor = this.getResources().getColor(R.color.viewfinder_border);
        this.mDefaultBorderStrokeWidth = this.getResources().getInteger(R.integer.viewfinder_border_width);
        this.mDefaultBorderLineLength = this.getResources().getInteger(R.integer.viewfinder_border_length);
        this.init();
    }

    private void init() {
        this.mLaserPaint = new Paint();
        this.mLaserPaint.setColor(this.mDefaultLaserColor);
        this.mLaserPaint.setStyle(Style.FILL);
        this.mFinderMaskPaint = new Paint();
        this.mFinderMaskPaint.setColor(this.mDefaultMaskColor);
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setColor(this.mDefaultBorderColor);
        this.mBorderPaint.setStyle(Style.STROKE);
        this.mBorderPaint.setStrokeWidth((float)this.mDefaultBorderStrokeWidth);
        this.mBorderLineLength = this.mDefaultBorderLineLength;
    }

    public void setLaserColor(int laserColor) {
        this.mLaserPaint.setColor(laserColor);
    }

    public void setMaskColor(int maskColor) {
        this.mFinderMaskPaint.setColor(maskColor);
    }

    public void setBorderColor(int borderColor) {
        this.mBorderPaint.setColor(borderColor);
    }

    public void setBorderStrokeWidth(int borderStrokeWidth) {
        this.mBorderPaint.setStrokeWidth((float)borderStrokeWidth);
    }

    public void setBorderLineLength(int borderLineLength) {
        this.mBorderLineLength = borderLineLength;
    }

    public void setupViewFinder() {
        this.updateFramingRect();
        this.invalidate();
    }

    public Rect getFramingRect() {
        return this.mFramingRect;
    }

    public void onDraw(Canvas canvas) {
        if(this.mFramingRect != null) {
            this.drawViewFinderMask(canvas);
            this.drawViewFinderBorder(canvas);
            this.drawLaser(canvas);
        }
    }

    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        canvas.drawRect(0.0F, 0.0F, (float)width, (float)this.mFramingRect.top, this.mFinderMaskPaint);
        canvas.drawRect(0.0F, (float)this.mFramingRect.top, (float)this.mFramingRect.left, (float)(this.mFramingRect.bottom + 1), this.mFinderMaskPaint);
        canvas.drawRect((float)(this.mFramingRect.right + 1), (float)this.mFramingRect.top, (float)width, (float)(this.mFramingRect.bottom + 1), this.mFinderMaskPaint);
        canvas.drawRect(0.0F, (float)(this.mFramingRect.bottom + 1), (float)width, (float)height, this.mFinderMaskPaint);
    }

    public void drawViewFinderBorder(Canvas canvas) {
        canvas.drawLine((float)(this.mFramingRect.left - 1), (float)(this.mFramingRect.top - 1), (float)(this.mFramingRect.left - 1), (float)(this.mFramingRect.top - 1 + this.mBorderLineLength), this.mBorderPaint);
        canvas.drawLine((float)(this.mFramingRect.left - 1), (float)(this.mFramingRect.top - 1), (float)(this.mFramingRect.left - 1 + this.mBorderLineLength), (float)(this.mFramingRect.top - 1), this.mBorderPaint);
        canvas.drawLine((float)(this.mFramingRect.left - 1), (float)(this.mFramingRect.bottom + 1), (float)(this.mFramingRect.left - 1), (float)(this.mFramingRect.bottom + 1 - this.mBorderLineLength), this.mBorderPaint);
        canvas.drawLine((float)(this.mFramingRect.left - 1), (float)(this.mFramingRect.bottom + 1), (float)(this.mFramingRect.left - 1 + this.mBorderLineLength), (float)(this.mFramingRect.bottom + 1), this.mBorderPaint);
        canvas.drawLine((float)(this.mFramingRect.right + 1), (float)(this.mFramingRect.top - 1), (float)(this.mFramingRect.right + 1), (float)(this.mFramingRect.top - 1 + this.mBorderLineLength), this.mBorderPaint);
        canvas.drawLine((float)(this.mFramingRect.right + 1), (float)(this.mFramingRect.top - 1), (float)(this.mFramingRect.right + 1 - this.mBorderLineLength), (float)(this.mFramingRect.top - 1), this.mBorderPaint);
        canvas.drawLine((float)(this.mFramingRect.right + 1), (float)(this.mFramingRect.bottom + 1), (float)(this.mFramingRect.right + 1), (float)(this.mFramingRect.bottom + 1 - this.mBorderLineLength), this.mBorderPaint);
        canvas.drawLine((float)(this.mFramingRect.right + 1), (float)(this.mFramingRect.bottom + 1), (float)(this.mFramingRect.right + 1 - this.mBorderLineLength), (float)(this.mFramingRect.bottom + 1), this.mBorderPaint);
    }

    public void drawLaser(Canvas canvas) {
        this.mLaserPaint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
        this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = this.mFramingRect.height() / 2 + this.mFramingRect.top;
        canvas.drawRect((float)(this.mFramingRect.left + 2), (float)(middle - 1), (float)(this.mFramingRect.right - 1), (float)(middle + 2), this.mLaserPaint);
        this.postInvalidateDelayed(80L, this.mFramingRect.left - 10, this.mFramingRect.top - 10, this.mFramingRect.right + 10, this.mFramingRect.bottom + 10);
    }

    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        this.updateFramingRect();
    }

    public synchronized void updateFramingRect() {
        Point viewResolution = new Point(this.getWidth(), this.getHeight());
        int orientation = DisplayUtils.getScreenOrientation(this.getContext());
        int width;
        int height;
        if(orientation != 1) {
            width = findDesiredDimensionInRange(0.625F, viewResolution.x, 240, 1200);
            height = findDesiredDimensionInRange(0.625F, viewResolution.y, 240, 675);
        } else {
            width = findDesiredDimensionInRange(0.875F, viewResolution.x, 240, 945);
            height = findDesiredDimensionInRange(0.375F, viewResolution.y, 240, 720);
        }

        int leftOffset = (viewResolution.x - width) / 2;
        int topOffset = (viewResolution.y - height) / 2;
        this.mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
    }

    private static int findDesiredDimensionInRange(float ratio, int resolution, int hardMin, int hardMax) {
        int dim = (int)(ratio * (float)resolution);
        return dim < hardMin?hardMin:(dim > hardMax?hardMax:dim);
    }
}
