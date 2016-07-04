package com.cnbs.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.cnbs.hanxirailway.R;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/1/8.
 */
public class MyApplication extends Application {

    private SharedPreferences sp;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sp = getSharedPreferences("user", MODE_PRIVATE);

        CrashReport.initCrashReport(getApplicationContext(), "900018204",
                HttpUtil.debug);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public String getUserName() {
        return sp.getString("username", "");
    }

    public void setUserName(String username) {
        sp.edit().putString("username", username).commit();
    }

    public int getUserId() {
        return sp.getInt("userid", 0);
    }

    public void setUserId(int userid) {
        sp.edit().putInt("userid", userid).commit();
    }

    public int getTypeId() {
        return sp.getInt("typeId", 0);
    }

    public void setTypeId(int typeId) {
        sp.edit().putInt("typeId", typeId).commit();
    }

    public int getJobId() {
        return sp.getInt("jobid", 0);
    }

    public void setJobId(int jobid) {
        sp.edit().putInt("jobid", jobid).commit();
    }

    public Boolean getHasPwd() {
        return sp.getBoolean("haspwd", false);
    }

    public void setHasPwd(boolean haspwd) {
        sp.edit().putBoolean("haspwd", haspwd).commit();
    }

    public void logout() {
        Boolean isUpload = getIsUpload();
        int pos = getGamePosition();
        sp.edit().clear().commit();
        setGamePosition(pos);
        setIsUpload(isUpload);
    }


    public String getTypeName() {
        return sp.getString("typeName", "");
    }

    public void setTypeName(String typeName) {
        sp.edit().putString("typeName", typeName).commit();
    }

    public String getTitleName() {
        return sp.getString("titleName", "");
    }

    public void setTitleName(String titleName) {
        sp.edit().putString("titleName", titleName).commit();
    }

    public Boolean getIsUpload() {
        return sp.getBoolean("isUpload", true);
    }

    public void setIsUpload(Boolean isUpload) {
        sp.edit().putBoolean("isUpload", isUpload).commit();
    }

    private static SQLiteDatabase database;
    public static final String DATABASE_FILENAME = "hanxi_exam.db"; //DB文件名字
    public static final String PACKAGE_NAME = "com.cnbs.hanxirailway"; //项目包路径
    public static final String DATABASE_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;


    public SQLiteDatabase openDatabase(Context context) {
        try {
            String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (!(new File(databaseFilename)).exists()) {
                InputStream is = context.getResources().openRawResource(
                        R.raw.hanxi_exam);
                FileOutputStream fos = new FileOutputStream(databaseFilename);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
                    null);
            return database;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getExercisePosition(int type) {
        switch (type) {
            case 0:
                return sp.getInt("exercise_all_position", -1);
            case 1:
                return sp.getInt("exercise_single_position", -1);
            case 2:
                return sp.getInt("exercise_mul_position", -1);
            case 3:
                return sp.getInt("exercise_tof_position", -1);
        }
        return 0;
    }

    public void setExercisePosition(int type, int position) {
        switch (type) {
            case 0:
                sp.edit().putInt("exercise_all_position", position).commit();
                break;
            case 1:
                sp.edit().putInt("exercise_single_position", position).commit();
                break;
            case 2:
                sp.edit().putInt("exercise_mul_position", position).commit();
                break;
            case 3:
                sp.edit().putInt("exercise_tof_position", position).commit();
                break;
        }
    }

    public void setGamePosition(int position) {
        sp.edit().putInt("game", position).commit();
    }

    public int getGamePosition() {
        return sp.getInt("game", 0);
    }

}
