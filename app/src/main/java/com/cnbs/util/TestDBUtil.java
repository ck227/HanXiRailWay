package com.cnbs.util;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cnbs.entity.ChooseAnswer;
import com.cnbs.entity.DeleteDb;
import com.cnbs.entity.Game;
import com.cnbs.entity.GameHistory;
import com.cnbs.entity.GameRecord;
import com.cnbs.entity.Question;
import com.cnbs.entity.SetTitleEntity;
import com.cnbs.entity.TestQS;
import com.cnbs.entity.TestResult;
import com.cnbs.entity.UpdateChoice;
import com.cnbs.entity.UpdateDecision;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 测评获存取数据的类，DBUtil已经太多行了
 * Created by Administrator on 2016/1/13.
 */
public class TestDBUtil {

    /**
     * 获取随机的40到选择题，1是单选，2是多选
     *
     * @param context
     * @param types
     * @return
     */
    public static ArrayList<Question> getChooseQuestions(Context context, int types) {
        ArrayList<Question> questions = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select choice_id,type_id,choice_content,answer from choice_questions where type_id = ? and job_id = ? ORDER BY random() LIMIT 40";
        Cursor c = db.rawQuery(sql, new String[]{types + "", MyApplication.getInstance().getJobId() + ""});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Question question = new Question();
            int choice_id = c.getInt(c.getColumnIndex("choice_id"));
            question.setId(choice_id);
            int type = c.getInt(c.getColumnIndex("type_id"));
            question.setType(type);
            question.setRightAnswer(c.getString(c.getColumnIndex("answer")));
            question.setTitle(c.getString(c.getColumnIndex("choice_content")));
            //这里获取选项
            ArrayList<String> items = new ArrayList<>();
            String item_sql = "select item_content from choice_items where choice_id = ?";
            Cursor cursor = db.rawQuery(item_sql, new String[]{choice_id + ""});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                items.add(cursor.getString(cursor.getColumnIndex("item_content")));
                cursor.moveToNext();
            }
            cursor.close();
            question.setAnswerA(items.get(0));
            question.setAnswerB(items.get(1));
            question.setAnswerC(items.get(2));
            question.setAnswerD(items.get(3));
            questions.add(question);
            c.moveToNext();
        }
        c.close();
        db.close();
        return questions;
    }

    /**
     * 获取随机的20到判断题
     *
     * @param context
     * @return
     */
    public static ArrayList<Question> getTofQuestions(Context context) {
        ArrayList<Question> questions = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select decision_id,type_id,content,answer from decision_question where job_id = ? ORDER BY random() LIMIT 20";
        Cursor c = db.rawQuery(sql, new String[]{MyApplication.getInstance().getJobId() + ""});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Question question = new Question();
            int decision_id = c.getInt(c.getColumnIndex("decision_id"));
            question.setId(decision_id);
            int type = c.getInt(c.getColumnIndex("type_id"));
            question.setType(type);
            question.setTitle(c.getString(c.getColumnIndex("content")));
            question.setIsRight(c.getInt(c.getColumnIndex("answer")));
            questions.add(question);
            c.moveToNext();
        }
        c.close();
        db.close();
        return questions;
    }

    /**
     * 保存一条测试记录
     *
     * @param context
     * @param user_answer
     * @param right_amount
     * @param wrong_amount
     * @param endTime
     */
    public static void saveExam(Context context, String user_answer, int right_amount, int wrong_amount, String endTime, String desc) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "insert into user_exam (paper_id,user_id,job_id,end_time,user_answer,right_amount,wrong_amount,description) values(?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new String[]{"0", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + "", endTime, user_answer, right_amount + "", wrong_amount + "", desc});
        db.close();
    }

    public static ArrayList<TestResult> getTestResult(Context context) {
        ArrayList<TestResult> testResults = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select end_time,user_answer,right_amount,description from user_exam where user_id = ? and job_id = ? order by id desc";
        Cursor c = db.rawQuery(sql, new String[]{MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            TestResult testResult = new TestResult();
            testResult.setEnd_time(c.getString(c.getColumnIndex("end_time")));
            testResult.setRight_amount(c.getInt(c.getColumnIndex("right_amount")));
            testResult.setDesc(c.getString(c.getColumnIndex("description")));
            String user_answer = c.getString(c.getColumnIndex("user_answer"));
            testResult.setUser_answer(user_answer);
            testResults.add(testResult);
            c.moveToNext();
        }
        c.close();
        db.close();
        return testResults;
    }

    public static ArrayList<Question> getHistoryTestQuestions(Context context, String user_answer) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        ArrayList<Question> questions = new ArrayList<>();
        Gson gson = new Gson();
        Type typeObj = new TypeToken<TestQS[]>() {
        }.getType();
        TestQS[] qses = gson.fromJson(user_answer, typeObj);
        int length = qses.length;
        for (int i = 0; i < length; i++) {
            Question question = new Question();
            TestQS qs = qses[i];
            int id = qs.getId();
            int type = qs.getType();

            question.setId(id);
            question.setType(type);
            question.setMyAnswer(qs.getAnswer());

            if (type == 1 || type == 2) {//如果是单选或者多选
                String get_content_sql = "select choice_content,answer from choice_questions where choice_id = ? and type_id = ?";
                Cursor get_content_cursor = db.rawQuery(get_content_sql, new String[]{id + "", type + ""});
                get_content_cursor.moveToFirst();
                while (!get_content_cursor.isAfterLast()) {
                    question.setRightAnswer(get_content_cursor.getString(get_content_cursor.getColumnIndex("answer")));
                    question.setTitle(get_content_cursor.getString(get_content_cursor.getColumnIndex("choice_content")));
                    break;
                }
                get_content_cursor.close();
                //单选或多选还要获取选项
                ArrayList<String> items = new ArrayList<>();
                String item_sql = "select item_content from choice_items where choice_id = ?";
                Cursor cursor = db.rawQuery(item_sql, new String[]{id + ""});
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    items.add(cursor.getString(cursor.getColumnIndex("item_content")));
                    cursor.moveToNext();
                }
                cursor.close();
                question.setAnswerA(items.get(0));
                question.setAnswerB(items.get(1));
                question.setAnswerC(items.get(2));
                question.setAnswerD(items.get(3));
                if (question.getType() == 1) {
                    questions.add(question);
                } else {
                    questions.add(question);
                }
            } else {//如果是判断，取出题目和答案就行
                String get_content_sql = "select content,answer from decision_question where decision_id = ? and type_id = ?";
                Cursor get_content_cursor = db.rawQuery(get_content_sql, new String[]{id + "", type + ""});
                get_content_cursor.moveToFirst();
                while (!get_content_cursor.isAfterLast()) {
                    question.setRightAnswer(get_content_cursor.getString(get_content_cursor.getColumnIndex("answer")));
                    question.setTitle(get_content_cursor.getString(get_content_cursor.getColumnIndex("content")));
                    break;
                }
                get_content_cursor.close();
                questions.add(question);
            }
        }
        db.close();
        return questions;
    }


    /**
     * 取闯关的题目,没有规则，暂时只从选择题里面拿数据
     *
     * @param context
     * @return
     */
    public static ArrayList<Question>[] getGameQuestions(Context context, Game game) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        ArrayList<Question>[] result = new ArrayList[3];
        ArrayList<Question> singleQuestions = new ArrayList<>();
        ArrayList<Question> mulQuestions = new ArrayList<>();
        ArrayList<Question> tofQuestions = new ArrayList<>();

        String sql = "select a.choice_id,a.type_id,a.choice_content,a.answer,GROUP_CONCAT(b.item_content) from choice_questions a,choice_items b where a.job_id = ? and a.type_id = ? and a.choice_id = b.[choice_id]  group by a.choice_id ORDER BY random() LIMIT ?";
        Cursor c = db.rawQuery(sql, new String[]{MyApplication.getInstance().getJobId() + "", "1", game.getSingle_choice_count() + ""});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Question question = new Question();
            int choice_id = c.getInt(c.getColumnIndex("choice_id"));
            question.setId(choice_id);
            int type = c.getInt(c.getColumnIndex("type_id"));
            question.setType(type);
            question.setRightAnswer(c.getString(c.getColumnIndex("answer")));
            question.setTitle(c.getString(c.getColumnIndex("choice_content")));
            //这里获取选项
            String itemss = c.getString(4);
            String sss[] = itemss.split(",");
            try {
                question.setAnswerA(sss[0]);
                question.setAnswerB(sss[1]);
                question.setAnswerC(sss[2]);
                question.setAnswerD(sss[3]);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ck", question.getId() + "");
            }
            singleQuestions.add(question);
            c.moveToNext();
        }
        c.close();

        //获取多选
        String sql2 = "select a.choice_id,a.type_id,a.choice_content,a.answer,GROUP_CONCAT(b.item_content) from choice_questions a,choice_items b where a.job_id = ? and a.type_id = ? and a.choice_id = b.[choice_id]  group by a.choice_id ORDER BY random() LIMIT ?";
        Cursor c2 = db.rawQuery(sql2, new String[]{MyApplication.getInstance().getJobId() + "", "2", game.getMulti_choice_count() + ""});
        c2.moveToFirst();
        while (!c2.isAfterLast()) {
            Question question = new Question();
            int choice_id = c2.getInt(c2.getColumnIndex("choice_id"));
            question.setId(choice_id);
            int type = c2.getInt(c2.getColumnIndex("type_id"));
            question.setType(type);
            question.setRightAnswer(c2.getString(c2.getColumnIndex("answer")));
            question.setTitle(c2.getString(c2.getColumnIndex("choice_content")));
            //这里获取选项
            String itemss = c2.getString(4);
            String sss[] = itemss.split(",");
            try {
                question.setAnswerA(sss[0]);
                question.setAnswerB(sss[1]);
                question.setAnswerC(sss[2]);
                question.setAnswerD(sss[3]);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ck", question.getId() + "");
            }
            mulQuestions.add(question);
            c2.moveToNext();
        }
        c2.close();
        //获取判断题
        String sql3 = "select decision_id,type_id,content,answer from decision_question where job_id = ? ORDER BY random() LIMIT ?";
        Cursor c3 = db.rawQuery(sql3, new String[]{MyApplication.getInstance().getJobId() + "", game.getDecision_count() + ""});
        c3.moveToFirst();
        while (!c3.isAfterLast()) {
            Question question = new Question();
            int decision_id = c3.getInt(c3.getColumnIndex("decision_id"));
            question.setId(decision_id);
            int type = c3.getInt(c3.getColumnIndex("type_id"));
            question.setType(type);
            question.setTitle(c3.getString(c3.getColumnIndex("content")));
            question.setIsRight(c3.getInt(c3.getColumnIndex("answer")));
            tofQuestions.add(question);
            c3.moveToNext();
        }
        c3.close();
        db.close();
        result[0] = singleQuestions;
        result[1] = mulQuestions;
        result[2] = tofQuestions;
        return result;
    }


    /**
     * 保存闯关记录，begin_time 放用的时间，end_time 实际结束时间，right_count
     *
     * @param context
     * @param name
     * @param use_time
     * @param is_success
     * @param end_time
     */
    public static void saveGame(Context context, String use_time, String end_time, int is_success, String name) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "insert into user_breakthrough_paper (paper_id,user_id,job_id,begin_time,end_time,right_count,description) values(?,?,?,?,?,?,?)";
        db.execSQL(sql, new String[]{"0", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + "", use_time, end_time, is_success + "", name});
        db.close();
    }

    public static ArrayList<GameHistory> getGameHistory(Context context) {
        ArrayList<GameHistory> gameHistories = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select begin_time,end_time,right_count,description from user_breakthrough_paper where user_id = ? and job_id = ? order by id desc";
        Cursor c = db.rawQuery(sql, new String[]{MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            GameHistory gameHistory = new GameHistory();
            gameHistory.setName(c.getString(c.getColumnIndex("description")));
            gameHistory.setIsSuccess(c.getInt(c.getColumnIndex("right_count")));
            gameHistory.setTimeEnd(c.getString(c.getColumnIndex("end_time")));
            gameHistory.setTimeUse(c.getString(c.getColumnIndex("begin_time")));
            gameHistories.add(gameHistory);
            c.moveToNext();
        }
        c.close();
        db.close();
        return gameHistories;
    }

    /**
     * 判断用户选择的职名是否存在，如果不存在，添加到数据库表
     *
     * @param context
     * @param job_id
     */
    public static void saveIfNotExistJobId(Context context, int job_id) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select * from job_question_version where job_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{job_id + ""});
        int count = 0;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            count++;
            break;
        }
        c.close();
        if (count == 0) {
            String addSql = "insert into job_question_version (job_id,is_valid,is_pbulish,version) values (?,?,?,?)";
            db.execSQL(addSql, new String[]{job_id + "", "1", "0", "1"});
        }
        db.close();
    }

    public static int getDBVersioion(Context context) {
        int result = 0;
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select version from job_question_version where job_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{MyApplication.getInstance().getJobId() + ""});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            result = c.getInt(c.getColumnIndex("version"));
            break;
        }
        c.close();
        db.close();
        return result;
    }

    public static void saveDBVersion(Context context, int version) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "update job_question_version set version = ? where job_id = ?";
        db.execSQL(sql, new String[]{version + "", MyApplication.getInstance().getJobId() + ""});
        db.close();
    }

    /**
     * 添加新的选择题
     *
     * @param updateChoice
     */
    public static void insertChoiceQuestion(UpdateChoice updateChoice, int jobId, SQLiteDatabase db) {
        try {
            String addSql = "insert into choice_questions (choice_id,type_id,job_id,choice_num,choice_content,is_img,answer,is_valid) values (?,?,?,?,?,?,?,?)";
            db.execSQL(addSql, new String[]{updateChoice.getId() + "", updateChoice.getTypeId() + "", jobId + "", updateChoice.getChoiceNum() + "", updateChoice.getChoiceContent(), "0", updateChoice.getAnswer(), "1"});

            List<UpdateChoice.ListEntity> list = updateChoice.getList();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                UpdateChoice.ListEntity entity = list.get(i);
                String addItemSql = "insert into choice_items (id,choice_id,item_number,item_content,is_img,is_answer) values(?,?,?,?,?,?)";
                db.execSQL(addItemSql, new String[]{entity.getId() + "", updateChoice.getId() + "", entity.getItemNumber(), entity.getItemContent(), "0", entity.isAnswer() ? "1" : "0"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateChoiceQuestion(UpdateChoice updateChoice, SQLiteDatabase db) {
        String addSql = "update choice_questions set type_id = ?,job_id = ?,choice_num = ?,choice_content = ? ,answer = ? where choice_id = ?";
        db.execSQL(addSql, new String[]{updateChoice.getTypeId() + "", updateChoice.getJobId() + "", updateChoice.getChoiceNum() + "", updateChoice.getChoiceContent(), updateChoice.getAnswer(), updateChoice.getId() + ""});

        List<UpdateChoice.ListEntity> list = updateChoice.getList();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            UpdateChoice.ListEntity entity = list.get(i);
            String addItemSql = "update choice_items set choice_id = ?,item_number = ? ,item_content = ?,is_answer = ? where id = ?";
            db.execSQL(addItemSql, new String[]{updateChoice.getId() + "", entity.getItemNumber(), entity.getItemContent(), entity.isAnswer() ? "1" : "0", entity.getId() + ""});
        }
    }

    public static void deleteChoiceQuestion(DeleteDb deleteDb, SQLiteDatabase db) {
        String deleteSql = "delete from choice_questions where choice_id = ?";
        db.execSQL(deleteSql, new String[]{deleteDb.getId() + ""});
        //只删题干
//        List<UpdateChoice.ListEntity> list = updateChoice.getList();
//        int size = list.size();
//        for (int i = 0; i < size; i++) {
//            UpdateChoice.ListEntity entity = list.get(i);
//            String deleteItemSql = "delete from choice_items where id = ?";
//            db.execSQL(deleteItemSql, new String[]{entity.getId() + ""});
//        }
    }

    public static void insertDecisionQuestion(UpdateDecision updateDecision, SQLiteDatabase db) {
        try {
            String addSql = "insert into decision_question (decision_id,job_id,type_id,content,answer,is_valid) values (?,?,?,?,?,?)";
            db.execSQL(addSql, new String[]{updateDecision.getId() + "", updateDecision.getJobId() + "", updateDecision.getTypeId() + "", updateDecision.getContent(), updateDecision.isAnswer() ? "1" : "0", updateDecision.isValid() ? "1" : "0"});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateDecisionQuestion(UpdateDecision updateDecision, SQLiteDatabase db) {
        String updateSql = "update decision_question set job_id = ?,type_id=?,content=?,answer=?,is_valid=? where decision_id = ?";
        db.execSQL(updateSql, new String[]{updateDecision.getJobId() + "", updateDecision.getTypeId() + "", updateDecision.getContent(), updateDecision.isAnswer() ? "1" : "0", updateDecision.isValid() ? "1" : "0", updateDecision.getId() + ""});
    }

    public static void deleteDecisionQuestion(DeleteDb deleteDb, SQLiteDatabase db) {
        String deleteSql = "delete from decision_question where decision_id = ?";
        db.execSQL(deleteSql, new String[]{deleteDb.getId() + ""});
    }

    public static void insertGame(Game game, SQLiteDatabase db) {
        String addSql = "insert into breakthrough_info (break_id,job_id,rank_id,is_valid,break_name,time_limit,single_choice_count,multi_choice_count,decision_count,description) values (?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(addSql, new String[]{game.getBreak_id() + "", game.getJob_id() + "", game.getRank_id() + "", "1", game.getBreak_name(), game.getTime_limit() + "", game.getSingle_choice_count() + "", game.getMulti_choice_count() + "", game.getDecision_count() + "", game.getDescription()});
    }

    public static void clearGame(Context context) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "delete from breakthrough_info";
        db.execSQL(sql);
        db.close();
    }

    public static ArrayList<Game> getGames(Context context) {
        ArrayList<Game> games = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select * from breakthrough_info";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Game game = new Game();
            game.setBreak_id(c.getInt(c.getColumnIndex("break_id")));
            game.setJob_id(c.getInt(c.getColumnIndex("job_id")));
            game.setRank_id(c.getInt(c.getColumnIndex("rank_id")));
            game.setBreak_name(c.getString(c.getColumnIndex("break_name")));
            game.setTime_limit(c.getInt(c.getColumnIndex("time_limit")));
            game.setSingle_choice_count(c.getInt(c.getColumnIndex("single_choice_count")));
            game.setMulti_choice_count(c.getInt(c.getColumnIndex("multi_choice_count")));
            game.setDecision_count(c.getInt(c.getColumnIndex("decision_count")));
            game.setDescription(c.getString(c.getColumnIndex("description")));
            games.add(game);
            c.moveToNext();
        }
        c.close();
        db.close();
        return games;
    }

    public static void saveOrUpdateGameRecord(Context context, Game game, int star, int rightNum) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select * from breakthrough_extra where breakthrough_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{game.getBreak_id() + ""});
        int count = 0;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            count++;
            break;
        }
        c.close();
        if (count == 0) {
            String addSql = "insert into breakthrough_extra (id,breakthrough_id,type_id,right_count,is_valid) values (?,?,?,?,?)";
            db.execSQL(addSql, new String[]{game.getBreak_id() + "", game.getBreak_id() + "", star + "", rightNum + "", "1"});
        } else {
            String upadteSql = "update breakthrough_extra set type_id = ?,right_count = ? where breakthrough_id = ?";
            db.execSQL(upadteSql, new String[]{star + "", rightNum + "", game.getBreak_id() + ""});
        }
        db.close();
    }

    public static String getGameRecord(Context context) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select breakthrough_id,type_id,right_count from breakthrough_extra";
        JSONArray array = new JSONArray();
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            JSONObject object = new JSONObject();
            try {
                object.put("break_id", c.getInt(c.getColumnIndex("breakthrough_id")));
                object.put("right", c.getInt(c.getColumnIndex("right_count")));
                object.put("type_id", c.getInt(c.getColumnIndex("type_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(object);
            c.moveToNext();
        }
        c.close();
        db.close();
        if (array.length() > 0) {
            JSONObject result = null;
            try {
                result = new JSONObject();
                result.put("list", array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result.toString();
        } else {
            return "";
        }
    }

    public static void clearGameRecord(Context context) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "delete from breakthrough_extra";
        db.execSQL(sql);
        db.close();
    }

    //2016.4.13  添加修改
    public static void saveOrUpdateJobName(Context context, SetTitleEntity entity) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select * from job_name where title_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{entity.getId() + ""});
        int count = 0;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            count++;
            break;
        }
        c.close();
        if (count == 0) {
            String addSql = "insert into job_name (title_id,title_name,type_id,is_valid) values (?,?,?,?)";
            db.execSQL(addSql, new String[]{entity.getId() + "", entity.getName() + "", "4", "1"});//4暂时写死，是上一级
        }
        db.close();
    }

    public static void saveOrUpdateJobQuestion(Context context, SetTitleEntity entity) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select * from job_question_version where job_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{entity.getId() + ""});
        int count = 0;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            count++;
            break;
        }
        c.close();
        if (count == 0) {
            String addSql = "insert into job_question_version (job_id,is_valid,is_pbulish,version) values (?,?,?,?)";
            db.execSQL(addSql, new String[]{ entity.getId() + "", "1", "0", "1"});
        }
        db.close();
    }

//   修改完毕

    /**
     * 获取二级职名信息
     *
     * @param context
     * @return
     */
    public static String[] getTitleName(Context context) {
        String[] result = new String[2];
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select * from job_name where title_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{MyApplication.getInstance().getJobId() + ""});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            result[0] = c.getString(c.getColumnIndex("title_name"));
            result[1] = c.getString(c.getColumnIndex("type_id"));
            break;
        }
        c.close();
        db.close();
        return result;
    }

    /**
     * 通过一级职名id获取名称
     *
     * @param context
     * @param type_id
     * @return
     */
    public static String getTypeName(Context context, int type_id) {
        String result = "";
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select type_name from sys_type where id = ?";
        Cursor c = db.rawQuery(sql, new String[]{type_id + ""});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            result = c.getString(c.getColumnIndex("type_name"));
            break;
        }
        c.close();
        db.close();
        return result;
    }


    @Deprecated
    public static ArrayList<SetTitleEntity> getLocalModule(Context context) {
        ArrayList<SetTitleEntity> setTitleEntities = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select id,type_name from sys_type";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            SetTitleEntity entity = new SetTitleEntity();
            entity.setId(c.getInt(c.getColumnIndex("id")));
            entity.setName(c.getString(c.getColumnIndex("type_name")));
            setTitleEntities.add(entity);
            c.moveToNext();
        }
        c.close();
        db.close();
        return setTitleEntities;
    }

    @Deprecated
    public static ArrayList<SetTitleEntity> getLocalTitle(Context context, int type_id) {
        ArrayList<SetTitleEntity> setTitleEntities = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select title_id,title_name from job_name where type_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{type_id + ""});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            SetTitleEntity entity = new SetTitleEntity();
            entity.setId(c.getInt(c.getColumnIndex("title_id")));
            entity.setName(c.getString(c.getColumnIndex("title_name")));
            setTitleEntities.add(entity);
            c.moveToNext();
        }
        c.close();
        db.close();
        return setTitleEntities;
    }


}
