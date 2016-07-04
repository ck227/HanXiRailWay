package com.cnbs.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cnbs.entity.ChooseAnswer;
import com.cnbs.entity.Collect;
import com.cnbs.entity.Note;
import com.cnbs.entity.Question;
import com.cnbs.entity.WrongAnswer;
import com.cnbs.hanxirailway.PractiseActivity;
import com.cnbs.hanxirailway.R;
import com.cnbs.util.DBUtil;

/**
 * Created by Administrator on 2016/1/8.
 */
public class PractiseTofFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private View contentView;
    private TextView title;
    private Question question;
    private TextView add_collect;
    private TextView next_question;

    private RadioGroup radioGroup;
    private RadioButton radioRight, radioWrong;
    private EditText note;
    private String oldanswer = "";
    private String newAnswer = "";
    private String oldNote = "";
    private String newNote = "";

    private LinearLayout answerLayout;
    private ImageView icon;
    private TextView theAnswer;

    private Boolean fromDB = false;

    public static PractiseTofFragment newInstance(Question question) {

        Bundle args = new Bundle();
        args.putParcelable("question", question);
        PractiseTofFragment fragment = new PractiseTofFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater
                    .inflate(R.layout.fragment_tof, container, false);
            question = getArguments().getParcelable("question");
            findViews(contentView);
        }
        return contentView;
    }

    private void findViews(View view) {
        title = (TextView) view.findViewById(R.id.title);
        add_collect = (TextView) view.findViewById(R.id.add_collect);
        add_collect.setOnClickListener(this);
        if (((PractiseActivity) getActivity()).type.equals("collect")) {
            add_collect.setText("取消收藏");
        }
        next_question = (TextView) view.findViewById(R.id.next_question);
        next_question.setOnClickListener(this);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);

        radioRight = (RadioButton) view.findViewById(R.id.radioRight);
        radioWrong = (RadioButton) view.findViewById(R.id.radioWrong);
        note = (EditText) view.findViewById(R.id.note);

//        title.setText("(判断题)" + question.getTitle());
        String str = "(判断题) " + question.getTitle();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(style);

        answerLayout = (LinearLayout) view.findViewById(R.id.answerLayout);
        icon = (ImageView) view.findViewById(R.id.icon);
        theAnswer = (TextView) view.findViewById(R.id.theAnswer);

        if (question.getMyAnswer() != null) {//如果取到了之前的答案
            fromDB = true;
            oldanswer = question.getMyAnswer();
            if (oldanswer.equals("1")) {
                radioRight.setChecked(true);
            } else if (oldanswer.equals("0")) {
                radioWrong.setChecked(true);
            }
            fromDB = false;
        }
        if (question.getMyNote() != null) {
            oldNote = question.getMyNote();
            note.setText(oldNote);
        }
    }

    private Boolean hasCollect = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_question:
                ((PractiseFragment) getParentFragment()).nextQuestion();
                break;
            case R.id.add_collect:
                Collect collect = new Collect();
                collect.setType_id(question.getType());
                collect.setItem_id(question.getId());
                if (((PractiseActivity) getActivity()).type.equals("collect")) {
                    if (hasCollect) {
                        DBUtil.deleteCollectItem(getActivity(), collect);
                        add_collect.setText("收藏");
                        hasCollect = false;
                    } else {
                        DBUtil.saveOrUpdateCollect(getActivity(), collect);
                        add_collect.setText("取消收藏");
                        hasCollect = true;
                    }
                } else {
                    DBUtil.saveOrUpdateCollect(getActivity(), collect);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == radioRight.getId()) {
            newAnswer = "1";
        } else if (checkedId == radioWrong.getId()) {
            newAnswer = "0";
        }
        String isRight = question.getIsRight() + "";
        if (fromDB) {//从db读出来的答案显示
            if (oldanswer.equals(isRight)) {
                icon.setImageResource(R.mipmap.icon_right);
            } else {
                icon.setImageResource(R.mipmap.icon_wrong);
            }
            if (isRight.equals("1")) {
                theAnswer.setText("正确答案: " + "对");
            } else {
                theAnswer.setText("正确答案: " + "错");
            }
            answerLayout.setVisibility(View.VISIBLE);
        } else {
            //这里是自己修改的
            if (newAnswer.equals(isRight)) {
                icon.setImageResource(R.mipmap.icon_right);
                if (((PractiseActivity) getActivity()).type.equals("practise")) {
                    //如果是练习模式，做对了，什么都不用干
                } else if (((PractiseActivity) getActivity()).type.equals("wrong")) {
                    //如果是错题重做，做对了，把db的is_valid重置了
                    WrongAnswer wrongAnswer = new WrongAnswer();
                    wrongAnswer.setType_id(question.getType());
                    wrongAnswer.setItem_id(question.getId());
                    DBUtil.deleteWrongItem(getActivity(), wrongAnswer);
                }
            } else {
                //错了这里存
                icon.setImageResource(R.mipmap.icon_wrong);
                WrongAnswer wrongAnswer = new WrongAnswer();
                wrongAnswer.setType_id(question.getType());
                wrongAnswer.setItem_id(question.getId());
                DBUtil.saveOrUpdateWrongItem(getActivity(), wrongAnswer);
            }
            if (isRight.equals("1")) {
                theAnswer.setText("正确答案: " + "对");
            } else {
                theAnswer.setText("正确答案: " + "错");
            }
            answerLayout.setVisibility(View.VISIBLE);
        }
    }

    private void saveOrUpdateChooseAnswer() {
        if (newAnswer.equals("")) {//如果没有选新的答案、什么都不用干

        } else if (newAnswer.equals(oldanswer)) {//如果新的答案和旧的一样，也什么都不用干

        } else {
            int is_right;
            if (newAnswer.equals(question.getIsRight()+"")) {
                is_right = 1;
            } else {
                is_right = 0;
            }
            ChooseAnswer ca = new ChooseAnswer();
            ca.setItem_id(question.getId());
            ca.setType_id(question.getType());
            ca.setUser_answer(newAnswer);
            ca.setIs_right(is_right);
            DBUtil.saveOrUpdateChooseAnswer(getActivity(), ca);
        }
    }

    private void saveOrUpdateNote() {
        newNote = note.getText().toString();
        if (!newNote.equals(oldNote)) {//只要以前的和新的不一样，就要保存
            Note notes = new Note();
            notes.setItem_id(question.getId());
            notes.setType_id(question.getType());
            notes.setNote(note.getText().toString());
            DBUtil.saveOrUpdateNote(getActivity(), notes);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveOrUpdateChooseAnswer();
        saveOrUpdateNote();
    }
}
