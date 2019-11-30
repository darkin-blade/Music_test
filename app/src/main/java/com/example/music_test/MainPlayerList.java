package com.example.music_test;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainPlayerList {
    View myView;
    Context myContext;

    ArrayList<String> musicSelected;// 被选中的歌曲

    public MainPlayerList(View view, Context context) {
        myView = view;
        myContext = context;
        musicSelected = new ArrayList<String>();
    }

    public void listMusic() {// 更新主界面的ui
        // 清空
        musicSelected.clear();
        LinearLayout layout = myView.findViewById(R.id.music_list);
        layout.removeAllViews();

        // TODO 列举所有歌曲;
        Cursor cursor = MainPlayer.database.query(
                MainPlayer.playList.curMix,// 当前歌单
                new String[]{"path", "name", "count"},
                null,
                null,
                null,
                null,
                "name");

        if (cursor.moveToFirst()) {// 歌单非空
            do {
                String music_path = cursor.getString(0);// 获取歌曲绝对路径
                String music_name = cursor.getString(1);// 获取歌曲名
                int play_times = cursor.getInt(2);// 获取播放次数
                create_item(new String[]{music_name, "play times: " + play_times, music_path}, 1);// TODO 列举歌曲
            } while (cursor.moveToNext());
        } else {
            MainPlayer.infoToast(myContext, "no music");
        }
    }

    // 列举歌单的参数
    public static final int
            box_width = 60,
            item_height = 130,
            detail_margin_right = 80,
            detail_margin_left = 10,
            box_margin_top = 35,
            box_margin_right = 10;

    public void create_item(final String[] item_detail, int mode) {
        // mode: 0:歌单 1:歌曲
        // item_detail: {[歌名], [播放次数], [绝对路径]} {[歌单名], [歌曲数目]}

        LinearLayout layout = myView.findViewById(R.id.music_list);
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
        final LinearLayout item = new LinearLayout(myContext);
        RelativeLayout contain = new RelativeLayout(myContext);
        LinearLayout detail = new LinearLayout(myContext);
        TextView name = new TextView(myContext);
        TextView number = new TextView(myContext);
        final CheckBox checkBox = new CheckBox(myContext);

        item.setBackgroundResource(R.color.grey);
        item.setLayoutParams(itemParam);

        contain.setLayoutParams(containParam);

        detailParam.setMargins(detail_margin_left, detail_margin_left, detail_margin_right, detail_margin_left);
        detail.setOrientation(LinearLayout.HORIZONTAL);// 水平
        detail.setBackgroundResource(R.color.grey);
        detail.setLayoutParams(detailParam);

        name.setGravity(Gravity.CENTER);
        name.setText(item_detail[0]);// 歌单名/歌曲名
        name.setLayoutParams(nameParam);

        number.setGravity(Gravity.CENTER);
        number.setText(item_detail[1]);// 歌曲数/播放数
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

        // TODO 播放该专辑
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 播放歌曲
                MainPlayer.playList.changeMusic(item_detail[2], 0);
            }
        });

        // 复选功能: 存储绝对路径
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    item.setBackgroundResource(R.color.grey_light);
                    musicSelected.add(item_detail[2]);
                    MainPlayer.infoLog("size: " + musicSelected.size());
                } else {
                    item.setBackgroundResource(R.color.grey);
                    musicSelected.remove(item_detail[2]);
                    MainPlayer.infoLog("size: " + musicSelected.size());
                }
            }
        });
    }
}
