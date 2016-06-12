package service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by YTW on 2016/6/6.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //设置通知内容并在onReceive（）这个函数执行时开启

        Intent i = new Intent(context, PlanService.class);
        context.startService(i);
    }
}
