package com.example.music_test.Interfaces;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.music_test.MainPlayer;

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

    public int isMusic(String musicPath) {// TODO 判断是否为音乐
        String path = null;
        Cursor cursor = myContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );

        int album_id = -1;
        if (cursor.moveToFirst()) {
            do {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                if (path.equals(musicPath)) {
                    album_id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));// TODO 返回album_id便于查找
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return album_id;
    }

    public String getAlbumArt(int album_id) {// 用于查找专辑封面
        String uriAlbum = "content://media/external/audio/albums";// TODO
        String[] proj = new String[]{"album_art"};
        Cursor cursor = myContext.getContentResolver().query(
                Uri.parse(uriAlbum + "/" + Integer.toString(album_id)),
                proj, null, null, null
        );
        String albumArt = null;
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            albumArt = cursor.getString(0);
        }
        cursor.close();
        return albumArt;
    }

    public Bitmap LoadThumb(final String musicPath, final int width, final int height) {// 加载缩略图
        int album_id = isMusic(musicPath);
        MainPlayer.infoLog(musicPath + " id: " + album_id);
        if (album_id == -1) {// 不是音乐
            return null;
        }

        String albumArt = getAlbumArt(album_id);
        if (albumArt == null) {
            return null;
        } else {
            return BitmapFactory.decodeFile(albumArt);
        }
    }
}
