package com.cnbs.hanxirailway;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cnbs.util.HttpUtil;
import com.cnbs.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/5.
 */
public class FindPwdActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleName;
    private TextView getCode;
    private EditText phone;
    private EditText code;
    private TextView button;
    private Timer timer;
    private int jishi = 60;
    private ProgressDialog dialog;
    private Boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);

        findViews();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText(R.string.find_pwd);
        getCode = (TextView) findViewById(R.id.getCode);
        phone = (EditText) findViewById(R.id.phone);
        code = (EditText) findViewById(R.id.code);
        button = (TextView) findViewById(R.id.button);

        getCode.setOnClickListener(this);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getCode:
                if (checkPhoneNumber()) {
                    GetCode gc = new GetCode();
                    gc.execute();
                }
                break;
            case R.id.button:
                if (!loading && checkValue()) {
                    loading = true;
                    Intent intent = new Intent(FindPwdActivity.this, SetPwdActivity.class);
                    intent.putExtra("phone", tmpPhone);
                    startActivity(intent);
                }
                break;
        }
    }

    private String tmpCode;
    private String tmpPhone;
    private String realCode = "";

    private Boolean checkPhoneNumber() {
        tmpPhone = phone.getText().toString();
        if (TextUtils.isEmpty(tmpPhone)) {
            Toast.makeText(getApplicationContext(), R.string.plz_input_phone,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (!Util.isMobileNO(tmpPhone)) {
            Toast.makeText(getApplicationContext(), R.string.wrong_phone,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (!Util.isNetWorkConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.plz_check_network,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        Util.hideKeyboard(FindPwdActivity.this);
        return true;
    }

    class GetCode extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(FindPwdActivity.this);
                dialog.setMessage(getResources().getString(R.string.loading));
            }
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("tag", "getCode");
            map.put("phone", tmpPhone);
            return HttpUtil.getResult(HttpUtil.Url + "venticationAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            loading = false;
            if (!Util.hasResult(s, FindPwdActivity.this))
                return;
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
                    realCode = object.getString("msg");
                    Toast.makeText(getApplicationContext(), "请查收短信", Toast.LENGTH_SHORT).show();
                    jishi = 60;
                    getCode.setEnabled(false);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            handler.sendEmptyMessage(jishi--);
                        }
                    }, 0, 1000);
                } else {
                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what <= 0) {
                getCode.setEnabled(true);
                getCode.setText("获取验证码");
                timer.cancel();
            } else {
                getCode.setText(msg.what
                        + getResources().getString(R.string.try_later));
            }
        }
    };

    private Boolean checkValue() {
        if (TextUtils.isEmpty(phone.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.plz_input_phone, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Util.isMobileNO(phone.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.wrong_phone, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(code.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.plz_input_code, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (realCode.equals("") || !code.getText().toString().equals(realCode)) {
            Toast.makeText(getApplicationContext(), R.string.wrong_code, Toast.LENGTH_SHORT).show();
            return false;
        }
        tmpPhone = phone.getText().toString();
        tmpCode = code.getText().toString();
        return true;
    }
}





