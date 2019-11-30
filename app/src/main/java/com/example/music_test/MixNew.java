package com.example.music_test;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

public class MixNew extends DialogFragment {
    public View myView;
    public Button button_create;
    public Button button_cancel;

    SQLiteDatabase database;// 数据库

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
        myView = inflater.inflate(R.layout.mix_new, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));// 背景透明

        initData();
        initButton();

        return myView;
    }

    public void initData() {
        MainPlayer.window_num = MainPlayer.MIX_NEW;// 修改窗口编号
        database = SQLiteDatabase.openOrCreateDatabase(MainPlayer.appPath + "/player.db", null);// TODO 参数
    }

    public void initButton() {// TODO 初始化按钮监听
        button_create = myView.findViewById(R.id.button_create);
        button_cancel = myView.findViewById(R.id.button_cancel);

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText = myView.findViewById(R.id.mix_name);
                String mix_name = editText.getText().toString();

                if (mix_name == null || mix_name.length() == 0 || mix_name.length() >= 32 || mix_name.equals("mix_list")) {// 歌单名不能为空
                    MainPlayer.infoToast(getContext(), "mix name invalid");
                    return;
                }

                // 数据库管理
                // 打开数据库
                cmd("create table if not exists mix_list (\n" +
                        "  name varchar (32) not null,\n" +
                        "  primary key (name)\n" +
                        ") ;");

                // 插入到歌单列表`mix_list`
                int result = cmd("insert into mix_list (name)\n" +
                        "  values\n" +
                        "  ('" + mix_name + "');");

                if (result == -1) {// TODO 创建失败
                    MainPlayer.infoToast(getContext(), mix_name + " already exists");
//                    return;
                }

                // 新建歌单`mix_name`
                result = cmd("create table if not exists " + mix_name + " (\n" +
                        "  path varchar (128) not null,\n" +
                        "  name varchar (64) not null,\n" +
                        "  count int default 0,\n" +
                        "  primary key (path)" +
                        ");");

                if (result == 0) {
                    MainPlayer.infoToast(getContext(), mix_name + " create succeed");
                } else {
                    MainPlayer.infoToast(getContext(), mix_name + " create failed");
                }

                dismiss();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public int cmd(String sql) {
        try {
            database.execSQL(sql);
        } catch (SQLException e) {
            // TODO 数据库操作出错
//            MainPlayer.infoToast(getContext(), "database error");
            MainPlayer.infoLog("database error: " + sql);
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
