package com.example.music_test;

import android.content.Context;
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
    Context myContext;

    static final int CIRCULATE = 0,// 顺序播放
            RANDOM = 1,// 随机
            SINGLE = 2,// 单曲循环
            AVERAGE = 3,// 平均
            POLARIZATION = 4;

    public PlayList() {
        curMusicList = new ArrayList<String>();
    }

    public PlayList(Context context) {
        myContext = context;
        curMusicList = new ArrayList<String>();
    }

    public void recover() {// 每次启动app时进行数据恢复
        // TODO
    }

    public void loadList(String nextMix, String nextMusic) {// 加载专辑曲目,并播放特定歌曲
        if (nextMusic == null) {// 异常处理
            curMusic = curMusicList.get(0);
        } else {
//            curMusic = nextMusic;// 在之后会判重
        }

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
                String music_name = cursor.getString(0);// 获取歌名
                curMusicList.add(music_name);
                curMixLen ++;
                MainPlayer.infoLog("add to play list: " + music_name);
            } while (cursor.moveToNext());
        } else {
            stopMusic();
        }

        MainPlayer.mainPlayerList.listMusic();

        curMusicIndex = curMusicList.indexOf(curMusic);// 获取当前播放的音乐的索引 此步可能会重复

        MainPlayer.infoLog("[" + curMix + "][" + curMusicIndex + "/" + curMixLen + "][" + curMusic + "]");
        if (curMusic == nextMusic && curMix == nextMix) {// TODO 歌单仍然有效
            if (curMusicIndex >= 0 && MainPlayer.player.isPlaying()) {
                return;
            }
        }

        if (nextMusic != null) {
            curMusic = nextMusic;
        } else {// 重新播放
            ;
        }

        changeMusic(curMusic, 0);
    }

    public void stopMusic() {// TODO 异常处理
        MainPlayer.infoLog("player error");
        MainPlayer.playTime.reset();
        MainPlayer.updateUI();
    }

    public void changeMusic(String nextMusic, int mode) {
        // mode: 0: 指定跳转, 1: 向后跳转, 2: 向前跳转, 3: 重新播放 TODO
        // TODO 播放模式
        if (curMusicList == null || curMusicList.size() <= 0) {
            stopMusic();
            return;
        }

        try {
            if (mode == 1) {// 往后播放
                curMusic = curMusicList.get((curMusicIndex + 1) % curMixLen);
            } else if (mode == 2) {// 往前播放
                curMusic = curMusicList.get((curMusicIndex + curMixLen - 1) % curMixLen);
            } else if (mode == 3) {// 重新播放
                if (curMusicList.size() <= 0) {
                    stopMusic();// TODO 异常
                    return;
                }
                curMusic = curMusicList.get(0);
            } else if (mode == 0) {// 指定播放
                curMusic = nextMusic;
            }

            if (curMusic == null) {
                stopMusic();// TODO 异常
                return;
            }

            curMusicIndex = curMusicList.indexOf(curMusic);
            if (curMusicIndex < 0) {
                loadList(curMix, null);
            } else {
                MainPlayer.infoLog("try to play " + curMusic + " [" + curMusicIndex  + "/" + curMixLen + "]");
                File tmp = new File(curMusic);
                if (tmp.exists()) {// 如果文件存在
                    MainPlayer.playTime.reset();// TODO 切歌
                    MainPlayer.player.setDataSource(curMusic);// TODO 异常
                    MainPlayer.player.prepare();// TODO 异常
                    // TODO 启动播放
                    MainPlayer.musicName.setText(curMix + "    " + curMusic.replaceAll(".*/+", ""));// 更新歌名
                    MainPlayer.playTime.play();
                } else {// TODO 歌曲不存在
                    stopMusic();
                    return;
//                MainPlayer.musicDelete(curMusic, curMix);// 从歌单中删除不存在的歌曲
//                loadList(curMix, null);// 重新加载歌单
//                changeMusic(null, 3);
                }
            }
        } catch (IOException e) {
            MainPlayer.infoLog("prepare failed: " + curMusic);
            MainPlayer.infoToast(myContext, "invalid");
            MainPlayer.musicDelete(curMusic, curMix);
            if (MainPlayer.window_num == MainPlayer.MAIN_PALYER) {// 留在主界面
                ;
            } else if (MainPlayer.window_num == MainPlayer.MIX_LIST) {// 歌单界面
                ;
            }
            e.printStackTrace();
        } catch (IllegalStateException e) {// TODO
            e.printStackTrace();
        }

    }
}
