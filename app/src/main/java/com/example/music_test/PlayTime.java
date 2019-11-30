package com.example.music_test;

import android.app.Activity;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayTime {
    public Context myContext = null;
    public Activity myActivity = null;
    public Thread musicPlay;

    public int total_time = 0;
    public int cur_time = 0;

    public PlayTime() {
        ;
    }

    public PlayTime(Context context, Activity activity) {
        this.myContext = context;
        this.myActivity = activity;
    }

    public void play() {
        if (MainPlayer.player.isPlaying()) {
            MainPlayer.infoLog("still playing!");
        }

        // 初始化音乐播放
        musicPlay = new Thread(new Runnable() {
            @Override
            public void run() {
                // 启动播放
                MainPlayer.player.start();
                total_time = MainPlayer.player.getDuration();

                myActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setBar();// TODO 在播放之前就已经调整进度
                    }
                });

                // 更新音乐进度
                while (Thread.currentThread().isInterrupted() == false) {
                    try {
                        cur_time = MainPlayer.player.getCurrentPosition();// 当前进度
                        MainPlayer.infoLog("music playing: " + cur_time / 1000 + "/" + total_time / 1000);// TODO

                        myActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateBar();// TODO 修改进度条
                                updateTime();// TODO 修改当前进度
                            }
                        });

                        // 每一秒更新一次
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (MainPlayer.player.isPlaying() == false) {// TODO 多余
                        Thread.currentThread().interrupt();// 暂停
                    }
                }
            }
        });

        musicPlay.start();
    }

    public void reset() {// TODO 切歌
        MainPlayer.player.reset();
        MainPlayer.seekBar.setProgress(0);// TODO
    }

    public void pause() {
        MainPlayer.player.pause();
        musicPlay.interrupt();
    }

    public void updateTime() {// 刷新时间
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

    public void updateBar() {// 刷新进度条
        int curProgress = cur_time * 100 / total_time;
        MainPlayer.seekBar.setProgress(curProgress);
    }

    public void setBar() {// 调整进度条
        int curProgress = MainPlayer.seekBar.getProgress();
        int maxProgress = MainPlayer.seekBar.getMax();

        MainPlayer.player.seekTo(curProgress * total_time / maxProgress);// TODO 调整时间
        updateTime();// TODO
    }
}
