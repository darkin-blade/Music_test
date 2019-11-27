package com.example.music_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MainPlayer extends AppCompatActivity {
    public String appPath;
    public MediaPlayer player;// 媒体播放器
    public SeekBar seekBar;// 进度条
    public BluetoothReceiver receiver;// 接收蓝牙信号
    public BluetoothAdapter bluetoothAdapter;// TODO 蓝牙
    public BluetoothSocket bluetoothSocket;// TODO
    public List<BluetoothDevice> bluetoothDevices;// TODO 所有配对的设备
    public BluetoothProfile.ServiceListener serviceListener;// TODO
    public OutputStream outputStream;
    public InputStream inputStream;

    public Button button_1;// `播放/暂停`按钮
    public Button button_2;// `开启蓝牙`按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_player);

        initApp();
        initPlayer();// 初始化播放器
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
                if (player.isPlaying()) {// 在播放时才有效 TODO 调整player进度
                    mainToast(seekBar.getProgress() + "%");
                }
            }
        });
    }

    public void initButton() {
        button_1 = findViewById(R.id.button_1);// 播放按钮

        button_1.setOnClickListener(new View.OnClickListener() {// `播放/暂停`功能
            @Override
            public void onClick(View v) {
                if (button_1.getText().equals("Play")) {
                    button_1.setText("Pause");
                    player.start();
                } else {
                    button_1.setText("Play");
                    player.pause();
                }
            }
        });

        button_2 = findViewById(R.id.button_2);// 蓝牙按钮

        button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开启蓝牙
                if (bluetoothAdapter.isEnabled() == false) {
                    bluetoothAdapter.enable();
                } else {
                    bluetoothAdapter.disable();
                }
            }
        });
    }

    public void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        receiver = new BluetoothReceiver();// 接收蓝牙信号
        receiver.registerReceiver(this);// 初始化广播:蓝牙按键

        // 监视蓝牙关闭和打开的状态
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);// 监视蓝牙设备与APP连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(this.receiver, intentFilter);// 注册广播
    }

    public void test1() {
        String musicPath = appPath + "/test2.wav";
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);// TODO 设置为音频
        try {
            File tmp = new File(musicPath);
            if (tmp.exists()) {// 如果文件存在
                player.setDataSource(musicPath);// TODO 异常
                player.prepare();
                initButton();
            } else {
                infoToast(this, musicPath + " not exists");
            }
        } catch (IOException e) {
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
        Toast toast = Toast.makeText(context, log, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        textView.setTextColor(Color.rgb(0x00, 0x00, 0x00));
        toast.show();
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
        receiver.unregisterReceiver(this);
        super.onDestroy();
    }
}
