package com.hjx.android.floatwindowcamera.filemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjx.android.floatwindowcamera.R;

import java.util.List;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by CWJ on 2017/3/20.
 */

public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder> {
    private Context context;

    public MultipleItemQuickAdapter(Context context,List<MultipleItem> data) {
        super(data);
        this.context = context;
        addItemType(MultipleItem.FOLD, R.layout.item_fold);
        addItemType(MultipleItem.FILE, R.layout.item_file);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultipleItem item) {
        helper.setText(R.id.tv_file_name, item.getData().getFileName());
        if (item.getItemType() == MultipleItem.FOLD) {
            Glide.with(mContext).load(R.drawable.rc_ad_list_folder_icon).fitCenter().into((ImageView) helper.getView(R.id.iv_file));
        } else {
            helper.setText(R.id.tv_file_size, FileUtil.FormetFileSize(item.getData().getFileSize()));
            helper.setText(R.id.tv_file_time, item.getData().getTime());
//            if (item.getData().getIsCheck()) {
//                ((CheckBox) helper.getView(R.id.cb_file)).setChecked(true, false);
//            } else {
//                ((CheckBox) helper.getView(R.id.cb_file)).setChecked(false, false);
//            }
            if (getFileTypeImage(item.getData().getFileName())){//为true即是图片文件
                Glide.with(mContext).load(item.getData().getFilePath()).fitCenter().into((ImageView) helper.getView(R.id.iv_file));
            }else if (getFileTypeVideo(item.getData().getFileName())){
                Bitmap thunBitmap = getVideoThunBitmap(item.getData().getFilePath());
                ImageView iv = (ImageView) helper.getView(R.id.iv_file);
                iv.setImageBitmap(thunBitmap);
            }else if (getFileTypeText(item.getData().getFileName())){
                Glide.with(mContext).load(R.drawable.rc_file_icon_file).fitCenter().into((ImageView) helper.getView(R.id.iv_file));
            }else {
                Glide.with(mContext).load(FileUtil.getFileTypeImageId(mContext, item.getData().getFileName())).fitCenter().into((ImageView) helper.getView(R.id.iv_file));
            }
//            Glide.with(mContext).load(FileUtil.getFileTypeImageId(mContext, item.getData().getFileName())).fitCenter().into((ImageView) helper.getView(R.id.iv_file));
        }
    }

    private Bitmap getVideoThunBitmap(String filePath){
        Bitmap bitmap = null;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap=retriever.getFrameAtTime();
//            int width=(int)(imageheight*bitmap.getWidth()/bitmap.getHeight());
            bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, (int)(100*metrics.density), (int)(100*metrics.density), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//            videoshots.add(bitmap);
//            Log.e("screen",videoshots.size()+"");
            return bitmap;
        }
        catch(IllegalArgumentException e) {
//            Log.e("screen",e.toString());
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            Drawable d=mContext.getResources().getDrawable(R.drawable.rc_ad_list_video_icon);
            BitmapDrawable bd = (BitmapDrawable) d;
            Bitmap bm = bd.getBitmap();
//            Log.e("screen",e.toString());
            e.printStackTrace();
            return bm;

        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public static boolean getFileTypeText( String fileName) {//文本文件
        if (checkSuffix(fileName, new String[]{"txt", "doc", "xls", "htm", "docx", "pdf"})) {
            return true;
        }else {
            return false;
        }
    }
    public static boolean getFileTypeVideo( String fileName) {//视频文件
        if (checkSuffix(fileName, new String[]{"wmv", "rmvb", "avi", "mp4", "3gp", "mkv"})) {
            return true;
        }else {
            return false;
        }
    }
    public static boolean getFileTypeImage( String fileName) {//图片文件
        if (checkSuffix(fileName, new String[]{"jpg","jpeg","gif","png","bmp"})) {
            return true;
        }else {
            return false;
        }
//        else if (checkSuffix(fileName, new String[]{"wmv", "rmvb", "avi", "mp4"})) {
//            id = R.drawable.rc_ad_list_video_icon;
//        } else if (checkSuffix(fileName, new String[]{"wav", "aac", "amr"})) {
//            id = R.drawable.rc_ad_list_video_icon;
//        }
//        if (checkSuffix(fileName, mContext.getResources().getStringArray(R.array.rc_file_file_suffix)))
//            id = R.drawable.rc_ad_list_file_icon;
//        else if (checkSuffix(fileName, mContext.getResources().getStringArray(R.array.rc_video_file_suffix)))
//            id = R.drawable.rc_ad_list_video_icon;
//        else if (checkSuffix(fileName, mContext.getResources().getStringArray(R.array.rc_audio_file_suffix)))
//            id = R.drawable.rc_ad_list_audio_icon;
    }

    public static boolean checkSuffix(String fileName,
                                      String[] fileSuffix) {
        for (String suffix : fileSuffix) {
            if (fileName != null) {
                if (fileName.toLowerCase().endsWith(suffix)) {
                    return true;
                }
            }
        }
        return false;
    }


}
