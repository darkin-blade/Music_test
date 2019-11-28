package com.example.music_test;

import android.app.Activity;
import android.content.Context;

public class MusicTime {
    public Context myContext = null;
    public Activity myActivity = null;

    public MusicTime() {
        ;
    }

    public MusicTime(Context context, Activity activity) {
        this.myContext = context;
        this.myActivity = activity;
    }

    public void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 启动播放
                MainPlayer.player.start();

                // 更新音乐进度
                while (Thread.currentThread().isInterrupted() == false) {
                    try {
                        Thread.sleep(1000);
                        myActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainPlayer.infoLog("music playing");// TODO
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
        }).start();
    }
}
