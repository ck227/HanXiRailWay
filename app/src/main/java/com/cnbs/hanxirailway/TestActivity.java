package com.cnbs.hanxirailway;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cnbs.adapter.TestAdapter;
import com.cnbs.dynamicbox.DynamicBox;
import com.cnbs.entity.Game;
import com.cnbs.entity.Question;
import com.cnbs.entity.TestQS;
import com.cnbs.util.MyApplication;
import com.cnbs.util.TestDBUtil;
import com.cnbs.view.FinishGameDialog;
import com.cnbs.view.FinishedDialog;
import com.cnbs.view.GameFailDialog;
import com.cnbs.view.GameSuccessDialog;
import com.cnbs.view.NoFinishSubmitDialog;
import com.cnbs.view.TimeOutDialog;
import com.cnbs.view.UnFinishedDailog;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/13.
 */
public class TestActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private TextView titleName;
    private ViewPager viewPager;
    private TestAdapter adapter;
    public ArrayList<Question> data;
    private TextView progress;
    private ProgressBar progressBar;
    private DynamicBox dynamicBox;
    private LinearLayout box;
    private int currentPage;
    private TextView timeLeft;
    private int timeleft;

    private int miniute = 30;//默认的测试时间
    private int time = 60 * miniute;
    private Timer timer;// 计时器

    private TestQS[] qses;//放答案和题目id
    private String finishTime;

    private String user_answer = "";
    public Boolean isHistory = false;

    private Game game;
    public Boolean isGame = false;

    private Boolean loading = false;

    private TextView submitAnytime;

    public void back(View view) {
        if (isHistory) {
            finish();
        } else {
            if (loading) {
                finish();
            } else {
                submit();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isHistory) {
                finish();
            } else {
                if (loading) {
                    finish();
                } else {
                    if (unFinishNum() > 0)
                        submit();
                }
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViews();

//        boolean hasMenu = ViewConfiguration.get(this).hasPermanentMenuKey();
//        if (!hasMenu) {
//            //getWindow().setFlags(0x08000000, 0x08000000);
//            try {
//                getWindow().addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));
//            } catch (NoSuchFieldException e) {
//                // Ignore since this field won't exist in most versions of Android
//            } catch (IllegalAccessException e) {
//                Log.w("Optionmenus", "Could not access FLAG_NEEDS_MENU_KEY in addLegacyOverflowButton()", e);
//            }
//        }

        user_answer = getIntent().getStringExtra("user_answer");
        if (user_answer != null && user_answer.length() > 0) {
            isHistory = true;
        }
        game = getIntent().getParcelableExtra("game");
        if (game != null) {//如果是闯关模式
            //标题，时间，总数，正确数
            isGame = true;
            miniute = game.getTime_limit();
            time = 60 * miniute;
            titleName.setText("闯关·" + game.getBreak_name());
        }
        data = new ArrayList<>();
        loading = true;
        if (isHistory) {//测试历史
            submitAnytime.setVisibility(View.INVISIBLE);
            GetDataFromDB gd = new GetDataFromDB();
            gd.execute();
        } else if (isGame) {//闯关
            submitAnytime.setVisibility(View.INVISIBLE);
            GetGameData gd = new GetGameData();
            gd.execute();
        } else {//测试
            GetData gd = new GetData();
            gd.execute();
        }
    }

    private void findViews() {
        titleName = (TextView) findViewById(R.id.titleName);
        titleName.setText(R.string.test);
        progress = (TextView) findViewById(R.id.progress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        timeLeft = (TextView) findViewById(R.id.timeLeft);

        submitAnytime = (TextView) findViewById(R.id.submitAnytime);
        submitAnytime.setOnClickListener(this);
        box = (LinearLayout) findViewById(R.id.box);
        dynamicBox = new DynamicBox(this, box);
        dynamicBox.showLoadingLayout();
    }

    /**
     * 随机的测试题
     */
    class GetData extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            //这里获取数据
            data.addAll(TestDBUtil.getChooseQuestions(TestActivity.this, 1));
            data.addAll(TestDBUtil.getChooseQuestions(TestActivity.this, 2));
            data.addAll(TestDBUtil.getTofQuestions(TestActivity.this));
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //关闭动画
            loading = false;
            dynamicBox.hideAll();
            qses = new TestQS[data.size()];
            initViewPager();
            int position = 0;
            currentPage = position + 1;
            progress.setText(currentPage + "/" + data.size());
            progressBar.setMax(data.size());
            progressBar.setProgress(1);
            //开始倒计时
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    handler.sendEmptyMessage(time--);
                }
            }, 0, 1000);
        }
    }

    /**
     * 历史记录
     */
    class GetDataFromDB extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            data = TestDBUtil.getHistoryTestQuestions(TestActivity.this, user_answer);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading = false;
            dynamicBox.hideAll();
            initViewPager();
            int position = 0;
            currentPage = position + 1;
            progress.setText(currentPage + "/" + data.size());
            progressBar.setMax(data.size());
            progressBar.setProgress(1);
        }
    }

    class GetGameData extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dynamicBox.showLoadingLayout();
        }

        @Override
        protected String doInBackground(Void... params) {
            data.clear();
            ArrayList<Question>[] questions = TestDBUtil.getGameQuestions(TestActivity.this, game);
            data.addAll(questions[0]);
            data.addAll(questions[1]);
            data.addAll(questions[2]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (data.size() > 0) {
                loading = false;
                dynamicBox.hideAll();
                qses = new TestQS[data.size()];
                initViewPager();
                int position = 0;
                currentPage = position + 1;
                progress.setText(currentPage + "/" + data.size());
                progressBar.setMax(data.size());
                progressBar.setProgress(1);
                timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        handler.sendEmptyMessage(time--);
                    }
                }, 0, 1000);
            } else {
                dynamicBox.showExceptionLayout();
            }

        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what <= 0) {
                timeLeft.setText("已超时");
                timeleft = 0;
                timer.cancel();
                autoSubmit();//到时间了自动提交
            } else {
                timeleft = msg.what;
                int min = msg.what / 60;
                int sec = msg.what % 60;
                if (min > 0) {
                    timeLeft.setText(min + "分" + (sec < 10 ? "0" + sec : sec) + "秒");
                } else {
                    timeLeft.setText((sec < 10 ? "0" + sec : sec) + "秒");
                }
            }
        }

        ;
    };

    private void initViewPager() {
        adapter = new TestAdapter(getSupportFragmentManager(), data);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
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


    FinishedDialog finishedDialog;

    /**
     * 闯关和测试的dialog不一样
     * 保存到db也不一样
     */
    public void submit() {
        int timeUse = 60 * miniute - timeleft;
        int min = timeUse / 60;
        int sec = timeUse % 60;
        final String timeuse;
        if (min > 0) {
            timeuse = min + "分" + (sec < 10 ? "0" + sec : sec) + "秒";
        } else {
            timeuse = (sec < 10 ? "0" + sec : sec) + "秒";
        }

        if (unFinishNum() == 0) {
            if (isGame) {//闯关
                FinishGameDialog dialog = new FinishGameDialog(this, timeuse, new FinishGameDialog.ButtonListener() {

                    @Override
                    public void left() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void right() {
                        // TODO Auto-generated method stub
                        //先保存到DB
                        if (rightNum >= Integer.valueOf(game.getDescription().split(",")[0])) {//闯关成功
                            gameSuccess(rightNum, timeuse, game.getDescription());
                            saveGame(timeuse, 1);
                        } else {
                            gameFail(rightNum, timeuse);
                            saveGame(timeuse, 0);
                        }
                    }
                });
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
            } else {//测试
                saveToDB();
                finishedDialog = new FinishedDialog(this, timeuse, new FinishedDialog.ButtonListener() {
                    @Override
                    public void button() {//跳转
                        Intent intent = new Intent(TestActivity.this, TestResultActivity.class);
                        intent.putExtra("right", rightNum);
                        intent.putExtra("wrong", wrongNum);
                        intent.putExtra("time", finishTime);
                        startActivity(intent);
                        finish();
                    }
                });
                finishedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                finishedDialog.show();

                finishedDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
            }
        } else {//没完成就不区分是闯关还是测试了，都不生成记录
            unFinish(timeuse, unFinishNum());
        }
    }

    /**
     * 到时间自动提交
     */
    private void autoSubmit() {
        try {
            TimeOutDialog dialog = new TimeOutDialog(this, new TimeOutDialog.ButtonListener() {

                @Override
                public void button() {
                    finish();
                }
            });
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private int rightNum = 0, wrongNum = 0;

    public void saveQS(TestQS qs, int position) {
        if (qs.getAnswer().equals("")) {//如果是多选，选了答案后又把答案取消了
            if (qses[position].getIs_right() == 1) {//如果取消的答案是对的
                rightNum--;
            } else {
                wrongNum--;
            }
            qses[position] = null;
            return;
        }
        if (qses[position] == null) {//如果是第一次做答
            if (qs.getIs_right() == 1) {//做对了
                rightNum++;
            } else {
                wrongNum++;
            }
        } else {//如果是做了又修改的
            if (qs.getIs_right() == 1 && qses[position].getIs_right() == 0) {//改对了
                rightNum++;
                wrongNum--;
            } else if (qs.getIs_right() == 0 && qses[position].getIs_right() == 1) {//改错了
                rightNum--;
                wrongNum++;
            }
        }
        qses[position] = qs;
    }

    private int unFinishNum() {
        return data.size() - rightNum - wrongNum;
    }

    private void saveToDB() {
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        finishTime = year + "年" + (month + 1) + "月" + day + "日  " + hour + ":" + minute;
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        Date currentTime = new Date();
//        String dateString = formatter.format(currentTime);

        //还要存title，时间，分数
        Gson gson = new Gson();
        String s = gson.toJson(qses);

        String title;
        if (rightNum == 100) {
            title = "高级技师";
        } else if (rightNum >= 90) {
            title = "技师";
        } else if (rightNum >= 80) {
            title = "高级工";
        } else if (rightNum >= 70) {
            title = "中级工";
        } else if (rightNum >= 60) {
            title = "初级工";
        } else {
            title = "入门";
        }

        TestDBUtil.saveExam(this, s, rightNum, wrongNum, finishTime, title);
    }

    private void saveGame(String time_use, int is_success) {
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        finishTime = year + "年" + (month + 1) + "月" + day + "日  " + hour + ":" + minute;
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        Date currentTime = new Date();
//        String dateString = formatter.format(currentTime);

        TestDBUtil.saveGame(this, time_use, finishTime, is_success, game.getBreak_name());
    }

    private void gameSuccess(int rightNum, String timeUse, String description) {
        //记录+1
        MyApplication.getInstance().setGamePosition(MyApplication.getInstance().getGamePosition() + 1);
        //保存记录到表

        int star = 0;
        String[] stars = description.split(",");
        if (rightNum >= Integer.valueOf(stars[2])) {
            star = 3;
        } else if (rightNum >= Integer.valueOf(stars[1])) {
            star = 2;
        } else if (rightNum >= Integer.valueOf(stars[0])) {
            star = 1;
        }
        TestDBUtil.saveOrUpdateGameRecord(this, game, star, rightNum);//闯关记录

        MediaPlayer mp = MediaPlayer.create(this, R.raw.success);
        mp.start();
        GameSuccessDialog dialog = new GameSuccessDialog(star, this, rightNum, timeUse, R.style.MyDialog, new GameSuccessDialog.ButtonListener() {
            @Override
            public void button() {
                finish();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        sendBroadcast(new Intent("refreshUI"));
    }

    private void gameFail(int rightnum, String timeUse) {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.fail);
        mp.start();

        GameFailDialog dialog = new GameFailDialog(this, rightnum, timeUse, R.style.MyDialog, new GameFailDialog.ButtonListener() {
            @Override
            public void tryAgain() {
                timer.cancel();
                time = miniute * 60;
                rightNum = 0;
                wrongNum = 0;
                GetGameData gd = new GetGameData();
                gd.execute();
            }

            @Override
            public void nextTime() {
                finish();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void unFinish(String timeuse, int unFinishNum) {
        UnFinishedDailog dialog = new UnFinishedDailog(this, timeuse, unFinishNum, new UnFinishedDailog.ButtonListener() {

            @Override
            public void left() {
                // TODO Auto-generated method stub
                finish();
            }

            @Override
            public void right() {
                // TODO Auto-generated method stub

            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!isHistory && !isGame) {
//            getMenuInflater().inflate(R.menu.submit, menu);
//        }
//        return true;
//    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.submit_que://这里是交卷
//                if (!loading) {
//                    if (unFinishNum() == 0) {//如果都做完了，直接提交呗
//                        submit();//有点重复，不过没什么关系
//                    } else {//显示dialog
//                        submitAnyWay();
//                    }
//                }
//                break;
//        }
//        return false;
//    }


    private void submitAnyWay() {
        int timeUse = 60 * miniute - timeleft;
        int min = timeUse / 60;
        int sec = timeUse % 60;
        final String timt;
        if (min > 0) {
            timt = min + "分" + (sec < 10 ? "0" + sec : sec) + "秒";
        } else {
            timt = (sec < 10 ? "0" + sec : sec) + "秒";
        }

        NoFinishSubmitDialog dialog = new NoFinishSubmitDialog(this, unFinishNum(), new NoFinishSubmitDialog.ButtonListener() {

            @Override
            public void left() {//取消
                // TODO Auto-generated method stub
            }

            @Override
            public void center() {//去（第一道）未做完的题
                int length = qses.length;
                for (int i = 0; i < length; i++) {
                    if (qses[i] == null) {//如果这题没有做，跳转到这题
                        viewPager.setCurrentItem(i, true);
                        break;
                    }
                }
            }

            @Override
            public void right() {
                // TODO Auto-generated method stub

                int length = qses.length;
                for (int i = 0; i < length; i++) {
                    if (qses[i] == null) {//如果这题没有做,把id和type给它，这样才能还原出试卷
                        qses[i] = new TestQS();
                        qses[i].setId(data.get(i).getId());
                        qses[i].setType(data.get(i).getType());
                    }
                }

                saveToDB();
                finishedDialog = new FinishedDialog(TestActivity.this, timt, new FinishedDialog.ButtonListener() {
                    @Override
                    public void button() {//跳转
                        Intent intent = new Intent(TestActivity.this, TestResultActivity.class);
                        intent.putExtra("right", rightNum);
                        intent.putExtra("wrong", wrongNum);
                        intent.putExtra("time", finishTime);
                        startActivity(intent);
                        finish();
                    }
                });
                finishedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                finishedDialog.show();

                finishedDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitAnytime:
                if (!loading) {
                    if (unFinishNum() == 0) {//如果都做完了，直接提交呗
                        submit();//有点重复，不过没什么关系
                    } else {//显示dialog
                        submitAnyWay();
                    }
                }
                break;
        }
    }
}
