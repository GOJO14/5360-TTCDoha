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
 *           * @license      http://cedcommerce.com/license-agreement.txt
 *
 */

package com.goodwill.wholesale.productviewsection;

/*
  Created by Developer on 10/8/2015.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.goodwill.wholesale.R;

import java.util.Objects;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.widget.LinearLayout.HORIZONTAL;

/**
 * Draws circles (one for each view). The current view position is filled and
 * others are only stroked.
 */
public class CirclePageIndicator extends View implements PageIndicator {
    private static final int INVALID_POINTER = -1;
    private final Paint mPaintPageFill = new Paint(ANTI_ALIAS_FLAG);
    private final Paint mPaintStroke = new Paint(ANTI_ALIAS_FLAG);
    private final Paint mPaintFill = new Paint(ANTI_ALIAS_FLAG);
    private float mRadius;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private int mCurrentPage;
    private int mSnapPage;
    private float mPageOffset;
    private int mScrollState;
    private int mOrientation;
    private boolean mCentered;
    private boolean mSnap;

    private int mTouchSlop;
    private float mLastMotionX = -1;
    private int mActivePointerId = INVALID_POINTER;
    private boolean mIsDragging;


    public CirclePageIndicator(@NonNull Context context) {
        this(context, null);
    }

    public CirclePageIndicator(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePageIndicator(@NonNull Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;

        //Load defaults from resources
        final Resources res = getResources();
        final int defaultPageColor = res.getColor(R.color.black);
        //default_circle_indicator_fill_color
        final int defaultFillColor = res.getColor(R.color.AppTheme);
        final int defaultOrientation = res.getInteger(R.integer.default_circle_indicator_orientation);
        final int defaultStrokeColor = res.getColor(R.color.white);
        final float defaultStrokeWidth = res.getDimension(R.dimen.default_circle_indicator_stroke_width);
        final float defaultRadius = res.getDimension(R.dimen.default_circle_indicator_radius);
        final boolean defaultCentered = res.getBoolean(R.bool.default_circle_indicator_centered);
        final boolean defaultSnap = res.getBoolean(R.bool.default_circle_indicator_snap);

        //Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Ced_CircleCedPageIndicator, defStyle, 0);

        mCentered = a.getBoolean(R.styleable.Ced_CircleCedPageIndicator_centered, defaultCentered);
        mOrientation = a.getInt(R.styleable.Ced_CircleCedPageIndicator_android_orientation, defaultOrientation);
        mPaintPageFill.setStyle(Style.FILL);
        mPaintPageFill.setColor(a.getColor(R.styleable.Ced_CircleCedPageIndicator_pageColor, defaultPageColor));
        mPaintStroke.setStyle(Style.STROKE);
        mPaintStroke.setColor(a.getColor(R.styleable.Ced_CircleCedPageIndicator_strokeColor, defaultStrokeColor));
        mPaintStroke.setStrokeWidth(a.getDimension(R.styleable.Ced_CircleCedPageIndicator_strokeWidth, defaultStrokeWidth));
        mPaintFill.setStyle(Style.FILL);
        mPaintFill.setColor(a.getColor(R.styleable.Ced_CircleCedPageIndicator_fillColor, defaultFillColor));
        mRadius = a.getDimension(R.styleable.Ced_CircleCedPageIndicator_radius, defaultRadius);
        mSnap = a.getBoolean(R.styleable.Ced_CircleCedPageIndicator_snap, defaultSnap);

        Drawable background = a.getDrawable(R.styleable.Ced_CircleCedPageIndicator_android_background);
        if (background != null) {
            setBackground(background);
        }

        a.recycle();

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop();
    }


// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public void setCentered(boolean centered) {
//        mCentered = centered;
//        invalidate();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public boolean isCentered() {
//        return mCentered;
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public void setPageColor(int pageColor) {
//        mPaintPageFill.setColor(pageColor);
//        invalidate();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public int getPageColor() {
//        return mPaintPageFill.getColor();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public void setFillColor(int fillColor) {
//        mPaintFill.setColor(fillColor);
//        invalidate();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public int getFillColor() {
//        return mPaintFill.getColor();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public void setOrientation(int orientation) {
//        switch (orientation) {
//            case HORIZONTAL:
//            case VERTICAL:
//                mOrientation = orientation;
//                requestLayout();
//                break;
//
//            default:
//                throw new IllegalArgumentException("Orientation must be either HORIZONTAL or VERTICAL.");
//        }
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public int getOrientation() {
//        return mOrientation;
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public void setStrokeColor(int strokeColor) {
//        mPaintStroke.setColor(strokeColor);
//        invalidate();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public int getStrokeColor() {
//        return mPaintStroke.getColor();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public void setStrokeWidth(float strokeWidth) {
//        mPaintStroke.setStrokeWidth(strokeWidth);
//        invalidate();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public float getStrokeWidth() {
//        return mPaintStroke.getStrokeWidth();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public void setRadius(float radius) {
//        mRadius = radius;
//        invalidate();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public float getRadius() {
//        return mRadius;
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public void setSnap(boolean snap) {
//        mSnap = snap;
//        invalidate();
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

// --Commented out by Inspection START (10/7/18 12:28 PM):
//    public boolean isSnap() {
//        return mSnap;
//    }
// --Commented out by Inspection STOP (10/7/18 12:28 PM)

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (mViewPager == null) {
            return;
        }
        final int count = Objects.requireNonNull(mViewPager.getAdapter()).getCount();
        if (count == 0) {
            return;
        }

        if (mCurrentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }

        int longSize;
        int longPaddingBefore;
        int longPaddingAfter;
        int shortPaddingBefore;
        if (mOrientation == HORIZONTAL) {
            longSize = getWidth();
            longPaddingBefore = getPaddingLeft();
            longPaddingAfter = getPaddingRight();
            shortPaddingBefore = getPaddingTop();
        } else {
            longSize = getHeight();
            longPaddingBefore = getPaddingTop();
            longPaddingAfter = getPaddingBottom();
            shortPaddingBefore = getPaddingLeft();
        }

        final float threeRadius = mRadius * 3;
        final float shortOffset = shortPaddingBefore + mRadius;
        float longOffset = longPaddingBefore + mRadius;
        if (mCentered) {
            longOffset += ((longSize - longPaddingBefore - longPaddingAfter) / 2.0f) - ((count * threeRadius) / 2.0f);
        }

        float dX;
        float dY;

        float pageFillRadius = mRadius;
        if (mPaintStroke.getStrokeWidth() > 0) {
            pageFillRadius -= mPaintStroke.getStrokeWidth() / 2.0f;
        }

        //Draw stroked circles
        for (int iLoop = 0; iLoop < count; iLoop++) {
            float drawLong = longOffset + (iLoop * threeRadius);
            if (mOrientation == HORIZONTAL) {
                dX = drawLong;
                dY = shortOffset;
            } else {
                dX = shortOffset;
                dY = drawLong;
            }
            // Only paint fill if not completely transparent
            if (mPaintPageFill.getAlpha() > 0) {
                canvas.drawCircle(dX, dY, pageFillRadius, mPaintPageFill);
            }

            // Only paint stroke if a stroke width was non-zero
            if (pageFillRadius != mRadius) {
                canvas.drawCircle(dX, dY, mRadius, mPaintStroke);
            }
        }

        //Draw the filled circle according to the current scroll
        float cx = (mSnap ? mSnapPage : mCurrentPage) * threeRadius;
        if (!mSnap) {
            cx += mPageOffset * threeRadius;
        }
        if (mOrientation == HORIZONTAL) {
            dX = longOffset + cx;
            dY = shortOffset;
        } else {
            dX = shortOffset;
            dY = longOffset + cx;
        }
        canvas.drawCircle(dX, dY, mRadius, mPaintFill);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        if (super.onTouchEvent(ev)) {
            return true;
        }
        if ((mViewPager == null) || (Objects.requireNonNull(mViewPager.getAdapter()).getCount() == 0)) {
            return false;
        }

        final int action = ev.getAction() & ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);// MotionEventCompat.getPointerId(ev, 0);
                mLastMotionX = ev.getX();
                break;

            case MotionEvent.ACTION_MOVE: {
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);//MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float x = ev.getX(activePointerIndex);//MotionEventCompat.getX(ev, activePointerIndex);
                final float deltaX = x - mLastMotionX;

                if (!mIsDragging) {
                    if (Math.abs(deltaX) > mTouchSlop) {
                        mIsDragging = true;
                    }
                }

                if (mIsDragging) {
                    mLastMotionX = x;
                    if (mViewPager.isFakeDragging() || mViewPager.beginFakeDrag()) {
                        mViewPager.fakeDragBy(deltaX);
                    }
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!mIsDragging) {
                    final int count = mViewPager.getAdapter().getCount();
                    final int width = getWidth();
                    final float halfWidth = width / 2f;
                    final float sixthWidth = width / 6f;

                    if ((mCurrentPage > 0) && (ev.getX() < halfWidth - sixthWidth)) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            mViewPager.setCurrentItem(mCurrentPage - 1);
                        }
                        return true;
                    } else if ((mCurrentPage < count - 1) && (ev.getX() > halfWidth + sixthWidth)) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            mViewPager.setCurrentItem(mCurrentPage + 1);
                        }
                        return true;
                    }
                }

                mIsDragging = false;
                mActivePointerId = INVALID_POINTER;
                if (mViewPager.isFakeDragging()) mViewPager.endFakeDrag();
                break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();//MotionEventCompat.getActionIndex(ev);
                mLastMotionX = ev.getX(index);//MotionEventCompat.getX(ev, index);
                mActivePointerId = ev.getPointerId(index);//MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                final int pointerIndex = ev.getActionIndex();//MotionEventCompat.getActionIndex(ev);
                final int pointerId = ev.getPointerId(pointerIndex);//MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newPointerIndex);//MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                mLastMotionX = ev.getX(mActivePointerId);//MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, mActivePointerId));
                break;
        }

        return true;
    }

    @Override
    public void setViewPager(@NonNull ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            //noinspection ConstantConditions
            mViewPager.addOnPageChangeListener(null);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        mViewPager.addOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void setViewPager(@NonNull ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        invalidate();
    }

    @Override
    public void notifyDataSetChanged() {
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mScrollState = state;

        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPage = position;
        mPageOffset = positionOffset;
        invalidate();

        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mSnap || mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mCurrentPage = position;
            mSnapPage = position;
            invalidate();
        }

        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == HORIZONTAL) {
            setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
        } else {
            setMeasuredDimension(measureShort(widthMeasureSpec), measureLong(heightMeasureSpec));
        }
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureLong(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
            //We were told how big to be
            result = specSize;
        } else {
            //Calculate the width according the views count
            final int count = Objects.requireNonNull(mViewPager.getAdapter()).getCount();
            result = (int) (getPaddingLeft() + getPaddingRight()
                    + (count * 2 * mRadius) + (count - 1) * mRadius + 1);
            //Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureShort(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            //We were told how big to be
            result = specSize;
        } else {
            //Measure the height
            result = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            //Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPage = savedState.currentPage;
        mSnapPage = savedState.currentPage;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPage = mCurrentPage;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        @SuppressWarnings("UnusedDeclaration")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(@NonNull Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int currentPage;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(@NonNull Parcel in) {
            super(in);
            currentPage = in.readInt();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPage);
        }
    }
}