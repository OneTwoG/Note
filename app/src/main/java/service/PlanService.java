package service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ytw.note.MainActivity;
import com.example.ytw.note.R;
import com.example.ytw.note.Write_Plan_Activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Tool.MyTool;
import plan.PlanModel;

/**
 * Created by YTW on 2016/6/6.
 */
public class PlanService extends Service {

    private List<String> dateTimeList;
    private List<PlanModel> planModelList;

    private static final String TAG = "PlanService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SimpleDateFormat simTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String path = Environment.getExternalStorageDirectory() + "/Note/plan/";
        dateTimeList = new ArrayList<>();
        planModelList = new ArrayList<>();
        File planFile = new File(path);

        File[] files = planFile.listFiles();
        int dataTime = getFileContent(files);

        new Thread(new Runnable() {
            @Override
            public void run() {

                String date = simTime.format(new Date());

                for (int i = 0; i < dateTimeList.size(); i++) {
                    if (date.equals(dateTimeList.get(i))) {

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = i;
                        handler.sendMessage(msg);
                    }
                }
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int alarmTime = 1000; // 定时10s
        long trigerAtTime = SystemClock.elapsedRealtime() + alarmTime;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    //读取指定目录下的所有TXT文件的文件内容
    protected int getFileContent(File[] files) {
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

                        String planInfo = MyTool.readSDcard("/Note/plan/" + file.getName());

                        String[] array = planInfo.split("\\|");
                        PlanModel planModel = new PlanModel(array[0], array[1], array[2]);
                        planModelList.add(planModel);
                        dateTimeList.add(array[1] + ":00");
                    }
                }
            }

        }
        return dateTimeList.size();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        manager.cancel(pi);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int i = (int) msg.obj;
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

// 设置通知的基本信息：icon、标题、内容
                    builder.setSmallIcon(R.drawable.weibo);
                    builder.setContentTitle(planModelList.get(i).getTitle());
                    builder.setContentText(planModelList.get(i).getContent());
                    builder.setAutoCancel(true);
                    builder.setTicker("这是通知的ticker");
                    long[] vibrates = {1, 1000, 1000, 1000};
                    builder.setVibrate(vibrates);
                    builder.setLights(Color.GREEN, 1000, 1000);
                    builder.setDefaults(Notification.DEFAULT_ALL);

// 设置通知的点击行为：这里启动一个 Activity
                    Intent intent = new Intent(PlanService.this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(PlanService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);

// 发送通知 id 需要在应用内唯一
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(i, builder.build());
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
