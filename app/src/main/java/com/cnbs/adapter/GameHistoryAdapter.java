package com.cnbs.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnbs.Interface.MyItemClickListener;
import com.cnbs.entity.GameHistory;
import com.cnbs.entity.TestResult;
import com.cnbs.hanxirailway.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/15.
 */
public class GameHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<GameHistory> data;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private MyItemClickListener myItemClickListener;

    public GameHistoryAdapter(ArrayList<GameHistory> data, MyItemClickListener listener) {
        this.data = data;
        this.myItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            GameHistory gameHistory = data.get(position);
            ((ItemViewHolder) holder).name.setText("闯关级别：" + gameHistory.getName());
            ((ItemViewHolder) holder).timeUse.setText("闯关时间：" + gameHistory.getTimeUse());
            int flag = gameHistory.getIsSuccess();
            if (flag == 1) {
                ((ItemViewHolder) holder).icon.setImageResource(R.mipmap.ic_game_success);
                ((ItemViewHolder) holder).isSuccess.setText("闯关成功");
            } else {
                ((ItemViewHolder) holder).icon.setImageResource(R.mipmap.ic_game_fail);
                ((ItemViewHolder) holder).isSuccess.setText("闯关失败");
            }
            ((ItemViewHolder) holder).endTime.setText(gameHistory.getTimeEnd());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_game_history, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myItemClickListener.onItemClick(v);
                }
            });
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
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
        TextView name;
        TextView timeUse;
        ImageView icon;
        TextView isSuccess;
        TextView endTime;

        public ItemViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            timeUse = (TextView) view.findViewById(R.id.timeUse);
            icon = (ImageView) view.findViewById(R.id.icon);
            isSuccess = (TextView) view.findViewById(R.id.isSuccess);
            endTime = (TextView) view.findViewById(R.id.endTime);
            view.setBackgroundResource(R.drawable.list_selector);
        }
    }
}