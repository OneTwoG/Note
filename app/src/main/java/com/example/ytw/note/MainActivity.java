package com.example.ytw.note;

import android.content.Intent;
import android.graphics.Bitmap;
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

    //头像文件
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    //请求识别码
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    //裁剪后图片的宽(X)和高(Y)，480X480的正方形
    private static int output_X = 480;
    private static int output_Y = 480;

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
            case CODE_GALLERY_REQUEST:
                /*这是从相册获取照片*/
                getPhotoFromGallery(data);
                break;
            case CODE_CAMERA_REQUEST:
                /*同过照相机获取照片*/
                /*再次判断SDcard*/
                getPhotoFromCamera();
                break;
            case CODE_RESULT_REQUEST:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        mUserImg.setImageBitmap(photo);
                    }
                }
                break;
        }
    }

    /**
     * 通过相机获取照片
     */
    private void getPhotoFromCamera() {
        if (hasSDcard()){
            File tempFile  = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(Uri.fromFile(tempFile), "image/*");

            //设置裁剪
            intent.putExtra("crop", "true");
            // aspectX,aspectY:宽高比例
            intent.putExtra("outputX", output_X);
            intent.putExtra("outputY", output_Y);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, CODE_RESULT_REQUEST);
        }else {
            Toast.makeText(MainActivity.this, "没有SD卡", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 从相册获取照片
     * @param data
     */
    private void getPhotoFromGallery(Intent data) {
        try{
            Intent intentFromGallery = new Intent("com.android.camera.action.CROP");
            intentFromGallery.setDataAndType(data.getData(), "image/*");

            //设置裁剪
            intentFromGallery.putExtra("crop", "true");
            // aspectX,aspectY:宽高比例
            intentFromGallery.putExtra("outputX", output_X);
            intentFromGallery.putExtra("outputY", output_Y);
            intentFromGallery.putExtra("return-data", true);
            startActivityForResult(intentFromGallery, CODE_RESULT_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
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
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MainActivity.this, UserLogin.class);
                    startActivityForResult(intent, CODE_LOGIN);
                }
                break;
//            case R.id.user_img:
//                //关闭侧滑栏
//                drawer.closeDrawer(GravityCompat.START);
//                //实例化底部窗口
//                botton_window = new Botton_Window(this);
//                //显示底部窗口
//                botton_window.showAtLocation(MainActivity.this.findViewById(R.id.drawer_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//                break;
            case R.id.btn_gallery:
                Intent intentFromGallery = new Intent();
                //设置文件类型
                intentFromGallery.setType("image/*");
                //允许用户选择特殊种类的数据，并返回（特殊种类的数据：照一张相片或录一段音）
                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
                botton_window.dismiss();
                break;
            case R.id.btn_camera:
                Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //判断SDcard是否可用
                if (hasSDcard()){
                    intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                }
                startActivityForResult(intentFromCamera, CODE_CAMERA_REQUEST);
                break;
        }
    }

    private boolean hasSDcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)){
            //表示有SDcard,返回true
            return true;
        }else {
            return false;
        }
    }

}
