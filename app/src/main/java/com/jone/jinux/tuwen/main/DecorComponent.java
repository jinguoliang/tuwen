package com.jone.jinux.tuwen.main;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Jinux on 16/12/14.
 */

public class DecorComponent {

    public static final String FLAG_DECOR = "decor";
    View view;

    public DecorComponent(View v) {
        view = v;
        if (v.getLayoutParams() == null) {
            setDefaultLayoutParams(v);
        }
        v.setTag(FLAG_DECOR);
    }

    private void setDefaultLayoutParams(View v) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        v.setLayoutParams(params);
    }
}
