package com.cnbs.hanxirailway;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cnbs.adapter.SetTitleAdapter;
import com.cnbs.entity.SetTitleEntity;
import com.cnbs.util.HttpUtil;
import com.cnbs.util.MyApplication;
import com.cnbs.util.TestDBUtil;
import com.cnbs.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/16.
 */
public class SetTitleActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleName;
    private RelativeLayout moduleRel;
    private AutoCompleteTextView module;
    private RelativeLayout nameRel;
    private AutoCompleteTextView name;
    private TextView button;

    private ArrayList<SetTitleEntity> data1;
    private ArrayList<SetTitleEntity> data2;
    private SetTitleAdapter adapter1;
    private SetTitleAdapter adapter2;

    private int module_id = 0, name_id = 0;
    private String tmpTypeName = "", tmpTitleName = "";

    private Boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_title);
        isUpdate = getIntent().getBooleanExtra("isUpdate", false);
        findViews();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText("设置职名");
        moduleRel = (RelativeLayout) findViewById(R.id.module_rel);
        module = (AutoCompleteTextView) findViewById(R.id.module);

        nameRel = (RelativeLayout) findViewById(R.id.name_rel);
        name = (AutoCompleteTextView) findViewById(R.id.name);
        button = (TextView) findViewById(R.id.button);

        module_id = MyApplication.getInstance().getTypeId();
        name_id = MyApplication.getInstance().getJobId();
        module.setText(MyApplication.getInstance().getTypeName());
        name.setText(MyApplication.getInstance().getTitleName());

        moduleRel.setOnClickListener(this);
        nameRel.setOnClickListener(this);
        button.setOnClickListener(this);

        data1 = new ArrayList<>();
        data2 = new ArrayList<>();
        adapter1 = new SetTitleAdapter(this, data1);
        adapter2 = new SetTitleAdapter(this, data2);
        module.setAdapter(adapter1);
        name.setAdapter(adapter2);

        module.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                module_id = data1.get(position).getId();
                module.setText(data1.get(position).getName());
                tmpTypeName = data1.get(position).getName();
                data2.clear();
                name_id = 0;
                name.setText("");
            }
        });

        name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                name_id = data2.get(position).getId();
                name.setText(data2.get(position).getName());
                tmpTitleName = data2.get(position).getName();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.module_rel:
                if (!loading && data1.size() == 0) {
//                    if (Util.isNetWorkConnected(this)) {
                    GetModuleData gmd = new GetModuleData();
                    gmd.execute();
//                    } else {
//                        data1 = TestDBUtil.getLocalModule(this);
//                        adapter1.notifyDataSetChanged();
//                    }

                } else {
                    module.showDropDown();
                }
                break;
            case R.id.name_rel:
                if (!loading && module_id != 0) {
                    if (data2.size() == 0) {
                        GetNameData gnd = new GetNameData();
                        gnd.execute();
                    } else {
                        name.showDropDown();
                    }
                }
                break;
            case R.id.button:
                if (module_id == 0) {
                    Toast.makeText(getApplicationContext(), "请选择模块", Toast.LENGTH_SHORT).show();
                } else if (name_id == 0) {
                    Toast.makeText(getApplicationContext(), "请选择职名", Toast.LENGTH_SHORT).show();
                } else {
                    //请求网络设置职名
                    if (name_id == MyApplication.getInstance().getJobId()) {
                        finish();
                    } else {
                        if (isUpdate) {
                            UploadData uploadData = new UploadData();
                            uploadData.execute();
                        } else {
                            SetTitle st = new SetTitle();
                            st.execute();
                        }


                    }
                }
                break;
        }
    }

    private ProgressDialog dialog;
    private Boolean loading = false;

    class GetModuleData extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = true;
            if (dialog == null) {
                dialog = new ProgressDialog(SetTitleActivity.this);
                dialog.setMessage(getResources().getString(R.string.loading));
            }
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<>();
            map.put("tag", "list");
            return HttpUtil.getResult(HttpUtil.Url + "systypeAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            loading = false;
            if (!Util.hasResult(s, SetTitleActivity.this))
                return;
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
                    JSONArray jsonArray = object.getJSONArray("list");
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        SetTitleEntity entity = new SetTitleEntity();
                        entity.setId(jsonObject.getInt("id"));
                        entity.setName(jsonObject.getString("type_name"));
                        data1.add(entity);
                    }
                    adapter1.notifyDataSetChanged();
                    handler.sendEmptyMessage(0);
                } else {
                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                module.showDropDown();
            } else {
                name.showDropDown();
            }
        }
    };


    class GetNameData extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = true;
            if (dialog == null) {
                dialog = new ProgressDialog(SetTitleActivity.this);
                dialog.setMessage(getResources().getString(R.string.loading));
            }
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("tag", "list");
            map.put("typeId", module_id + "");
            return HttpUtil.getResult(HttpUtil.Url + "jobnameAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            loading = false;
            if (!Util.hasResult(s, SetTitleActivity.this))
                return;
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
                    JSONArray jsonArray = object.getJSONArray("list");
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        SetTitleEntity entity = new SetTitleEntity();
                        entity.setId(jsonObject.getInt("id"));
                        entity.setName(jsonObject.getString("titleName"));
                        //2016.4.13添加修改
                        TestDBUtil.saveOrUpdateJobName(SetTitleActivity.this, entity);
                        TestDBUtil.saveOrUpdateJobQuestion(SetTitleActivity.this, entity);

                        data2.add(entity);
                    }
                    dialog.dismiss();
                    adapter2.notifyDataSetChanged();
                    handler.sendEmptyMessage(1);
                } else {
                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    class UploadData extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = true;
            if (dialog == null) {
                dialog = new ProgressDialog(SetTitleActivity.this);
                dialog.setMessage(getResources().getString(R.string.loading));
            }
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String tmp = TestDBUtil.getGameRecord(SetTitleActivity.this);
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
//            if (!Util.hasResult(s, SetTitleActivity.this)) {
//                dialog.dismiss();
//                return;
//            }
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("code").equals("0")) {
                    TestDBUtil.clearGameRecord(SetTitleActivity.this);//清空闯关结果
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SetTitle st = new SetTitle();
            st.execute();
        }
    }

    /**
     * 设置职名
     */
    class SetTitle extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {

            Map<String, String> map = new HashMap<>();
            if (isUpdate) {
                map.put("tag", "update");
            } else {
                map.put("tag", "add");
            }
            map.put("loginName", MyApplication.getInstance().getUserName());
            map.put("jobId", name_id + "");
            return HttpUtil.getResult(HttpUtil.Url + "userjobAct.htm?", map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            loading = false;
            if (!Util.hasResult(s, SetTitleActivity.this))
                return;
            try {
                JSONObject object = new JSONObject(s);
                String code = object.getString("code");
                if (code.equals("0")) {
                    JSONObject obj = object.getJSONObject("obj");
                    MyApplication.getInstance().setJobId(obj.getInt("jobId"));
                    MyApplication.getInstance().setUserId(obj.getInt("userId"));

                    MyApplication.getInstance().setTypeId(module_id);
                    MyApplication.getInstance().setTypeName(tmpTypeName);
                    MyApplication.getInstance().setTitleName(tmpTitleName);

                    TestDBUtil.saveIfNotExistJobId(getApplicationContext(), obj.getInt("jobId"));
                    if (isUpdate) {
                        sendBroadcast(new Intent("CheckNewDB"));
                        sendBroadcast(new Intent("refreshGame"));
                        finish();
                    } else {
                        Intent intent = new Intent(SetTitleActivity.this, MainActivity.class);
                        startActivity(intent);
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





