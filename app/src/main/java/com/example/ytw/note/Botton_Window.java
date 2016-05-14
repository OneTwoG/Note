package com.example.ytw.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by YTW on 2016/4/28.
 */
public class Botton_Window extends PopupWindow {

    //定义各种控件
    private Button mTakePhoto;
    private Button mSelecetOfGallery;
    private Button mCanceled;
    private View mMenu;

    public Botton_Window(Activity context){
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenu = inflater.inflate(R.layout.botton_photo, null);

        //初始化控件
        mTakePhoto = (Button) mMenu.findViewById(R.id.btn_camera);
        mSelecetOfGallery = (Button) mMenu.findViewById(R.id.btn_gallery);
        mCanceled = (Button) mMenu.findViewById(R.id.btn_canceled);

        //设置按钮监听事件
        mTakePhoto.setOnClickListener((View.OnClickListener) context);
        mSelecetOfGallery.setOnClickListener((View.OnClickListener) context);
        mCanceled.setOnClickListener((View.OnClickListener) context);

        //设置Botton_Window的View
        this.setContentView(mMenu);
        //设置窗体弹出的宽度
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置弹出窗体可以点击
        this.setFocusable(true);
        //设置窗体弹出动画效果
        this.setAnimationStyle(R.style.AppTheme);
        //实例化一个半透明的ColoDrawable,并设置窗口的背景
        ColorDrawable colorDrawable = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(colorDrawable);

        //给mMenu设置一个点击监听事件,判断用户是否点击到窗口外面，如果是 则销毁
        mMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenu.findViewById(R.id.layout_bottom).getTop();

                //获取用户点击的Y轴坐标
                int onTouch_Y = (int) event.getY();

                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (onTouch_Y < height){
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}
