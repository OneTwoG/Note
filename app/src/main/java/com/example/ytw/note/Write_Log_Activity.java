package com.example.ytw.note;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Tool.MyTool;
import db.MyDatabaseHelper;
import uitl.HttpUtil;

import static com.example.ytw.note.R.id.write_toolbar;

/**
 * Created by YTW on 2016/5/26.
 */
public class Write_Log_Activity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mTitle;
    private EditText mContent;
    private TextView mTime;
    private TextView mClass;
    private int mId = 0;

    private String user_number;

    private static final int INTENT_RESULT = 0x0;
    public static final String TAG = "Write";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        //初始化控件
        init();

        //初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {

        //读取本地用户数据，获取登录的用户
        String user_info = new MyTool().readSDcard("/Note/user_info.txt");
        if (!TextUtils.isEmpty(user_info)) {
            String[] array = user_info.split("\\|");
            if (array != null) {
                user_number = array[0];
            }
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {               //当用户点击某篇日记

            mId = bundle.getInt("id");
            mTitle.setText(bundle.getString("title"));
            mTime.setText(bundle.getString("date"));
            mClass.setText(bundle.getString("class"));
            mContent.setText(bundle.getString("content"));
        }else {
            //获取系统当前时间，并将时间显示到控件上
            SimpleDateFormat simTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            mTime.setText(simTime.format(new Date()).toString());
        }

    }

    /**
     * 引入菜单布局文件
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_mwnu, menu);
        return true;
    }

    /**
     * 菜单项选择
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //返回之前弹出提示框，是否要保存
                finish();
                break;
            case R.id.action_save:

                //保存全部内容，并上传服务器
                String mmTitle = mTitle.getText().toString();       //临时变量，保存日记标题
                String mmTime = mTime.getText().toString();         //临时变量，保存日记时间
                String mmContent = mContent.getText().toString();   //临时变量，保存日记内容
                String mmClass = mClass.getText().toString();       //临时变量，保存日记类型
                String logInfo;                                     //临时变量，保存日记所有信息

                if (mmTitle.equals("") || mmTime.equals("") || mmClass.equals("") || mmContent.equals("")){
                    Toast.makeText(Write_Log_Activity.this, "日记内容为空", Toast.LENGTH_SHORT).show();
                }else {
                    /**
                     * 对ID进行判断，如果有id就是更改，没有就是插入
                     */

                    if (mId == 0) {          //如果是等于0，则插入

                        //上传的日记信息
                        logInfo = "user_number=" + user_number
                                + "&title=" + mmTitle + "&time=" + mmTime
                                + "&class=" + mmClass + "&content=" + mmContent;

                        final String address = "http://139.129.39.66/api/log_insert.php";    //服务器地址
                        //上传服务器的方法
                        sendLogToServer(address, logInfo);
                    } else {                 //如果不是，则修改
                        logInfo = "id=" + mId +"&title=" + mmTitle + "&time=" + mmTime
                                + "&class=" + mmClass + "&content=" + mmContent;

                        final String address = "http://139.129.39.66/api/log_update.php";    //服务器地址

                        sendLogToServer(address, logInfo);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendLogToServer(final String address, final String logInfo) {
        new Thread() {
            @Override
            public void run() {
                String respones = HttpUtil.sendPost(address, logInfo);

                //对返回结果进行判断
                if (respones == null) {
                    //上传失败
                } else {
                    try {

                        //对返回的json进行解析
                        JSONObject returnJson = new JSONObject(respones);
                        int ret_code = returnJson.getInt("ret_code");
                        int log_id;
                        if (mId == 0){
                            log_id = returnJson.getInt("log_id");
                        }else {
                            log_id = mId;
                        }

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = log_id;

                        if (ret_code == 0) {
                            //上传成功
                            handler.sendMessage(msg);
                        } else {
                            //上传失败
                            handler.sendEmptyMessage(2);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                }


            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent();
                    if (mId != 0){
                        Toast.makeText(Write_Log_Activity.this, "日记修改成功", Toast.LENGTH_SHORT).show();
                        intent.putExtra("type", "update");
                        Log.d(TAG, "handleMessage: update");
                    }else {
                        Toast.makeText(Write_Log_Activity.this, "日记保存成功", Toast.LENGTH_SHORT).show();                        intent.putExtra("type", "update");
                        intent.putExtra("type", "insert");
                    }
                    intent.putExtra("log_id", (Integer) msg.obj);
                    setResult(INTENT_RESULT, intent);
                    finish();
                    break;
                case 2:
                    Toast.makeText(Write_Log_Activity.this, "没有内容更改", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 初始化控件
     */
    private void init() {
        mToolbar = (Toolbar) findViewById(write_toolbar);
        mToolbar.setTitle("写日记");
//        mToolbar.setLogo(R.drawable.ic_note1);
        mToolbar.setNavigationIcon(R.drawable.ic_ab_back_holo_light_am);
        setSupportActionBar(mToolbar);

        mTitle = (EditText) findViewById(R.id.write_title);
        mContent = (EditText) findViewById(R.id.write_content);
        mTime = (TextView) findViewById(R.id.write_time);
        mClass = (TextView) findViewById(R.id.write_class);

    }
}
