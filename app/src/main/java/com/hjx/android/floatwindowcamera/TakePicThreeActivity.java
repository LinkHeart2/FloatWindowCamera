package com.hjx.android.floatwindowcamera;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import io.fotoapparat.Fotoapparat;
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

public class TakePicThreeActivity extends AppCompatActivity {

    private ImageView btnTakePic;
    private Fotoapparat fotoapparat;
    private CameraView cameraView;

    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic_three);
        btnTakePic = ((ImageView) findViewById(R.id.take_pic));
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTakePic.setEnabled(false);
                final PhotoResult photoResult = fotoapparat.takePicture();


                new Thread(){
                    @Override
                    public void run() {
                        // Asynchronously saves photo to file
                        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), System.currentTimeMillis() + ".jpg");
                        photoResult.saveToFile(file);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                btnTakePic.setEnabled(true);
                                Toast.makeText(TakePicThreeActivity.this, "拍摄成功,图片已保存到" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }.start();

//        // Asynchronously converts photo to bitmap and returns result on main thread
//                photoResult
//                        .toBitmap()
//                        .whenAvailable(new PendingResult.Callback<BitmapPhoto>() {
//                            @Override
//                            public void onResult(BitmapPhoto result) {
//                                ImageView imageView = (ImageView) findViewById(R.id.result);
//
//                                imageView.setImageBitmap(result.bitmap);
//                                imageView.setRotation(-result.rotationDegrees);
//                            }
//                        });



            }
        });
        cameraView = ((CameraView) findViewById(R.id.camera_view));

        fotoapparat = Fotoapparat
                .with(this)
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
                        fileLogger(this)    // ... and to file
                ))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fotoapparat.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fotoapparat.stop();
    }
}
