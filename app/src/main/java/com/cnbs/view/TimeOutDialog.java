package com.cnbs.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;

/**
 * Created by Administrator on 2016/1/15.
 */
public class TimeOutDialog extends Dialog {

    private ButtonListener listener;

    private TextView button;

    public interface ButtonListener {
        public void button();
    }

    public TimeOutDialog(Context context, ButtonListener listener) {
        super(context);
        this.listener = listener;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_out);
        button = (TextView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();
                listener.button();
            }
        });
    }
}
