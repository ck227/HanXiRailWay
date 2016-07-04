package com.cnbs.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnbs.dynamicbox.DynamicBox;
import com.cnbs.hanxirailway.R;
import com.cnbs.hanxirailway.TestActivity;
import com.cnbs.hanxirailway.TestHistoryActivity;
import com.cnbs.hanxirailway.TestLineChartActivity;
import com.cnbs.util.MyApplication;
import com.cnbs.util.TestDBUtil;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Fragment1 extends Fragment implements View.OnClickListener {

    private TextView titieName;
    private View contentview;
    private TextView startTest;
    private TextView history;
    private TextView goChart;
    private TextView name;
    private MyReceiver myReceiver;

    public static Fragment1 newInstance() {
        Bundle args = new Bundle();
        Fragment1 fragment = new Fragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentview == null) {
            contentview = inflater
                    .inflate(R.layout.fragment1, container, false);
            myReceiver = new MyReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("refreshGame");
            getActivity().registerReceiver(myReceiver, filter);
            findViews(contentview);
        }
        ViewGroup parent = (ViewGroup) contentview.getParent();
        if (parent != null) {
            parent.removeView(contentview);
        }
        return contentview;
    }

    private void findViews(View view) {
        titieName = (TextView) view.findViewById(R.id.titleName);
        titieName.setText(R.string.fragment1_title);
        startTest = (TextView) view.findViewById(R.id.startTest);
        startTest.setOnClickListener(this);
        history = (TextView) view.findViewById(R.id.history);
        history.setOnClickListener(this);
        name = (TextView) view.findViewById(R.id.name);
        name.setText(MyApplication.getInstance().getTitleName());

        goChart = (TextView) view.findViewById(R.id.goChart);
        goChart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.startTest:
                intent = new Intent(getActivity(), TestActivity.class);
                startActivity(intent);
                break;
            case R.id.history:
                intent = new Intent(getActivity(), TestHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.goChart:
                intent = new Intent(getActivity(), TestLineChartActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            name.setText(MyApplication.getInstance().getTitleName());
        }
    }
}
