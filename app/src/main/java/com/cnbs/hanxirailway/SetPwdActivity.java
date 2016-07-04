package com.cnbs.hanxirailway;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cnbs.util.HttpUtil;
import com.cnbs.util.MyApplication;
import com.cnbs.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/16.
 */
public class SetPwdActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleName;
    private EditText password;
    private EditText password2;
    private TextView button;

    private String phone;

    private Boolean fromMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        phone = getIntent().getStringExtra("phone");
        fromMain = getIntent().getBooleanExtra("fromMain", false);
        findViews();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText("设置密码");

        password = (EditText) findViewById(R.id.password);
        password2 = (EditText) findViewById(R.id.password2);
        button = (TextView) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (!loading && checkValue()) {
                    SetPwd sp = new SetPwd();
                    sp.execute();
                }
                break;
        }
    }

    private String tmpPwd;

    private boolean checkValue() {
        if (TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.plz_input_pwd, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password2.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.plz_input_pwd, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.getText().toString().equals(password2.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.pwd_not_match, Toast.LENGTH_SHORT).show();
            return false;
        }
        tmpPwd = password.getText().toString();
        return true;
    }

    private ProgressDialog dialog;
    private Boolean loading = false;

    class SetPwd extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(SetPwdActivity.this);
                dialog.setMessage(getResources().getString(R.string.loading));
            }
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<>();
            map.put("tag", "setPassword");
            map.put("loginName", phone);
            map.put("passWord", tmpPwd);
            return HttpUtil.getResult(HttpUtil.Url + "userAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            loading = false;
            if (!Util.hasResult(s, SetPwdActivity.this))
                return;
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
                    Toast.makeText(getApplicationContext(), R.string.set_pwd_success, Toast.LENGTH_SHORT).show();
                    MyApplication.getInstance().setHasPwd(true);
                    JSONObject obj = object.getJSONObject("obj");
                    int jobId = obj.getInt("jobId");
                    if (fromMain) {//如果是主界面进来的
                        finish();
                    } else {
                        if (jobId > 0) {//如果设置过职名了，进入主界面
                            MyApplication.getInstance().setUserName(obj.getString("loginName"));
                            MyApplication.getInstance().setUserId(obj.getInt("userId"));
                            MyApplication.getInstance().setJobId(obj.getInt("jobId"));
                            Intent intent = new Intent(SetPwdActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            MyApplication.getInstance().setUserName(obj.getString("loginName"));
                            MyApplication.getInstance().setUserId(obj.getInt("userId"));
                            Intent intent = new Intent(SetPwdActivity.this, SetTitleActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}





