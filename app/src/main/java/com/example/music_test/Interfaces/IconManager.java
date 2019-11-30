package com.example.music_test.Interfaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

public class IconManager {
    public Context myContext;
    public MediaPlayer player;

    public IconManager(Context context) {// TODO 初始化
        this.myContext = context;
        this.player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置为音频
    }

    public boolean isImg(String imgPath) {// 判断是否是图片 TODO
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// TODO
        bitmap = BitmapFactory.decodeFile(imgPath, options);
        if (options.outHeight == 0 || options.outWidth == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isMusic(String musicPath) {// TODO 判断是否为音乐
        File tmp = new File(musicPath);
        if (tmp.exists() == false) return false;

        try {
            player.setDataSource(musicPath);// TODO 异常
        } catch (IOException e) {
            return false;
        }

        try {
            player.prepareAsync();
        } catch (IllegalStateException e) {
            return false;
        }

        return true;
    }

    public Bitmap LoadThumb(final String imgPath, final int width, final int height) {// 加载缩略图
        if (isMusic(imgPath) == false) {// TODO 判断是否为音乐
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// TODO 此时decode的bitmap为null
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
        options.inJustDecodeBounds = false;// TODO

        // 缩放
        int h_rate = options.outHeight / height;
        int w_rate = options.outWidth / width;
        int rate = 1;
        if (h_rate < w_rate) {
            rate = h_rate;
        } else {
            rate = w_rate;
        }
        options.inSampleSize = rate;
        bitmap = BitmapFactory.decodeFile(imgPath, options);

        return bitmap;// TODO
    }
}
