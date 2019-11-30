package com.example.music_test;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class MixAdd extends DialogFragment {
    public View myView;

    public Button button_cancel;

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
        myView = inflater.inflate(R.layout.mix_add, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));// 背景透明

        initData();
        initButton();
        listMix();

        return myView;
    }

    public void initData() {
        MainPlayer.window_num = MainPlayer.MIX_ADD;// 修改窗口编号
    }

    public void initButton() {// TODO 初始化按钮
        button_cancel = myView.findViewById(R.id.button_cancel);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void listMix() {
        // 清空
        LinearLayout layout = myView.findViewById(R.id.mix_list);
        layout.removeAllViews();

        // 列举所有歌单
        Cursor cursor = MainPlayer.database.query(
                "mix_list",// 歌单列表
                new String[]{"name"},
                null,
                null,
                null,
                null,
                "name");

        if (cursor.moveToFirst()) {// TODO 判断非空
            do {
                String mix_name = cursor.getString(0);// 获取歌单名
                create_item(mix_name, "TODO", 0);// TODO 列举歌单
            } while (cursor.moveToNext());
        } else {
            MainPlayer.infoToast(getContext(), "no mix");
        }

    }

    // TODO 列举歌单的参数
    public static final int
            item_height = 130,
            detail_margin_left = 10;

    public void create_item(final String item_name, final String item_detail, int mode) {// mode: 0:歌单 1:歌曲
        LinearLayout layout = myView.findViewById(R.id.mix_list);
        // 每一项 LL
        LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, item_height);
        // 文字区 LL
        LinearLayout.LayoutParams detailParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        // 歌单名
        LinearLayout.LayoutParams nameParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,2);
        // 歌曲数目
        LinearLayout.LayoutParams numberParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);;

        // 新建实例
        final LinearLayout item = new LinearLayout(getContext());
        LinearLayout detail = new LinearLayout(getContext());
        TextView name = new TextView(getContext());
        TextView number = new TextView(getContext());

        item.setBackgroundResource(R.color.grey);
        item.setLayoutParams(itemParam);

        detailParam.setMargins(detail_margin_left, detail_margin_left, detail_margin_left, detail_margin_left);
        detail.setOrientation(LinearLayout.HORIZONTAL);// 水平
        detail.setBackgroundResource(R.color.grey);
        detail.setLayoutParams(detailParam);

        name.setGravity(Gravity.CENTER);
        name.setText(item_name);
        name.setLayoutParams(nameParam);

        number.setGravity(Gravity.CENTER);
        number.setText(item_detail);
        number.setLayoutParams(numberParam);

        // 合并ui
        detail.addView(name);
        detail.addView(number);
        item.addView(detail);
        layout.addView(item);

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 添加到该歌单
                for (int i = 0; i < MainPlayer.mixList.musicSelected.size(); i ++) {
                    String tmp = MainPlayer.mixList.musicSelected.get(i);
                    MainPlayer.cmd("insert into " + item_name + " (path, name, count)\n" +
                            "  values\n" +
                            "  ('" + tmp + "', '" + tmp.replaceAll(".*/", "") + "', 0);");
                }
                dismiss();// TODO
            }
        });
    }
}
