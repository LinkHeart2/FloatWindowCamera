package com.hjx.android.floatwindowcamera.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.hjx.android.floatwindowcamera.R;
import com.hjx.android.floatwindowcamera.widget.VideoTexttureView;

import static com.hjx.android.floatwindowcamera.PicApp.videoPath;

public class TexttureTestActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoTexttureView vtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textture_test);
        vtv = ((VideoTexttureView) findViewById(R.id.vtv));
        vtv.setVideoPath(videoPath);
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.mipmap.icon_play);
        vtv.setIvTip(iv);
        vtv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.vtv:
                vtv.startMediaPlayer();
            break;
            default:
            break;
            }
    }
}
