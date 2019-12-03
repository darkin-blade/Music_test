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

    int is_playing;
    public int is_complete;// TODO 切歌时继续下一首

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
        is_playing = 0;
        is_complete = 0;

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
                        } while (cursor.moveToNext());
                    } else {
                        ;// TODO 出现异常
                    }
                    cursor.close();

                    curMusicIndex = curMusicList.indexOf(curMusic);// 获取当前播放的音乐的索引 此步可能会重复 且如果没有播放音乐时该索引可能为负
                    MainPlayer.mainPlayerList.listMusic();// TODO 加载歌单
                    loadMusic();
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

    public void loadMix(String nextMix, String nextMusic) {// 加载专辑曲目,并播放特定歌曲
        String tmpMix = curMix;// 记录之前正在播放的歌单
        String tmpMusic = curMusic;// 记录之前正在播放的音乐

        if (nextMusic == null) {// 异常处理
            curMusic = curMusicList.get(0);
        } else {
            curMusic = nextMusic;// 在之后会判重
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
            } while (cursor.moveToNext());
        } else {
            stopMusic();
        }
        cursor.close();

        // TODO ui: 刷新播放次数
        MainPlayer.mainPlayerList.listMusic();

        curMusicIndex = curMusicList.indexOf(curMusic);// 获取当前播放的音乐的索引 此步可能会重复 TODO 且如果没有播放音乐时该索引可能为负

        MainPlayer.infoLog("load " + curMix + ", " + curMusic + ", " + curMusicIndex);

        if (tmpMusic.equals(nextMusic) && tmpMix.equals(nextMix)) {// TODO 歌单仍然有效
            if (curMusicIndex >= 0) {
                return;
            }
        }

        // 累计播放时间
        MainPlayer.cmd("update " + tmpMix + " set count = count + " + MainPlayer.playTime.cumulate_time + " where path = '" + tmpMusic + "';");
        MainPlayer.infoLog("update cumulative time: " + tmpMix + ", " + tmpMusic + ", " + MainPlayer.playTime.cumulate_time);
        MainPlayer.playTime.cumulate_time = 0;// 重置时间

        changeMusic(curMusic, 0);
    }

    public int loadMusic() {// 加载音乐并更新ui
        File tmp = new File(curMusic);
        if (tmp.exists()) {// 如果文件存在
            try {
                MainPlayer.player.setDataSource(curMusic);
                MainPlayer.player.prepare();

                // TODO 获取音乐总时长
                MainPlayer.playTime.total_time = MainPlayer.player.getDuration();

                // TODO 更新ui
                MainPlayer.musicName.setText(curMix + "    " + curMusic.replaceAll(".*/+", ""));// 更新歌名

                // 如果是切歌的话应当在loadMusic函数之前置0进度
                MainPlayer.playTime.updateTime();// 更新音乐时长
                MainPlayer.playTime.updateBar();// 更新seekBar
            } catch (IOException e) {// TODO prepare出错,强制删除音乐
                MainPlayer.infoLog("prepare failed: " + curMusic);
                MainPlayer.musicDelete(curMusic, curMix);
                if (MainPlayer.window_num == MainPlayer.MAIN_PALYER) {// 留在主界面
                    MainPlayer.mainPlayerList.listMusic();// 刷新歌单
                } else if (MainPlayer.window_num == MainPlayer.MIX_LIST) {// 歌单界面
                    if (MainPlayer.mixList.curMix == curMix) {// TODO 删除的歌曲在目前正在浏览的歌单里
                        MainPlayer.mixList.listMusic(curMix);
                    }
                }
                e.printStackTrace();
                return -1;
            } catch (IllegalStateException e) {// TODO
                e.printStackTrace();
                return -1;
            }
        } else {// TODO 歌曲不存在,删除并自动切换
            MainPlayer.infoLog(curMusic + " not exists");
            return -1;
//                MainPlayer.musicDelete(curMusic, curMix);// 从歌单中删除不存在的歌曲
//                loadMix(curMix, null);// 重新加载歌单
//                changeMusic(null, 3);
        }

        return 0;
    }

    public void stopMusic() {// TODO 异常处理
        MainPlayer.infoLog("force stop");
        MainPlayer.playTime.reset();// 进度置0
        MainPlayer.updateUI();
    }

    public void changeMusic(String nextMusic, int mode) {
        // mode: 0: 指定跳转, 1: 向后跳转, 2: 向前跳转, 3: 重新播放 TODO
        // TODO 播放模式
        if (curMusicList == null || curMusicList.size() <= 0) {
            stopMusic();
            return;
        }

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
            loadMix(curMix, null);
        } else {
            MainPlayer.infoLog("change music " + curMusic + " [" + curMusicIndex  + "/" + curMixLen + "]");
            if (MainPlayer.player.isPlaying()) {
                is_playing = 1;
            } else if (is_complete == 1) {// TODO 自动播放下一首
                is_playing = 1;
                is_complete = 0;
            } else {
                is_playing = 0;
            }

            MainPlayer.playTime.reset();// TODO 切歌,进度置0

            int result = loadMusic();// 加载但不播放
            if (result != 0) {// TODO 加载失败
                stopMusic();
                return;
            }

            // 如果在播放时切歌,那么立即播放下一首,否则不进行播放
            if (is_playing == 1) {
                MainPlayer.playTime.play();
            }
        }

    }
}
