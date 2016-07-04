package com.cnbs.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtil {


//    public static String UploadURL = "http://192.168.100.166:8080/hanxiExam/upload/txt/";
//
//    public static String BaseURL = "http://192.168.100.222:8080/hanxiExamInterface/";

    public static String UploadURL = "http://www.zhongxinlan.com/hanxiExam/upload/txt/";

    public static String BaseURL = "http://www.zhongxinlan.com/hanxiExamInterface/";

    public static String Url = BaseURL + "front/";

    public static Boolean debug = false;

    public static Boolean loadData = false;

    public static String getResult(String urlString, Map<String, String> map) {

        HttpURLConnection conn;
        OutputStream os;
        InputStream is;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();
            /**写入参数**/
            os = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            String data = "";
            for (String key : map.keySet())
                data += key + "=" + map.get(key) + "&";
            dos.write(data.getBytes());
            //关闭外包装流
            dos.close();
            /**读服务器数据**/
            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}


