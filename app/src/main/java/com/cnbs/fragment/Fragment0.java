package com.cnbs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnbs.hanxirailway.PractiseActivity;
import com.cnbs.hanxirailway.R;
import com.cnbs.util.DBUtil;
import com.cnbs.util.MyApplication;
import com.cnbs.view.ContinueHistoryDialog;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Fragment0 extends Fragment implements View.OnClickListener {

    private TextView titieName;
    private RelativeLayout practise;
    private RelativeLayout strong_practise;
    private RelativeLayout wrong_practise;
    private RelativeLayout collect_practise;
    private RelativeLayout note_practise;
    private View contentview;

    public static Fragment0 newInstance() {
        Bundle args = new Bundle();
        Fragment0 fragment = new Fragment0();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentview == null) {
            contentview = inflater
                    .inflate(R.layout.fragment0, container, false);
            findViews(contentview);
        }
        ViewGroup parent = (ViewGroup) contentview.getParent();
        if (parent != null) {
            parent.removeView(contentview);
        }
        return contentview;
    }

    private void findViews(View view) {
        titieName = (TextView) view.findViewById(R.id.titleName);
        titieName.setText(R.string.fragment0_title);
        practise = (RelativeLayout) view.findViewById(R.id.practise);
        practise.setOnClickListener(this);
        strong_practise = (RelativeLayout) view.findViewById(R.id.strong_practise);
        strong_practise.setOnClickListener(this);
        wrong_practise = (RelativeLayout) view.findViewById(R.id.wrong_practise);
        wrong_practise.setOnClickListener(this);
        collect_practise = (RelativeLayout) view.findViewById(R.id.collect_practise);
        collect_practise.setOnClickListener(this);
        note_practise = (RelativeLayout) view.findViewById(R.id.note_practise);
        note_practise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.practise://题库练习
                if(MyApplication.getInstance().getExercisePosition(0) >= 0){//说明有记录
                    //删除做题记录，删除保存的位置，然后再跳转
                    ContinueHistoryDialog dialog = new ContinueHistoryDialog(getActivity(),new ContinueHistoryDialog.ButtonListener() {
                        @Override
                        public void left() {
                            DBUtil.deleteChooseAnswer(getActivity());
                            MyApplication.getInstance().setExercisePosition(0,-1);
                            MyApplication.getInstance().setExercisePosition(1,-1);
                            MyApplication.getInstance().setExercisePosition(2,-1);
                            MyApplication.getInstance().setExercisePosition(3,-1);

                            //跳转
                            Intent intent = new Intent(getActivity(), PractiseActivity.class);
                            intent.putExtra("type", "practise");
                            startActivity(intent);
                        }

                        @Override
                        public void right() {
                            //跳转
                            Intent intent = new Intent(getActivity(), PractiseActivity.class);
                            intent.putExtra("type", "practise");
                            startActivity(intent);
                        }
                    });
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();
                }else{
                    //还是一样的跳转
                    intent = new Intent(getActivity(), PractiseActivity.class);
                    intent.putExtra("type", "practise");
                    startActivity(intent);
                }
                break;
            case R.id.strong_practise://强化练习
                intent = new Intent(getActivity(), PractiseActivity.class);
                intent.putExtra("type", "strong");
                startActivity(intent);
                break;
            case R.id.wrong_practise://错题重做
                intent = new Intent(getActivity(), PractiseActivity.class);
                intent.putExtra("type", "wrong");
                startActivity(intent);
                break;
            case R.id.collect_practise:
                intent = new Intent(getActivity(), PractiseActivity.class);
                intent.putExtra("type", "collect");
                startActivity(intent);
                break;
            case R.id.note_practise:
                intent = new Intent(getActivity(), PractiseActivity.class);
                intent.putExtra("type", "note");
                startActivity(intent);
                break;

        }
    }

}
