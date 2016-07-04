package com.cnbs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnbs.Interface.MyItemClickListener;
import com.cnbs.entity.Game;
import com.cnbs.hanxirailway.R;
import com.cnbs.util.MyApplication;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/14.
 */
public class GameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Game> data;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;
    private MyItemClickListener myItemClickListener;
    private Context context;

    public GameAdapter(Context context, ArrayList<Game> data, MyItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.myItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return data.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == data.size() + 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            Game game = data.get(position - 1);
            ((ItemViewHolder) holder).name.setText("级别：" + game.getBreak_name());
            ((ItemViewHolder) holder).time.setText("时间：" + game.getTime_limit() + "分钟");
            ((ItemViewHolder) holder).sum.setText("总题数：" + game.geSum());
            ((ItemViewHolder) holder).passSum.setText("通过数：" + game.getDescription().split(",")[0]);
            int pos = MyApplication.getInstance().getGamePosition();
            if (position - 1 < pos) {
                ((ItemViewHolder) holder).start.setText("闯关成功");
                ((ItemViewHolder) holder).start.setTextColor(context.getResources().getColor(R.color.light_dark));
                ((ItemViewHolder) holder).start.setBackgroundResource(R.drawable.shape_game_success);
                ((ItemViewHolder) holder).start.setEnabled(false);
            } else if (position - 1 == pos) {
                ((ItemViewHolder) holder).start.setText("开始闯关");
                ((ItemViewHolder) holder).start.setTextColor(context.getResources().getColor(R.color.light_dark));
                ((ItemViewHolder) holder).start.setBackgroundResource(R.drawable.selector_yellow_btn);
                ((ItemViewHolder) holder).start.setEnabled(true);
            } else {
                ((ItemViewHolder) holder).start.setText("未解锁");
                ((ItemViewHolder) holder).start.setTextColor(context.getResources().getColor(R.color.white));
                ((ItemViewHolder) holder).start.setBackgroundResource(R.drawable.shape_game_unlock);
                ((ItemViewHolder) holder).start.setEnabled(false);
            }
        } else if (holder instanceof HeaderViewHolder) {//头部
            ((HeaderViewHolder) holder).titleName.setText(MyApplication.getInstance().getTitleName());
        } else {//底部
            if(data.size()==0){
                ((FooterViewHolder) holder).getMore.setText("暂无闯关");
            }else{
                ((FooterViewHolder) holder).getMore.setText("加载更多");
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_game, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView start = (TextView) view.findViewById(R.id.start);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myItemClickListener.onItemClick(view);
                }
            });
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.header_game, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer_game, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myItemClickListener.onItemClick(view);
                }
            });
            return new FooterViewHolder(view);
        }
        return null;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView titleName;

        public HeaderViewHolder(View view) {
            super(view);
            titleName = (TextView) view.findViewById(R.id.titleName);
        }
    }


    class FooterViewHolder extends RecyclerView.ViewHolder {

        TextView getMore;

        public FooterViewHolder(View view) {
            super(view);
            getMore = (TextView) view.findViewById(R.id.text);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView sum;
        TextView passSum;
        TextView start;

        public ItemViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            sum = (TextView) view.findViewById(R.id.sum);
            passSum = (TextView) view.findViewById(R.id.passSum);
            start = (TextView) view.findViewById(R.id.start);
        }
    }


}