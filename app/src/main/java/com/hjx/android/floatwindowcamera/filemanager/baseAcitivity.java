package com.hjx.android.floatwindowcamera.filemanager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.hjx.android.floatwindowcamera.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by Administrator on 2017/9/8/008.
 */

public class baseAcitivity  extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarUtil.setTranslucentStatus(this, R.color.color_48baf3);
        setStatusBarTranslucent();
    }
    private void setStatusBarTranslucent(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.background_main);
        setContentView(R.layout.activity_main);
    }
}
