package com.cnbs.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;

/**
 * Created by Administrator on 2016/2/3.
 */
public class NoFinishSubmitDialog extends Dialog {

    private TextView title;
    private TextView left, center, right;
    private ButtonListener listener;
    private int unFinishNum;

    public interface ButtonListener {
        public void left();

        public void center();

        public void right();
    }

    public NoFinishSubmitDialog(Context context, int unFinishNum, ButtonListener listener) {
        super(context);
        this.unFinishNum = unFinishNum;
        this.listener = listener;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_no_finish_submit);
        title = (TextView) findViewById(R.id.title);
        left = (TextView) findViewById(R.id.left);
        center = (TextView) findViewById(R.id.center);
        right = (TextView) findViewById(R.id.right);

        title.setText("您还有" + unFinishNum + "道题目未完成,是否继续交卷？");
        left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();
                listener.left();
            }
        });
        center.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();
                listener.center();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();
                listener.right();
            }
        });
    }


}
