/*
 * Copyright/**
 *          * CedCommerce
 *           *
 *           * NOTICE OF LICENSE
 *           *
 *           * This source file is subject to the End User License Agreement (EULA)
 *           * that is bundled with this package in the file LICENSE.txt.
 *           * It is also available through the world-wide-web at this URL:
 *           * http://cedcommerce.com/license-agreement.txt
 *           *
 *           * @category  Ced
 *           * @package   MageNative
 *           * @author    CedCommerce Core Team <connect@cedcommerce.com >
 *           * @copyright Copyright CEDCOMMERCE (http://cedcommerce.com/)
 *           * @license   http://cedcommerce.com/license-agreement.txt
 *
 */
package com.goodwill.wholesale.productviewsection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ScaleImageView extends android.support.v7.widget.AppCompatImageView implements OnTouchListener {
    @NonNull
    private final Context mContext;
    private final float MAX_SCALE = 2f;
    private final float[] mMatrixValues = new float[9];
    private Matrix mMatrix;
    private int mWidth;
    private int mHeight;
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private float mMinScale;
    private float mPrevDistance;
    private boolean isScaling;
    private int mPrevMoveX;
    private int mPrevMoveY;
    private GestureDetector mDetector;

    public ScaleImageView(@NonNull Context context, AttributeSet attr) {
        super(context, attr);
        this.mContext = context;
        initialize();
    }

    public ScaleImageView(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initialize();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.initialize();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        this.initialize();
    }

    private void initialize() {
        this.setScaleType(ScaleType.MATRIX);
        this.mMatrix = new Matrix();


        Drawable d = getDrawable();
        if (d != null) {
            mIntrinsicWidth = d.getIntrinsicWidth();
            mIntrinsicHeight = d.getIntrinsicHeight();
            setOnTouchListener(this);
        }
        mDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                maxZoomTo((int) e.getX(), (int) e.getY());
                cutting();
                return super.onDoubleTap(e);
            }
        });

    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        mWidth = r - l;
        mHeight = b - t;

        mMatrix.reset();
        int r_norm = r - l;
        float mScale = (float) r_norm / (float) mIntrinsicWidth;

        int paddingHeight;
        int paddingWidth;
        if (mScale * mIntrinsicHeight > mHeight) {
            mScale = (float) mHeight / (float) mIntrinsicHeight;
            mMatrix.postScale(mScale, mScale);
            paddingWidth = (r - mWidth) / 2;
            paddingHeight = 0;
        } else {
            mMatrix.postScale(mScale, mScale);
            paddingHeight = (b - mHeight) / 2;
            paddingWidth = 0;
        }
        mMatrix.postTranslate(paddingWidth, paddingHeight);

        setImageMatrix(mMatrix);
        mMinScale = mScale;
        zoomTo(mScale, mWidth / 2, mHeight / 2);
        cutting();
        return super.setFrame(l, t, r, b);
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    private float getScale() {
        return getValue(mMatrix, Matrix.MSCALE_X);
    }

    private float getTranslateX() {
        return getValue(mMatrix, Matrix.MTRANS_X);
    }

    private float getTranslateY() {
        return getValue(mMatrix, Matrix.MTRANS_Y);
    }

    private void maxZoomTo(int x, int y) {
        if (mMinScale != getScale() && (getScale() - mMinScale) > 0.1f) {
            float scale = mMinScale / getScale();
            zoomTo(scale, x, y);
        } else {
            float scale = MAX_SCALE / getScale();
            zoomTo(scale, x, y);
        }
    }

    private void zoomTo(float scale, int x, int y) {
        if (getScale() * scale < mMinScale) {
            return;
        }
        if (scale >= 1 && getScale() * scale > MAX_SCALE) {
            return;
        }
        mMatrix.postScale(scale, scale);
        mMatrix.postTranslate(-(mWidth * scale - mWidth) / 2, -(mHeight * scale - mHeight) / 2);
        mMatrix.postTranslate(-(x - (mWidth / 2)) * scale, 0);
        mMatrix.postTranslate(0, -(y - (mHeight / 2)) * scale);
        setImageMatrix(mMatrix);
    }

    private void cutting() {
        int width = (int) (mIntrinsicWidth * getScale());
        int height = (int) (mIntrinsicHeight * getScale());
        if (getTranslateX() < -(width - mWidth)) {
            mMatrix.postTranslate(-(getTranslateX() + width - mWidth), 0);
        }
        if (getTranslateX() > 0) {
            mMatrix.postTranslate(-getTranslateX(), 0);
        }
        if (getTranslateY() < -(height - mHeight)) {
            mMatrix.postTranslate(0, -(getTranslateY() + height - mHeight));
        }
        if (getTranslateY() > 0) {
            mMatrix.postTranslate(0, -getTranslateY());
        }
        if (width < mWidth) {
            mMatrix.postTranslate((mWidth - width) / 2, 0);
        }
        if (height < mHeight) {
            mMatrix.postTranslate(0, (mHeight - height) / 2);
        }
        setImageMatrix(mMatrix);
    }

    private float distance(float x0, float x1, float y0, float y1) {
        float x = x0 - x1;
        float y = y0 - y1;
        return (float) Math.sqrt(x * x + y * y);
    }

    private float dispDistance() {
        return (float) Math.sqrt(mWidth * mWidth + mHeight * mHeight);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (mDetector.onTouchEvent(event)) {
            return true;
        }
        int touchCount = event.getPointerCount();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (touchCount >= 2) {
                    mPrevDistance = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                    isScaling = true;
                } else {
                    mPrevMoveX = (int) event.getX();
                    mPrevMoveY = (int) event.getY();
                }
            case MotionEvent.ACTION_MOVE:
                if (touchCount >= 2 && isScaling) {
                    float dist = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                    float scale = (dist - mPrevDistance) / dispDistance();
                    mPrevDistance = dist;
                    scale += 1;
                    scale = scale * scale;
                    zoomTo(scale, mWidth / 2, mHeight / 2);
                    cutting();
                } else if (!isScaling) {
                    int distanceX = mPrevMoveX - (int) event.getX();
                    int distanceY = mPrevMoveY - (int) event.getY();
                    mPrevMoveX = (int) event.getX();
                    mPrevMoveY = (int) event.getY();
                    mMatrix.postTranslate(-distanceX, -distanceY);
                    cutting();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() <= 1) {
                    isScaling = false;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouchEvent(event);
    }


}
