package com.hjx.android.floatwindowcamera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class MainActivity extends Activity {

    private String TAG = getClass().getSimpleName();
    private static final int REQUEST_PERMISSION_WINDOW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startFloatWindow = (Button) findViewById(R.id.start_float_window);
        startFloatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                if (!(checkPermission(Manifest.permission.SYSTEM_ALERT_WINDOW))) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, REQUEST_PERMISSION_WINDOW);
//                } else {
                    showFloatWindow();
//                }
            }
        });
        Button btnTakePic = (Button) findViewById(R.id.btn_to_take_pic);
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TakePicActivity.class);
                startActivity(intent);
            }
        });
        Button btnThreePic = (Button) findViewById(R.id.btn_to_three_pic);
        btnThreePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TakePicThreeActivity.class);
                startActivity(intent);
            }
        });

        doCheckPermission();
    }

    private void showFloatWindow() {
        Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
        startService(intent);
        finish();
    }

    private void doCheckPermission() {
        List<PermissionItem> list = new ArrayList<>();
        list.add(new PermissionItem(Manifest.permission.CAMERA, "照相机", R.drawable.permission_ic_camera));
        list.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE,"存储",R.drawable.permission_ic_storage));
        HiPermission.create(this)
                .permissions(list)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        Log.i(TAG, "onClose");
//                        showToast("用户关闭权限申请");
                    }

                    @Override
                    public void onFinish() {
//                        showToast("所有权限申请完成");
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                        Log.i(TAG, "onDeny");
                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                        Log.i(TAG, "onGuarantee");
                    }
                });
        
    }

    private void showToast(String str) {
        Toast.makeText(this, str , Toast.LENGTH_SHORT).show();
    }

    public boolean checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WINDOW) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFloatWindow();
            } else {
                Toast.makeText(this, "请到", Toast.LENGTH_SHORT).show();
            }
        }
    }

}