package com.cnbs.hanxirailway;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cnbs.util.HttpUtil;
import com.cnbs.util.MyApplication;
import com.cnbs.util.Util;
import com.cnbs.view.FinishGameDialog;
import com.cnbs.view.LoginSuccessDialog;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/4.
 */
public class CodeLoginActivity extends BaseActivity implements View.OnClickListener {

    private TextView getCode;
    private EditText phone;
    private EditText code;
    private TextView login;
    private Boolean loading = false;

    private Timer timer;
    private int jishi = 60;

    private Boolean hasPwd = false;
    private int jobId = 0;

    private SmsObserver obderver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_login);

        findViews();
        obderver = new SmsObserver(new Handler());
        getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, obderver);
    }

    private void findViews() {
        getCode = (TextView) findViewById(R.id.getCode);
        phone = (EditText) findViewById(R.id.phone);
        code = (EditText) findViewById(R.id.code);
        login = (TextView) findViewById(R.id.login);
        getCode.setOnClickListener(this);
        login.setOnClickListener(this);

//        phone.setText("18507104251");
//        code.setText("804134");
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
            case R.id.login:
                if (!loading && checkValue()) {
                    loading = true;
                    LoginServer ls = new LoginServer();
                    ls.execute();
                }
                break;
        }
    }

    class GetCode extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(CodeLoginActivity.this);
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
            if (!Util.hasResult(s, CodeLoginActivity.this))
                return;
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
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

        ;
    };

    private String tmpCode;
    private String tmpPhone;

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
        Util.hideKeyboard(CodeLoginActivity.this);
        return true;
    }

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
        tmpPhone = phone.getText().toString();
        tmpCode = code.getText().toString();
        return true;
    }

    private ProgressDialog dialog;

    class LoginServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(CodeLoginActivity.this);
                dialog.setMessage(getResources().getString(R.string.loading));
            }
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<>();
            map.put("tag", "logincode");
            map.put("loginName", tmpPhone);
            map.put("code", tmpCode);
            return HttpUtil.getResult(HttpUtil.Url + "userAct.htm?", map);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            loading = false;
            if (!Util.hasResult(s, CodeLoginActivity.this))
                return;
//            {"obj":{"jobId":0,"loginName":"18507104251","passWord":false,"userId":2,"userName":"李四"},"code":"0","msg":"用户信息"}
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
                    JSONObject obj = object.getJSONObject("obj");
                    MyApplication.getInstance().setUserName(obj.getString("loginName"));
                    MyApplication.getInstance().setUserId(obj.getInt("userId"));
                    hasPwd = obj.getBoolean("passWord");
                    jobId = obj.getInt("jobId");
                    MyApplication.getInstance().setHasPwd(hasPwd);
                    MyApplication.getInstance().setJobId(jobId);
                    if (hasPwd) {
                        if (jobId == 0) {//进入到设置职名的界面
                            Intent intent = new Intent(CodeLoginActivity.this, SetTitleActivity.class);
                            startActivity(intent);
                        } else {//进入主界面
                            Intent intent = new Intent(CodeLoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        loginSuccessDialog();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loginSuccessDialog() {
        LoginSuccessDialog dialog = new LoginSuccessDialog(this, new LoginSuccessDialog.ButtonListener() {

            @Override
            public void left() {
                // TODO Auto-generated method stub
                Intent intent;
                if (jobId != 0) {//设置了职名，直接去主界面
                    intent = new Intent(CodeLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {//去设置职名
                    intent = new Intent(CodeLoginActivity.this, SetTitleActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void right() {
                // TODO Auto-generated method stub
                Intent intent = new Intent(CodeLoginActivity.this, SetPwdActivity.class);
                intent.putExtra("phone", tmpPhone);
                startActivity(intent);
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    class SmsObserver extends ContentObserver {

        private Cursor cursor = null;

        public SmsObserver(Handler handler) {
            super(handler);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onChange(boolean selfChange) {
            // TODO Auto-generated method stub
            super.onChange(selfChange);
            cursor = managedQuery(Uri.parse("content://sms/inbox"),
                    new String[]{"_id", "address", "read", "body"},
                    " address=? and read=?", new String[]{"106550200646220",
                            "0"}, "_id desc");
            // 按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
            if (cursor != null && cursor.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put("read", "1"); // 修改短信为已读模式
                cursor.moveToNext();
                int smsbodyColumn = cursor.getColumnIndex("body");
                String smsBody = cursor.getString(smsbodyColumn);
                code.setText(Util.getDynamicPassword(smsBody));// code是输入验证码的地方
            }
            // 在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃
            if (Build.VERSION.SDK_INT < 14) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(obderver);
    }
}










