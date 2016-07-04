package com.cnbs.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnbs.Interface.MyItemClickListener;
import com.cnbs.entity.TestResult;
import com.cnbs.hanxirailway.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/14.
 */
public class TestHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<TestResult> data;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private MyItemClickListener myItemClickListener;

    public TestHistoryAdapter(ArrayList<TestResult> data, MyItemClickListener listener) {
        this.data = data;
        this.myItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            TestResult testResult = data.get(position);
            ((ItemViewHolder) holder).title.setText(testResult.getDesc());
            ((ItemViewHolder) holder).time.setText(testResult.getEnd_time());
            ((ItemViewHolder) holder).score.setText(testResult.getRight_amount()+"分");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_test_history, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myItemClickListener.onItemClick(v);
                }
            });
            return new ItemViewHolder(view);
        }
        // type == TYPE_FOOTER 返回footerView
        else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footview_end, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }
        return null;
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        TextView score;

        public ItemViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            time = (TextView) view.findViewById(R.id.time);
            score = (TextView) view.findViewById(R.id.score);
            view.setBackgroundResource(R.drawable.list_selector);
        }
    }
}