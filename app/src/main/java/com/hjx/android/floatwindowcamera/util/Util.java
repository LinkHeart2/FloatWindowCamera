package com.hjx.android.floatwindowcamera.util;

import android.os.Environment;

import java.io.File;
import java.util.Calendar;

/**
 * Created by hjx on 0013 9-13.
 * You can make it better
 */

public class Util {
    /**
     * 获取SD path
     */
    public static String getSDPath(){
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
