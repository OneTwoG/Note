package com.example.ytw.note;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Tool.MyTool;
import plan.PlanModel;
import timeline.TimeLineAdapter;
import timeline.TimeLineModel;
import uitl.HttpUtil;

/**
 * Created by YTW on 2016/5/22.
 */
public class Main_Fragment_Log extends Fragment {

    private RecyclerView mRecyclerView;                         //RecyclerView控件
    private TimeLineAdapter mTimeLineAdapter;                   //自定义的空间
    private FloatingActionButton mFab;                          // 浮动按钮
    public List<TimeLineModel> listData = new ArrayList<>();    //TimeLineModel 集合
    private View view;                                          // fragment布局
    private SwipeRefreshLayout mRefresh;
    LinearLayoutManager linearLayoutManager;

    private static final int LOG_FRAGMENT = 0x00;       //Activity跳转的tag

    private static final String TAG = "Main_Fragment";          //Debug的TAG

    private String type;        //用于判断保存或者更改
    private int log_id;         //返回日记id

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment_log, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.log_recycleerview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //初始化FloatingActionButton按钮控件
        mFab = (FloatingActionButton) getActivity().findViewById(R.id.log_fab);

        //编辑日记按钮点击事件
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断文件是否存在
                File mFile = new File(Environment.getExternalStorageDirectory() + "/Note/user_info.txt");
                if (mFile.exists()) {
                    //点击这个按钮，进入写日记界面Toast.makeText(getActivity(), "点击之后进入编写日记界面", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), Write_Log_Activity.class);
                    startActivityForResult(intent, LOG_FRAGMENT);
                } else {
                    Toast.makeText(getActivity(), "请先登录...", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();     //得到拖拽ViewHolder的position
                int toPosition = target.getAdapterPosition();           //得到目标ViewHolder的position
                if (fromPosition < toPosition) {
                    //分别把中间所有的item的位置交换
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(listData, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(listData, i, i - 1);
                    }
                }
                mTimeLineAdapter.notifyItemMoved(fromPosition, toPosition);
                //返回true表示执行拖动
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);

                    deletedLog(listData.get(viewHolder.getAdapterPosition()).getId());
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        //初始化数据
        initData();
    }

    /**
     * 删除日记
     *
     * @param adapterPosition
     */
    private void deletedLog(int adapterPosition) {
        final String address = "http://139.129.39.66/api/log_delete.php";

        final String log_id = "id=" + adapterPosition;

        Log.d(TAG, "deletedLog: " + address + "?" + log_id);

        new Thread() {
            @Override
            public void run() {
                String response = HttpUtil.sendPost(address, log_id);

                if (response != null) {
                    try {
                        JSONObject resultJSON = new JSONObject(response);
                        int ret_code = resultJSON.getInt("ret_code");

                        if (ret_code == 0){
                            //成功
                            handler.sendEmptyMessage(3);

                        }else {
                            //失败
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //删除失败
                }
            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOG_FRAGMENT:
                if (resultCode == 0x0) {
                    //保存成功，刷新主界面的数据
                    if (data != null) {

                        //获取从Write_Log_Activity返回的数据
                        log_id = data.getIntExtra("log_id", 0);     //返回的日记id
                        type = data.getStringExtra("type");         //返回的操作类型
                        getLogFormServer(type, log_id);             //从服务器获取数据，并更新UI
                    }
                }
                break;
        }
    }

    /**
     * 界面显示之前调用
     */
    @Override
    public void onResume() {

        //判断用户信息文件是否存在
        File mFile = new File(Environment.getExternalStorageDirectory() + "/Note/user_info.txt");

        if (!mFile.exists()) {       //如果不存在，清空listData数据
            listData.clear();
        } else {
            Log.d(TAG, "onResume: ");
//            handler.sendEmptyMessage(2);        //当Insert第一条数据时起作用
        }
        super.onResume();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取用户信息文件路径
        String path = Environment.getExternalStorageDirectory() + "/Note/user_info.txt";

        //判断文件是否存在
        if (path != null) {
            if (listData.size() == 0) {             //判断当前listData
                if (mTimeLineAdapter == null) {     //判断
                    //从服务器上获取数据
                    type = "init";                  //将获取类型设置为 init（初始化）
                    int default_id = 0;             //设置初始化默认ID
                    getLogFormServer(type, default_id);     //  从服务器获取数据
                }
            }
        }
    }

    /**
     * 从服务器获取数据的方法
     *
     * @param type //数据操作类型,Insert Update
     * @param id   // 日记的ID
     */
    public void getLogFormServer(final String type, final int id) {

        String mValue = null;           //存储用户账号
        String mAddress = null;         //存储api地址
        if (type.equals("init") || type.equals("insert ")) {       //初始化更新
            mValue = "user_number=" + getUserNumber();
            mAddress = "http://139.129.39.66/api/log_query.php";
        } else {                         //用户修改更新
            mValue = "id=" + id;
            mAddress = "http://139.129.39.66/api/log_query_id.php";
        }
        //从本地获取用户账号
        final String value = mValue;

        //服务器地址
        final String address = mAddress;

        new Thread() {
            @Override
            public void run() {
                //通过该方法将ID获取Number上传获取返回的日记内容
                String response = HttpUtil.sendPost(address, value);

                String json_log = "";                     //存储服务器返回的所有该用户的日记

                if (response == null) {
                    handler.sendEmptyMessage(0);        //从数据库获取数据失败
                } else {
                    //数据获取成功，解析从服务器返回的数据
                    try {
                        JSONObject returnJSON = new JSONObject(response);
                        int ret_code = returnJSON.getInt("ret_code");
                        if (ret_code == 0) {            //获取数据成功

                            //解析返回的JSON数据
                            JSONArray jsonArray = returnJSON.getJSONArray("page_result");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject nowjson = jsonArray.getJSONObject(i);

                                int mmId;                           //用户区分修改和插入

                                if (jsonArray.length() == 1) {      //这种情况是用户修改
                                    mmId = id;
                                } else {                            //这种情况是用户插入数据
                                    mmId = nowjson.getInt("id");
                                }

                                String mmTitle = nowjson.getString("log_title");     //获取日记标题
                                String mmDate = nowjson.getString("log_time");       //获取日记时间
                                String mmClass = nowjson.getString("log_class");    //获取日记类型
                                String mmContent = nowjson.getString("log_content"); //获取日记内容

                                String year = "";           //日记的年份
                                String month = "";          //日记的月份
                                String day = "";            //日记的天数
                                String time = "";           //具体时刻

                                //分离出日记的年月日和具体的时间
                                if (mmDate != null) {
                                    String[] array = mmDate.split("\\b");
                                    year = array[1];
                                    month = array[3];
                                    day = array[5];
                                    time = array[7] + array[8] + array[9] + array[10] + array[11];
                                }

                                json_log += mmId + "|" + mmTitle + "|" + time + "|" + mmClass + "|" + mmContent
                                        + "|" + year + "|" + month + "|" + day;
                                //将获取的数据封装成TimeLineModel对象
                                TimeLineModel model = new TimeLineModel(mmId, mmTitle, time, mmClass, mmContent, year, month, day);

                                //将TimeLineModel对象添加到listData集合
                                listData.add(model);
                            }

                            //创建一个Message对象
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = json_log;

                            Log.d(TAG, "run: " + jsonArray.length() + "     type   " + type);
                            //如果返回的数据长度是1 类型是update，sendMessage(msg)
                            if (jsonArray.length() == 1 && type.equals("update")) {
                                handler.sendMessage(msg);
                            } else {
                                //数据获取成功，通知Handler更新UI，初始化时调用
                                handler.sendEmptyMessage(1);
                            }

                        } else {
                            //数据为空或者网络错误
                            handler.sendEmptyMessage(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    /**
     * 获取用户账号
     *
     * @return
     */
    private String getUserNumber() {
        //从本地获取用户数据
        String user_info = MyTool.readSDcard("/Note/user_info.txt");
        String number = "";
        if (!TextUtils.isEmpty(user_info)) {
            String array[] = user_info.split("\\|");
            if (array != null) {
                number = array[0];
            }
        }
        return number;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Log.d(TAG, "handleMessage: 1");
                    mTimeLineAdapter = new TimeLineAdapter(listData);       //创建TimeLineAdapter对象
                    mTimeLineAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mTimeLineAdapter);
                    /**
                     * Item的点击事件
                     */
                    mTimeLineAdapter.setOnItemClickListener(new TimeLineAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClickListener(View view, int position) {
                            Intent intent = new Intent(getActivity(), Write_Log_Activity.class);
                            TimeLineModel mModel = listData.get(position);
                            String date = mModel.getYear() + "-" + mModel.getMonth()
                                    + "-" + mModel.getDay() + " " + mModel.getTime();
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", mModel.getId());
                            bundle.putString("title", mModel.getTitle());
                            bundle.putString("date", date);
                            bundle.putString("class", mModel.getTitleClass());
                            bundle.putString("content", mModel.getContent());
                            intent.putExtras(bundle);
                            startActivityForResult(intent, LOG_FRAGMENT);
                        }

                        @Override
                        public void onItemLongClickListener(View view, int position) {
                            Toast.makeText(getActivity(), "点击了长按事件", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 2:
                    //如果是update就把数据清空
                    //再重新从服务器上获取数据
                    if (type.equals("init")) {
                    } else if (type.equals("update")) {
                        listData.clear();
                        mTimeLineAdapter.notifyDataSetChanged();
                        //从服务器上获取数据
                        type = "init";
                        int default_id = 0;
                        getLogFormServer(type, default_id);
                    }
                    break;
                case 3:
                    Toast.makeText(getActivity(), "日记删除成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

//    public void isRefresh(boolean isRefresh){
//
//        if (isRefresh == true){
//            listData.clear();
//            Log.d(TAG, "handleMessage: 清空了没" + listData.size());
//                        mTimeLineAdapter = new TimeLineAdapter(listData);       //创建TimeLineAdapter对象
////            mTimeLineAdapter.notifyDataSetChanged();
//            //从服务器上获取数据
//            type = "init";
//            int default_id = 0;
//            getLogFormServer(type, default_id);
//        }
//    }
}