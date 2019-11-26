package com.example.music_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public String appPath;
    public MediaPlayer player;// 媒体播放器
    public SeekBar seekBar;// 进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initApp();
        initPlayer();// 初始化播放器
        initSeekBar();// 初始化进度条
        test1();
    }

    public void initApp() {
        // 检查权限
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int check_result = ActivityCompat.checkSelfPermission(this, permission);// `允许`返回0,`拒绝`返回-1
        if (check_result != PackageManager.PERMISSION_GRANTED) {// 没有`写`权限
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);// 获取`写`权限
        }

        // 初始化路径字符串
        appPath = getExternalFilesDir("").getAbsolutePath();
    }

    public void initPlayer() {
        player = new MediaPlayer();
        player.setLooping(false);// TODO 不循环播放
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {// 播放完毕回调函数
            @Override
            public void onCompletion(MediaPlayer mp) {
                mainToast("play finished");
            }
        });
    }

    public void initSeekBar() {
        seekBar = findViewById(R.id.music_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {// 开始拖动
                mainToast("start touch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {// 停止拖动
                mainToast("end touch");
                if (player.isPlaying()) {// 在播放时才有效
                    ;
                }
            }
        });
    }

    public void test1() {
        String musicPath = appPath + "/test2.wav";
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);// TODO 设置为音频
        try {
            File tmp = new File(musicPath);
            if (tmp.exists()) {// 如果文件存在
                player.setDataSource(musicPath);// TODO 异常
                player.prepare();
                player.start();
            } else {
                infoToast(this, musicPath + " not exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mainToast(String log) {
        Toast toast = Toast.makeText(MainActivity.this, log, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        textView.setTextColor(Color.rgb(0x00, 0x00, 0x00));
        toast.show();
    }

    static public void infoLog(String log) {
        Log.i("fuck", log);
    }

    static public void infoToast(Context context, String log) {
        Toast toast = Toast.makeText(context, log, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        textView.setTextColor(Color.rgb(0x00, 0x00, 0x00));
        toast.show();
    }

    @Override

    public void onBackPressed() {// TODO 返回就停止
        player.stop();// 停止
        super.onBackPressed();
    }
}
