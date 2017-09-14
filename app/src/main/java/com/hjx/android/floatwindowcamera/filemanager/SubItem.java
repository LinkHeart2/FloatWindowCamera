package com.hjx.android.floatwindowcamera.filemanager;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;


/**
 * Created by CWJ on 2017/3/22.
 */

public class SubItem extends AbstractExpandableItem<FileInfo> implements MultiItemEntity {
    public String title;

    public SubItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    @Override
    public int getLevel() {
        return ExpandableItemAdapter.HEAD;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
