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

    public PlayTime(Context context, Activity activity) {
        this.myContext = context;
        this.myActivity = activity;
    }

    public void play() {
        if (MainPlayer.player.isPlaying()) {
            MainPlayer.infoLog("still playing!");
        }

        if (MainPlayer.playList.curMusicList == null || MainPlayer.playList.curMusicList.size() <= 0) {// TODO 异常处理
            return;
        }

        MainPlayer.button_play.setBackgroundResource(R.drawable.player_pause);

        // 初始化音乐播放
        musicPlay = new Thread(new Runnable() {
            @Override
            public void run() {
                // 启动播放
                MainPlayer.player.start();
                total_time = MainPlayer.player.getDuration();

                // 在播放之前就已经调整进度
                // player的进度调整不能够有延迟
                setBar();

                // 更新音乐进度
                while (Thread.currentThread().isInterrupted() == false) {
                    try {
                        MainPlayer.infoLog("cur time [" + cur_time + ", " + total_time + "]");
                        cur_time = MainPlayer.player.getCurrentPosition();// 当前进度

                        myActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateBar();// 修改进度条
                                updateTime();// 修改当前进度
                            }
                        });

                        // 每一秒更新一次
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        MainPlayer.infoLog("pause music");
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

    public void reset() {// 切歌,将进度置0
        MainPlayer.player.reset();
        MainPlayer.seekBar.setProgress(0);
        cur_time = 0;// TODO
    }

    public void pause() {
        MainPlayer.player.pause();
        MainPlayer.button_play.setBackgroundResource(R.drawable.player_play);
        musicPlay.interrupt();
    }

    public void updateTime() {// 刷新时间
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");

        if ((cur_time - 1) * 2 < total_time && cur_time * 2 > total_time) {// TODO 统计播放次数
            MainPlayer.cmd("update " + MainPlayer.playList.curMix + " set count = count + 1\n" +
                    "  where path = '" + MainPlayer.playList.curMusic + "';");
        }

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

        cur_time = curProgress * total_time / maxProgress;
        MainPlayer.player.seekTo(cur_time);// TODO 调整时间

        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateTime();// TODO
            }
        });
    }
}
