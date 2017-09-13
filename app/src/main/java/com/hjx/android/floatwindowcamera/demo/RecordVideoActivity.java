package com.hjx.android.floatwindowcamera.demo;

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hjx.android.floatwindowcamera.R;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class RecordVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera.AutoFocusCallback mAutoFocusCallback;
    private final String TAG = getClass().getSimpleName();
    private SurfaceView mSurfaceview;
    private ImageButton mBtnStartStop;
    private ImageButton mBtnSet;
    private ImageButton mBtnShowFile;
    private SurfaceHolder mSurfaceHolder;
    private Camera myCamera;
    private boolean isView;
    private Camera.Parameters myParameters;
    private boolean mStartedFlg;
    private MediaRecorder mRecorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        initScreen();
        setContentView(R.layout.activity_record_video);

        mSurfaceview  = (SurfaceView)findViewById(R.id.capture_surfaceview);
        mBtnStartStop = (ImageButton) findViewById(R.id.ib_stop);
        mBtnSet= (ImageButton) findViewById(R.id.capture_imagebutton_setting);
        mBtnShowFile= (ImageButton) findViewById(R.id.capture_imagebutton_showfiles);

        SurfaceHolder holder = mSurfaceview.getHolder();// 取得holder

        holder.addCallback(this); // holder加入回调接口


        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }


    //初始化屏幕设置
    public void initScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        // 设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        initCamera();
        mBtnStartStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!mStartedFlg){
                    if(mRecorder == null){
                        mRecorder = new MediaRecorder();
                    }
                    try{
                        myCamera.unlock();
                        mRecorder.setCamera(myCamera);
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                        mRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
                        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

                        String path = getSDPath();
                        if(path!=null){
                            File dir = new File(path + "/VideoRecordTest");
                            if(!dir.exists()){
                                dir.mkdir();
                            }
                            path = dir + "/" + getDate() + ".mp4";
                            mRecorder.setOutputFile(path);
                            mRecorder.prepare();
                            mRecorder.start();
                            mStartedFlg = true;
                            mBtnStartStop.setBackground(getResources().getDrawable(R.drawable.rec_stop));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    if(mStartedFlg){
                        try {
                            mRecorder.stop();
                            mRecorder.reset();
                            mBtnStartStop.setBackground(getResources().getDrawable(R.drawable.rec_start));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        mStartedFlg = false;
                    }
                }
            }
        });
    }

    private void initCamera() {
        if(myCamera == null && !isView) {
            myCamera = Camera.open();
            Log.i(TAG, "camera.open");
        }
        if(myCamera != null && !isView){
            try{
                myParameters = myCamera.getParameters();
                myParameters.setPreviewSize(1920,1080);
                myParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                myCamera.setParameters(myParameters);
                myCamera.setPreviewDisplay(mSurfaceHolder);
                myCamera.startPreview();
                myCamera.enableShutterSound(false);
                isView = true;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(RecordVideoActivity.this, "初始化相机错误",
                        Toast.LENGTH_SHORT).show();
            }
        }
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

    /**
     * 获取SD path
     */
    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();// 获取根目录
            return sdDir.toString();
        }

        return null;
    }


    /**
     * 获取系统时间，保存文件以系统时间戳命名
     */
    public static String getDate(){
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int day = ca.get(Calendar.DATE);
        int minute = ca.get(Calendar.MINUTE);
        int hour = ca.get(Calendar.HOUR);
        int second = ca.get(Calendar.SECOND);

        String date = "" + year + (month + 1 )+ day + hour + minute + second;

        return date;
    }
}
