package com.cnbs.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;

/**
 * Created by Administrator on 2016/1/16.
 */
public class LoginSuccessDialog extends Dialog{

    private ButtonListener listener;
    private TextView leftbtn, rightbtn;

    public interface ButtonListener {
        public void left();

        public void right();
    }

    public LoginSuccessDialog(Context context,ButtonListener listener) {
        super(context);
        this.listener = listener;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_success);

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
