package com.cnbs.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;

/**
 * Created by Administrator on 2016/1/14.
 */
public class FinishGameDialog extends Dialog {

    private String useTime;
    private ButtonListener listener;
    private TextView leftbtn, rightbtn, titletext;

    public interface ButtonListener {
        public void left();

        public void right();
    }

    public FinishGameDialog(Context context, String useTime, ButtonListener listener) {
        super(context);
        this.useTime = useTime;
        this.listener = listener;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_finish_game);
        titletext = (TextView) findViewById(R.id.title);
        leftbtn = (TextView) findViewById(R.id.left);
        rightbtn = (TextView) findViewById(R.id.right);
        titletext.setText("用时：" + useTime);
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
