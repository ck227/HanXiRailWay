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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class PractiseMultChooseFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private View contentView;
    private TextView title;
    private TextView answerA;
    private TextView answerB;
    private TextView answerC;
    private TextView answerD;
    private Question question;
    private TextView add_collect;
    private TextView next_question;

    private CheckBox checkboxA, checkboxB, checkboxC, checkboxD;
    private EditText note;
    private String oldanswer = "";
    private String newAnswer = "";
    private String oldNote = "";
    private String newNote = "";

    private LinearLayout answerLayout;
    private ImageView icon;
    private TextView theAnswer;

    private Boolean fromDB = false;

    public static PractiseMultChooseFragment newInstance(Question question) {
        Bundle args = new Bundle();
        args.putParcelable("question", question);
        PractiseMultChooseFragment fragment = new PractiseMultChooseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater
                    .inflate(R.layout.fragment_muti_choose, container, false);
            question = getArguments().getParcelable("question");
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
        add_collect = (TextView) view.findViewById(R.id.add_collect);
        add_collect.setOnClickListener(this);
        if (((PractiseActivity) getActivity()).type.equals("collect")) {
            add_collect.setText("取消收藏");
        }
        next_question = (TextView) view.findViewById(R.id.next_question);
        next_question.setOnClickListener(this);

        checkboxA = (CheckBox) view.findViewById(R.id.checkboxA);
        checkboxB = (CheckBox) view.findViewById(R.id.checkboxB);
        checkboxC = (CheckBox) view.findViewById(R.id.checkboxC);
        checkboxD = (CheckBox) view.findViewById(R.id.checkboxD);
        checkboxA.setOnCheckedChangeListener(this);
        checkboxB.setOnCheckedChangeListener(this);
        checkboxC.setOnCheckedChangeListener(this);
        checkboxD.setOnCheckedChangeListener(this);
        note = (EditText) view.findViewById(R.id.note);

//        title.setText("(多选题)" + question.getTitle());
        String str = "(多选题) " + question.getTitle();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(style);
        answerA.setText("(A) " + question.getAnswerA());
        answerB.setText("(B) " + question.getAnswerB());
        answerC.setText("(C) " + question.getAnswerC());

        if (question.getAnswerD() == null) {
            answerD.setVisibility(View.GONE);
            checkboxD.setVisibility(View.GONE);
        } else {
            answerD.setText("(D) " + question.getAnswerD());
        }
//        answerD.setText("(D) " + question.getAnswerD());

        answerLayout = (LinearLayout) view.findViewById(R.id.answerLayout);
        icon = (ImageView) view.findViewById(R.id.icon);
        theAnswer = (TextView) view.findViewById(R.id.theAnswer);

        if (question.getMyAnswer() != null) {//如果取到了之前的答案
            fromDB = true;
            oldanswer = question.getMyAnswer();
            if (oldanswer.contains("A")) {
                checkboxA.setChecked(true);
            }
            if (oldanswer.contains("B")) {
                checkboxB.setChecked(true);
            }
            if (oldanswer.contains("C")) {
                checkboxC.setChecked(true);
            }
            if (oldanswer.contains("D")) {
                checkboxD.setChecked(true);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (fromDB) {
            if (oldanswer.equals(question.getRightAnswer())) {
                icon.setImageResource(R.mipmap.icon_right);
            } else {
                icon.setImageResource(R.mipmap.icon_wrong);
            }
            theAnswer.setText("正确答案: " + question.getRightAnswer());
            answerLayout.setVisibility(View.VISIBLE);
        } else {
            newAnswer = "";
            if (checkboxA.isChecked()) {
                newAnswer += "A";
            }
            if (checkboxB.isChecked()) {
                newAnswer += "B";
            }
            if (checkboxC.isChecked()) {
                newAnswer += "C";
            }
            if (checkboxD.isChecked()) {
                newAnswer += "D";
            }
            if (question.getRightAnswer().equals(newAnswer)) {
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
                theAnswer.setText("正确答案: " + question.getRightAnswer());
                answerLayout.setVisibility(View.VISIBLE);
            } else if (question.getRightAnswer().contains(newAnswer)) {

            } else {//答案不包括选的
                icon.setImageResource(R.mipmap.icon_wrong);
                theAnswer.setText("正确答案: " + question.getRightAnswer());
                answerLayout.setVisibility(View.VISIBLE);
                //错了这里存
                WrongAnswer wrongAnswer = new WrongAnswer();
                wrongAnswer.setType_id(question.getType());
                wrongAnswer.setItem_id(question.getId());
                DBUtil.saveOrUpdateWrongItem(getActivity(), wrongAnswer);
            }

        }
    }

    private void saveOrUpdateChooseAnswer() {
        newAnswer = "";
        if (checkboxA.isChecked()) {
            newAnswer += "A";
        }
        if (checkboxB.isChecked()) {
            newAnswer += "B";
        }
        if (checkboxC.isChecked()) {
            newAnswer += "C";
        }
        if (checkboxD.isChecked()) {
            newAnswer += "D";
        }
        if (!newAnswer.equals(oldanswer)) {//多选题，只要新的答案和旧的不一样，就要保存
            int is_right;
            if (newAnswer.equals(question.getRightAnswer())) {
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