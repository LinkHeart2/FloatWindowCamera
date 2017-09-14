package com.hjx.android.floatwindowcamera;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hjx.android.floatwindowcamera.filemanager.SDCardActivity;
import com.hjx.android.floatwindowcamera.util.SpUtil;

import java.io.File;

/**
 * Created by hjx on 0012 7-12.
 * You can make it better
 */

public class FloatWindowBigView extends LinearLayout {

    /**
     * 记录大悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;

    private Context context;
    public FloatWindowBigView(final Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        Button close = (Button) findViewById(R.id.close);
        Button back = (Button) findViewById(R.id.back);
        Button open_img_dir = (Button) findViewById(R.id.open_img_dir);
        Button open_video_dir = (Button) findViewById(R.id.open_video_dir);
        Button showPreview = (Button) findViewById(R.id.show_preview);
        Button changeMode = (Button) findViewById(R.id.change_mode);
        open_img_dir.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //调转到图片存放的文件夹
//                openAssignFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
                String path = SpUtil.PIC_PATH;
                openOppointDir(path);
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
        open_video_dir.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //调转到视频存放的文件夹
//                openAssignFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
                String path = SpUtil.VIDEO_PATH;
                openOppointDir(path);
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.removeSmallWindow(context);
                Intent intent = new Intent(getContext(), FloatWindowService.class);
                context.stopService(intent);
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
        showPreview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开预览功能
                SpUtil.putPreViewState(!SpUtil.getPreViewState());
                MyWindowManager.setPreView();
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
        changeMode.setText(SpUtil.getMode()?"切换为录视频":"切换为拍照");
        changeMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //在拍照和录视频间切换
                SpUtil.putMode(!SpUtil.getMode());
                MyWindowManager.setMode();
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
    }

    private void openOppointDir(String path){
        Intent intent = new Intent(context, SDCardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("path",path);//Environment.getDataDirectory().getParentFile().getAbsolutePath(),本地手机根目录路径
        bundle.putString("name", "SD卡");
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
    private void openAssignFolder(String path){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, path);
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        intent.setDataAndType(uri, "file/*");
        try {
//            getContext().startActivity(intent);
            getContext().startActivity(Intent.createChooser(intent,"选择浏览工具"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
