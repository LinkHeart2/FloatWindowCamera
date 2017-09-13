package com.hjx.android.floatwindowcamera.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;

import com.hjx.android.floatwindowcamera.PicApp;
import com.hjx.android.floatwindowcamera.R;
import com.hjx.android.floatwindowcamera.util.ToastUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by hjx on 0031 8-31.
 * You can make it better
 */

public class VideoTexttureView extends TextureView implements TextureView.SurfaceTextureListener {
    private MediaPlayer mediaPlayer;
    private Surface surface;
    private ImageView ivTip;
    private boolean isPlaying;
    private boolean isSurfaceTextureAvailable;
    private String videoPath;

    private boolean isCreateInit;


    public VideoTexttureView(Context context) {
        this(context, null);
    }

    public VideoTexttureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTexttureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        surface = new Surface(surfaceTexture);
        isSurfaceTextureAvailable = true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        surface = null;
        onVideoTextureViewDestroy();
        return true;
    }


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    public void startMediaPlayer() {
        if (isPlaying || !isSurfaceTextureAvailable)
            return;
        if (TextUtils.isEmpty(videoPath)) {
            ToastUtils.showShortToastSafe(getContext(), "视频路径异常");
            return;
        }
        try {

            if (videoPath.equals(PicApp.videoPath)) {
                mediaPlayer = MediaPlayer.create(getContext(), R.raw.test);
                isCreateInit = true;
            } else {
                File file = new File(videoPath);
                if (!file.exists()) {
                    ToastUtils.showShortToastSafe(getContext(), "视频不存在");
                    return;
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(file.getAbsolutePath());
                isCreateInit = false;
            }
            mediaPlayer.setSurface(surface);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(0,0);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    showIvTip(false);
                    isPlaying = true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMediaPlayer();
                }
            });
            if(isCreateInit){
                mediaPlayer.start();
                showIvTip(false);
                isPlaying = true;
            }else {
                mediaPlayer.prepare();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void stopMediaPlayer() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        showIvTip(true);
        isPlaying = false;
    }

    public void showIvTip(boolean show) {
        if (ivTip != null) {
            ivTip.setVisibility(show ? VISIBLE : GONE);
        }
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIvTip(ImageView ivTip) {
        this.ivTip = ivTip;
    }

    private void onVideoTextureViewDestroy() {
        isPlaying = false;
        if (mediaPlayer != null) {
            stopMediaPlayer();
            mediaPlayer.release();
        }
    }
}
