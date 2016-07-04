package com.cnbs.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cnbs.Interface.MyItemClickListener;
import com.cnbs.adapter.GameAdapter;
import com.cnbs.dynamicbox.DynamicBox;
import com.cnbs.entity.Game;
import com.cnbs.hanxirailway.GameHistoryActivity;
import com.cnbs.hanxirailway.R;
import com.cnbs.hanxirailway.TestActivity;
import com.cnbs.util.HttpUtil;
import com.cnbs.util.MyApplication;
import com.cnbs.util.TestDBUtil;
import com.cnbs.util.Util;
import com.cnbs.view.DividerItemDecoration;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Fragment2 extends Fragment implements View.OnClickListener {

    private TextView titieName;
    private View contentview;
    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private ArrayList<Game> data;
    private LinearLayoutManager lm;

    private MyReceiver myReceiver;
    private TextView history;
    private DynamicBox box;

    public static Fragment2 newInstance() {
        Bundle args = new Bundle();
        Fragment2 fragment = new Fragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentview == null) {
            contentview = inflater
                    .inflate(R.layout.fragment2, container, false);
            myReceiver = new MyReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("refreshUI");
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
        titieName.setText(R.string.fragment2_title);
        history = (TextView) view.findViewById(R.id.history);
        history.setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        data = new ArrayList<>();
        adapter = new GameAdapter(getActivity(), data, new MyItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);//要-1才能对应data
                if (pos == data.size() + 1) {//如果点击的是最后一项
                    GetData2 gd = new GetData2();
                    gd.execute();
                } else {
                    Intent intent = new Intent(getActivity(), TestActivity.class);
                    intent.putExtra("game", data.get(pos - 1));
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        lm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        data.addAll(TestDBUtil.getGames(getActivity()));
        if (data.size() == 0) {//如果本地没有数据，从服务端读取
            box = new DynamicBox(getActivity(), recyclerView);
            box.showLoadingLayout();
            GetData gd = new GetData();
            gd.execute();
        }

    }

    class GetData extends AsyncTask<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<>();
            map.put("tag", "list");
            map.put("loginName", MyApplication.getInstance().getUserName());
            map.put("jobId", MyApplication.getInstance().getJobId() + "");
            return HttpUtil.getResult(HttpUtil.Url + "breakthroughAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String result = Util.hasResult(s);
            if (result.equals("0")) {
                box.showInternetOffLayout();
                return;
            } else if (result.equals("1")) {
                box.showExceptionLayout();
                return;
            } else {
                box.hideAll();
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                String code = jsonObject.getString("code");
                if (code.equals("0")) {
                    JSONArray array = jsonObject.getJSONArray("list");
                    int length = array.length();
                    Gson gson = new Gson();
                    SQLiteDatabase db = MyApplication.getInstance().openDatabase(getActivity());

//                    {"break_id":26,"break_name":"第一关","decision_count":5,"description":"1,2,3",
//                            "job_id":15,"multi_choice_count":5,"rank_id":37,"right_count":0,"single_choice_count":5,"time_limit":5,"type_id":0}

                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject1 = (JSONObject) array.get(i);
                        Game game = gson.fromJson(jsonObject1.toString(), Game.class);
//                        Game game = new Game();
//                        game.setBreak_id(jsonObject1.getInt("break_id"));
//                        game.setDescription(jsonObject1.getString("description"));
//                        game.setDecision_count(jsonObject1.getInt("decision_count"));
//                        game.setType_id(jsonObject1.getInt("type_id"));
//                        game.setMulti_choice_count(jsonObject1.getInt("multi_choice_count"));
//                        game.setBreak_name(jsonObject1.getString("break_name"));
//                        game.setJob_id(jsonObject1.getInt("job_id"));
//                        game.setRank_id(jsonObject1.getInt("rank_id"));
//                        game.setRight_count(jsonObject1.getInt("right_count"));
//                        game.setSingle_choice_count(jsonObject1.getInt("single_choice_count"));
//                        game.setTime_limit(jsonObject1.getInt("time_limit"));
                        TestDBUtil.insertGame(game, db);
                        if (game.getType_id() > 0) {
                            MyApplication.getInstance().setGamePosition(i+1);
                        }
                        data.add(game);
                    }

                } else {
//                    Toast.makeText(getActivity(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private ProgressDialog dialog;

    class GetData2 extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage(getResources().getString(R.string.loading));
            }
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<>();
            map.put("tag", "list");
            map.put("loginName", MyApplication.getInstance().getUserName());
            map.put("jobId", MyApplication.getInstance().getJobId() + "");
            if (data.size() > 0)
                map.put("id", data.get(data.size() - 1).getBreak_id() + "");
            return HttpUtil.getResult(HttpUtil.Url + "breakthroughAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (!Util.hasResult(s, getActivity()))
                return;
            try {
                JSONObject jsonObject = new JSONObject(s);
                String code = jsonObject.getString("code");
                if (code.equals("0")) {
                    Boolean flag = false;
                    JSONArray array = jsonObject.getJSONArray("list");
                    int length = array.length();
                    Gson gson = new Gson();
                    SQLiteDatabase db = MyApplication.getInstance().openDatabase(getActivity());
                    for (int i = 0; i < length; i++) {
                        Game game = gson.fromJson(array.get(i).toString(), Game.class);
                        TestDBUtil.insertGame(game, db);
                        if (game.getType_id() > 0 && !flag) {
                            flag = true;
                            MyApplication.getInstance().setGamePosition(i);
                        }
                        data.add(game);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history:
                Intent intent = new Intent(getActivity(), GameHistoryActivity.class);
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
            if (intent.getAction().equals("refreshUI")) {
                adapter.notifyDataSetChanged();
            } else if (intent.getAction().equals("refreshGame")) {
                TestDBUtil.clearGame(getActivity());
                if (box == null)
                    box = new DynamicBox(getActivity(), recyclerView);
                box.showLoadingLayout();
                data.clear();
                MyApplication.getInstance().setGamePosition(0);//切换职名后本地存的闯关记录就不需要了,是否要加上这句
                GetData gd = new GetData();
                gd.execute();
            }

        }
    }

}
