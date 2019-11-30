package com.example.music_test;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
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

public class MixList extends DialogFragment {
    public ArrayList<String> mixSelected;// 被选中的歌单
    public ArrayList<String> musicSelected;// 被选中的歌曲/在文件管理器中被选中的歌曲
    public View myView;
    public String curMix;// TODO 当前歌单名

    public Button button_back;
    public Button button_edit;
    public Button button_new;

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
        myView = inflater.inflate(R.layout.mix_list, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));// 背景透明

        initData();
        initButton();
        listMix();

        return myView;
    }

    public void initData() {
        MainPlayer.window_num = MainPlayer.MIX_LIST;// 修改窗口编号
        database = SQLiteDatabase.openOrCreateDatabase(MainPlayer.appPath + "/player.db", null);
        mixSelected = new ArrayList<String>();
        musicSelected = new ArrayList<String>();
    }

    public void initButton() {// TODO 初始化按钮
        button_back = myView.findViewById(R.id.button_1);
        button_edit = myView.findViewById(R.id.button_2);
        button_new = myView.findViewById(R.id.button_3);
    }

    public void changeButton(int mode) {// mode: 0: 歌单, 1: 歌曲
        if (mode == 0) {
            button_back.setOnClickListener(new View.OnClickListener() {// 返回主界面
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            button_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainPlayer.mixEdit.show(getFragmentManager(), "edit mix");
                }
            });

            button_new.setText("New");
            button_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainPlayer.mixNew.show(getFragmentManager(), "new");
                }
            });
        } else if (mode == 1) {
            button_back.setOnClickListener(new View.OnClickListener() {// 返回歌单列表
                @Override
                public void onClick(View v) {
                    listMix();
                }
            });

            button_new.setText("Add");
            button_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainPlayer.musicAdd.show(getFragmentManager(), "add");
                }
            });
        }
    }

    public void listMix() {
        // 清空
        musicSelected.clear();
        LinearLayout layout = myView.findViewById(R.id.mix_list);
        layout.removeAllViews();

        // 列举所有歌单
        Cursor cursor = database.query(
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

        changeButton(0);
    }

    public void listMusic(String mix_name) {
        // 清空
        mixSelected.clear();
        LinearLayout layout = myView.findViewById(R.id.mix_list);
        layout.removeAllViews();
        curMix = mix_name;// TODO 刷新歌单名

        // TODO 列举所有歌曲;
        Cursor cursor = database.query(
                mix_name,// 歌单详情
                new String[]{"path", "name", "count"},
                null,
                null,
                null,
                null,
                "name");

        if (cursor.moveToFirst()) {// 歌单非空
            do {
                String music_name = cursor.getString(1);// 获取歌单名
                int play_times = cursor.getInt(2);// 获取播放次数
                create_item(music_name, "play times: " + play_times,1);// TODO 列举歌曲
            } while (cursor.moveToNext());
        } else {
            MainPlayer.infoToast(getContext(), "no music");
        }

        changeButton(1);
    }

    // TODO 列举歌单的参数
    public static final int
            box_width = 60,
            item_height = 130,
            detail_margin_right = 80,
            detail_margin_left = 10,
            box_margin_top = 35,
            box_margin_right = 10;

    public void create_item(final String item_name, final String item_detail, int mode) {// mode: 0:歌单 1:歌曲
        LinearLayout layout = myView.findViewById(R.id.mix_list);
        // 每一项 LL
        LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, item_height);
        // 每一项,用于定位 RL
        LinearLayout.LayoutParams containParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        // 文字区 LL
        LinearLayout.LayoutParams detailParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        // 歌单名
        LinearLayout.LayoutParams nameParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,2);
        // 歌曲数目
        LinearLayout.LayoutParams numberParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);;
        // 复选框
        LinearLayout.LayoutParams checkboxParam = new LinearLayout.LayoutParams(box_width, box_width);

        // 新建实例
        final LinearLayout item = new LinearLayout(getContext());
        RelativeLayout contain = new RelativeLayout(getContext());
        LinearLayout detail = new LinearLayout(getContext());
        TextView name = new TextView(getContext());
        TextView number = new TextView(getContext());
        final CheckBox checkBox = new CheckBox(getContext());

        item.setBackgroundResource(R.color.grey);
        item.setLayoutParams(itemParam);

        contain.setLayoutParams(containParam);

        detailParam.setMargins(detail_margin_left, detail_margin_left, detail_margin_right, detail_margin_left);
        detail.setOrientation(LinearLayout.HORIZONTAL);// 水平
        detail.setBackgroundResource(R.color.grey);
        detail.setLayoutParams(detailParam);

        name.setGravity(Gravity.CENTER);
        name.setText(item_name);
        name.setLayoutParams(nameParam);

        number.setGravity(Gravity.CENTER);
        number.setText(item_detail);
        number.setLayoutParams(numberParam);

        checkboxParam.setMargins(box_margin_right, box_margin_top, box_margin_right, box_margin_top);
        checkBox.setButtonDrawable(R.drawable.checkbox_library);
        checkBox.setLayoutParams(checkboxParam);

        // 合并ui
        detail.addView(name);
        detail.addView(number);
        contain.addView(checkBox);
        contain.addView(detail);
        item.addView(contain);
        layout.addView(item);

        // 动态修改布局
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) checkBox.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);// 选框靠右
        checkBox.setLayoutParams(params);

        // TODO 查看详情
        if (mode == 0) {// 当前为歌单列表
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listMusic(item_name);// 点击查看对应歌单详情
                }
            });

            // TODO 复选功能
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (checkBox.isChecked()) {
                        item.setBackgroundResource(R.color.grey_light);
                        mixSelected.add(item_name);
                        MainPlayer.infoLog("size: " + mixSelected.size());
                    } else {
                        item.setBackgroundResource(R.color.grey);
                        mixSelected.remove(item_name);
                        MainPlayer.infoLog("size: " + mixSelected.size());
                    }
                }
            });
        } else if (mode == 1) {// 当前为歌曲列表
            // TODO 播放该专辑
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainPlayer.infoToast(getContext(), "todo play music");
                }
            });

            // TODO 复选功能
        }

        // TODO debug
        MainPlayer.infoLog("create item finished");
    }
}
