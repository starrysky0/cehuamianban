package com.example.administrator.cehuamianban.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * 侧滑面板
 * Created by Administrator on 2016/11/24.
 */

public class DragDayout extends FrameLayout {

    private static final String TAG = "DragDayout";
    private ViewDragHelper mHelper;
    private ViewGroup mLeftContent;
    private ViewGroup mMainContent;
    private int mMeasuredHeight;
    private int mMeasuredWidth;

    private int mRange;


    private Status status = Status.Close;
    private OnDragDayoutListener mOnDragDayoutListener;
    public static enum Status{
        Close,Open,Draging;

    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
    public interface OnDragDayoutListener{
        void onClose();
        void onOpen();
        void onDraging(float percent);
    }
    public void setOnDragDayoutListener(OnDragDayoutListener onDragDayoutListener) {
        mOnDragDayoutListener = onDragDayoutListener;
    }

    public OnDragDayoutListener getOnDragDayoutListener() {
        return mOnDragDayoutListener;
    }

    public DragDayout(Context context) {
        this(context, null);
    }
    public DragDayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragDayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //forParent 父类的容器
        //sensitivity 敏感度,越大越敏感,1.0f是默认值
        //Callback 事件回调
        //1.创建ViewDragHelper辅助类
        mHelper = ViewDragHelper.create(this, 1.0f, mCallback);
        mHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        /*
        返回值,决定child是否可以被拖拽
        pointerId 多点触摸手指ID
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mLeftContent;
        }

        //返回拖拽的范围,返回一个大于0 的值,决定了动画的时长,水平方向是否可以滑开
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        /**
         * 修正子View水平方向的位置,还没有真正发生移动
         * @param child 被拖拽的子view
         * @param left 建议移动到的位置
         * @param dx 跟旧位置的差值
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int oldLeft = mMainContent.getLeft();
            if (child == mMainContent) {
                Integer x = fixLeft(left);
                if (x != null) return x;
            }
            return left;
        }

        //当控件位置发生变化时调用,伴随动画,状态的更新,事件的回调,
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //left最新的位置
            //dx刚刚发生的变化
            if (changedView == mLeftContent) {
                //如果移动的是左面板
                //放回原来的位置
               mLeftContent.layout(0, 0, 0 + mMeasuredWidth, 0 + mMeasuredHeight);
                //把发生变化的量传递给主面板
                int newLeft = mMainContent.getLeft() + dx;
                newLeft = fixLeft(newLeft);
                mMainContent.layout(newLeft, 0, newLeft + mMeasuredWidth, 0 + mMeasuredHeight);
            }
            dispatchDragEvent();
            //为了兼容低版本,手动重绘界面的所有内容
            invalidate();
        }
                @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mHelper.captureChildView(mMainContent, pointerId);
        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //releasedChild 被释放的孩子
            //xvel 水平方向的速度向右为+向左为-
            if (xvel == 0 && mMainContent.getLeft() > mRange * 0.5f) {
                open();
            } else if (xvel > 0) {
                open();
            } else {
                close();
            }
        }


        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
        }
    };

    /**
     * 分发拖拽事件,伴随动画,更新状态
     */
    private void dispatchDragEvent() {
        //获取变化值
        float percent = mMainContent.getLeft() * 1.0f / mRange;
        if(mOnDragDayoutListener!=null){
            mOnDragDayoutListener.onDraging(percent);
        }
        //更新状态
        Status lastStaus = status;
       status =  updateStatus(percent);
        if(lastStaus!=status&&mOnDragDayoutListener!=null){
            if(status == Status.Close){
                mOnDragDayoutListener.onClose();
            }else if(status == Status.Open){
                mOnDragDayoutListener.onOpen();
            }
        }

        //执行动画
        animView(percent);

    }

    private Status updateStatus(float percent) {
        if(percent == 0){
            return Status.Close;
        }else if(percent == 1){
            return Status.Open;
        }
        return Status.Draging;
    }

    private void animView(float percent) {
        //缩放动画0.5----1.0
//        mLeftContent.setScaleX(percent * 0.5f + 0.5f);
//        mLeftContent.setScaleY(percent * 0.5f + 0.5f);
//        ViewHelper.setScaleX(mLeftContent,percent * 0.5f + 0.5f);
//        ViewHelper.setScaleY(mLeftContent,percent*0.5f+0.5f);
        //平移动画
     //   ViewHelper.setTranslationX(mLeftContent,evaluate(percent,-mMeasuredWidth/2.0f,0));
        //透明度动画
        ViewHelper.setAlpha(mLeftContent,evaluate(percent,0.2f,1f));
        //缩放动画1.0---0.8
        ViewHelper.setScaleX(mMainContent,evaluate(percent,1.0f,0.8f));
        ViewHelper.setScaleY(mMainContent,evaluate(percent,1.0f,0.8f));
        //背景亮度变化
        getBackground().setColorFilter((Integer) evaluateColor(percent, Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    public Float evaluate(float fraction,Number startValue,Number endValue){
        float startFloat = startValue.floatValue();
        return startFloat + fraction*(endValue.floatValue()-startFloat);
    }


    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }
    private void close() {
        close(true);
    }

    public void close(boolean isSmooth) {
        int finalLeft = 0;
        if (isSmooth) {
            if (mHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mMeasuredWidth, 0 + mMeasuredHeight);
        }
    }

    private void open() {
        open(true);
    }

    public void open(boolean isSmooth) {
        int finalLeft = mRange;
        if (isSmooth) {
            //走平滑动画
            //1.触发一个平滑动画
            if (mHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
                //需要重绘界面,一定要传子View所在的容器
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mMeasuredWidth, 0 + mMeasuredHeight);
        }
    }

    //2维持动画的继续,高频率调用
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    //修正坐标值
    private int fixLeft(int left) {
        if (left < 0) {
            return 0;
        } else if (left > mRange) {
            return mRange;
        }
        return left;
    }

    //2.转交触摸事件

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //由ViewDragHelper 判断触摸事件是否该拦截

        return mHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //由ViewDragHelper 处理事件
        try {
            mHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 默认宽高是0,0
     * 当控件尺寸发生变化的时候调用
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasuredHeight = getMeasuredHeight();
        mMeasuredWidth = getMeasuredWidth();
        //拖拽范围
        mRange = (int) (mMeasuredWidth * 0.6f);
        Log.e(TAG, "onSizeChanged: mMeasuredHeight" + mMeasuredHeight + "mMeasuredWidth" + mMeasuredWidth);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //代码的健壮性
        //孩子至少两个
        if (getChildCount() < 2) {
            throw new IllegalStateException("子View至少两个");
        }
        if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalStateException("子View必须是ViewGroup的子类");
        }
        mLeftContent = (ViewGroup) getChildAt(0);
        mMainContent = (ViewGroup) getChildAt(1);
    }
}
