package com.example.ytw.note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YTW on 2016/5/5.
 */
public class UserInfoActivity extends AppCompatActivity {

    //定义控件
    private ViewPager viewPage;
    private List<Fragment> fragments;
    private MyPagerAdapter pagerAdapter;
    private PagerTabStrip mPagerTab;
    private Toolbar mToolBar;
    private ImageView mUserImg;
    private TextView mUserIntro;
    private Button mExit;

    private String mUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_content);

        //获取MainActivity传过来的信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUserName = bundle.getString("user_name");


        //初始化控件
        initView();

        //为ViewPager组件绑定事件监听器
        viewPage.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            //当ViewPager显示的Fragment发生改变是激发该方法
            @Override
            public void onPageSelected(int position) {
            }
        });

        //连接服务器，获取用户信息
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.userinfo_toolbar);
        mToolBar.setTitle(mUserName);
        mToolBar.setNavigationIcon(R.drawable.dialog_ic_close_focused_holo_light);
        setSupportActionBar(mToolBar);
        //
        viewPage = (ViewPager) findViewById(R.id.pager);
        mPagerTab = (PagerTabStrip) findViewById(R.id.pager_title);

        //设置Tab的字体大小
        mPagerTab.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        //设置Tab背景颜色
        mPagerTab.setBackgroundResource(R.color.overflowTextColor);
        mPagerTab.setTextSpacing(100);

        fragments = new ArrayList<>();
        fragments.add(Fragment.instantiate(this, Fragment_plan.class.getName()));
        fragments.add(Fragment.instantiate(this, Fragment_log.class.getName()));

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        viewPage.setAdapter(pagerAdapter);

        mUserImg = (ImageView) findViewById(R.id.iv_userinfo_img);
        mUserIntro = (TextView) findViewById(R.id.tv_userinfo_intro);
        mExit = (Button) findViewById(R.id.btn_exit);
    }

}
