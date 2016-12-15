package com.jone.jinux.tuwen;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by Jinux on 16/12/14.
 */

public class JOneCanvas extends FrameLayout {

    private ViewDragHelper mDragHelper;

    private LinkedList<DecorComponent> mDecors = new LinkedList<>();

    public JOneCanvas(Context context) {
        super(context);
        init();
    }

    public JOneCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JOneCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper
                .Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return DecorComponent.FLAG_DECOR.equals(child.getTag());
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {

                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }
        });
    }

    private void initView() {
        Context context = getContext();

        setDrawingCacheEnabled(true);

        // 图片
        ImageView pic = new ImageView(context);
        addView(pic, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        // 装饰
        TextView textView = new TextView(context);
        textView.setText("hello world");
        textView.setTextSize(20);

        mDecors.add(new DecorComponent(textView));
        for (DecorComponent component : mDecors) {
            addView(component.view);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }
}
