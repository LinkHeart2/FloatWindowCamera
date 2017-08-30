package com.hjx.android.floatwindowcamera;

import android.app.Application;

/**
 * Created by hjx on 0013 7-13.
 * You can make it better
 */

public class PicApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SpUtil.init(this);
    }
}
