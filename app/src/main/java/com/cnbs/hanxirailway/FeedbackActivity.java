package com.cnbs.hanxirailway;

import android.app.ProgressDialog;
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
 * Created by Administrator on 2016/1/25.
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private TextView title;
    private TextView send;
    private EditText content;
    private String tmpcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        findViews();
    }

    private void findViews() {
        title = (TextView) findViewById(R.id.titleName);
        title.setText(R.string.feedback);
        send = (TextView) findViewById(R.id.send);
        send.setOnClickListener(this);
        content = (EditText) findViewById(R.id.content);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.send:
                if (TextUtils.isEmpty(content.getText().toString())) {
                    Toast.makeText(getApplicationContext(), R.string.plz_input_feedback, Toast.LENGTH_SHORT).show();
                } else {
                    Util.hideKeyboard(this);
                    tmpcontent = content.getText().toString();
                    SendFeedback sf = new SendFeedback();
                    sf.execute();
                }
                break;

            default:
                break;
        }
    }

    private ProgressDialog dialog;

    class SendFeedback extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(FeedbackActivity.this);
                dialog.setMessage(getResources().getString(R.string.loading));
            }
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("tag", "add");
            map.put("loginName", MyApplication.getInstance().getUserName());
            map.put("content", tmpcontent);
            try {
                return HttpUtil.getResult(
                        HttpUtil.Url + "feedbackAct.htm?", map);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (!Util.hasResult(s, getApplicationContext()))
                return;
            try {
                JSONObject jsonobject = new JSONObject(s);
                Toast.makeText(getApplicationContext(), jsonobject.getString("msg"), Toast.LENGTH_SHORT).show();
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}