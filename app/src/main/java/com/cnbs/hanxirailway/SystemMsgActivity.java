package com.cnbs.hanxirailway;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cnbs.Interface.MyItemClickListener;
import com.cnbs.adapter.SystemMsgAdapter;
import com.cnbs.dynamicbox.DynamicBox;
import com.cnbs.entity.SystemMsg;
import com.cnbs.util.HttpUtil;
import com.cnbs.util.Util;
import com.cnbs.view.DividerItemDecoration;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/22.
 */
public class SystemMsgActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener {

    private TextView titleName;
    private RecyclerView recyclerView;
    private SystemMsgAdapter adapter;
    private ArrayList<SystemMsg> data;
    private DynamicBox box;
    private int page = 1;
    private LinearLayoutManager lm;
    private int visibleThreshold = 2;//当还剩两条未见时加载更多
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private Boolean isEnd = false;
    private boolean loading = true;
    private Boolean needClear = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_msg);

        findViews();
        getPage1Data();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText(R.string.system_msg);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        data = new ArrayList<>();
        adapter = new SystemMsgAdapter(data, new MyItemClickListener() {
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = lm.getItemCount();
                firstVisibleItem = lm.findFirstVisibleItemPosition();
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold) && !isEnd) {
                    getOtherPagerData();
                }
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.base_color);

        box = new DynamicBox(this, swipeRefreshLayout);
        box.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPage1Data();
            }
        });
    }

    private void getPage1Data() {
        box.showLoadingLayout();
        if (Util.isNetWorkConnected(this)) {
            GetComment go = new GetComment();
            go.execute();
        } else {
            box.showInternetOffLayout();
        }
    }

    private void getOtherPagerData() {
        GetComment go = new GetComment();
        go.execute();
    }

    class GetComment extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = true;
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("tag", "list");
            map.put("pageSizeStr", "10");
            map.put("pageNoStr", page + "");
            return HttpUtil.getResult(HttpUtil.Url + "bulletinAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String s = Util.hasResult(result);
            loading = false;
            if (!needClear) {
                if (s.equals("0")) {
                    box.showInternetOffLayout();
                    return;
                } else if (s.equals("1")) {
                    box.showExceptionLayout();
                    return;
                } else {
                    box.hideAll();
                }
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                String code = jsonObject.getString("code");
                if (!code.equals("0")) {
                    if (page == 1) {
                        box.showExceptionLayout();
                    } else {
                        isEnd = true;//到头了
                    }
                    return;
                }
                JSONArray array = jsonObject.getJSONArray("list");
                int length = array.length();
                if (needClear) {
                    data.clear();
                    page = 1;//下拉刷新只有在获取到数据的情况下才能修改page的值，否则在特定情况（下拉加载失败，上拉成功）下，会出现加载更多的数据的时候有加载第一页数据的情况
                    isEnd = false;
                }
                page++;
                for (int i = 0; i < length; i++) {
                    JSONObject object = array.getJSONObject(i);
                    Gson gson = new Gson();
                    SystemMsg systemMsg = gson.fromJson(object.toString(), SystemMsg.class);
                    data.add(systemMsg);
                }
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRefresh() {
        if (!loading) {
            needClear = true;
            getOtherPagerData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
