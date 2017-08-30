package com.hjx.android.floatwindowcamera;


import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TakePicActivity extends Activity {

    private SurfaceView mViewSurface;
    private ImageView btnTakePic;
    private Camera mCamera = null;

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback(){

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopPreview();
        }
    };
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);
        mViewSurface = ((SurfaceView) findViewById(R.id.view_surface));
        mViewSurface.getHolder().addCallback(surfaceCallback);


        btnTakePic = ((ImageView) findViewById(R.id.take_pic));
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        String path = "";
                        if ((path = saveFile(data)) != null) {
                            Log.d(TAG, "onPictureTaken: "+path);
                            Intent it = new Intent(TakePicActivity.this, PreviewActivity.class);
                            it.putExtra("path", path);
                            startActivity(it);
                        } else {
                            Toast.makeText(TakePicActivity.this, "保存照片失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //保存临时文件的方法
    private String saveFile(byte[] bytes){
        try {
            File file = File.createTempFile("img","");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    private void startPreview(){
        mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(mViewSurface.getHolder());
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPreview(){
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}
