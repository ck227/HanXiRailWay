package com.cnbs.hanxirailway;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cnbs.Interface.MyItemClickListener;
import com.cnbs.adapter.GameHistoryAdapter;
import com.cnbs.dynamicbox.DynamicBox;
import com.cnbs.entity.GameHistory;
import com.cnbs.util.TestDBUtil;
import com.cnbs.view.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/15.
 */
public class GameHistoryActivity extends BaseActivity {

    private TextView titleName;
    private RecyclerView recyclerView;
    private GameHistoryAdapter adapter;
    private ArrayList<GameHistory> data;
    private LinearLayoutManager lm;
    private DynamicBox box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);
        findViews();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText(R.string.game_history);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        data = new ArrayList<>();
        adapter = new GameHistoryAdapter(data, new MyItemClickListener() {
            @Override
            public void onItemClick(View view) {

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
            data.addAll(TestDBUtil.getGameHistory(GameHistoryActivity.this));
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
