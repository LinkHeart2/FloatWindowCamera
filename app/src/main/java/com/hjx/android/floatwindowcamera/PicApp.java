package com.hjx.android.floatwindowcamera;

import android.app.Application;

import com.hjx.android.floatwindowcamera.util.SpUtil;

/**
 * Created by hjx on 0013 7-13.
 * You can make it better
 */

public class PicApp extends Application {

    public static final String videoPath = "file:///android_asset/test.mp4";

    @Override
    public void onCreate() {
        super.onCreate();
        SpUtil.init(this);
    }
}
