package com.hjx.android.floatwindowcamera;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.lang.reflect.Field;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.selector.FlashSelectors;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

import static io.fotoapparat.log.Loggers.fileLogger;
import static io.fotoapparat.log.Loggers.logcat;
import static io.fotoapparat.log.Loggers.loggers;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoRedEye;
import static io.fotoapparat.parameter.selector.FlashSelectors.torch;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.back;
import static io.fotoapparat.parameter.selector.Selectors.firstAvailable;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;

/**
 * Created by hjx on 0012 7-12.
 * You can make it better
 */

public class FloatWindowSmallView extends LinearLayout {

    private static final int longClickTime = 300;
    private static final int takePicDelay = 300;

    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    private final Fotoapparat fotoapparat;
    private final CameraView cameraView;
    private final ImageView ivTakePic;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    /**
     * 记录按下小球的时间
     */
    private long pressDownTime;
    private long prePressTime;

    public FloatWindowSmallView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view = findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;

        ivTakePic = ((ImageView) findViewById(R.id.take_pic));
        setPreView();


        cameraView = ((CameraView) findViewById(R.id.camera_view));

        FlashSelectors selectors = new FlashSelectors();
        fotoapparat = Fotoapparat
                .with(getContext())
                .into(cameraView)           // view which will draw the camera preview
                .photoSize(biggestSize())   // we want to have the biggest photo possible
                .lensPosition(back())       // we want back camera
//                .focusMode(firstAvailable(  // (optional) use the first focus mode which is supported by device
//                        continuousFocus(),
//                        autoFocus(),        // in case if continuous focus is not available on device, auto focus will be used
//                        fixed()             // if even auto focus is not available - fixed focus mode will be used
//                ))
                .flash(firstAvailable(      // (optional) similar to how it is done for focus mode, this time for flash
                        autoRedEye(),
                        autoFlash(),
                        torch()
                ))
//                .frameProcessor(myFrameProcessor)   // (optional) receives each frame from preview stream
                .logger(loggers(            // (optional) we want to log camera events in 2 places at once
                        logcat(),           // ... in logcat
                        fileLogger(getContext())    // ... and to file
                ))
                .flash(selectors.off())
                .build();
//        TextView percentView = (TextView) findViewById(R.id.percent);
//        percentView.setText(MyWindowManager.getUsedPercentValue(context));
        fotoapparat.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                pressDownTime = System.currentTimeMillis();
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {

                    if(System.currentTimeMillis() - pressDownTime>longClickTime){//长按
                        openBigWindow();
                    }else{//点击
                        /**
                         * 在这里放拍照操作
                         */
                        if(pressDownTime - prePressTime>takePicDelay) {
                            final PhotoResult photoResult = fotoapparat.takePicture();

                            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), System.currentTimeMillis() + ".jpg");
                            photoResult.saveToFile(file);
//                            Toast.makeText(getContext(), "拍摄成功,图片已保存到" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
                prePressTime = pressDownTime;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params
     *            小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
    private void openBigWindow() {
        MyWindowManager.createBigWindow(getContext());
        MyWindowManager.removeSmallWindow(getContext());
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public void stopCamera(){
        fotoapparat.stop();
    }

    public void setPreView(){
        ivTakePic.setVisibility(SpUtil.getPreViewState()?GONE:VISIBLE);
    }
}
