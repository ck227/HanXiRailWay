package com.cnbs.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;

/**
 * Created by Administrator on 2016/1/19.
 */
public class UpdateDBDialog extends Dialog {

    private ButtonListener listener;

    private TextView button;
    private ProgressBar progressBar;
    private Context context;

    public interface ButtonListener {
        public void button();
    }

    public UpdateDBDialog(Context context, ButtonListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_db);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        progressBar.getIndeterminateDrawable().setColorFilter(
//                context.getResources().getColor(R.color.base_color),
//                android.graphics.PorterDuff.Mode.SRC_IN);
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
