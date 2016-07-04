package com.cnbs.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.cnbs.hanxirailway.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/1/16.
 */
public class Util {

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^(1[3,4,5,7,8][0-9])\\d{8}$");
//        Pattern p = Pattern.compile("^(1[3,4,5,7,8][0,1,2,3,4,5,6,7,8,9])\\d{8}$");
        Matcher m;
        try {
            m = p.matcher(mobiles);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return m.matches();
    }

    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static Boolean hasResult(String result, Context context) {
        if (result == null || result.equals("")) {
            Toast.makeText(context,
                    context.getResources().getString(R.string.noconnect),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (result.startsWith("TimeOut")) {
            Toast.makeText(context,
                    context.getResources().getString(R.string.timeout),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static String hasResult(String result) {
        if (result.startsWith("TimeOut")) {
            return "0";
        }
        if (result == null || result.equals("")) {
            return "1";
        }
        return "2";
    }

    public static void hideKeyboard(Activity context) {
        InputMethodManager manager = null;
        manager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (context.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (context.getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(context.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String getDynamicPassword(String str) {
        // 6是验证码的位数一般为六位
        Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
                + 6 + "})(?![0-9])");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            System.out.print(m.group());
            dynamicPassword = m.group();
        }
        return dynamicPassword;
    }
}
