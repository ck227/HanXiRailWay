package com.cnbs.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;

/**
 * Created by Administrator on 2016/1/14.
 */
public class GameSuccessDialog extends Dialog {

    private ButtonListener listener;

    private TextView button;

    private int rightNum;
    private String timeUse;

    private RatingBar stars;
    private int star;

    public interface ButtonListener {
        public void button();
    }

    public GameSuccessDialog(int star, Context context, int rightNum, String timeUse, int theme, ButtonListener listener) {
        super(context, theme);
        this.rightNum = rightNum;
        this.timeUse = timeUse;
        this.listener = listener;
        this.star = star;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_game_success);
        button = (TextView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();
                listener.button();
            }
        });
        ((TextView) findViewById(R.id.rightNum)).setText(rightNum + "");
        ((TextView) findViewById(R.id.timeUse)).setText(timeUse);
        ((RatingBar) findViewById(R.id.star)).setProgress(star);
    }
}
