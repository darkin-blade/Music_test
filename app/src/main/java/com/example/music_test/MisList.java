package com.example.music_test;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

public class MisList extends DialogFragment {
    public List<String> musicSelected;// 当前在文件管理器中选中的所有音乐
    public View myView;

    public Button button_back;
    public Button button_edit;
    public Button button_new;

    SQLiteDatabase database;// 数据库

    // TODO 列举歌单的参数

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
        myView = inflater.inflate(R.layout.mix_list, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));// 背景透明

        initData();
        initButton();
        listMix();

        return myView;
    }

    public void initData() {
        MainPlayer.window_num = MainPlayer.MUSIC_LISTS;// 修改窗口编号
        database = SQLiteDatabase.openOrCreateDatabase(MainPlayer.appPath + "/player.db", null);
    }

    public void initButton() {// TODO 初始化按钮监听
        button_back = myView.findViewById(R.id.button_1);
        button_edit = myView.findViewById(R.id.button_2);
        button_new = myView.findViewById(R.id.button_3);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainPlayer.mixNew.show(getFragmentManager(), "add");
            }
        });
    }

    public void listMix() {
        // 清空并显示父目录
        LinearLayout layout = myView.findViewById(R.id.mix_list);
        layout.removeAllViews();

        // TODO 列举所有歌单
        Cursor cursor = database.query(
                "mix_list",// 歌单列表
                new String[]{"name"},
                null,
                null,
                null,
                null,
                "name");

        if (cursor.moveToFirst()) {// TODO 判断非空
            for (int i = 0; i < cursor.getCount(); i ++) {
                String mix_name = cursor.getString(0);// 获取歌单名
            }
        }
    }

    public void create_item(String mix_name) {
        ;
    }
}
