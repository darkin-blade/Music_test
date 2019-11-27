package com.example.music_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.widget.Adapter;

public class BluetoothReceiver extends BroadcastReceiver {
    public Context myContext;

    public BluetoothReceiver() {
        ;
    }

    public BluetoothReceiver(Context context) {
        this.myContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {// 接收信号
        MainPlayer.infoLog("receive");
        String action = intent.getAction();
        if (action != null) {
//            MainPlayer.infoToast(myContext, "action: " + action);
            switch (action) {
                // 有线耳机状态改变
                case Intent.ACTION_HEADSET_PLUG:
                    int mediaState = intent.getIntExtra("state", 0);//
                    if (mediaState == 0) {// 拔出耳机
                        if (MainPlayer.player.isPlaying()) {
                            MainPlayer.button_1.callOnClick();// TODO 强制暂停
                        }
                        MainPlayer.infoToast(myContext, "isplaying: " + MainPlayer.player.isPlaying());
                    } else if (mediaState == 1) {// 插入耳机
                        MainPlayer.infoToast(myContext, "isplaying: " + MainPlayer.player.isPlaying());
                    }
                    break;

                // 蓝牙连接状态改变
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);// 获取蓝牙状态
                    switch (bluetoothState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            MainPlayer.infoLog("turning on");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            MainPlayer.infoLog("on");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            MainPlayer.infoLog("turning off");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            MainPlayer.infoLog("off");
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:// TODO
                    MainPlayer.infoLog("connected");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:// TODO
                    MainPlayer.infoLog("disconnected");
                    break;

                // 接收蓝牙按键信号
                case Intent.ACTION_MEDIA_BUTTON:// TODO 按键
                    KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);// 获取键码
                    int keycode = keyEvent.getKeyCode();
                    MainPlayer.infoLog("media button: " + keycode);
                    switch (keycode) {
                        case KeyEvent.KEYCODE_MEDIA_NEXT:// 下一首 87
                            MainPlayer.infoLog("next");
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:// 播放 126
                            MainPlayer.infoLog("play");
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:// 暂停 127
                            MainPlayer.infoLog("pause");
                            break;
                    }
            }
        }
    }

    public void registerReceiver(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // 另说 context.AUDIO_SERVICE
        ComponentName name = new ComponentName(context.getPackageName(), BluetoothReceiver.class.getName());
        // 另说 MediaButtonReceiver.class.getName()
        audioManager.registerMediaButtonEventReceiver(name);
    }

    public void unregisterReceiver(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName name = new ComponentName(context.getPackageName(), BluetoothReceiver.class.getName());
        audioManager.unregisterMediaButtonEventReceiver(name);
    }
}
