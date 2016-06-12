package com.example.ytw.note;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import Tool.MyTool;
import plan.PlanModel;
import plan.PlanRecyclerAdapter;
import service.PlanService;

/**
 * Created by YTW on 2016/5/22.
 */
public class Main_Fragment_Plan extends Fragment {

    private RecyclerView mRecyclerView;
    private List<PlanModel> list;
    private PlanRecyclerAdapter planAdapter;
    private FloatingActionButton mFab;                          // 浮动按钮
    private int tempPosition;

    private static final int CODE_START_RESULT = 1;

    private NotificationManager mManager;
    private Notification mNotification;
    private static final int NOTIFYID_1 = 1;

    private static final String TAG = "Plan";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_plan, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();

        bindView();

        planAdapter = new PlanRecyclerAdapter(getActivity(), list);
        mRecyclerView.setAdapter(planAdapter);

        planAdapter.setOnClickListener(new PlanRecyclerAdapter.OnItemClickListener() {
            @Override
            public void ItemClickListener(View view, int postion) {
//                Toast.makeText(getActivity(), "点击了：" + postion, Toast.LENGTH_SHORT).show();
                tempPosition = postion;
                Intent intent = new Intent(getActivity(), Write_Plan_Activity.class);
                PlanModel model = list.get(postion);
                intent.putExtra("title", model.getTitle());
                intent.putExtra("date", model.getDate());
                intent.putExtra("content", model.getContent());
                startActivityForResult(intent, CODE_START_RESULT);

            }

            @Override
            public void ItemLongClickListener(View view, int postion) {
                //长按删除
                list.remove(postion);
                planAdapter.notifyItemRemoved(postion);
            }
        });

        //编辑日记按钮点击事件
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断文件是否存在
                File mFile = new File(Environment.getExternalStorageDirectory() + "/Note/user_info.txt");
                if (mFile.exists()) {
                    //点击这个按钮，进入写日记界面Toast.makeText(getActivity(), "点击之后进入编写日记界面", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), Write_Plan_Activity.class);
                    startActivityForResult(intent, CODE_START_RESULT);
                } else {
                    Toast.makeText(getActivity(), "请先登录...", Toast.LENGTH_SHORT).show();
                }

//                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
//
//// 设置通知的基本信息：icon、标题、内容
//                builder.setSmallIcon(R.drawable.weibo);
//                builder.setContentTitle("My notification");
//                builder.setContentText("Hello World!");
//                builder.setAutoCancel(true);
//                builder.setTicker("这是通知的ticker");
//
//// 设置通知的点击行为：这里启动一个 Activity
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//
//// 发送通知 id 需要在应用内唯一
//                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(0, builder.build());
            }
        });
    }

    private void bindView() {
        mManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.plan_recycler);
        //初始化FloatingActionButton按钮控件
        mFab = (FloatingActionButton) getActivity().findViewById(R.id.plan_fab);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));//设置RecyclerView布局管理器为2列垂直排布


        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();     //得到拖拽ViewHolder的position
                int toPosition = target.getAdapterPosition();           //得到目标ViewHolder的position
                if (fromPosition < toPosition){
                    //分别把中间所有的item的位置交换
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(list, i, i+ 1);
                    }
                }else {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(list, i , i - 1);
                    }
                }
                planAdapter.notifyItemMoved(fromPosition, toPosition);
                //返回true表示执行拖动
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);

                    PlanModel mmPlanModel = list.get(viewHolder.getAdapterPosition());
                    MyTool.deleteFile(new File(Environment.getExternalStorageDirectory() + "/Note/plan/" + mmPlanModel.getTitle() + ".txt"));

                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_START_RESULT:
                if (resultCode == 1) {
                    //接收返回的内容，依次 显示，保存
                    if (data != null) {
                        //显示
                        String title = data.getStringExtra("title");
                        String content = data.getStringExtra("content");
                        String time = data.getStringExtra("remindTime");
                        boolean isExist = data.getBooleanExtra("is_exist", false);

                        if (isExist) {
                            planAdapter.removeItem(tempPosition);
                        }
                        PlanModel planModel = new PlanModel(title, time, content);
                        planAdapter.addItem(planModel, list.size());
                    }
                }
                break;
        }
    }

    private void initData() {
        list = new ArrayList();

        String path = Environment.getExternalStorageDirectory() + "/Note/plan/";

        File planFile = new File(path);

        File[] files = planFile.listFiles();
        getFileContent(files);
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

                        String planInfo = MyTool.readSDcard("/Note/plan/" + file.getName());

                        String[] array = planInfo.split("\\|");
                        PlanModel planModel = new PlanModel(array[0], array[1], array[2]);
                        list.add(planModel);

                    }
                }
            }

        }
        return content;
    }
}
