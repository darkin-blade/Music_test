package com.example.music_test;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlayList {
    // 每次加载必须要恢复的数据
    String curMix;// 当前歌单
    String curMusic;// TODO 当前播放的歌曲
    int playMode;// 播放模式
    // 当前时长
    // 总时长
    // seekBar

    ArrayList<String> curMusicList;// 当前歌单的所有歌曲
    int curMusicIndex;
    int curMixLen;
    Context myContext;

    static public final int CIRCULATE = 0,// 顺序播放
            RANDOM = 1,// 随机
            SINGLE = 2,// 单曲循环
            AVERAGE = 3,// 平均
            POLARIZATION = 4;

    public PlayList() {
    }

    public PlayList(Context context) {
        myContext = context;
    }

    public void initData() {
        curMix = "";
        curMusic = "";
        curMusicList = new ArrayList<String>();
        recover();// 恢复数据
    }

    public void recover() {// 每次启动app时进行数据恢复
        try {
            Cursor cursor = MainPlayer.database.query(
                    "user_data",
                    new String[] {"cur_mix", "cur_music", "play_mode", "cur_time", "total_time"},
                    null,
                    null,
                    null,
                    null,
                    "cur_music");// 没用
            if (cursor.moveToFirst()) {// 有之前的应用数据
                // 恢复数据
                curMix = cursor.getString(0);
                curMusic = cursor.getString(1);
                playMode = cursor.getInt(2);
                MainPlayer.playTime.cur_time = cursor.getInt(3);
                MainPlayer.playTime.total_time = cursor.getInt(4);
                cursor.close();

                // 手动加载歌单
                if (curMix.length() > 0 && curMusic.length() > 0) {// 有效数据
                    curMusicList.clear();
                    curMixLen = 0;

                    cursor = MainPlayer.database.query(
                            curMix,// 当前歌单
                            new String[]{"path", "name", "count"},
                            null,
                            null,
                            null,
                            null,
                            "name");

                    if (cursor.moveToFirst()) {// 歌单非空
                        do {
                            String music_name = cursor.getString(0);// 获取歌名
                            curMusicList.add(music_name);
                            curMixLen ++;
                            MainPlayer.infoLog("add to play list: " + music_name);
                        } while (cursor.moveToNext());
                    } else {
                        ;// TODO 出现异常
                    }
                    cursor.close();

                    curMusicIndex = curMusicList.indexOf(curMusic);// 获取当前播放的音乐的索引 此步可能会重复 且如果没有播放音乐时该索引可能为负
                    MainPlayer.mainPlayerList.listMusic();// TODO 加载歌单
                    MainPlayer.playTime.updateTime();// 更新音乐时长
                    MainPlayer.playTime.updateBar();// 更新seekBar
                }
                MainPlayer.infoLog("[" + curMix + "][" + curMusicIndex + "/" + curMixLen + "][" + curMusic + "]["
                        + MainPlayer.playTime.cur_time + "][" + MainPlayer.playTime.total_time + "]");
            } else {
                MainPlayer.infoLog("cannot find user data");
            }
        } catch (SQLException e) {// TODO 用于更新user_data
            e.printStackTrace();
            MainPlayer.infoLog("cannot find table");
            return;
        }

    }

    public void save() {// TODO 保存应用数据到数据库
        MainPlayer.cmd("drop table user_data;");
        MainPlayer.cmd("create table if not exists user_data (\n" +
                "  cur_mix varchar(32) default \"\",\n" +
                "  cur_music varchar(128) default \"\",\n" +
                "  play_mode int default 0,\n" +
                "  cur_time int default 0,\n" +
                "  total_time int default 0\n" +
                ");");// 用户数据存储

        int result = MainPlayer.cmd("insert into user_data (cur_mix, cur_music, play_mode, cur_time, total_time)\n" +
                "  values ('" + curMix + "', '" + curMusic + "', " + playMode + ", "
                + MainPlayer.playTime.cur_time +", " + MainPlayer.playTime.total_time +");");

        MainPlayer.infoLog("[" + curMix + "][" + curMusicIndex + "/" + curMixLen + "][" + curMusic + "]["
                + MainPlayer.playTime.cur_time + "][" + MainPlayer.playTime.total_time + "]");

        if (result == 0) {
            MainPlayer.infoLog("save user data succeed");
        }
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
        cursor.close();

        MainPlayer.mainPlayerList.listMusic();

        curMusicIndex = curMusicList.indexOf(curMusic);// 获取当前播放的音乐的索引 此步可能会重复 且如果没有播放音乐时该索引可能为负

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
        MainPlayer.infoLog("force stop");
        MainPlayer.playTime.reset();
        MainPlayer.updateUI();
    }

    public void changeMusic(String nextMusic, int mode) {
        MainPlayer.infoLog("change to " + nextMusic);
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
                    MainPlayer.playTime.reset();// 切歌
                    MainPlayer.player.setDataSource(curMusic);
                    MainPlayer.player.prepare();
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
        } catch (IOException e) {// TODO prepare出错
            MainPlayer.infoLog("prepare failed: " + curMusic);
            MainPlayer.musicDelete(curMusic, curMix);
            if (MainPlayer.window_num == MainPlayer.MAIN_PALYER) {// 留在主界面
                MainPlayer.mainPlayerList.listMusic();// 刷新歌单
            } else if (MainPlayer.window_num == MainPlayer.MIX_LIST) {// 歌单界面
                if (MainPlayer.mixList.curMix == curMix) {// TODO 删除的歌曲在目前正在浏览的歌单里
                    MainPlayer.mixList.listMusic(curMix);
                }
            }
            stopMusic();// 强制暂停
            e.printStackTrace();
        } catch (IllegalStateException e) {// TODO
            e.printStackTrace();
        }

    }
}
