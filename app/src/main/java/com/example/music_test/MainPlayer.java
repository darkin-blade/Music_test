package com.example.music_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainPlayer extends AppCompatActivity implements DialogInterface.OnDismissListener {
    static public String appPath;
    static public MediaPlayer player;// 媒体播放器
    static public SeekBar seekBar;// 进度条
    static public TextView totalTime;// 音乐总时长
    static public TextView curTime;// 音乐已播放时长

    // TODO media多次点击
    static public Long myTime = System.currentTimeMillis();// 微秒时间
    static public int clickTimes = 0;// 耳机信号次数

    public MusicTime musicTime;// 音乐进度相关
    public MediaReceiver receiver;// 接收`蓝牙/媒体`信号
    public BluetoothAdapter bluetoothAdapter;// 蓝牙

    // TODO ui 界面
    static public MixList mixList;// TODO 歌单管理
    static public MusicAdd musicAdd;// TODO 添加音乐
    // TODO dialog 界面
    static public MixNew mixNew;// TODO 新建歌单

    static public View button_1;// `播放/暂停`按钮
    static public View button_2;// `开启蓝牙`按钮
    static public View button_5;// `歌单管理`按钮

    static int window_num = 0;
    static final int MUSIC_LISTS = 1;// 歌单管理
    static final int MUSIC_ADD = 2;// 文件管理器
    static final int MIX_NEW = 3;// 新建歌单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_player);

        initApp();
        initPlayer();// 初始化播放器
        initButton();// 初始化按钮监听
        initSeekBar();// 初始化进度条
        initBluetooth();// 初始化蓝牙
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

        // 初始化ui
        musicAdd = new MusicAdd();
        mixList = new MixList();
        // 初始化dialog
        mixNew = new MixNew();
    }

    public void initPlayer() {
        player = new MediaPlayer();
        player.setLooping(false);// TODO 不循环播放
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);// TODO 设置为音频
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {// 播放完毕回调函数
            @Override
            public void onCompletion(MediaPlayer mp) {
                mainToast("play finished");
            }
        });

        // 初始化音乐进度
        totalTime = findViewById(R.id.total_time);
        curTime = findViewById(R.id.cur_time);
        musicTime = new MusicTime(this, this);
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
//                if (player.isPlaying()) {// 在播放时才有效
                    musicTime.setBar();// TODO 调整player进度
//                }
            }
        });
    }

    public void initButton() {
        button_1 = findViewById(R.id.button_1);// 播放按钮

        button_1.setOnClickListener(new View.OnClickListener() {// `播放/暂停`功能
            @Override
            public void onClick(View v) {
                MainPlayer.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (player.isPlaying() == false) {// TODO 判断的条件 正在暂停
                            musicTime.play();
                            button_1.setBackgroundResource(R.drawable.player_pause);
                        } else {
                            musicTime.pause();
                            button_1.setBackgroundResource(R.drawable.player_play);
                        }
                    }
                });
            }
        });

//        button_2 = findViewById(R.id.button_2);// TODO 蓝牙管理界面
//
//        button_2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 开启蓝牙
//                if (bluetoothAdapter.isEnabled() == false) {
//                    bluetoothAdapter.enable();
//                } else {
//                    bluetoothAdapter.disable();
//                }
//            }
//        });

        button_5 = findViewById(R.id.button_5);

        button_5.setOnClickListener(new View.OnClickListener() {// TODO 歌单管理界面
            @Override
            public void onClick(View v) {
                mixList.show(getSupportFragmentManager(), "lists");
            }
        });
    }

    public void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();// 获取蓝牙适配器
        receiver = new MediaReceiver(this);// 接收蓝牙信号

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);// 监视蓝牙设备与APP连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);// 监听有线耳机的插拔
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);// TODO

        registerReceiver(this.receiver, intentFilter);// 注册广播
        receiver.registerReceiver(this);// 初始化广播
    }

    public void test1() {
        String musicPath = appPath + "/test2.wav";
        try {
            File tmp = new File(musicPath);
            if (tmp.exists()) {// 如果文件存在
                player.setDataSource(musicPath);// TODO 异常
                player.prepareAsync();// TODO 异常

                // TODO 启动播放
            } else {
                infoToast(this, musicPath + " not exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void mainToast(String log) {
        Toast toast = Toast.makeText(MainPlayer.this, log, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        textView.setTextColor(Color.rgb(0x00, 0x00, 0x00));
        toast.show();
    }

    static public void infoLog(String log) {
        Log.i("fuck", log);
    }

    static public void infoToast(Context context, String log) {
        if (context == null) {// 增加容错
            return;
        }
        Toast toast = Toast.makeText(context, log, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        textView.setTextColor(Color.rgb(0x00, 0x00, 0x00));
        toast.show();
    }

    static public String pathSimplify(String path) {// TODO 简化路径
        path = path.replaceAll("/+\\./", "/");// 除去`/.`
        infoLog("path: " + path);

        Pattern pattern = Pattern.compile("/+\\.[\\.]+");
        Matcher matcher = pattern.matcher(path);
        int count = 0;
        while (matcher.find()) {
            count ++;
        }

        path = path.replaceAll("/+\\.[\\.]+", "");// 除去`/..`

        int index = path.length() - 1;
        if (path.charAt(index) == '/') {
            index --;
        }

        while (index >= 0) {
            if (path.charAt(index) == '/') {
                count --;
                if (count == 0) {
                    infoLog("simplified path: " + path.substring(0, index));
                    return path.substring(0, index);
                }
            }
            index --;
        }

        return "/";
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {// TODO 返回就停止
        player.stop();// 停止
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        receiver.unregisterReceiver(this);// TODO 貌似没用
        super.onDestroy();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        //mainToast("main player: TODO on dismiss");
    }
}
