package com.cnbs.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;

/**
 * Created by ${ck} on 2016/6/17.
 */
public class ContinueHistoryDialog extends Dialog {

    private ButtonListener listener;

    private TextView leftbtn, rightbtn;

    public interface ButtonListener {
        public void left();

        public void right();
    }

    public ContinueHistoryDialog(Context context, ButtonListener listener) {
        super(context);
        this.listener = listener;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_clear_history);
        leftbtn = (TextView) findViewById(R.id.left);
        rightbtn = (TextView) findViewById(R.id.right);
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