package com.cnbs.hanxirailway;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/1/13.
 */
public class TestResultActivity extends BaseActivity {

    private TextView titleName;
    private int right, wrong;
    private Intent intent;
    private TextView score, rightNum, wrongNum;
    private TextView testTime;
    private String time;

    private TextView testTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        intent = getIntent();
        right = intent.getIntExtra("right", 0);
        wrong = intent.getIntExtra("wrong", 0);
        time = intent.getStringExtra("time");
        findViews();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText("评测结果");
        score = (TextView) findViewById(R.id.score);
        rightNum = (TextView) findViewById(R.id.rightNum);
        wrongNum = (TextView) findViewById(R.id.wrongNum);
        score.setText(right + "分");
        rightNum.setText("正确" + right + "题");
        wrongNum.setText("错误" + wrong + "题");

        testTime = (TextView) findViewById(R.id.testTime);
        testTime.setText("测试时间：" + time);

        String title;
        testTitle = (TextView) findViewById(R.id.testTitle);
        if (right == 100) {
            title = "高级技师";
        } else if (right >= 90) {
            title = "技师";
        } else if (right >= 80) {
            title = "高级工";
        } else if (right >= 70) {
            title = "中级工";
        } else if (right >= 60) {
            title = "初级工";
        } else {
            title = "不合格";
        }
        testTitle.setText("测评结果：" + title);
    }
}
