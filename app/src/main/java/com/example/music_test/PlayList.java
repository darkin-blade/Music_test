package com.example.music_test;

import android.database.Cursor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlayList {
    // 每次加载必须要恢复的数据
    String curMix;// 当前歌单
    String curMusic;// TODO 当前播放的歌曲
    int curMusicIndex;
    int curMixLen;
    ArrayList<String> curMusicList;// 当前歌单的所有歌曲
    int playMode;// 播放模式

    int is_start = 0;// 是否启动播放器

    static final int CIRCULATE = 0,// 顺序播放
            RANDOM = 1,// 随机
            SINGLE = 2,// 单曲循环
            AVERAGE = 3,// 平均
            POLARIZATION = 4;

    public PlayList() {
        curMusicList = new ArrayList<String>();
    }

    public void recover() {// 每次启动app时进行数据恢复
        // TODO
    }

    public void loadList(String nextMix, String nextMusic) {// 加载专辑曲目
        curMusicIndex = curMusicList.indexOf(curMusic);// 获取当前播放的音乐的索引

        if (nextMix == curMix) {
            if (nextMusic == null) {// 处理音乐不存在的异常情况
                nextMusic = curMusicList.get(0);
            }

            if (nextMusic != curMusic) {
                changeMusic(nextMusic);
            }
            return;
        }

        // 清空
        curMusicList.clear();
        curMixLen = 0;

        Cursor cursor = MainPlayer.database.query(
                curMix,// 当前歌单
                new String[]{"path", "name", "count"},
                null,
                null,
                null,
                null,
                "name");

        if (cursor.moveToFirst()) {// 非空
            do {
                String music_name = cursor.getString(1);// 获取歌名
                curMusicList.add(music_name);
                curMixLen ++;
                MainPlayer.infoLog("add to play list: " + music_name);
            } while (cursor.moveToNext());
        }
    }

    public void startMusic(String nextMusic) {// TODO 第一次播放音乐
        ;
    }

    public void changeMusic(String nextMusic) {// 切换到特定歌曲
        try {
            File tmp = new File(nextMusic);
            if (tmp.exists()) {// 如果文件存在
                MainPlayer.player.setDataSource(nextMusic);// TODO 异常
                MainPlayer.player.prepareAsync();// TODO 异常
                // TODO 启动播放
                MainPlayer.player.start();
            } else {// TODO 歌曲不存在
                MainPlayer.musicDelete(nextMusic, curMix);// 从歌单中删除不存在的歌曲
                loadList(curMix, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
