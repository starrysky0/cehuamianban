package com.example.administrator.cehuamianban.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/11/24.
 */

public class MyLinearLayout extends LinearLayout {

    private DragDayout dragDayout1;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //如果当前状态不是关闭状态,就不往下传递了
        if (dragDayout1 != null && dragDayout1.getStatus() != DragDayout.Status.Close) {
            return true;
        } else {
            //如果是关闭状态就按原来的事件处理
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dragDayout1 != null && dragDayout1.getStatus() != DragDayout.Status.Close) {
            //如果手指抬起,执行关闭动画
            if(event.getAction() == MotionEvent.ACTION_UP){
                dragDayout1.close(true);
            }
            return true;
        } else {

            return super.onTouchEvent(event);
        }
    }

    public void setDragLayout(DragDayout dragDayout) {
        this.dragDayout1 = dragDayout;
    }
}
