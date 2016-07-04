package com.cnbs.hanxirailway;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cnbs.entity.DeleteDb;
import com.cnbs.entity.UpdateChoice;
import com.cnbs.entity.UpdateDecision;
import com.cnbs.util.HttpUtil;
import com.cnbs.util.MyApplication;
import com.cnbs.util.TestDBUtil;
import com.cnbs.view.NewDataDialog;
import com.cnbs.view.UpdateDBDialog;
import com.google.gson.Gson;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/5.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleName;
    private RelativeLayout introduce;
    private RelativeLayout feedback;
    private RelativeLayout versionUpdate;
    private RelativeLayout DBUpdate;
    private RelativeLayout help;
    private TextView logout;

    private TextView isUpload;
    private ImageView icon;

    private Boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        flag = MyApplication.getInstance().getIsUpload();
        findViews();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText(R.string.setting);
        introduce = (RelativeLayout) findViewById(R.id.introduce);
        feedback = (RelativeLayout) findViewById(R.id.feedback);
        versionUpdate = (RelativeLayout) findViewById(R.id.versionUpdate);
        DBUpdate = (RelativeLayout) findViewById(R.id.DBUpdate);
        help = (RelativeLayout) findViewById(R.id.help);
        logout = (TextView) findViewById(R.id.logout);

        isUpload = (TextView) findViewById(R.id.isUpload);
        icon = (ImageView) findViewById(R.id.icon);
        icon.setOnClickListener(this);
        if (flag) {
            icon.setImageResource(R.mipmap.open);
            isUpload.setText("是");
        } else {
            icon.setImageResource(R.mipmap.close);
            isUpload.setText("否");
        }

        introduce.setOnClickListener(this);
        feedback.setOnClickListener(this);
        versionUpdate.setOnClickListener(this);
        DBUpdate.setOnClickListener(this);
        help.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.introduce:
                intent = new Intent(SettingActivity.this, WebViewActivity.class);
                intent.putExtra("url", HttpUtil.BaseURL + "platform.html");
                intent.putExtra("title", getResources().getString(R.string.introduce));
                startActivity(intent);
                break;
            case R.id.help:
                intent = new Intent(SettingActivity.this, WebViewActivity.class);
                intent.putExtra("url", HttpUtil.BaseURL + "help.html");
                intent.putExtra("title", getResources().getString(R.string.help));
                startActivity(intent);
                break;
            case R.id.feedback:
                intent = new Intent(SettingActivity.this, FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.versionUpdate:
                Toast.makeText(SettingActivity.this, R.string.checking_update, Toast.LENGTH_SHORT).show();
                UmengUpdateAgent.setUpdateOnlyWifi(false);
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

                    @Override
                    public void onUpdateReturned(int updateStatus,
                                                 UpdateResponse updateInfo) {
                        // TOD switch (updateStatus)
                        switch (updateStatus) {
                            case UpdateStatus.Yes:
                                UmengUpdateAgent.showUpdateDialog(SettingActivity.this, updateInfo);
                                break;
                            case UpdateStatus.No:
                                Toast.makeText(SettingActivity.this, R.string.no_update, Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case UpdateStatus.NoneWifi:
                                Toast.makeText(SettingActivity.this, R.string.no_wifi_update,
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.Timeout:
                                Toast.makeText(SettingActivity.this, R.string.timeout_update, Toast.LENGTH_SHORT)
                                        .show();
                                break;
                        }
                    }
                });
                UmengUpdateAgent.forceUpdate(SettingActivity.this);
                break;
            case R.id.DBUpdate:
                CheckNewDB cnb = new CheckNewDB();
                cnb.execute();
                break;
            case R.id.logout:
                MyApplication.getInstance().logout();
                intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.icon:
                flag = !flag;
                MyApplication.getInstance().setIsUpload(flag);
                if (flag) {
                    icon.setImageResource(R.mipmap.open);
                    isUpload.setText("是");
                } else {
                    icon.setImageResource(R.mipmap.close);
                    isUpload.setText("否");
                }
                break;
        }

    }

    //下面是数据库更新的操作，和主界面相同
    int localVersion;
    int version;

    class CheckNewDB extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<>();
            map.put("tag", "jobversion");
            map.put("jobId", MyApplication.getInstance().getJobId() + "");
            localVersion = TestDBUtil.getDBVersioion(SettingActivity.this);
            map.put("version", localVersion + "");
            return HttpUtil.getResult(HttpUtil.Url + "jobquestionversionAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
                    version = object.getInt("value");
                    if (version > 0) {
                        showNewDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "暂无更新", Toast.LENGTH_SHORT).show();
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
        NewDataDialog dialog = new NewDataDialog(SettingActivity.this, new NewDataDialog.ButtonListener() {
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
            dialog = new UpdateDBDialog(SettingActivity.this, new UpdateDBDialog.ButtonListener() {
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
                TestDBUtil.saveDBVersion(SettingActivity.this, version);
                Toast.makeText(getApplicationContext(), "数据更新完成", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "数据文件不存在", Toast.LENGTH_LONG).show();
            }

        }
    }
}







