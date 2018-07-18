package com.cimcitech.cimcly.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * 日期时间等转换工具类.
 *
 * @author yangbo
 */
public class DateTool {

    public static String getSystemDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    public static String transferDateStr(String string){
        //下面将“2018-8-8”格式时间，转化为标准日期“2018-08-06”
        SimpleDateFormat foramt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try{
            date = foramt.parse(string);
        }catch (Exception e){

        }
        string = foramt.format(date);
        return string;
    }

    /**
     * long类型的日期时间值转换成yyyy/MM/dd/ HH:mm:ss格式
     * 精确到时分秒
     *
     * @param millis
     * @return
     */
    public static String getDateStrNoTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        Formatter ft = new Formatter(Locale.CHINA);
        return ft.format("%1$tY-%1$tm-%1$td %1$tT", cal).toString();
    }

    /**
     * long类型的日期时间值转换成yyyy/MM/dd 格式
     *
     * @param millis
     * @return
     */
    public static String getDateStr(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        Formatter ft = new Formatter(Locale.CHINA);
        return ft.format("%1$tY-%1$tm-%1$td", cal).toString();
    }

    public static String betweenDate(String date) {
        String between = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date rat_date = sdf.parse(date);
            long between_time = (System.currentTimeMillis() - rat_date.getTime()) / 1000;
            long day = between_time / (24 * 3600);
            long hour = between_time % (24 * 3600) / 3600;
            long minute = between_time % 3600 / 60;
            between = day + "天" + hour + "时" + minute + "分";
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return between;
    }
}
