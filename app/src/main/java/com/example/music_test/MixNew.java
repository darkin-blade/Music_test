package com.example.music_test;

import android.app.Activity;
import android.content.DialogInterface;
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

public class MixNew extends DialogFragment {
    public View myView;
    public Button button_create;
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
        myView = inflater.inflate(R.layout.mix_new, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));// 背景透明

        initData();
        initButton();
        listMix();

        return myView;
    }

    public void initData() {
        MainPlayer.window_num = MainPlayer.MIX_NEW;// 修改窗口编号
    }

    public void initButton() {// TODO 初始化按钮监听
    }

    public void listMix() {
        button_create = myView.findViewById(R.id.button_create);
        button_cancel = myView.findViewById(R.id.button_cancel);

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainPlayer.infoToast(getContext(), "TODO");
                dismiss();
                // TODO 数据库管理
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
