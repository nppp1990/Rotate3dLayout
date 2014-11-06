package com.yj.views;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by yj1990 on 14-11-5.
 */
public class Rotate3dLayout extends ViewGroup {
    private Scroller scroller;
    private VelocityTracker mVelocityTracker;

    private float angle = 90;
    private static int SNAP_VELOCITY = 600;
    private int currentPosition;
    private int mTouchSlop;


    private boolean isRecycle = true;
    private boolean isRotate = true;

    public Rotate3dLayout(Context context) {
        super(context);
        init(context);
    }

    public Rotate3dLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Rotate3dLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        scroller = new Scroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    public boolean isRecycle() {
        return isRecycle;
    }

    public void setRecycle(boolean isRecycle) {
        this.isRecycle = isRecycle;
    }

    public boolean isRotate() {
        return isRotate;
    }

    public void setRotate(boolean isRotate) {
        this.isRotate = isRotate;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    protected void onLayout(boolean changed, int left, int i2, int i3, int i4) {
        final int size = getChildCount();
        int childLeft = 0;
        if (changed && size > 0) {
            for (int i = 0; i < size; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != View.GONE) {
                    int width = child.getMeasuredWidth();
                    child.layout(childLeft, 0, childLeft + width, child.getMeasuredHeight());
                    childLeft += width;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        angle = 360.0f / count;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int width = getWidth();
        final long drawingTime = getDrawingTime();
        final int count = getChildCount();
        if (isRotate) {
            for (int i = 0; i < count; i++) {
                drawScreen(canvas, i, drawingTime, getChildAt(i));
                if (isRecycle) {
                    if (i == count - 1) {
                        drawTranslateRotate(canvas, count, drawingTime, getChildAt(0), width * count);
                    } else if (i == 0) {
                        drawTranslateRotate(canvas, -1, drawingTime, getChildAt(count - 1), -width * count);
                    }
                }
            }
        } else {
            if (isRecycle) {
                super.dispatchDraw(canvas);
                canvas.save();
                canvas.translate(width * count, 0);
                View first = getChildAt(0);
                drawChild(canvas, first, drawingTime);
                canvas.restore();
                canvas.save();
                canvas.translate(-width * count, 0);
                View last = getChildAt(count - 1);
                drawChild(canvas, last, drawingTime);
                canvas.restore();
            } else {
                super.dispatchDraw(canvas);
            }
        }
    }


    private void drawTranslateRotate(Canvas canvas, int screen, long drawingTime, View child, int translate) {
        // 得到当前子View的宽度
        final int width = getWidth();
        final int scrollWidth = screen * width;
        final int scrollX = getScrollX();
        // 只画当前页的子view，最多两个

        if (scrollWidth > scrollX + width || scrollWidth + width < scrollX) {
            return;
        }
        final int faceIndex = screen;
        final float currentDegree = getScrollX() * (angle / getMeasuredWidth());
        final float faceDegree = currentDegree - faceIndex * angle;
        if (faceDegree > 90 || faceDegree < -90) {
            return;
        }
        final float centerX = (scrollWidth < scrollX) ? scrollWidth + width
                : scrollWidth;
        final float centerY = getHeight() / 2;
        final Camera camera = new Camera();
        final Matrix matrix = new Matrix();
        canvas.save();
        camera.save();
        // camera和你的视角是反着的

        camera.rotateY(-faceDegree);
        camera.getMatrix(matrix);
        camera.restore();
        // 先把matrix的原点0，0移到中心点，转完后移回来
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

        canvas.concat(matrix);
        canvas.translate(translate, 0);
        drawChild(canvas, child, drawingTime);
        camera.restore();
    }


    private void drawScreen(Canvas canvas, int screen, long drawingTime, View child) {
        // 得到当前子View的宽度
        final int width = getWidth();
        final int scrollWidth = screen * width;
        final int scrollX = getScrollX();
        // 只画当前页的子view，最多两个

        if (scrollWidth > scrollX + width || scrollWidth + width < scrollX) {
            return;
        }
        final int faceIndex = screen;
        final float currentDegree = getScrollX() * (angle / getMeasuredWidth());
        final float faceDegree = currentDegree - faceIndex * angle;
        if (faceDegree > 90 || faceDegree < -90) {
            return;
        }
        final float centerX = (scrollWidth < scrollX) ? scrollWidth + width
                : scrollWidth;
        final float centerY = getHeight() / 2;
        final Camera camera = new Camera();
        final Matrix matrix = new Matrix();
        canvas.save();
        camera.save();

        // camera和你的视角是反着的
        camera.rotateY(-faceDegree);
        camera.getMatrix(matrix);
        camera.restore();
        // 先把matrix的原点0，0移到中心点，转完后移回来
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

        canvas.concat(matrix);
        drawChild(canvas, child, drawingTime);
        canvas.restore();
    }

    private float downX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = x;
                if (scroller != null) {
                    if (!scroller.isFinished()) {
                        scroller.abortAnimation();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int disX = (int) (downX - x);
                if (!isRecycle) {
                    if (disX > 0 && (getScrollX() + disX) > getWidth() * (getChildCount() - 1)) {
                        break;
                    }
                    if (disX < 0 && (getScrollX() + disX) < 0) {
                        break;
                    }
                }


                scrollBy(disX, 0);
                downX = x;

                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();

                int widthX = currentPosition * getWidth();
                if (widthX - getWidth() / 2 > getScrollX()) {
                    moveToScreen(currentPosition - 1);
                } else if (widthX + getWidth() / 2 < getScrollX()) {
                    moveToScreen(currentPosition + 1);
                } else {
                    if (velocityX > SNAP_VELOCITY && (isRecycle || currentPosition > 0)) {
                        moveToScreen(currentPosition - 1);
                    } else if (velocityX < -SNAP_VELOCITY && (isRecycle || currentPosition < (getChildCount() - 1))) {
                        moveToScreen(currentPosition + 1);
                    } else {
                        moveToScreen(currentPosition);
                    }

                }


                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;

            default:
                break;
        }

        return true;
    }

    private int mTouchState = TOUCH_STATE_REST;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }

        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(downX - x);
                if (xDiff > mTouchSlop) {
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                downX = x;
                mTouchState = scroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return mTouchState != TOUCH_STATE_REST;
    }

    //滑动到相应的View
    private void moveToScreen(int position) {
        if (position == getChildCount()) {
            currentPosition = 0;
            scrollBy(-getWidth() * getChildCount(), 0);
        } else if (position == -1) {
            currentPosition = getChildCount() - 1;
            scrollBy(getWidth() * getChildCount(), 0);
        } else {
            currentPosition = position;
        }
        int dx = getWidth() * currentPosition - getScrollX();
        scroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(dx) * 2);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            postInvalidate();
        }
    }
}
