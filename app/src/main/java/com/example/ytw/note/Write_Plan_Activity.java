package com.example.ytw.note;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import Tool.MyTool;
import service.PlanService;

/**
 * Created by YTW on 2016/6/3.
 */
public class Write_Plan_Activity extends AppCompatActivity {

    private Toolbar mToolbar;       //
    private EditText mTitle;
    private TextView mNowdate;
    private TextView mRemindTime;
    private EditText mContent;
    private String tempTitle;

    private Calendar mCalendar;     //创建一个日历对象
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    private static String REMIND_INFO_PATH;
    private boolean isPlanExist = false;
    public static String tempDate;


    private static final int CODE_RESULT = 1;
    private static final String TAG = "Write";
    private boolean planEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        //初始化控件
        init();

        //对
        mRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCalendar = Calendar.getInstance();             //获取日历对象
                mCalendar.setTimeInMillis(System.currentTimeMillis());


                mYear = mCalendar.get(Calendar.YEAR);
                mMonth = mCalendar.get(Calendar.MONTH);
                mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
                mMinute = mCalendar.get(Calendar.MINUTE);

                showDateAndTimePickerDialog();
            }
        });

    }

    private void changDateAndTimeView(int year, int month, int day, int hour, int minute) {


        //获取系统当前时间，并将时间显示到控件上
        SimpleDateFormat simTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(year - 1900, month, day, hour, minute);

        mRemindTime.setText(simTime.format(date));
        tempDate = simTime.format(date);
    }

    private void showDateAndTimePickerDialog() {
        //初始化一个DatePickerDialog
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                changDateAndTimeView(year, monthOfYear, dayOfMonth, mHour, mMinute);
            }

        }, mYear, mMonth, mDay).show();

        //初始化一个TimePickerDialog
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay ;
                mMinute = minute;
            }
        }, mHour, mMinute, true).show();
    }

    /**
     * 导入Toolbar菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plan_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.plan_save:

                //判断输入是否为空
                planEmpty = isPlanEmpty();

                if (planEmpty) {
                    String mmTitle = mTitle.getText().toString();
                    Log.d(TAG, "onOptionsItemSelected: " + mmTitle);
                    String mmRemindTime = mRemindTime.getText().toString();
                    String mmContent = mContent.getText().toString();

                    String remindInfo = mmTitle + "|" + mmRemindTime + "|" + mmContent;

                    if (isPlanExist) {
                        MyTool.deleteFile(new File(Environment.getExternalStorageDirectory() + "/Note/plan/" + tempTitle + ".txt"));
                    }

                    REMIND_INFO_PATH = Environment.getExternalStorageDirectory() + "/Note/plan/";

                    //该方法将remindInfo信息存储到手机
                    MyTool.writeTxtToFile(remindInfo, REMIND_INFO_PATH, mmTitle + ".txt");

                    Intent intent = new Intent();
                    intent.putExtra("title", mmTitle);
                    intent.putExtra("remindTime", mmRemindTime);
                    intent.putExtra("content", mmContent);
                    intent.putExtra("remindInfo", remindInfo);
                    intent.putExtra("is_exist", isPlanExist);
                    setResult(CODE_RESULT, intent);
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.plan_toolbar);
        mToolbar.setTitle("写提醒");
        mToolbar.setNavigationIcon(R.drawable.ic_ab_back_holo_light_am);
        setSupportActionBar(mToolbar);

        mTitle = (EditText) findViewById(R.id.plan_title);
        mRemindTime = (TextView) findViewById(R.id.plan_remind);
        mContent = (EditText) findViewById(R.id.plan_content);
        mNowdate = (TextView) findViewById(R.id.plan_date);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String content = intent.getStringExtra("content");

        Log.d(TAG, "init: info" + title + "  " + date);

        if (title != null) {
            mTitle.setText(title);
            tempTitle = title;
            if (!date.equals("")) {
                mRemindTime.setText(date);
                if (!content.equals("")) {
                    mContent.setText(content);
                    isPlanExist = true;
                    //获取系统当前时间，并将时间显示到控件上
//                    SimpleDateFormat simTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//                    mNowdate.setText(simTime.format(new Date()).toString());
                }
            }
        }

        SimpleDateFormat simTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        mNowdate.setText(simTime.format(new Date()).toString());
    }

    /**
     * 判断用户输入是为空空
     *
     * @return
     */
    public boolean isPlanEmpty() {

        if (mTitle.getText().equals("")) {
            Toast.makeText(Write_Plan_Activity.this, "请输入标题", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mRemindTime.getText().equals("提醒我于")) {
            Toast.makeText(Write_Plan_Activity.this, "请选择提醒的时间", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mContent.equals("")) {
            Toast.makeText(Write_Plan_Activity.this, "请输入提醒的内容", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mRemindTime.getText().toString().equals("选择提醒日期")) {
            return false;
        }

        return true;
    }

//    /**
//     * 计算两个日期型的时间相差多少时间
//     *
//     * @param startDate 开始日期
//     * @param endDate   结束日期
//     * @return
//     */
//    public String twoDateDistance(Date startDate, Date endDate) {
//
//        if (startDate == null || endDate == null) {
//            return null;
//        }
//        long timeLong = endDate.getTime() - startDate.getTime();
//        if (timeLong < 60 * 1000)
//            return timeLong / 1000 + "秒前";
//        else if (timeLong < 60 * 60 * 1000) {
//            timeLong = timeLong / 1000 / 60;
//            return timeLong + "分钟前";
//        } else if (timeLong < 60 * 60 * 24 * 1000) {
//            timeLong = timeLong / 60 / 60 / 1000;
//            return timeLong + "小时前";
//        } else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
//            timeLong = timeLong / 1000 / 60 / 60 / 24;
//            return timeLong + "天前";
//        } else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4) {
//            timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
//            return timeLong + "周前";
//        } else {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
//            return sdf.format(startDate);
//        }
//    }
}
