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
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/5.
 */
public class ModifyPwdActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleName;
    private EditText password;
    private EditText newPwd;
    private EditText newPwd2;
    private TextView button;

    private ProgressDialog dialog;
    private Boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);

        findViews();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText(R.string.modify_pwd);
        password = (EditText) findViewById(R.id.password);
        newPwd = (EditText) findViewById(R.id.new_pwd);
        newPwd2 = (EditText) findViewById(R.id.new_pwd2);
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

    private String tmpOldPwd, tmpNewPwd;

    private boolean checkValue() {
        if (TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.plz_input_pwd, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(newPwd.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.plz_input_pwd, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(newPwd2.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.plz_input_pwd, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPwd.getText().toString().equals(newPwd2.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.pwd_not_match, Toast.LENGTH_SHORT).show();
            return false;
        }
        tmpOldPwd = password.getText().toString();
        tmpNewPwd = newPwd.getText().toString();
        return true;
    }

    class SetPwd extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(ModifyPwdActivity.this);
                dialog.setMessage(getResources().getString(R.string.loading));
            }
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<>();
            map.put("tag", "updtePassWord");
            map.put("loginName", MyApplication.getInstance().getUserName());
            map.put("passWord", tmpOldPwd);
            map.put("newPassWord", tmpNewPwd);
            return HttpUtil.getResult(HttpUtil.Url + "userAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            loading = false;
            if (!Util.hasResult(s, ModifyPwdActivity.this))
                return;
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
                    Toast.makeText(getApplicationContext(), R.string.update_pwd_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}





