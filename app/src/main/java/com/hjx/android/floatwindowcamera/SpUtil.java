package com.hjx.android.floatwindowcamera;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hjx on 0013 7-13.
 * You can make it better
 */

public class SpUtil {
    private static String SP_NAME = "pic_sp";

    private static String PREVIEW_STATE = "preview_state";

    private static SharedPreferences sp;

    public static void init(Context context){
        if(sp == null){
            sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        }
    }

    public static void putPreViewState(boolean isShow){
        sp.edit().putBoolean(PREVIEW_STATE,isShow).commit();
    }

    public static boolean getPreViewState(){
        return sp.getBoolean(PREVIEW_STATE,false);
    }


}
