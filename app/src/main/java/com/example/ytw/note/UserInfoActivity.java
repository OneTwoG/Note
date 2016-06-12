package com.example.ytw.note;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Tool.MyTool;
import plan.PlanModel;
import timeline.TimeLineAdapter;
import uitl.HttpUtil;

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
    private Button mExit;
    private List<String> planList;

    private TimeLineAdapter mTimeLineAdapter;

    private String mUserInfo;               //用户的总信息，保存到本地user_info.txt
    private String mUserName;               //存储用户名的变量
    private String mUserNumber;             //存储用户账号的变量

    String IMGNAME = "note.png";            //截图后保存的照片的名称
    String PATHIMG = "/Note/img/";          //保存照片的路径
    private static final int GALLERY = 1;   //从相册获取照片编号
    private static final int CUT_OK = 2;    //截图成功编号
    private static final int CAMERA = 3;    //从照相机获取照片编号

    private Bitmap bm = null;   //用于存储照片的Bitmap

    private HttpUtil httpUtil = new HttpUtil();
    private MyTool myTool = new MyTool();
    private Botton_Window botton_window;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_content);

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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GALLERY:
                /*这是从相册获取照片*/
                if (data == null) {
                    return;
                }
                startImageZoom(data.getData());
                break;
            case CAMERA:
                /*同过照相机获取照片*/
                startImageZoom(data.getData());
                break;
            case CUT_OK:
                if (data == null) {
                    return;
                }

                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = null;
                    bitmap = extras.getParcelable("data");

                    //将截取的图片保存
                    savePic(bitmap, IMGNAME);
                    new Thread() {
                        @Override
                        public void run() {
                            //上传图片
                            String request = null;
                            try {
                                //上传图片，拿到返回值
                                request = httpUtil.uploadFileToPhpServer("http://139.129.39.66/api/user_updateimg.php?user_number=" + mUserNumber,
                                        Environment.getExternalStorageDirectory().getCanonicalPath() + PATHIMG + IMGNAME);
                                JSONObject jsonObject = new JSONObject(request);
                                int resultCode = jsonObject.getInt("ret_code");
                                String resultPhoto = jsonObject.getString("ret_photo");

                                String oldPhoto = getUserInfo();

                                //将获取的到的resultPhoto拼接到mUser_info
                                mUserInfo = mUserNumber + "|" + mUserName + "|" + resultPhoto;
                                //将拼接好的用户信息保存
                                myTool.SDcardSave(Environment.getExternalStorageDirectory()
                                        + "/Note/user_info.txt", mUserInfo);
                                if (resultCode == 0) {
                                    //成功
                                    //删除oldPhoto
                                    MyTool.deleteFile(new File(Environment.getExternalStorageDirectory() + "/Note/img/" + oldPhoto));
                                    handler.sendEmptyMessage(0);
                                } else {
                                    //失败
                                    handler.sendEmptyMessage(1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(1);
                            }
                        }
                    }.start();

                }
                break;
        }
    }

    /**
     * 当Activity被销毁之前，在这里执行释放资源
     */
    @Override
    protected void onDestroy() {
        Log.d("UserInfo--", "执行在onDestroy");
        if (mUserImg != null && mUserImg.getDrawable() != null) {

            if (bm != null){
                bm.recycle();
            }

            mUserImg.setImageDrawable(null);
        }
        super.onDestroy();
    }

    /**
     * 保存裁剪之后的图片
     */
    private void savePic(Bitmap mBitmap, String bitName) {
        File file = new File(Environment.getExternalStorageDirectory() + PATHIMG + bitName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (mBitmap.compress(Bitmap.CompressFormat.PNG, 120, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //从服务器上下载上传的图片，并显示头上上传成功
                    downPhoto();
                    Toast.makeText(UserInfoActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(UserInfoActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    mUserImg.setImageBitmap(bm);
                    break;
            }
        }
    };

    /**
     * 将服务器的头像下载下来
     */
    private void downPhoto() {
        new Thread() {
            @Override
            public void run() {

                //读取本地的user_info.txt
                String fileName = getUserInfo();
                try {
                    //获取图片的URL
                    URL url = new URL("http://139.129.39.66/img/" + fileName);
                    try {
                        //打开该URL对应的资源的输入流
                        InputStream is = url.openStream();
                        //从InputStream中解析出图片
                        bm = BitmapFactory.decodeStream(is);
                        is.close();

                        //打开手机文件对应的资源的输入流
                        is = url.openStream();
                        //打开手机文件对应的输出流
                        OutputStream os = new FileOutputStream(new File(Environment.getExternalStorageDirectory()
                                + "/Note/img/" + fileName));
                        byte[] buff = new byte[1024];
                        int hasRead = 0;
                        //将URL对应资源下载到本地
                        while ((hasRead = is.read(buff)) > 0) {
                            os.write(buff, 0, hasRead);
                        }
                        //发送消息，通知UI组建显示该图片
                        handler.sendEmptyMessage(2);
                        is.close();
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @Nullable
    private String getUserInfo() {
        String fileName = null;
        String user_info = myTool.readSDcard("/Note/user_info.txt");
        if (!TextUtils.isEmpty(user_info)) {
            String[] array = user_info.split("\\|");
            if (array[2] != "未设置") {
                fileName = array[2];
            }
        }
        return fileName;
    }

    private void setListener() {
        mUserImg.setOnClickListener(this);
        mExit.setOnClickListener(this);
    }

    /**
     * 裁剪图片
     *
     * @param
     */
    private void startImageZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 90);
        intent.putExtra("outputY", 90);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                setResult(0, intent);
                finish();
                Log.d("UserInfo--", "执行在onDestroy之前");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 对Activity进行初始化
     */
    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.userinfo_toolbar);
        viewPage = (ViewPager) findViewById(R.id.pager);
        mPagerTab = (PagerTabStrip) findViewById(R.id.pager_title);

        //设置Tab的字体大小
        mPagerTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        //设置Tab背景颜色
        mPagerTab.setBackgroundResource(R.color.overflowTextColor);
        mPagerTab.setTextSpacing(100);

        //初始化两个两个Fragment
        fragments = new ArrayList<>();
        fragments.add(Fragment.instantiate(this, Fragment_plan.class.getName()));
        fragments.add(Fragment.instantiate(this, Fragment_log.class.getName()));

        //给ViewPager设置Adapter
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        viewPage.setAdapter(pagerAdapter);

        mUserImg = (ImageView) findViewById(R.id.iv_userinfo_img);
        mExit = (Button) findViewById(R.id.btn_exit);

        //从本地读取用户信息，然后显示
        showUserInfo();

        mToolBar.setTitle(mUserName);
        mToolBar.setNavigationIcon(R.drawable.dialog_ic_close_focused_holo_light);
        setSupportActionBar(mToolBar);
    }

    /**
     * 读取本地存储的信息，并显示
     */
    private void showUserInfo() {
        //从本地读取登录用户的信息
        String user_info = myTool.readSDcard("/Note/user_info.txt");

        if (!TextUtils.isEmpty(user_info)) {
            String[] array = user_info.split("\\|");
            if (array != null) {
                mUserNumber = array[0];
                mUserName = array[1];

                if (!array[2].equals("未设置")) {
                    String path = Environment.getExternalStorageDirectory() + PATHIMG + array[2].split("\n")[0];
                    if (path != null) {
                        File file = new File(path);

                        //如果该文件存在
                        if (file.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/Note/img/" + array[2]);
                            mUserImg.setImageBitmap(bitmap);
                        }
                    }
                }
            }
        }
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
                myTool.deleteFile(new File(Environment.getExternalStorageDirectory() + "/Note/user_info.txt"));

                planList = new ArrayList<>();

                String path = Environment.getExternalStorageDirectory() + "/Note/plan/";

                File planFile = new File(path);

                File[] files = planFile.listFiles();
                getFileContent(files);
                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                setResult(2, intent);
                startActivity(intent);
                break;
            case R.id.btn_gallery:
                //选择图片
                selectImage();
                botton_window.dismiss();
                break;
            case R.id.btn_camera:

                botton_window.dismiss();
                break;
        }
    }

    /**
     * 打开相册选择图片
     */
    protected void selectImage() {
        //打开本地相册,这里是通过启动一个系统的隐式意图打开的本地相册
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, GALLERY);
    }

    //读取指定目录下的所有TXT文件的文件内容
    protected String getFileContent(File[] files) {
        String content = "";
        if (files != null) {    // 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                //检查此路径名的文件是否是一个目录(文件夹)
                if (file.isDirectory()) {
                    Log.i("zeng", "若是文件目录。继续读1" +
                            file.getName().toString() + file.getPath().toString());
                    getFileContent(file.listFiles());
                    Log.i("zeng", "若是文件目录。继续读2" +
                            file.getName().toString() + file.getPath().toString());
                } else {
                    if (file.getName().endsWith(".txt")) {//格式为txt文件

                        file.delete();

                    }
                }
            }

        }
        return content;
    }
}
