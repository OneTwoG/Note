package com.example.ytw.note;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import timeline.TimeLineAdapter;
import timeline.TimeLineModel;

/**
 * Created by YTW on 2016/5/22.
 */
public class Main_Fragment_Log extends Fragment {

    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> listData = new ArrayList<>();
    View view;

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
        Log.d("Fragment", mRecyclerView.toString() + "");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        init();
    }

    private void init() {
        for (int i = 0; i < 10; i++){
            TimeLineModel model = new TimeLineModel();
            listData.add(model);
        }

        Log.d("Fragment", listData.size() + "");
        mTimeLineAdapter = new TimeLineAdapter(listData);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }
}
