package com.hjx.android.floatwindowcamera.filemanager;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/8/26/026.
 */

public class ListImgsSerializable implements Serializable {
    private List<String > stringList;

    public ListImgsSerializable() {
    }

    public ListImgsSerializable(List<String> stringList) {
        this.stringList = stringList;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }
}
