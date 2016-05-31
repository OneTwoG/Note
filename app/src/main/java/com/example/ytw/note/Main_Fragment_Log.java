package com.example.ytw.note;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Tool.MyTool;
import timeline.TimeLineAdapter;
import timeline.TimeLineModel;
import uitl.HttpUtil;

/**
 * Created by YTW on 2016/5/22.
 */
public class Main_Fragment_Log extends Fragment {

    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private FloatingActionButton mFab;
    private List<TimeLineModel> listData = new ArrayList<>();
    private View view;

    private static final int LOG_FRAGMENT = 0x00;       //Activity跳转的tag

    private static final String TAG = "Main_Fragment";          //Debug的TAG

    private String type;
    private int tempPosition;

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

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
                    startActivityForResult(intent,LOG_FRAGMENT);
                } else {
                    Toast.makeText(getActivity(), "请先登录...", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //初始化
        initData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                switch (requestCode){
                    case LOG_FRAGMENT:
                            if (resultCode == 0x0){
                                //保存成功，刷新主界面的数据
                                if (data != null){
                                    int log_id = data.getIntExtra("log_id", 0);
                                    type = "update";
                                    getLogFormServer(type, log_id);
                                }
                            }
                        break;
                }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String path = Environment.getExternalStorageDirectory() + "/Note/user_info.txt";
        if (path != null) {
            if (listData.size() == 0) {
                if (mTimeLineAdapter == null) {
                    //从服务器上获取数据
                    type = "init";
                    int default_id = 0;
                    getLogFormServer(type, default_id);
                }
            }
        }
    }

    /**
     * 从服务器上获取日记数据
     */
    private void getLogFormServer(String type, final int id) {

        String mValue = null;
        String mAddress = null;
        if (type.equals("init")){       //初始化更新
            mValue = "user_number=" + getUserNumber();
            mAddress = "http://139.129.39.66/api/log_query.php";
        }else {                         //用户修改更新
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
                            JSONArray jsonArray = returnJSON.getJSONArray("page_result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject nowjson = jsonArray.getJSONObject(i);

                                int mmId;

                                if (jsonArray.length() == 1){
                                    mmId = id;
                                }else {
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

                                if (mmDate != null) {
                                    String[] array = mmDate.split("\\b");
                                    year = array[1];
                                    month = array[3];
                                    day = array[5];
                                    time = array[7] + array[8] + array[9] + array[10] + array[11];
                                }

                                json_log += mmId + "|" + mmTitle + "|" + time + "|" + mmClass + "|" + mmContent
                                            + "|" + year + "|" + month + "|" + day;

                                TimeLineModel model = new TimeLineModel(mmId,mmTitle, time, mmClass, mmContent, year, month, day);
                                listData.add(model);
                            }

                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = json_log;

                            if (jsonArray.length() == 1){
                                handler.sendMessage(msg);
                            }

                            //数据获取成功，通知Handler更新UI
                            handler.sendEmptyMessage(1);

                        } else {
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
                    Toast.makeText(getActivity(), "数据获取失败，请检查网络设置", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    mTimeLineAdapter = new TimeLineAdapter(listData);
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
                            tempPosition = position;
                            String date = mModel.getYear() + "-" + mModel.getMonth()
                                    + "-" +mModel.getDay() + " " + mModel.getTime();
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
                    mTimeLineAdapter = new TimeLineAdapter(listData);

                    if (type.equals("init")){
                        mTimeLineAdapter.notifyDataSetChanged();
                    }else {
                        listData.clear();
                        //从服务器上获取数据
                        type = "init";
                        int default_id = 0;
                        getLogFormServer(type, default_id);
                        mTimeLineAdapter.notifyItemChanged(tempPosition);
                    }
                    break;
            }
        }
    };
}