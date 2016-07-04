package com.cnbs.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cnbs.hanxirailway.R;

/**
 * Created by Administrator on 2016/1/15.
 */
public class GameFailDialog extends Dialog {

    private ButtonListener listener;

    private TextView tryAgain, nextTime;

    private int rightNum;

    private String timeUse;

    public interface ButtonListener {
        public void tryAgain();

        public void nextTime();
    }

    public GameFailDialog(Context context, int rightNum, String timeUse, int theme, ButtonListener listener) {
        super(context, theme);
        this.rightNum = rightNum;
        this.timeUse = timeUse;
        this.listener = listener;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_game_fail);
        tryAgain = (TextView) findViewById(R.id.tryAgain);
        nextTime = (TextView) findViewById(R.id.nextTime);
        nextTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        nextTime.getPaint().setAntiAlias(true);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.tryAgain();
            }
        });
        nextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.nextTime();
            }
        });
        ((TextView) findViewById(R.id.rightNum)).setText(rightNum + "");
        ((TextView) findViewById(R.id.timeUse)).setText(timeUse);
    }
}
