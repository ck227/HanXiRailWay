package com.cnbs.hanxirailway;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cnbs.Interface.MyItemClickListener;
import com.cnbs.adapter.TestHistoryAdapter;
import com.cnbs.dynamicbox.DynamicBox;
import com.cnbs.entity.TestResult;
import com.cnbs.util.TestDBUtil;
import com.cnbs.view.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/14.
 */
public class TestHistoryActivity extends BaseActivity {

    private TextView titleName;
    private RecyclerView recyclerView;
    private TestHistoryAdapter adapter;
    private ArrayList<TestResult> data;
    private LinearLayoutManager lm;
    private DynamicBox box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_history);
        findViews();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText(R.string.test_history);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        data = new ArrayList<>();
        adapter = new TestHistoryAdapter(data, new MyItemClickListener() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(TestHistoryActivity.this, TestActivity.class);
                intent.putExtra("user_answer", data.get(recyclerView.getChildAdapterPosition(view)).getUser_answer());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        box = new DynamicBox(this, recyclerView);
        box.showLoadingLayout();
        GetData gd = new GetData();
        gd.execute();
    }

    class GetData extends AsyncTask<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... params) {
            data.addAll(TestDBUtil.getTestResult(TestHistoryActivity.this));
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (data.size() > 0) {
                box.hideAll();
            } else {
                box.showExceptionLayout();
            }
            adapter.notifyDataSetChanged();
        }
    }
}
