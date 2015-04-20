package me.yourbay.croppedimage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ram on 15/4/20.
 */
public class CroppedImage extends ImageView {

    private Matrix mMatrix;
    private float x_gravity = 0.5f;
    private float y_gravity = 0.5f;
    private Paint mPaint;

    public CroppedImage(Context context) {
        this(context, null);
    }

    public CroppedImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(50);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setFakeBoldText(true);
        mPaint.setShadowLayer(1, 1, 1, 0x99000000);
        mMatrix = new Matrix();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        resetMatrix(drawable);
        super.setImageDrawable(drawable);
    }

    public void setGravity(float x, float y) {
        this.x_gravity = x;
        this.y_gravity = y;
    }

    public void setImageDrawable(Drawable drawable, float x, float y) {
        super.setImageDrawable(drawable);
        resetDrawableGravity(x, y);
    }

    public void resetDrawableGravity(float x, float y) {
        setGravity(x, y);
        resetMatrix(getDrawable());
    }

    private void resetMatrix(Drawable d) {
        if (d == null) {
            return;
        }
        if (getScaleType() != ScaleType.MATRIX) {
            return;
        }
        final Matrix mDrawMatrix = mMatrix;
        final int dwidth = d.getIntrinsicWidth();
        final int dheight = d.getIntrinsicHeight();
        final int vwidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int vheight = getHeight() - getPaddingTop() - getPaddingBottom();

        float scale;
        float dx = 0, dy = 0;
        if (dwidth * vheight > vwidth * dheight) {
            scale = (float) vheight / (float) dheight;
            final float vHalfW = vwidth * 0.5f;
            final float dScaledW = dwidth * scale;
            final float dPositionW = dScaledW * x_gravity;
            if (dPositionW > vHalfW) {
                dx = -Math.min(dPositionW - vHalfW, dScaledW - vwidth);
            }
        } else {
            scale = (float) vwidth / (float) dwidth;
            final float vHalfH = vheight * 0.5f;
            final float dScaledH = dheight * scale;
            final float dPositionH = dScaledH * y_gravity;
            if (dPositionH > vHalfH) {
                dy = -Math.min(dPositionH - vHalfH, dScaledH - vheight);
            }
        }
        mDrawMatrix.setScale(scale, scale);
        mDrawMatrix.postTranslate((int) (dx + x_gravity), (int) (dy + y_gravity));
        setImageMatrix(mDrawMatrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("(" + x_gravity + "," + y_gravity + ")", getWidth() / 2, getHeight() / 2 + 10, mPaint);
    }
}
