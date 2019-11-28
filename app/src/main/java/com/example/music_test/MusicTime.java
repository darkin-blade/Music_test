package com.example.music_test;

import android.content.Context;

public class MusicTime {
    public Context myContext = null;

    public MusicTime() {
        ;
    }

    public MusicTime(Context context) {
        this.myContext = context;
    }

    public void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
