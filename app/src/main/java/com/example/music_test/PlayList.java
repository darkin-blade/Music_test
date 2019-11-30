package com.example.music_test;

import android.database.Cursor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlayList {
    // 每次加载必须要恢复的数据
    String curMix;// 当前歌单
    String curMusic;// TODO 当前播放的歌曲
    int playMode;// 播放模式

    ArrayList<String> curMusicList;// 当前歌单的所有歌曲
    int curMusicIndex;
    int curMixLen;
    int is_start;// TODO 是否已经启动播放器

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

    public void loadList(String nextMix, String nextMusic) {// 加载专辑曲目,并播放特定歌曲
        if (nextMix == curMix && nextMusic == curMusic) {// 相同的歌曲和专辑
            return;
        }

        if (curMix != nextMix) {
            curMix = nextMix;

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
            } else {
                stopMusic();
            }
        }

        curMusic = nextMusic;
        curMusicIndex = curMusicList.indexOf(curMusic.replaceAll(".*/+", ""));// 获取当前播放的音乐的索引

        MainPlayer.infoLog("[" + curMix + "][" + curMusicIndex + "/" + curMixLen + "][" + curMusic + "]");
        changeMusic(curMusic, 0);
    }

    public void stopMusic() {// TODO 异常处理
        MainPlayer.infoLog("player error");
    }

    public void startMusic(String nextMusic) {// TODO 第一次播放音乐
        ;
    }

    public void changeMusic(String nextMusic, int mode) {
        // mode: 0: 指定跳转, 1: 向后跳转, 2: 向前跳转, 3: 异常情况下的跳转 TODO
        // TODO 播放模式
        try {
            if (mode == 1) {// 往后播放
                nextMusic = curMusicList.get((curMusicIndex + 1) % curMixLen);
            } else if (mode == 2) {// 往前播放
                nextMusic = curMusicList.get((curMusicIndex + curMixLen - 1) % curMixLen);
            } else if (mode == 3) {// 重新播放
                if (curMusicList.size() <= 0) {
                    stopMusic();// TODO 异常
                }
                nextMusic = curMusicList.get(0);
            } else if (mode == 0) {// 指定播放
            }

            if (nextMusic == null) {
                stopMusic();// TODO 异常
            }

            File tmp = new File(nextMusic);
            if (tmp.exists()) {// 如果文件存在
                MainPlayer.playTime.reset();// TODO 切歌
                MainPlayer.player.setDataSource(nextMusic);// TODO 异常
                MainPlayer.player.prepare();// TODO 异常
                // TODO 启动播放
                MainPlayer.playTime.play();
                MainPlayer.infoLog("start play " + nextMusic);
            } else {// TODO 歌曲不存在
                MainPlayer.musicDelete(nextMusic, curMix);// 从歌单中删除不存在的歌曲
                loadList(curMix, null);// 重新加载歌单
                changeMusic(null, 3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {// TODO
            e.printStackTrace();
        }

    }
}
