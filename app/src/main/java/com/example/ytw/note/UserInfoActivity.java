package com.example.ytw.note;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_content);

        //初始化控件
        initView();


        //为ViewPager组件绑定事件监听器
        viewPage.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            //当ViewPager显示的Fragment发生改变是激发该方法
            @Override
            public void onPageSelected(int position) {
            }
        });
    }

    private void initView() {
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

        //设置ActionBar使用Tab导航方式
//        toolbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

//        //遍历pagerAdapter对象所包含的全部Fragment
//        //每个Fragment对应创建一个Tab标签
//        for (int i = 0; i < pagerAdapter.getCount(); i++){
//            toolbar.addTab(toolbar.newTab().setText(pagerAdapter.getPageTitle(i)).setTabListener(this));
//        }
    }

}
