package com.cnbs.hanxirailway;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cnbs.entity.TestResult;
import com.cnbs.util.TestDBUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2016/1/21.
 */
public class TestLineChartActivity extends BaseActivity implements View.OnClickListener {

    private LineChart chart, chart2;
    private LineData data, data2;

    private ArrayList<String> xVals;
    private LineDataSet dataSet;
    private ArrayList<Entry> yVals;

    private ArrayList<String> xVals2;
    private LineDataSet dataSet2;
    private ArrayList<Entry> yVals2;

    private TextView forwardPage;
    private TextView nextPage;
    private TextView titleName;

    private ArrayList<TestResult> testResults;
    private int page = 1;
    private int size = 0;

    private int totalPage;
    private TextView upPage, downPage;
    private int page2 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_line_chart);
        testResults = new ArrayList<>();
        testResults.addAll(TestDBUtil.getTestResult(TestLineChartActivity.this));
        size = testResults.size();
        if (size % 7 == 0) {
            totalPage = size / 7;
        } else {
            totalPage = ((int) size / 7) + 1;
        }
        findViews();
        setChartData();
        setChartData2();
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText(R.string.test_data);
        forwardPage = (TextView) findViewById(R.id.forwardPage);
        nextPage = (TextView) findViewById(R.id.nextPage);
        forwardPage.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        forwardPage.getPaint().setAntiAlias(true);
        nextPage.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        nextPage.getPaint().setAntiAlias(true);
        forwardPage.setOnClickListener(this);
        nextPage.setOnClickListener(this);

        upPage = (TextView) findViewById(R.id.upPage);
        downPage = (TextView) findViewById(R.id.downPage);
        upPage.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        upPage.getPaint().setAntiAlias(true);
        downPage.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        downPage.getPaint().setAntiAlias(true);
        upPage.setOnClickListener(this);
        downPage.setOnClickListener(this);

        chart = (LineChart) findViewById(R.id.chart);
        chart2 = (LineChart) findViewById(R.id.chart2);
    }

    private void setChartData() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(R.color.base_text_color);
        xAxis.setGridColor(R.color.grid_line_color);

        YAxis leftAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(false);
        leftAxis.setEnabled(true);
        leftAxis.setTextColor(R.color.base_text_color);
        leftAxis.setGridColor(R.color.grid_line_color);
        leftAxis.setAxisMaxValue(100);
        leftAxis.setValueFormatter(new MyYAxisValueFormatter());

        xVals = new ArrayList<>();
        yVals = new ArrayList<>();

        dataSet = new LineDataSet(yVals, "");
        data = new LineData(xVals, dataSet);

        dataSet.setLineWidth(1.5f);
        dataSet.setCircleRadius(3.0f);
        dataSet.setCircleColor(getResources().getColor(R.color.base_color));
        dataSet.setDrawValues(false);
        dataSet.setColor(getResources().getColor(R.color.base_color));
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(R.color.fill_color));

        chart.setData(data);
        chart.setDescription("");

        getPageData();
        chart.invalidate();
    }

    private void setChartData2() {
        XAxis xAxis = chart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(R.color.base_text_color);
        xAxis.setGridColor(R.color.grid_line_color);

        YAxis leftAxis = chart2.getAxisLeft();
        chart2.getAxisRight().setEnabled(false);
        leftAxis.setEnabled(true);
        leftAxis.setTextColor(R.color.base_text_color);
        leftAxis.setGridColor(R.color.grid_line_color);
        leftAxis.setAxisMaxValue(100);
        leftAxis.setValueFormatter(new MyYAxisValueFormatter2());

        xVals2 = new ArrayList<>();
        yVals2 = new ArrayList<>();

        dataSet2 = new LineDataSet(yVals2, "");
        data2 = new LineData(xVals2, dataSet2);

        dataSet2.setLineWidth(1.5f);
        dataSet2.setCircleRadius(3.0f);
        dataSet2.setCircleColor(getResources().getColor(R.color.base_color));
        dataSet2.setDrawValues(false);
        dataSet2.setColor(getResources().getColor(R.color.base_color));
        dataSet2.setDrawFilled(true);
        dataSet2.setFillColor(getResources().getColor(R.color.fill_color));

        chart2.setData(data2);
        chart2.setDescription("");

        getPageData2();
        chart2.invalidate();
    }

    public class MyYAxisValueFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYAxisValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return mFormat.format(value) + " %";
        }
    }

    public class MyYAxisValueFormatter2 implements YAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {

            if (value == 100f) {
                return "高级技师";
            } else if (value >= 90) {
                return "技师";
            } else if (value >= 80) {
                return "高级工";
            } else if (value >= 70) {
                return "中级工";
            } else if (value >= 60) {
                return "初级工";
            } else {
                return "入门";
            }
        }
    }

    private void getPageData() {
        xVals.clear();
        yVals.clear();
        if (size >= page * 7) {
            for (int i = 0; i < 7; i++) {
                String time = testResults.get((page - 1) * 7 + i).getEnd_time();
                xVals.add(getTime(time));
                int right = testResults.get((page - 1) * 7 + i).getRight_amount();
                yVals.add(new Entry(Float.valueOf(right), i));//*10
            }
        } else {
            for (int i = 0; i < size - 7 * (page - 1); i++) {
                String time = testResults.get((page - 1) * 7 + i).getEnd_time();
                xVals.add(getTime(time));
                int right = testResults.get((page - 1) * 7 + i).getRight_amount();
                yVals.add(new Entry(Float.valueOf(right), i));//*10
            }
        }

        chart.notifyDataSetChanged(); // let the chart know it's data changed
        chart.invalidate(); // refresh
    }

    private void getPageData2() {
        xVals2.clear();
        yVals2.clear();
        if (size >= page2 * 7) {
            for (int i = 0; i < 7; i++) {
                String time = testResults.get((page2 - 1) * 7 + i).getEnd_time();
                xVals2.add(getTime(time));
                int right = testResults.get((page2 - 1) * 7 + i).getRight_amount();
                yVals2.add(new Entry(Float.valueOf(right), i));// * 10
            }
        } else {
            for (int i = 0; i < size - 7 * (page2 - 1); i++) {
                String time = testResults.get((page2 - 1) * 7 + i).getEnd_time();
                xVals2.add(getTime(time));
                int right = testResults.get((page2 - 1) * 7 + i).getRight_amount();
                yVals2.add(new Entry(Float.valueOf(right), i));// * 10
            }
        }

        chart2.notifyDataSetChanged(); // let the chart know it's data changed
        chart2.invalidate(); // refresh
    }

    private String getTime(String time) {
        int yearPos = time.indexOf("年");
        int monthPos = time.indexOf("月");
        int dayPos = time.indexOf("日");
        return time.substring(yearPos + 1, monthPos) + "." + time.substring(monthPos + 1, dayPos);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forwardPage:
                if (page < totalPage) {
                    page++;
                    getPageData();
                } else {
                    Toast.makeText(getApplicationContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nextPage:
                if (page > 1) {
                    page--;
                    getPageData();
                } else {
                    Toast.makeText(getApplicationContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.upPage:
                if (page2 < totalPage) {
                    page2++;
                    getPageData2();
                } else {
                    Toast.makeText(getApplicationContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.downPage:
                if (page2 > 1) {
                    page2--;
                    getPageData2();
                } else {
                    Toast.makeText(getApplicationContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
