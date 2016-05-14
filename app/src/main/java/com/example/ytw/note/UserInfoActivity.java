package com.example.ytw.note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YTW on 2016/5/5.
 */
public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    //定义控件
    private ViewPager viewPage;
    private List<Fragment> fragments;
    private MyPagerAdapter pagerAdapter;
    private PagerTabStrip mPagerTab;
    private Toolbar mToolBar;
    private ImageView mUserImg;
    private TextView mUserIntro;
    private Button mExit;

    private Botton_Window botton_window;

    private String mUserName;

    //头像文件
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    //请求识别码
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    //裁剪后图片的宽(X)和高(Y)，480X480的正方形
    private static int output_X = 480;
    private static int output_Y = 480;

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

        //设置监听事件
        setListener();


        //为ViewPager组件绑定事件监听器
        viewPage.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            //当ViewPager显示的Fragment发生改变是激发该方法
            @Override
            public void onPageSelected(int position) {
            }
        });

        //连接服务器，获取用户信息
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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

    private void setListener() {
        mUserImg.setOnClickListener(this);
        mExit.setOnClickListener(this);
    }

    /**
     * 通过相机获取照片
     */
    private void getPhotoFromCamera() {
        if (hasSDcard()) {
            File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(Uri.fromFile(tempFile), "image/*");

            //设置裁剪
            intent.putExtra("crop", "true");
            // aspectX,aspectY:宽高比例
            intent.putExtra("outputX", output_X);
            intent.putExtra("outputY", output_Y);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, CODE_RESULT_REQUEST);
        } else {
            Toast.makeText(UserInfoActivity.this, "没有SD卡", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 从相册获取照片
     *
     * @param data
     */
    private void getPhotoFromGallery(Intent data) {
        try {
            Intent intentFromGallery = new Intent("com.android.camera.action.CROP");
            intentFromGallery.setDataAndType(data.getData(), "image/*");

            //设置裁剪
            intentFromGallery.putExtra("crop", "true");
            // aspectX,aspectY:宽高比例
            intentFromGallery.putExtra("outputX", output_X);
            intentFromGallery.putExtra("outputY", output_Y);
            intentFromGallery.putExtra("return-data", true);
            startActivityForResult(intentFromGallery, CODE_RESULT_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        mPagerTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_userinfo_img:
                //实例化底部窗口
                botton_window = new Botton_Window(this);
                //显示底部窗口
                botton_window.showAtLocation(UserInfoActivity.this.findViewById(R.id.userinfo_layout),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_exit:
                break;
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
                if (hasSDcard()) {
                    intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                }
                startActivityForResult(intentFromCamera, CODE_CAMERA_REQUEST);
                break;
        }
    }

    private boolean hasSDcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            //表示有SDcard,返回true
            return true;
        } else {
            return false;
        }
    }
}
