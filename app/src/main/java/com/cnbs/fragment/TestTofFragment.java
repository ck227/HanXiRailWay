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
public class TestTofFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private View contentView;
    private TextView title;
    private Question question;
    private TextView next_question;

    private RadioGroup radioGroup;
    private RadioButton radioRight, radioWrong;

    private String newAnswer = "";
    private int position;//测评的第多少题

    private Boolean isLastQuestion = false;

    private String oldanswer = "";
    private LinearLayout answerLayout;
    private ImageView icon;
    private TextView theAnswer;

    public static TestTofFragment newInstance(Question question, int position) {

        Bundle args = new Bundle();
        args.putParcelable("question", question);
        args.putInt("position", position);
        TestTofFragment fragment = new TestTofFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater
                    .inflate(R.layout.test_tof, container, false);
            question = getArguments().getParcelable("question");
            position = getArguments().getInt("position");
            if (position == ((TestActivity) getActivity()).data.size() - 1) {//position从0开始算的
                isLastQuestion = true;
            }
            findViews(contentView);
        }
        return contentView;
    }

    private void findViews(View view) {
        title = (TextView) view.findViewById(R.id.title);
        next_question = (TextView) view.findViewById(R.id.next_question);
        if (isLastQuestion) {
            next_question.setText("提交");
            if (((TestActivity) getActivity()).isHistory) {
                next_question.setBackgroundResource(R.drawable.shape_base_disable);
                next_question.setEnabled(false);
            }
        }
        next_question.setOnClickListener(this);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        radioRight = (RadioButton) view.findViewById(R.id.radioRight);
        radioWrong = (RadioButton) view.findViewById(R.id.radioWrong);
        String str = "(判断题) " + question.getTitle();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(style);

        answerLayout = (LinearLayout) view.findViewById(R.id.answerLayout);
        icon = (ImageView) view.findViewById(R.id.icon);
        theAnswer = (TextView) view.findViewById(R.id.theAnswer);

        if (((TestActivity) getActivity()).isHistory) {
            if (question.getMyAnswer() != null) {//如果取到了之前的答案
                oldanswer = question.getMyAnswer();
                if (oldanswer.equals("1")) {
                    radioRight.setChecked(true);
                } else if (oldanswer.equals("0")) {
                    radioWrong.setChecked(true);
                }
            }

            if (oldanswer.equals(question.getRightAnswer())) {
                icon.setImageResource(R.mipmap.icon_right);
            } else {
                icon.setImageResource(R.mipmap.icon_wrong);
            }
            if (question.getRightAnswer().equals("1")) {
                theAnswer.setText("正确答案: " + "对");
            } else {
                theAnswer.setText("正确答案: " + "错");
            }
            answerLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_question:
                if (isLastQuestion) {
                    ((TestActivity) getActivity()).submit();
                } else {
                    ((TestActivity) getActivity()).nextQuestion();
                }

                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (((TestActivity) getActivity()).isHistory) {//如果是历史记录，改变了什么都不做

        } else {
            if (checkedId == radioRight.getId()) {
                newAnswer = "1";
            } else if (checkedId == radioWrong.getId()) {
                newAnswer = "0";
            }
            TestQS qs = new TestQS();
            qs.setId(question.getId());
            qs.setAnswer(newAnswer);
            int is_right;
            if (newAnswer.equals(question.getIsRight()+"")) {
                is_right = 1;
            } else {
                is_right = 0;
            }
            qs.setIs_right(is_right);
            qs.setType(question.getType());
            ((TestActivity) getActivity()).saveQS(qs, position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
