package com.hjx.android.floatwindowcamera.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by hjx on 0013 9-13.
 * You can make it better
 */

public class UiUtil {
    public static int dpToPx(Context context,float dp){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }
}
