package com.cnbs.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cnbs.entity.Question;
import com.cnbs.entity.TestQS;
import com.cnbs.hanxirailway.R;
import com.cnbs.hanxirailway.TestActivity;

/**
 * Created by Administrator on 2016/1/13.
 */
public class TestChooseFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private View contentView;
    private TextView title;
    private TextView answerA;
    private TextView answerB;
    private TextView answerC;
    private TextView answerD;
    private Question question;
    private TextView next_question;

    private RadioGroup radioGroup;
    private RadioButton radioA, radioB, radioC, radioD;
    private String newAnswer = "";

    private int position;//测评的第多少题

    private String oldanswer = "";
    private LinearLayout answerLayout;
    private ImageView icon;
    private TextView theAnswer;

    public static TestChooseFragment newInstance(Question question, int position) {
        Bundle args = new Bundle();
        args.putParcelable("question", question);
        args.putInt("position", position);
        TestChooseFragment fragment = new TestChooseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater
                    .inflate(R.layout.test_choose, container, false);
            question = getArguments().getParcelable("question");
            position = getArguments().getInt("position");
            findViews(contentView);
        }
        return contentView;
    }

    private void findViews(View view) {
        title = (TextView) view.findViewById(R.id.title);
        answerA = (TextView) view.findViewById(R.id.answerA);
        answerB = (TextView) view.findViewById(R.id.answerB);
        answerC = (TextView) view.findViewById(R.id.answerC);
        answerD = (TextView) view.findViewById(R.id.answerD);
        next_question = (TextView) view.findViewById(R.id.next_question);
        next_question.setOnClickListener(this);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        radioA = (RadioButton) view.findViewById(R.id.radioA);
        radioB = (RadioButton) view.findViewById(R.id.radioB);
        radioC = (RadioButton) view.findViewById(R.id.radioC);
        radioD = (RadioButton) view.findViewById(R.id.radioD);

        String str = "(单选题) " + question.getTitle();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(style);

        answerA.setText("(A) " + question.getAnswerA());
        answerB.setText("(B) " + question.getAnswerB());
        answerC.setText("(C) " + question.getAnswerC());
//        answerD.setText("(D) " + question.getAnswerD());

        if (question.getAnswerD() == null) {
            answerD.setVisibility(View.GONE);
            radioD.setVisibility(View.GONE);
        } else {
            answerD.setText("(D) " + question.getAnswerD());
        }

        answerLayout = (LinearLayout) view.findViewById(R.id.answerLayout);
        icon = (ImageView) view.findViewById(R.id.icon);
        theAnswer = (TextView) view.findViewById(R.id.theAnswer);

        if (((TestActivity) getActivity()).isHistory) {
            if (question.getMyAnswer() != null) {//如果取到了之前的答案
                oldanswer = question.getMyAnswer();
                if (oldanswer.equals("A")) {
                    radioA.setChecked(true);
                } else if (oldanswer.equals("B")) {
                    radioB.setChecked(true);
                } else if (oldanswer.equals("C")) {
                    radioC.setChecked(true);
                } else if (oldanswer.equals("D")) {
                    radioD.setChecked(true);
                }
            }

            if (oldanswer.equals(question.getRightAnswer())) {
                icon.setImageResource(R.mipmap.icon_right);
            } else {
                icon.setImageResource(R.mipmap.icon_wrong);
            }
            theAnswer.setText("正确答案: " + question.getRightAnswer());
            answerLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_question:
                ((TestActivity) getActivity()).nextQuestion();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (((TestActivity) getActivity()).isHistory) {//如果是历史记录，改变了什么都不做

        } else {
            if (checkedId == radioA.getId()) {
                newAnswer = "A";
            } else if (checkedId == radioB.getId()) {
                newAnswer = "B";
            } else if (checkedId == radioC.getId()) {
                newAnswer = "C";
            } else if (checkedId == radioD.getId()) {
                newAnswer = "D";
            }
            //选了答案就要告诉adapter
            TestQS qs = new TestQS();
            qs.setId(question.getId());
            qs.setAnswer(newAnswer);
            int is_right;
            if (newAnswer.equals(question.getRightAnswer())) {
                is_right = 1;
            } else {
                is_right = 0;
            }
            qs.setIs_right(is_right);
            qs.setType(question.getType());
            ((TestActivity) getActivity()).saveQS(qs, position);
        }
    }

}
