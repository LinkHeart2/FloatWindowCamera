package com.hjx.android.floatwindowcamera;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hjx.android.floatwindowcamera.util.SpUtil;
import com.hjx.android.floatwindowcamera.util.UiUtil;
import com.hjx.android.floatwindowcamera.util.Util;

import java.io.File;
import java.io.IOException;
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

public class FloatWindowSmallView extends LinearLayout implements SurfaceHolder.Callback {

    private static final int longClickTime = 300;
    private static final int takePicDelay = 300;
    private static final String TAG = "FloatWindowSmallView";

    private boolean mode;

    private SurfaceView mSurfaceview;
    private SurfaceHolder mSurfaceHolder;
    private Camera myCamera;
    private boolean isView;
    private Camera.Parameters myParameters;
    private boolean mStartedFlg;
    private MediaRecorder mRecorder;
    private Camera.AutoFocusCallback mAutoFocusCallback;


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

    private Fotoapparat fotoapparat;
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
    private String path_video;

    public FloatWindowSmallView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view = findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;

        ivTakePic = ((ImageView) findViewById(R.id.take_pic));

        cameraView = ((CameraView) findViewById(R.id.camera_view));

        mSurfaceview = (SurfaceView) findViewById(R.id.capture_surfaceview);

        setPreView();

        setMode();




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
                            if(mode) {//拍照
                                final PhotoResult photoResult = fotoapparat.takePicture();
//                                File dir = new File(Util.getSavePath() + "/MTPhoto");
                                File dir = new File(SpUtil.PIC_PATH);
                                if(!dir.exists()){
                                    dir.mkdir();
                                }
                                final File file = new File( dir , Util.getDate() + ".jpg");
                                photoResult.saveToFile(file);
                            Toast.makeText(getContext(), "拍摄成功,图片已保存到" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                            }else{//录像
                                if(!mStartedFlg){////true时表示正在录像，false表示已经停止了录像
                                    if(mRecorder == null){
                                        mRecorder = new MediaRecorder();
                                    }
                                    try{
                                        myCamera.unlock();
                                        mRecorder.setCamera(myCamera);
                                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                                        mRecorder.setOrientationHint(90);

                                        //设置video的编码格式
//                                        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                                        //设置录制的视频编码比特率
                                        mRecorder.setVideoEncodingBitRate(1024 * 1024);
                                        //设置录制的视频帧率,注意文档的说明:
                                        mRecorder.setVideoFrameRate(30);
                                        //设置要捕获的视频的宽度和高度
                                        mSurfaceHolder.setFixedSize(640, 480);//最高只能设置640x480
                                        mRecorder.setVideoSize(640, 480);//最高只能设置640x480
                                        mRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
                                        if(mSurfaceHolder == null)return false;
                                        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

                                        path_video = Util.getSavePath();
                                        if(path_video !=null){
//                                            File dir = new File(path + "/MTVideo");
                                            File dir = new File(SpUtil.VIDEO_PATH);
                                            if(!dir.exists()){
                                                dir.mkdir();
                                            }
                                            path_video = dir + "/" + Util.getDate() + ".mp4";
                                            mRecorder.setOutputFile(path_video);
                                            mRecorder.prepare();
                                            mRecorder.start();
                                            mStartedFlg = true;//true时表示正在录像，false表示已经停止了录像
                                            ivTakePic.setImageDrawable(getResources().getDrawable(R.drawable.rec_stop));
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    if(mStartedFlg){//true时表示正在录像，false表示已经停止了录像
                                        try {
                                            mRecorder.stop();
                                            mRecorder.reset();
                                            ivTakePic.setImageDrawable(getResources().getDrawable(R.drawable.rec_start));
                                            if (path_video != null){
                                                Toast.makeText(getContext(), "录像成功,已保存到" + path_video, Toast.LENGTH_SHORT).show();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        mStartedFlg = false;
                                    }
                                }
                            }

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
        if(fotoapparat!=null)
            fotoapparat.stop();
        if(mStartedFlg){
            try {
                mRecorder.stop();
                mRecorder.reset();
                ivTakePic.setImageDrawable(getResources().getDrawable(R.drawable.rec_start));
            }catch (Exception e){
                e.printStackTrace();
            }
            mStartedFlg = false;
        }
        if(mSurfaceview!=null){
            mSurfaceview.setVisibility(GONE);
        }

    }

    public void setPreView(){
        ivTakePic.setVisibility(SpUtil.getPreViewState()?GONE:VISIBLE);
    }

    public void setMode(){
        mode = SpUtil.getMode();//true为拍照。。false为录像
        cameraView.setVisibility(mode?VISIBLE:GONE);
        mSurfaceview.setVisibility(mode?GONE:VISIBLE);
        ivTakePic.setVisibility(mode?SpUtil.getPreViewState()?GONE:VISIBLE:VISIBLE);
        ivTakePic.setImageDrawable(mode?getResources().getDrawable(R.drawable.take_shoot):getResources().getDrawable(R.drawable.rec_start));
        if(mode){//true为拍照。。false为录像
            initTakePic();
        }else{
            initRecordVideo();
        }
    }

    private void initRecordVideo() {


        mSurfaceview.setVisibility(VISIBLE);

        //重写AutoFocusCallback接口
        mAutoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    Log.i(TAG, "AutoFocus: success...");
                } else {
                    Log.i(TAG, "AutoFocus: failure...");
                }
            }
        };

        SurfaceHolder holder = mSurfaceview.getHolder();// 取得holder

        holder.addCallback(this); // holder加入回调接口


        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initTakePic(){
        if(fotoapparat==null) {
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
        }
//        TextView percentView = (TextView) findViewById(R.id.percent);
//        percentView.setText(MyWindowManager.getUsedPercentValue(context));
        fotoapparat.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        initCamera();
//        ivTakePic.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceview = null;
        mSurfaceHolder = null;
        if(mRecorder != null){
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void initCamera() {
        if(myCamera == null && !isView) {
            myCamera = Camera.open();
            Log.i(TAG, "camera.open");
        }
        if(myCamera != null && !isView){
            try{
                myParameters = myCamera.getParameters();
                int dp48 = UiUtil.dpToPx(getContext(), 48);
                myCamera.setDisplayOrientation(90);
                myParameters.setPreviewSize(1920,1080);
                myParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                myCamera.setParameters(myParameters);
                myCamera.setPreviewDisplay(mSurfaceHolder);
                myCamera.startPreview();
                isView = true;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "初始化相机错误",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
