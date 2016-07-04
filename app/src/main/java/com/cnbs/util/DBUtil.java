package com.cnbs.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.cnbs.entity.ChooseAnswer;
import com.cnbs.entity.Collect;
import com.cnbs.entity.Note;
import com.cnbs.entity.Question;
import com.cnbs.entity.WrongAnswer;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/8.
 */
public class DBUtil {

    /**
     * 取所有的选择题（单选和多选）
     * 是否需要取自己写过的答案和笔记
     *
     * @param context
     * @param needAnswer
     * @param needNote
     * @return
     */
    public static ArrayList<Question>[] getChooseQuestions(Context context, Boolean needAnswer, Boolean needNote) {
        ArrayList<Question>[] result = new ArrayList[2];
        ArrayList<Question> singleQuestions = new ArrayList<>();
        ArrayList<Question> mulQuestions = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
//        String sql = "select choice_id,type_id,choice_content,answer from choice_questions where job_id = ?";
        String sql = "select a.choice_id,a.type_id,a.choice_content,a.answer,GROUP_CONCAT(b.item_content) from choice_questions a,choice_items b where a.job_id = ? and a.choice_id = b.[choice_id]  group by a.choice_id";
        Cursor c = db.rawQuery(sql, new String[]{MyApplication.getInstance().getJobId() + ""});
//        String sql = "select choice_id,type_id,choice_content,answer from choice_questions ";
//        Cursor c = db.rawQuery(sql, null);

//        Cursor cur;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (!HttpUtil.loadData) {
                break;
            }
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
                Log.e("ck",question.getId()+"");
            }
            //取自己的答案
            if (needAnswer) {
                String myAnswer_sql = "select user_answer from user_exercise_ext where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
                Cursor cur = db.rawQuery(myAnswer_sql, new String[]{choice_id + "", type + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    String myAnswer = cur.getString(cur.getColumnIndex("user_answer"));
                    question.setMyAnswer(myAnswer);
                    break;
                }
                cur.close();
            }
            //取自己的笔记
            if (needNote) {
                String myAnswer_sql = "select note from user_note where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
                Cursor cur2 = db.rawQuery(myAnswer_sql, new String[]{choice_id + "", type + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
                cur2.moveToFirst();
                while (!cur2.isAfterLast()) {
                    String myNote = cur2.getString(cur2.getColumnIndex("note"));
                    question.setMyNote(myNote);
                    break;
                }
                cur2.close();
            }
            if (question.getType() == 1) {
                singleQuestions.add(question);
            } else {
                mulQuestions.add(question);
            }
            c.moveToNext();
        }
        c.close();
        result[0] = singleQuestions;
        result[1] = mulQuestions;
        return result;
    }

    /**
     * 取所有的判断题
     *
     * @param context
     * @param needAnswer
     * @param needNote
     * @return
     */
    public static ArrayList<Question> getTofQuestions(Context context, Boolean needAnswer, Boolean needNote) {
        ArrayList<Question> questions = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select decision_id,type_id,content,answer from decision_question where job_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{MyApplication.getInstance().getJobId() + ""});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (!HttpUtil.loadData) {
                break;
            }
            Question question = new Question();
            int decision_id = c.getInt(c.getColumnIndex("decision_id"));
            question.setId(decision_id);
            int type = c.getInt(c.getColumnIndex("type_id"));
            question.setType(type);
            question.setTitle(c.getString(c.getColumnIndex("content")));
            question.setIsRight(c.getInt(c.getColumnIndex("answer")));
            //取自己的答案
            if (needAnswer) {
                String myAnswer_sql = "select user_answer from user_exercise_ext where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
                Cursor cur = db.rawQuery(myAnswer_sql, new String[]{decision_id + "", type + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    String myAnswer = cur.getString(cur.getColumnIndex("user_answer"));
                    question.setMyAnswer(myAnswer);
                    break;
                }
                cur.close();
            }
            //取自己的笔记
            if (needNote) {
                String myAnswer_sql = "select note from user_note where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
                Cursor cur2 = db.rawQuery(myAnswer_sql, new String[]{decision_id + "", type + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
                cur2.moveToFirst();
                while (!cur2.isAfterLast()) {
                    String myNote = cur2.getString(cur2.getColumnIndex("note"));
                    question.setMyNote(myNote);
                    break;
                }
                cur2.close();
            }
            questions.add(question);
            c.moveToNext();
        }
        c.close();
        return questions;
    }

    /**
     * 保存或更新用户选择（判断）的答案
     *
     * @param context
     * @param ca
     */
    public static void saveOrUpdateChooseAnswer(Context context, ChooseAnswer ca) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select * from user_exercise_ext where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{ca.getItem_id() + "", ca.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        int count = 0;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            count++;
            break;
        }
        if (count == 0) {
            String addSql = "insert into user_exercise_ext  (exercise_id,user_id,job_id,item_id,type_id,user_answer,is_right) values (?,?,?,?,?,?,?)";
            db.execSQL(addSql, new String[]{ca.getExercise_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + "", ca.getItem_id() + "", ca.getType_id() + "", ca.getUser_answer(), ca.getIs_right() + ""});
        } else {
            String updateSql = "update user_exercise_ext set user_answer = ? , is_right = ? where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
            db.execSQL(updateSql, new String[]{ca.getUser_answer(), ca.getIs_right() + "", ca.getItem_id() + "", ca.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        }
    }

    /**
     * 2016/6/17
     * @param context
     */
    public static void deleteChooseAnswer(Context context){
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "delete from user_exercise_ext where user_id = ? and job_id = ?";
        db.execSQL(sql, new String[]{MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
    }

    /**
     * 保存或更新笔记
     *
     * @param context
     * @param note
     */
    public static void saveOrUpdateNote(Context context, Note note) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select * from user_note where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{note.getItem_id() + "", note.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        int count = 0;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            count++;
            break;
        }
        if (count == 0) {
            String addSql = "insert into user_note (item_id,type_id,user_id,job_id,note,is_valid) values (?,?,?,?,?,?)";
            db.execSQL(addSql, new String[]{note.getItem_id() + "", note.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + "", note.getNote(), note.getIs_valid() + ""});
        } else {
            String updateSql = "update user_note set note = ? where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
            db.execSQL(updateSql, new String[]{note.getNote(), note.getItem_id() + "", note.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        }
    }

    /**
     * 保存或更新错误的题目
     *
     * @param context
     * @param wrongAnswer
     */
    public static void saveOrUpdateWrongItem(Context context, WrongAnswer wrongAnswer) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select * from user_wrong_item where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{wrongAnswer.getItem_id() + "", wrongAnswer.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        int sum = 0;
        int count = 0;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            sum++;
            count = c.getInt(c.getColumnIndex("count"));
            break;
        }
        if (sum == 0) {
            String addSql = "insert into user_wrong_item (item_id,type_id,user_id,job_id,add_time,count,is_valid) values (?,?,?,?,?,?,?)";
            db.execSQL(addSql, new String[]{wrongAnswer.getItem_id() + "", wrongAnswer.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + "", wrongAnswer.getAddTime(), wrongAnswer.getCount() + "", wrongAnswer.getIs_valid() + ""});
        } else {
            //取出count值++
            count++;
            String updateSql = "update user_wrong_item set count = ? where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
            db.execSQL(updateSql, new String[]{count + "", wrongAnswer.getItem_id() + "", wrongAnswer.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        }
    }

    /**
     * 获取错误的题目
     *
     * @param context
     * @return
     */
    public static ArrayList<Question>[] getWrongQuestions(Context context) {
        ArrayList<Question>[] result = new ArrayList[3];
        ArrayList<Question> singleQuestions = new ArrayList<>();
        ArrayList<Question> mulQuestions = new ArrayList<>();
        ArrayList<Question> tofQuestions = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        //先获取到id才行
        String get_ids_sql = "select item_id,type_id from user_wrong_item where is_valid = 1 and user_id = ? and job_id = ?";
        Cursor get_ids_cursor = db.rawQuery(get_ids_sql, new String[]{MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        get_ids_cursor.moveToFirst();
        while (!get_ids_cursor.isAfterLast()) {
            Question question = new Question();
            int item_id = get_ids_cursor.getInt(get_ids_cursor.getColumnIndex("item_id"));
            question.setId(item_id);
            int type = get_ids_cursor.getInt(get_ids_cursor.getColumnIndex("type_id"));
            question.setType(type);
            if (type == 1 || type == 2) {//如果是单选或者多选
                String get_content_sql = "select choice_content,answer from choice_questions where choice_id = ? and type_id = ?";
                Cursor get_content_cursor = db.rawQuery(get_content_sql, new String[]{item_id + "", type + ""});
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
                Cursor cursor = db.rawQuery(item_sql, new String[]{item_id + ""});
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
                    singleQuestions.add(question);
                } else {
                    mulQuestions.add(question);
                }
            } else {//如果是单选，取出题目和答案就行
                String get_content_sql = "select content,answer from decision_question where decision_id = ? and type_id = ?";
                Cursor get_content_cursor = db.rawQuery(get_content_sql, new String[]{item_id + "", type + ""});
                get_content_cursor.moveToFirst();
                while (!get_content_cursor.isAfterLast()) {
                    question.setRightAnswer(get_content_cursor.getString(get_content_cursor.getColumnIndex("answer")));
                    question.setTitle(get_content_cursor.getString(get_content_cursor.getColumnIndex("content")));
                    break;
                }
                get_content_cursor.close();
                tofQuestions.add(question);
            }
            get_ids_cursor.moveToNext();
        }
        get_ids_cursor.close();
        result[0] = singleQuestions;
        result[1] = mulQuestions;
        result[2] = tofQuestions;
        return result;
    }


    /**
     * 错误的题目做对了，更新is_valid的值
     *
     * @param context
     * @param wrongAnswer
     */
    public static void deleteWrongItem(Context context, WrongAnswer wrongAnswer) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "update user_wrong_item set is_valid = 0 where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
        db.execSQL(sql, new String[]{wrongAnswer.getItem_id() + "", wrongAnswer.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
    }

    /**
     * 保存收藏的题目
     *
     * @param context
     * @param collect
     */
    public static void saveOrUpdateCollect(Context context, Collect collect) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "select * from user_collection where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
        Cursor c = db.rawQuery(sql, new String[]{collect.getItem_id() + "", collect.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        int count = 0;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            count++;
            break;
        }
        if (count == 0) {
            String addSql = "insert into user_collection  (item_id,type_id,user_id,job_id,add_time,is_valid) values (?,?,?,?,?,?)";
            db.execSQL(addSql, new String[]{collect.getItem_id() + "", collect.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + "", collect.getAdd_time(), collect.getIs_valid() + ""});
        } else {//已经收藏过的，或者收藏过，但是取消收藏了的，重置is_valid
            String updateSql = "update user_collection set is_valid = 1 where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
            db.execSQL(updateSql, new String[]{collect.getItem_id() + "", collect.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        }
        Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
    }

    public static void deleteCollectItem(Context context, Collect collect) {
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        String sql = "update user_collection set is_valid = 0 where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
        db.execSQL(sql, new String[]{collect.getItem_id() + "", collect.getType_id() + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
    }


    //下面是收藏的
    public static ArrayList<Question>[] getCollectQuestions(Context context, int is_valid) {
        ArrayList<Question>[] result = new ArrayList[3];
        ArrayList<Question> singleQuestions = new ArrayList<>();
        ArrayList<Question> mulQuestions = new ArrayList<>();
        ArrayList<Question> tofQuestions = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        //先获取到id才行
        String get_ids_sql = "select item_id,type_id from user_collection where is_valid = ? and user_id = ? and job_id = ?";
        Cursor get_ids_cursor = db.rawQuery(get_ids_sql, new String[]{is_valid + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        get_ids_cursor.moveToFirst();
        while (!get_ids_cursor.isAfterLast()) {
            Question question = new Question();
            int item_id = get_ids_cursor.getInt(get_ids_cursor.getColumnIndex("item_id"));
            question.setId(item_id);
            int type = get_ids_cursor.getInt(get_ids_cursor.getColumnIndex("type_id"));
            question.setType(type);

            if(is_valid==1){//收藏的才需要取自己的答案
                //取自己的答案
                String myAnswer_sql = "select user_answer from user_exercise_ext where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
                Cursor cur = db.rawQuery(myAnswer_sql, new String[]{item_id + "", type + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    String myAnswer = cur.getString(cur.getColumnIndex("user_answer"));
                    question.setMyAnswer(myAnswer);
                    break;
                }
                cur.close();
            }


            //收藏的题目还是要取笔记的
            String myNote_sql = "select note from user_note where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
            Cursor cur2 = db.rawQuery(myNote_sql, new String[]{item_id + "", type + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
            cur2.moveToFirst();
            while (!cur2.isAfterLast()) {
                String myNote = cur2.getString(cur2.getColumnIndex("note"));
                question.setMyNote(myNote);
                break;
            }
            cur2.close();

            if (type == 1 || type == 2) {//如果是单选或者多选
                String get_content_sql = "select choice_content,answer from choice_questions where choice_id = ? and type_id = ?";
                Cursor get_content_cursor = db.rawQuery(get_content_sql, new String[]{item_id + "", type + ""});
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
                Cursor cursor = db.rawQuery(item_sql, new String[]{item_id + ""});
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
                    singleQuestions.add(question);
                } else {
                    mulQuestions.add(question);
                }
            } else {//如果是判断，取出题目和答案就行
                String get_content_sql = "select content,answer from decision_question where decision_id = ? and type_id = ?";
                Cursor get_content_cursor = db.rawQuery(get_content_sql, new String[]{item_id + "", type + ""});
                get_content_cursor.moveToFirst();
                while (!get_content_cursor.isAfterLast()) {
                    question.setRightAnswer(get_content_cursor.getString(get_content_cursor.getColumnIndex("answer")));
                    question.setTitle(get_content_cursor.getString(get_content_cursor.getColumnIndex("content")));
                    break;
                }
                get_content_cursor.close();
                tofQuestions.add(question);
            }
            get_ids_cursor.moveToNext();
        }
        get_ids_cursor.close();
        result[0] = singleQuestions;
        result[1] = mulQuestions;
        result[2] = tofQuestions;
        return result;
    }

    public static ArrayList<Question>[] getNotesQuestions(Context context) {
        ArrayList<Question>[] result = new ArrayList[3];
        ArrayList<Question> singleQuestions = new ArrayList<>();
        ArrayList<Question> mulQuestions = new ArrayList<>();
        ArrayList<Question> tofQuestions = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        //先获取到id才行
        String get_ids_sql = "select item_id,type_id from user_note where user_id = ? and job_id = ?";
        Cursor get_ids_cursor = db.rawQuery(get_ids_sql, new String[]{MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        get_ids_cursor.moveToFirst();
        while (!get_ids_cursor.isAfterLast()) {
            Question question = new Question();
            int item_id = get_ids_cursor.getInt(get_ids_cursor.getColumnIndex("item_id"));
            question.setId(item_id);
            int type = get_ids_cursor.getInt(get_ids_cursor.getColumnIndex("type_id"));
            question.setType(type);
            //取自己的答案
            String myAnswer_sql = "select user_answer from user_exercise_ext where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
            Cursor cur = db.rawQuery(myAnswer_sql, new String[]{item_id + "", type + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                String myAnswer = cur.getString(cur.getColumnIndex("user_answer"));
                question.setMyAnswer(myAnswer);
                break;
            }
            cur.close();

            //笔记
            String myNote_sql = "select note from user_note where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
            Cursor cur2 = db.rawQuery(myNote_sql, new String[]{item_id + "", type + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
            cur2.moveToFirst();
            while (!cur2.isAfterLast()) {
                String myNote = cur2.getString(cur2.getColumnIndex("note"));
                question.setMyNote(myNote);
                break;
            }
            cur2.close();

            if (type == 1 || type == 2) {//如果是单选或者多选
                String get_content_sql = "select choice_content,answer from choice_questions where choice_id = ? and type_id = ?";
                Cursor get_content_cursor = db.rawQuery(get_content_sql, new String[]{item_id + "", type + ""});
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
                Cursor cursor = db.rawQuery(item_sql, new String[]{item_id + ""});
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
                    singleQuestions.add(question);
                } else {
                    mulQuestions.add(question);
                }
            } else {//如果是单选，取出题目和答案就行
                String get_content_sql = "select content,answer from decision_question where decision_id = ? and type_id = ?";
                Cursor get_content_cursor = db.rawQuery(get_content_sql, new String[]{item_id + "", type + ""});
                get_content_cursor.moveToFirst();
                while (!get_content_cursor.isAfterLast()) {
                    question.setRightAnswer(get_content_cursor.getString(get_content_cursor.getColumnIndex("answer")));
                    question.setTitle(get_content_cursor.getString(get_content_cursor.getColumnIndex("content")));
                    break;
                }
                get_content_cursor.close();
                tofQuestions.add(question);
            }
            get_ids_cursor.moveToNext();
        }
        get_ids_cursor.close();
        result[0] = singleQuestions;
        result[1] = mulQuestions;
        result[2] = tofQuestions;
        return result;
    }

    public static ArrayList<Question>[] getStrongQuestions(Context context) {
        ArrayList<Question>[] result = new ArrayList[3];
        ArrayList<Question> singleQuestions = new ArrayList<>();
        ArrayList<Question> mulQuestions = new ArrayList<>();
        ArrayList<Question> tofQuestions = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getInstance().openDatabase(context);
        //先获取到id才行
        String get_ids_sql = "select item_id,type_id from user_wrong_item where (count >1 or is_valid = 0) and user_id = ? and job_id = ?";
        Cursor get_ids_cursor = db.rawQuery(get_ids_sql, new String[]{MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
        get_ids_cursor.moveToFirst();
        while (!get_ids_cursor.isAfterLast()) {
            Question question = new Question();
            int item_id = get_ids_cursor.getInt(get_ids_cursor.getColumnIndex("item_id"));
            question.setId(item_id);
            int type = get_ids_cursor.getInt(get_ids_cursor.getColumnIndex("type_id"));
            question.setType(type);
            //取自己的答案
//            String myAnswer_sql = "select user_answer from user_exercise_ext where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
//            Cursor cur = db.rawQuery(myAnswer_sql, new String[]{item_id + "", type + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
//            cur.moveToFirst();
//            while (!cur.isAfterLast()) {
//                String myAnswer = cur.getString(cur.getColumnIndex("user_answer"));
//                question.setMyAnswer(myAnswer);
//                break;
//            }
//            cur.close();

            //笔记
            String myNote_sql = "select note from user_note where item_id = ? and type_id = ? and user_id = ? and job_id = ?";
            Cursor cur2 = db.rawQuery(myNote_sql, new String[]{item_id + "", type + "", MyApplication.getInstance().getUserId() + "", MyApplication.getInstance().getJobId() + ""});
            cur2.moveToFirst();
            while (!cur2.isAfterLast()) {
                String myNote = cur2.getString(cur2.getColumnIndex("note"));
                question.setMyNote(myNote);
                break;
            }
            cur2.close();

            if (type == 1 || type == 2) {//如果是单选或者多选
                String get_content_sql = "select choice_content,answer from choice_questions where choice_id = ? and type_id = ?";
                Cursor get_content_cursor = db.rawQuery(get_content_sql, new String[]{item_id + "", type + ""});
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
                Cursor cursor = db.rawQuery(item_sql, new String[]{item_id + ""});
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
                    singleQuestions.add(question);
                } else {
                    mulQuestions.add(question);
                }
            } else {//如果是单选，取出题目和答案就行
                String get_content_sql = "select content,answer from decision_question where decision_id = ? and type_id = ?";
                Cursor get_content_cursor = db.rawQuery(get_content_sql, new String[]{item_id + "", type + ""});
                get_content_cursor.moveToFirst();
                while (!get_content_cursor.isAfterLast()) {
                    question.setRightAnswer(get_content_cursor.getString(get_content_cursor.getColumnIndex("answer")));
                    question.setTitle(get_content_cursor.getString(get_content_cursor.getColumnIndex("content")));
                    break;
                }
                get_content_cursor.close();
                tofQuestions.add(question);
            }
            get_ids_cursor.moveToNext();
        }
        get_ids_cursor.close();
        result[0] = singleQuestions;
        result[1] = mulQuestions;
        result[2] = tofQuestions;
        return result;
    }


}
