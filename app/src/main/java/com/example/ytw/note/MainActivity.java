package com.example.ytw.note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Tool.MyTool;
import user.UserLogin;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //定义控件
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView mLogin;
    private TextView mRegister;
    private ImageView mUserImg;
    private ViewPager mViewPager;

    private List<Fragment> fragmentList;
    private MyPagerAdapter myPagerAdapter;

    //用作Login跳转时的requestCode
    private static final int CODE_RESULT = 1;   //activity跳转的requestCode
    private static final int LOGIN_OK = 0;      //登录成功的编号
    private static final int RETURN_DEL = 2;    //执行注销登录的编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("Mian", "onCreate");

        //初始化控件
        initView();

        //设置监听事件方法
        setListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_RESULT:
                if (data != null) {
                    switch (resultCode) {
                        case LOGIN_OK:  //如果用户已经登录成功，读取存储在本地的user_info.txt
                            showUserInfo();
                            break;
                        case RETURN_DEL:          //删除了本地存储用户信息的user_info.txt
                            Log.e("Main", "Delete");
                            mLogin.setText("登录");       //将原本的按钮登录还原
                            mRegister.setVisibility(View.VISIBLE);      //注册按钮可见
                            mUserImg.setImageResource(R.drawable.head);     //头像还原
                            break;
                    }
                }
                break;
        }
    }

//    @Override
//    protected void onPause() {
//        Log.e("Main", "onPause()");
//        if (mUserImg != null && mUserImg.getDrawable() != null){
//            Bitmap oldBitmap = ((BitmapDrawable)mUserImg.getDrawable()).getBitmap();
//            mUserImg.setImageDrawable(null);
//
//            if (oldBitmap != null){
//                oldBitmap.recycle();
//                oldBitmap = null;
//            }
//        }
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        Log.e("Main", "onStop()");
//        if (mUserImg != null && mUserImg.getDrawable() != null){
//            Bitmap oldBitmap = ((BitmapDrawable)mUserImg.getDrawable()).getBitmap();
//            mUserImg.setImageDrawable(null);
//
//            if (oldBitmap != null){
//                oldBitmap.recycle();
//                oldBitmap = null;
//            }
//        }
//        super.onStop();
//    }

    /**
     * 读取用户的本地信息。
     */
    private void showUserInfo() {
        //从本地读取登录用户的信息
        MyTool myTool = new MyTool();
        String user_info = myTool.readSDcard("/Note/user_info.txt");
        if (!TextUtils.isEmpty(user_info)) {
            String[] array = user_info.split("\\|");
            if (array != null) {
                mLogin.setText(array[1]);
                mRegister.setVisibility(View.INVISIBLE);
                if (!array[2].equals("未设置")) {
                    String path = Environment.getExternalStorageDirectory() + "/Note/img/" + array[2].split("\n")[0];
                    if (path != null) {
                        File mFile = new File(path);
                        Bitmap bm = null;
                        //若该文件存在
                        if (mFile.exists()) {
                            bm = BitmapFactory.decodeFile(path);
                            mUserImg.setImageBitmap(bm);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置监听事件
     */
    private void setListener() {
        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mUserImg.setOnClickListener(this);
    }

    /**
     * 控件绑定和初始化
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Secret Base");
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        mLogin = (TextView) view.findViewById(R.id.tv_login);
        mRegister = (TextView) view.findViewById(R.id.tv_register);
        mUserImg = (ImageView) view.findViewById(R.id.user_img);

        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        fragmentList = new ArrayList<>();
        fragmentList.add(Fragment.instantiate(this, Main_Fragment_Log.class.getName()));
        fragmentList.add(Fragment.instantiate(this, Main_Fragment_Plan.class.getName()));

        //初始化PagerAdapter
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        //给ViewPager设置Adapter
        mViewPager.setAdapter(myPagerAdapter);

        //判断是否已经有用户登录过,如果有就直接显示用户信息
        showUserInfo();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_index) {
            // Handle the camera action
        } else if (id == R.id.nav_site) {

        } else if (id == R.id.nav_theme) {

        } else if (id == R.id.nav_weekly) {

        } else if (id == R.id.nav_my) {

        } else if (id == R.id.nav_download) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
            case R.id.tv_register:
            case R.id.user_info:
            case R.id.user_img:
                File mFile = new File(Environment.getExternalStorageDirectory() + "/Note/user_info.txt");
                if (mFile.exists()) {
                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_name", mLogin.getText().toString());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CODE_RESULT);
                } else {
                    Intent intent = new Intent(MainActivity.this, UserLogin.class);
                    startActivityForResult(intent, CODE_RESULT);
                }
                break;
        }
    }
}
