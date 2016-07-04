package com.cnbs.hanxirailway;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
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

/**
 * Created by Administrator on 2016/1/4.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private TextView codeLogin;
    private TextView findPwd;
    private TextView login;
    private Boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (MyApplication.getInstance().getUserId() > 0 && MyApplication.getInstance().getJobId() > 0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        findViews();
    }

    private void findViews() {
        username = (EditText) findViewById(R.id.username);
//        username.setText("18507104251");
        password = (EditText) findViewById(R.id.password);
//        password.setText("123456");
        codeLogin = (TextView) findViewById(R.id.code_login);
        codeLogin.setOnClickListener(this);
        findPwd = (TextView) findViewById(R.id.find_pwd);
        findPwd.setOnClickListener(this);
        login = (TextView) findViewById(R.id.login);
        login.setOnClickListener(this);
        codeLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        codeLogin.getPaint().setAntiAlias(true);
        findPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        findPwd.getPaint().setAntiAlias(true);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login:
                if (!loading && checkValue()) {
                    loading = true;
                    LoginServer ls = new LoginServer();
                    ls.execute();
                }
                break;
            case R.id.code_login:
                intent = new Intent(LoginActivity.this, CodeLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.find_pwd:
                intent = new Intent(LoginActivity.this, FindPwdActivity.class);
                startActivity(intent);
                break;
        }
    }

    private String tmpPhone, tmpPwd;

    private Boolean checkValue() {
        if (TextUtils.isEmpty(username.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.plz_input_username, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Util.isMobileNO(username.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.wrong_username, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.plz_input_pwd, Toast.LENGTH_SHORT).show();
            return false;
        }
        tmpPhone = username.getText().toString();
        tmpPwd = password.getText().toString();
        return true;
    }

    private ProgressDialog dialog;
    private Boolean hasPwd = false;
    private int jobId;

    class LoginServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setMessage(getResources().getString(R.string.loading));
            }
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<>();
            map.put("tag", "login");
            map.put("loginName", tmpPhone);
            map.put("passWord", tmpPwd);
            return HttpUtil.getResult(HttpUtil.Url + "userAct.htm?", map);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            loading = false;
            if (!Util.hasResult(s, LoginActivity.this))
                return;
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
                    JSONObject obj = object.getJSONObject("obj");
                    MyApplication.getInstance().setUserName(obj.getString("loginName"));
                    MyApplication.getInstance().setUserId(obj.getInt("userId"));
                    hasPwd = obj.getBoolean("passWord");//已经登录了，说明hasPwd = true
                    jobId = obj.getInt("jobId");
                    MyApplication.getInstance().setHasPwd(hasPwd);
                    if (hasPwd) {
                        if (jobId == 0) {//进入到设置职名的界面
                            Intent intent = new Intent(LoginActivity.this, SetTitleActivity.class);
                            startActivity(intent);
                        } else {//进入主界面
                            MyApplication.getInstance().setJobId(jobId);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
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
