/*
package com.cnbs.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

*/
/**
 * Created by Administrator on 2016/1/4.
 *//*

public class Fragment0_back extends Fragment {

    private TextView titieName;
    private View contentview;
    private PieChart mChart;

    public static Fragment0_back newInstance() {
        Bundle args = new Bundle();
        Fragment0_back fragment = new Fragment0_back();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentview == null) {
            contentview = inflater
                    .inflate(R.layout.fragment0, container, false);
            findViews(contentview);
        }
        ViewGroup parent = (ViewGroup) contentview.getParent();
        if (parent != null) {
            parent.removeView(contentview);
        }
        return contentview;
    }

    private void findViews(View view) {
        titieName = (TextView) view.findViewById(R.id.titleName);
        titieName.setText(R.string.fragment0_title);
        mChart = (PieChart) view.findViewById(R.id.pie);

        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        setData(2, 100);
    }

    private void setData(int count, float range) {

        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        yVals1.add(new Entry((float) 30.0, 0));
        yVals1.add(new Entry((float) 70.0, 0));

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("未完成");
        xVals.add("已完成");

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(Color.rgb(218, 156, 141));
        colors.add(Color.rgb(211, 210, 208));

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVals, dataSet);
//        data.setDataSet(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);
        mChart.highlightValues(null);
        mChart.invalidate();
    }


}
*/
