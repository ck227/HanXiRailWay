package com.cnbs.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cnbs.adapter.QuestionAdapter;
import com.cnbs.dynamicbox.DynamicBox;
import com.cnbs.entity.Question;
import com.cnbs.hanxirailway.PractiseActivity;
import com.cnbs.hanxirailway.R;
import com.cnbs.util.DBUtil;
import com.cnbs.util.HttpUtil;
import com.cnbs.util.MyApplication;
import com.cnbs.view.ChildViewPager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/7.
 */
public class PractiseFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private View contentView;
    private LayoutInflater inflater;
    private ChildViewPager viewPager;
    private QuestionAdapter questionAdapter;
    private ArrayList<Question> data;
    public ArrayList<Question> singleData;
    public ArrayList<Question> mulData;
    public ArrayList<Question> tofData;

    private TextView progress;
    private ProgressBar progressBar;
    private int type;//type==0是全部
    private DynamicBox dynamicBox;
    private LinearLayout box;
    private int currentPage;

    private PractiseActivity activity;
    private GetData gd;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (PractiseActivity) context;
//        if (context instanceof Activity){
//
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (gd != null && !gd.isCancelled())
            HttpUtil.loadData = false;
//            gd.cancel(true);
    }

    public static PractiseFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        PractiseFragment fragment = new PractiseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater
                    .inflate(R.layout.fragment_practise, container, false);
            type = getArguments().getInt("type");
            this.inflater = inflater;
            findViews(contentView);
            data = new ArrayList<>();
            if (type == 0) {//这里的type是全部/单选/多选/判断
                gd = new GetData();
                gd.execute();
            } else if (type == 1) {

            } else {//多选和判断
                initOtherPagerView();
            }
        }
        return contentView;
    }

    private void findViews(View view) {
        progress = (TextView) view.findViewById(R.id.progress);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        box = (LinearLayout) view.findViewById(R.id.box);
        viewPager = (ChildViewPager) view.findViewById(R.id.viewPager);
    }

    private void initViewPager() {
        questionAdapter = new QuestionAdapter(getChildFragmentManager(), data);
        viewPager.setAdapter(questionAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    class GetData extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HttpUtil.loadData = true;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (isCancelled())
                return "";
            if (activity.type.equals("practise")) {
                ArrayList<Question>[] result = DBUtil.getChooseQuestions(getActivity(), true, true);
                singleData = result[0];
                mulData = result[1];
                tofData = DBUtil.getTofQuestions(getActivity(), true, true);
                data.addAll(singleData);
                data.addAll(mulData);
                data.addAll(tofData);
            } else if (activity.type.equals("strong")) {
                //先取count>1的错题，再取收藏又取消的题
                ArrayList<Question>[] result = DBUtil.getStrongQuestions(getActivity());
                singleData = result[0];
                mulData = result[1];
                tofData = result[2];
                ArrayList<Question>[] result2 = DBUtil.getCollectQuestions(getActivity(), 0);
                singleData.addAll(result2[0]);
                mulData.addAll(result2[1]);
                tofData.addAll(result2[2]);
                data.addAll(singleData);
                data.addAll(mulData);
                data.addAll(tofData);
            } else if (activity.type.equals("wrong")) {
                ArrayList<Question>[] result = DBUtil.getWrongQuestions(getActivity());
                singleData = result[0];
                mulData = result[1];
                tofData = result[2];
                data.addAll(singleData);
                data.addAll(mulData);
                data.addAll(tofData);
            } else if (activity.type.equals("collect")) {
                ArrayList<Question>[] result = DBUtil.getCollectQuestions(getActivity(), 1);
                singleData = result[0];
                mulData = result[1];
                tofData = result[2];
                data.addAll(singleData);
                data.addAll(mulData);
                data.addAll(tofData);
            } else if (activity.type.equals("note")) {
                ArrayList<Question>[] result = DBUtil.getNotesQuestions(getActivity());
                singleData = result[0];
                mulData = result[1];
                tofData = result[2];
                data.addAll(singleData);
                data.addAll(mulData);
                data.addAll(tofData);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            HttpUtil.loadData = false;
            //关闭动画
            if (data.size() > 0) {
                activity.box.hideAll();
            } else {
                activity.box.showExceptionLayout();
            }

            initViewPager();
            int position = 0;
            if (activity.type.equals("practise")) {
                position = MyApplication.getInstance().getExercisePosition(type);
            } else if (activity.type.equals("strong")) {
                position = 0;
            } else if (activity.type.equals("wrong")) {
                position = 0;
            } else if (activity.type.equals("collect")) {
                position = 0;
            } else if (activity.type.equals("note")) {
                position = 0;
            }
            if (data.size() > 0) {
                if(position == -1)
                    position = 0;
                currentPage = position + 1;
                progress.setText(currentPage + "/" + data.size());
                progressBar.setMax(data.size());
                progressBar.setProgress(1);
                viewPager.setCurrentItem(position, false);
                initOtherPages();
            }
        }
    }

    private void initOtherPages() {//单选页
        activity.notifyData();
    }

    public void initOtherPagerView() {
        data = activity.getData(type);
        initViewPager();
        int position = 0;
        if (activity.type.equals("practise")) {
            position = MyApplication.getInstance().getExercisePosition(type);
        } else if (activity.type.equals("strong")) {
            position = 0;
        } else if (activity.type.equals("wrong")) {
            position = 0;
        } else if (activity.type.equals("collect")) {
            position = 0;
        } else if (activity.type.equals("note")) {
            position = 0;
        }
        if (data.size() > 0) {
            if(position == -1)
                position = 0;
            currentPage = position + 1;
            progress.setText(currentPage + "/" + data.size());
            progressBar.setMax(data.size());
            progressBar.setProgress(1);
            viewPager.setCurrentItem(position, false);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPage = position + 1;
        progress.setText(currentPage + "/" + data.size());
        progressBar.setProgress(currentPage);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void nextQuestion() {
        if (currentPage < data.size()) {
            viewPager.setCurrentItem(currentPage, true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (activity.type.equals("practise")) {
            MyApplication.getInstance().setExercisePosition(type, currentPage - 1);
        } else if (activity.type.equals("strong")) {//万一要存其他的类型的位置（其他位置可能因为题目增减，不固定）

        } else if (activity.type.equals("wrong")) {

        } else if (activity.type.equals("collect")) {

        } else if (activity.type.equals("note")) {

        }
    }
}
