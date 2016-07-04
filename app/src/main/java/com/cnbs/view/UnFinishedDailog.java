package com.cnbs.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;

/**
 * 测评没有完成的弹窗
 * Created by Administrator on 2016/1/13.
 */
public class UnFinishedDailog extends Dialog {

    private String useTime;
    private int sumLeft;
    private ButtonListener listener;
    private TextView leftbtn, rightbtn, titletext, content;

    public interface ButtonListener {
        public void left();

        public void right();
    }

    public UnFinishedDailog(Context context, String useTime, int sumLeft, ButtonListener listener) {
        super(context);
        this.useTime = useTime;
        this.sumLeft = sumLeft;
        this.listener = listener;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_un_finished);
        titletext = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        leftbtn = (TextView) findViewById(R.id.left);
        rightbtn = (TextView) findViewById(R.id.right);
        titletext.setText("用时：" + useTime);
        content.setText("您还有" + sumLeft + "道题目未完成,是否继续答题？");
        leftbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();
                listener.left();
            }
        });
        rightbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();
                listener.right();
            }
        });
    }
}
