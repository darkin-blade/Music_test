package com.example.music_test;

import android.app.Activity;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MusicTime {
    public Context myContext = null;
    public Activity myActivity = null;
    public Thread musicPlay;

    public int total_time = 0;
    public int cur_time = 0;

    public MusicTime() {
        ;
    }

    public MusicTime(Context context, Activity activity) {
        this.myContext = context;
        this.myActivity = activity;

        // 初始化音乐播放
        musicPlay = new Thread(new Runnable() {
            @Override
            public void run() {
                // 启动播放
                MainPlayer.player.start();
                total_time = MainPlayer.player.getDuration();

                // 更新音乐进度
                while (Thread.currentThread().isInterrupted() == false) {
                    try {
                        // 每一秒更新一次
                        Thread.sleep(1000);
                        cur_time = MainPlayer.player.getCurrentPosition();// 当前进度
                        MainPlayer.infoLog("music playing: " + cur_time / 1000 + "/" + total_time / 1000);// TODO

                        myActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateBar();// TODO 修改进度条
                                updateTime();// TODO 修改当前进度
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (MainPlayer.player.isPlaying() == false) {
                        Thread.currentThread().interrupt();// 暂停
                    }
                }
            }
        });
    }

    public void play() {
        musicPlay.start();
    }

    public void pause() {
        musicPlay.interrupt();
    }

    public void updateTime() {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");

        // 设置当前进度
        Date tmp = new Date(cur_time);
        String formatTime = format.format(tmp);
        MainPlayer.curTime.setText(formatTime);

        // TODO 更新总时长
        tmp = new Date(total_time);
        formatTime = format.format(tmp);
        MainPlayer.totalTime.setText(formatTime);
    }

    public void updateBar() {
        ;
    }
}
