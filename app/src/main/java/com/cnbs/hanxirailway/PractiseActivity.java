package com.cnbs.hanxirailway;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cnbs.adapter.PractiseAdapter;
import com.cnbs.dynamicbox.DynamicBox;
import com.cnbs.entity.Question;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/6.
 */
public class PractiseActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, ViewPager.OnPageChangeListener {//implements View.OnClickListener

    private TextView titleName;
    private RadioGroup radioGroup;
    private RadioButton all;
    private RadioButton single;
    private RadioButton multiple;
    private RadioButton tof;

    private ViewPager viewPager;
    private PractiseAdapter adapter;

    public String type;

    private LinearLayout dynamicBox;
    public DynamicBox box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practise);
        type = getIntent().getStringExtra("type");
        findViews();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        if (type.equals("practise")) {
            titleName.setText(R.string.practise);
        } else if (type.equals("strong")) {
            titleName.setText(R.string.strong_practise);
        } else if (type.equals("wrong")) {
            titleName.setText(R.string.wrong_practise);
        } else if (type.equals("collect")) {
            titleName.setText(R.string.collect_practise);
        } else if (type.equals("note")) {
            titleName.setText(R.string.note_practise);
        }
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        all = (RadioButton) findViewById(R.id.all);
        single = (RadioButton) findViewById(R.id.single);
        multiple = (RadioButton) findViewById(R.id.multiple);
        tof = (RadioButton) findViewById(R.id.tof);
        all.setChecked(true);
        all.setOnCheckedChangeListener(this);
        single.setOnCheckedChangeListener(this);
        multiple.setOnCheckedChangeListener(this);
        tof.setOnCheckedChangeListener(this);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PractiseAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        dynamicBox = (LinearLayout) findViewById(R.id.dynamicBox);
        box = new DynamicBox(this, dynamicBox);
        box.showLoadingLayout();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.all:
                if (isChecked) {
                    viewPager.setCurrentItem(0, true);
                }
                break;
            case R.id.single:
                if (isChecked) {
                    viewPager.setCurrentItem(1, true);
                }
                break;
            case R.id.multiple:
                if (isChecked) {
                    viewPager.setCurrentItem(2, true);
                }
                break;
            case R.id.tof:
                if (isChecked) {
                    viewPager.setCurrentItem(3, true);
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                all.setChecked(true);
                break;
            case 1:
                single.setChecked(true);
                break;
            case 2:
                multiple.setChecked(true);
                break;
            case 3:
                tof.setChecked(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public ArrayList<Question> getData(int type) {
        if (type == 1) {
            return adapter.getAllFragment().singleData;
        } else if (type == 2) {
            return adapter.getAllFragment().mulData;
        } else if (type == 3) {
            return adapter.getAllFragment().tofData;
        }
        return null;
    }

    public void notifyData() {
        adapter.getSingleFragment().initOtherPagerView();
    }

}
