/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTools {
    //时间转化，变成相对时间
    public static String replaceTime(String dateStr) {
        if(null==dateStr){
            return null;
        }
        try {
            String returnStr = "";
            char[] buf = new char[10];
            char[] buf2 = new char[8];
            dateStr.getChars(0, 10, buf, 0);
            dateStr.getChars(13, 21, buf2, 0);
            String tempStr = new String(buf);
            String tempStr2 = new String(buf2);
            dateStr = tempStr + " " + tempStr2;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                Date date = sdf.parse(dateStr);
                Date nowDate = new Date();
                Long time = nowDate.getTime() - date.getTime();
                if (time / (1000 * 60 * 60 * 24) >= 2) {
                    //昨天以前
                    returnStr = new String(buf);
                } else if (time / (1000 * 60 * 60 * 24) >= 1 && (time / (1000 * 60 * 60 * 24) < 2)) {
                    returnStr = "昨天";
                } else if (time / (1000 * 60 * 60 * 24) == 0) {
                    //一天以内
                    if (time / (1000 * 60 * 60) > 0) {
                        //一小时内上
                        returnStr = time / (1000 * 60 * 60) + "小时前";
                    } else {
                        //一小时内
                        if (time / (1000 * 60) > 0) {
                            //一分钟以上
                            returnStr = time / (1000 * 60) + "分钟前";
                        } else {
                            returnStr = "刚刚";
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return returnStr;
        } catch (Exception e) {
            Log.i("yollpoll", dateStr);
        }
        return "未知异次元时间";
    }

    /**
     * 秒数变成具体时间
     *
     * @param timeStr
     * @return
     */
    public static String changeTime(String timeStr) {
        int time = 0;
        try {
            time = Integer.parseInt(timeStr);
        } catch (Exception e) {
            return "";
        }
        try {
            int day = time / (60 * 60 * 24);
            int hour = (time % (day * 60 * 60 * 24)) / (60 * 60);
            int min = (time - day * (60 * 60 * 24) - hour * (60 * 60)) / 60;
            int second = time - day * (60 * 60 * 24) - hour * (60 * 60) - min * (60);
            return day + "日" + hour + "时" + min + "分" + second + "秒";
        } catch (ArithmeticException e) {
            return "未知时间";
        }
    }


    /**
     * 根据秒数返回日期
     *
     * @param time
     * @return
     */
    public static String getDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        return simpleDateFormat.format(new Date(time));
    }

}
