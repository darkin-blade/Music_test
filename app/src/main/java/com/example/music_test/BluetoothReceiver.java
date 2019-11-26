package com.example.music_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:// TODO
                    int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);// default value TODO
                    switch (bluetoothState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            MainPlayer.infoToast(context, "turning on");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            MainPlayer.infoToast(context, "on");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            MainPlayer.infoToast(context, "turning off");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            MainPlayer.infoToast(context, "off");
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:// TODO
                    MainPlayer.infoToast(context, "connected");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:// TODO
                    MainPlayer.infoToast(context, "disconnected");
                    break;
                case Intent.ACTION_MEDIA_BUTTON:// TODO 按键
                    KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    switch (keyEvent.getKeyCode()) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY:// 播放
                            MainPlayer.infoToast(context, "play");
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:// 暂停
                            MainPlayer.infoToast(context, "pause");
                            break;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:// 下一首
                            MainPlayer.infoToast(context, "next");
                            break;
                    }
            }
        }
    }
}