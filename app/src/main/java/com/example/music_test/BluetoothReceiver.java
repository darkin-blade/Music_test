package com.example.music_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {// 接收信号
        MainPlayer.infoLog("receive");
        String action = intent.getAction();
        if (action != null) {
            MainPlayer.infoLog("action: " + action);
            switch (action) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:// TODO
                    int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);// default value TODO
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
