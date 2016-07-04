package com.cnbs.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;

/**
 * Created by Administrator on 2016/1/13.
 */
public class FinishedDialog extends Dialog {

    private String useTime;

    private ButtonListener listener;

    private TextView titletext, button;

    public interface ButtonListener {
        public void button();
    }

    public FinishedDialog(Context context, String useTime, ButtonListener listener) {
        super(context);
        this.useTime = useTime;
        this.listener = listener;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_finished);
        titletext = (TextView) findViewById(R.id.title);
        button = (TextView) findViewById(R.id.button);
        titletext.setText("用时：" + useTime);
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
