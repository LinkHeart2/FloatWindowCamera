package com.hjx.android.floatwindowcamera;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class PreviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ImageView img = new ImageView(this);
        String path = getIntent().getStringExtra("path");
        if(path != null){
            img.setImageURI(Uri.fromFile(new File(path)));
        }
        setContentView(img);
    }
}
