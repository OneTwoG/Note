package com.example.ytw.note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
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
    private RelativeLayout mUserInfo;

    //
    private static final int LOGIN_NULL = 1;
    private static final int LOGIN_OK = 0;
    private static final int LOGIN_ERROR = 2;


    //当前登录状态
    private boolean loginState = false;

    //用作Login跳转时的requestCode
    private static final int CODE_LOGIN = 1;


    Botton_Window botton_window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        initView();

        //设置监听事件方法
        setListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_LOGIN:

                if (data != null){
                    Bundle bundle = data.getExtras();
                    if (resultCode == LOGIN_OK){
                        mLogin.setText(bundle.getString("user_name"));
                        loginState = bundle.getBoolean("isLogin");
                        mRegister.setVisibility(View.INVISIBLE);
                    }
                }
                break;
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
        mUserInfo = (RelativeLayout) view.findViewById(R.id.user_info);

        mUserImg.setImageResource(R.drawable.head);
        mUserImg.setDrawingCacheEnabled(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
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

                if (loginState){
                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_name", mLogin.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MainActivity.this, UserLogin.class);
                    startActivityForResult(intent, CODE_LOGIN);
                    loginState = true;
                }
                break;
        }
    }
}
