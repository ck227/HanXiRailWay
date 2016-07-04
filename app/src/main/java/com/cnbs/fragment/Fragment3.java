package com.cnbs.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.cnbs.entity.UpdateChoice;
import com.cnbs.hanxirailway.ModifyPwdActivity;
import com.cnbs.hanxirailway.R;
import com.cnbs.hanxirailway.SetPwdActivity;
import com.cnbs.hanxirailway.SetTitleActivity;
import com.cnbs.hanxirailway.SettingActivity;
import com.cnbs.hanxirailway.SystemMsgActivity;
import com.cnbs.util.HttpUtil;
import com.cnbs.util.MyApplication;
import com.cnbs.util.TestDBUtil;
import com.cnbs.util.Util;
import com.cnbs.view.NewDataDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Fragment3 extends Fragment implements View.OnClickListener {

    private TextView setting;
    private TextView modifyPwd;
    private TextView manageName;
    private TextView modifyData;
    private TextView systemMsg;
    private View contentview;

    public static Fragment3 newInstance() {

        Bundle args = new Bundle();

        Fragment3 fragment = new Fragment3();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentview == null) {
            contentview = inflater
                    .inflate(R.layout.fragment3, container, false);
            findViews(contentview);
        }
        ViewGroup parent = (ViewGroup) contentview.getParent();
        if (parent != null) {
            parent.removeView(contentview);
        }
        return contentview;
    }

    private void findViews(View view) {
        setting = (TextView) view.findViewById(R.id.setting);
        modifyPwd = (TextView) view.findViewById(R.id.modify_pwd);
        manageName = (TextView) view.findViewById(R.id.manage_name);
        modifyData = (TextView) view.findViewById(R.id.modify_data);
        systemMsg = (TextView) view.findViewById(R.id.system_msg);

        setting.setOnClickListener(this);
        modifyPwd.setOnClickListener(this);
        manageName.setOnClickListener(this);
        modifyData.setOnClickListener(this);
        systemMsg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.modify_pwd:
                if (MyApplication.getInstance().getHasPwd()) {
                    intent = new Intent(getActivity(), ModifyPwdActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), SetPwdActivity.class);
                    intent.putExtra("fromMain", true);
                    startActivity(intent);
                }
                break;
            case R.id.manage_name:
                intent = new Intent(getActivity(), SetTitleActivity.class);
                intent.putExtra("isUpdate", true);
                startActivity(intent);
                break;
            case R.id.modify_data:
                String tmp = TestDBUtil.getGameRecord(getActivity());
                if (tmp.length() > 0) {
                    UploadData ud = new UploadData();
                    ud.execute(tmp);
                } else {
                    Toast.makeText(getActivity(), "没有数据需要更新", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.system_msg:
                intent = new Intent(getActivity(), SystemMsgActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    private ProgressDialog dialog;

    class UploadData extends AsyncTask<String, Integer, String> {

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
        protected String doInBackground(String... params) {
            Map<String, String> map = new HashMap<>();
            map.put("tag", "add");
            map.put("loginName", MyApplication.getInstance().getUserName());
            map.put("jobId", MyApplication.getInstance().getJobId() + "");
            map.put("json", params[0]);
            return HttpUtil.getResult(HttpUtil.Url + "breakthroughAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (!Util.hasResult(s, getActivity()))
                return;
            try {
                JSONObject object = new JSONObject(s);
//                String code = object.getString("code");
//                if (code.equals("0")) {//同步成功
Toast.makeText(getActivity(),object.getString("msg"),Toast.LENGTH_SHORT).show();
//                } else {
//
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
