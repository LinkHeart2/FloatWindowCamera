package com.hjx.android.floatwindowcamera.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * Created by hjx on 0013 9-13.
 * You can make it better
 */

public class NoFocusSurfaceView extends SurfaceView {
    public NoFocusSurfaceView(Context context) {
        super(context);
    }

    public NoFocusSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoFocusSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
