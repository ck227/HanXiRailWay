package com.cnbs.hanxirailway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.cnbs.adapter.MainAdapter;
import com.cnbs.entity.DeleteDb;
import com.cnbs.entity.UpdateChoice;
import com.cnbs.entity.UpdateDecision;
import com.cnbs.util.HttpUtil;
import com.cnbs.util.MyApplication;
import com.cnbs.util.TestDBUtil;
import com.cnbs.view.NewDataDialog;
import com.cnbs.view.UpdateDBDialog;
import com.cnbs.viewpager.TabPageIndicator;
import com.google.gson.Gson;
import com.umeng.update.UmengUpdateAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ViewPager pager;
    private TabPageIndicator indicator;
    private MainAdapter adapter;

    private MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.base_color));

        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setOnPageChangeListener(this);
        adapter = new MainAdapter(getApplicationContext(),
                getSupportFragmentManager());
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);


        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("CheckNewDB");
        registerReceiver(myReceiver, filter);

        checkNewDB();
        if (MyApplication.getInstance().getIsUpload()) {//如果是自动同步
            UploadData ud = new UploadData();
            ud.execute();
        }

        // 检测更新
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateCheckConfig(false);
        UmengUpdateAgent.update(this);

    }

    class UploadData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String tmp = TestDBUtil.getGameRecord(MainActivity.this);
            if (tmp.length() > 0) {
                Map<String, String> map = new HashMap<>();
                map.put("tag", "add");
                map.put("loginName", MyApplication.getInstance().getUserName());
                map.put("jobId", MyApplication.getInstance().getJobId() + "");
                map.put("json", tmp);
                return HttpUtil.getResult(HttpUtil.Url + "breakthroughAct.htm?", map);
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            dialog.dismiss();
//            if (!Util.hasResult(s, getActivity()))
//                return;
//            try {
//                JSONObject object = new JSONObject(s);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void checkNewDB() {
        CheckNewDB cnb = new CheckNewDB();
        cnb.execute();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            moveTaskToBack(true);//
        }
        return super.onKeyDown(keyCode, event);
    }

    int localVersion;
    int version;

    class CheckNewDB extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                String[] result = TestDBUtil.getTitleName(MainActivity.this);
                String titleName = result[0];
                int type_id = Integer.valueOf(result[1]);
                String typeName = TestDBUtil.getTypeName(MainActivity.this, type_id);

                MyApplication.getInstance().setTypeId(type_id);
                MyApplication.getInstance().setTypeName(typeName);
                MyApplication.getInstance().setTitleName(titleName);

                Map<String, String> map = new HashMap<>();
                map.put("tag", "jobversion");
                map.put("jobId", MyApplication.getInstance().getJobId() + "");
                localVersion = TestDBUtil.getDBVersioion(MainActivity.this);
                map.put("version", localVersion + "");
                return HttpUtil.getResult(HttpUtil.Url + "jobquestionversionAct.htm?", map);
            } catch (NumberFormatException e) {
                e.printStackTrace();

                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                Intent intent = new Intent(MainActivity.this,SetTitleActivity.class);
                intent.putExtra("isUpdate",true);
                startActivity(intent);
                return;
            }
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
                    version = object.getInt("value");
                    if (version > 0) {
                        showNewDialog();
                    }
                } else {
//                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showNewDialog() {
        NewDataDialog dialog = new NewDataDialog(MainActivity.this, new NewDataDialog.ButtonListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {
                GetData gd = new GetData();
                gd.execute();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    UpdateDBDialog dialog;

    class GetData extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new UpdateDBDialog(MainActivity.this, new UpdateDBDialog.ButtonListener() {
                @Override
                public void button() {

                }
            });
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Gson gson = new Gson();
            int jobId = MyApplication.getInstance().getJobId();
            SQLiteDatabase db = MyApplication.getInstance().openDatabase(getApplicationContext());
            for (int j = 1; j < version - localVersion + 1; j++) {
                try {
                    URL robotURL = new URL(HttpUtil.UploadURL + MyApplication.getInstance().getJobId() + "_" + (localVersion + j) + ".txt");
                    BufferedReader in = new BufferedReader(new InputStreamReader(robotURL.openStream()));
                    String line = null;
                    while ((line = in.readLine()) != null) {//每行都是一个json操作
                        JSONObject jsonObject = new JSONObject(line);
                        String filetype = jsonObject.getString("filetype");

                        JSONArray choicelist = new JSONArray();//选择
                        try {
                            choicelist = jsonObject.getJSONArray("choicelist");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONArray decisionlist = new JSONArray();//判断
                        try {
                            decisionlist = jsonObject.getJSONArray("decisionlist");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //选择题
                        if (choicelist != null && choicelist.length() > 0) {
                            int length = choicelist.length();
                            if (filetype.equals("insert")) {
                                for (int i = 0; i < length; i++) {
                                    JSONObject object = choicelist.getJSONObject(i);
                                    UpdateChoice updateChoice = gson.fromJson(object.toString(), UpdateChoice.class);
                                    TestDBUtil.insertChoiceQuestion(updateChoice, jobId, db);
                                }
                            } else if (filetype.equals("update")) {
                                for (int i = 0; i < length; i++) {
                                    JSONObject object = choicelist.getJSONObject(i);
                                    UpdateChoice updateChoice = gson.fromJson(object.toString(), UpdateChoice.class);
                                    TestDBUtil.updateChoiceQuestion(updateChoice, db);
                                }
                            } else {//删除
                                for (int i = 0; i < length; i++) {
                                    JSONObject object = choicelist.getJSONObject(i);
                                    DeleteDb deleteDb = gson.fromJson(object.toString(), DeleteDb.class);
                                    TestDBUtil.deleteChoiceQuestion(deleteDb, db);
                                }
                            }
                        }

                        //判断题
                        if (decisionlist != null && decisionlist.length() > 0) {
                            int length = decisionlist.length();
                            if (filetype.equals("insert")) {
                                for (int i = 0; i < length; i++) {
                                    JSONObject object = decisionlist.getJSONObject(i);
                                    UpdateDecision updateDecision = gson.fromJson(object.toString(), UpdateDecision.class);
                                    TestDBUtil.insertDecisionQuestion(updateDecision, db);
                                }
                            } else if (filetype.equals("update")) {
                                for (int i = 0; i < length; i++) {
                                    JSONObject object = decisionlist.getJSONObject(i);
                                    UpdateDecision updateDecision = gson.fromJson(object.toString(), UpdateDecision.class);
                                    TestDBUtil.updateDecisionQuestion(updateDecision, db);
                                }
                            } else {
                                for (int i = 0; i < length; i++) {
                                    JSONObject object = decisionlist.getJSONObject(i);
                                    DeleteDb deleteDb = gson.fromJson(object.toString(), DeleteDb.class);
                                    TestDBUtil.deleteDecisionQuestion(deleteDb, db);
                                }
                            }
                        }

                    }
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (s) {//如果更新没有异常
                TestDBUtil.saveDBVersion(MainActivity.this, version);
                Toast.makeText(getApplicationContext(), "数据更新完成", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "数据文件不存在", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNewDB();
        }
    }


    public void setStatusBarColor(View statusBar, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int actionBarHeight = 0;
            int statusBarHeight = getStatusBarHeight();
            //action bar height
            statusBar.getLayoutParams().height = actionBarHeight + statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
