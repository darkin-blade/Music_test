package com.example.music_test;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class MusicEdit extends DialogFragment {
    public View myView;
    public Button button_delete;
    public Button button_add;// 添加到其他歌单
    public Button button_cancel;
    public TextView textView;// 显示选中的歌单数目

    SQLiteDatabase database;// 数据库

    public void initView() {
        textView.setText(MainPlayer.mixList.mixSelected.size() + " mix selected");
    }

    @Override
    public void show(FragmentManager fragmentManager, String tag) {
        super.show(fragmentManager, tag);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme);// 关闭背景(点击外部不能取消)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.mix_edit, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));// 背景透明

        initData();
        initButton();
        initView();

        return myView;
    }

    public void initData() {
        MainPlayer.window_num = MainPlayer.MIX_EDIT;// 修改窗口编号
        database = SQLiteDatabase.openOrCreateDatabase(MainPlayer.appPath + "/player.db", null);// TODO 参数
    }

    public void initButton() {// TODO 初始化按钮监听/其他ui
        textView = myView.findViewById(R.id.edit_title);

        button_delete = myView.findViewById(R.id.button_delete);
        button_add = myView.findViewById(R.id.button_add);
        button_cancel = myView.findViewById(R.id.button_cancel);

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// TODO 删除选中的歌曲
                for (int i = 0; i < MainPlayer.mixList.musicSelected.size(); i ++) {
                    String tmp = MainPlayer.mixList.musicSelected.get(i);

                    // 从歌单中删除歌曲
                    musicDelete(tmp);
                }
                MainPlayer.mixList.musicSelected.clear();
                dismiss();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainPlayer.window_num = MainPlayer.MIX_LIST;// TODO 防止清空
                dismiss();
            }
        });
    }

    public int cmd(String sql) {
        try {
            database.execSQL(sql);
        } catch (SQLException e) {
            MainPlayer.infoLog("database error: " + sql);
            return -1;
        }
        return 0;
    }

    public void musicDelete(String musicPath) {
        cmd("delete from " + MainPlayer.mixList.curMix + "\n" +
                "where path = '" + musicPath + "';");
    }
}
