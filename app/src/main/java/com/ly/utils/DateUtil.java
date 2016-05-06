package com.ly.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间处理工具类
 *
 * @author Administrator
 */
public final class DateUtil {

    private DateUtil() {
    }

    public static String format(long date) {
        return format(new Date(date));
    }

    public static String format(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parse(String date, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTimeToNow(String oldTime, long time) {
        String ret = null;
        long l = (time - Long.parseLong(oldTime)*1000) / 60000;
        ret = l + "分钟前";
        if (l > 60) {
            l = l / 60;
            ret = l + "小时前";
            if (l > 24) {
                l = l / 24;
                ret = l + "天前";
                if(l>30){
                    l= l/30;
                    ret = l+"月前";
                    if(l>12){
                        l=l/12;
                        ret = l+"年前";
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 毫秒转化为字符串格式的时间  yyyy-MM-dd hh:mm:ss
     * @param time
     * @return
     */
    public  static String miilToString(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String sb = format.format(time);
        return sb;
    }/**
     * 毫秒转化为字符串格式的时间  yyyy-MM-dd
     * @param time
     * @return
     */
    public  static String miilToStringSS(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sb = format.format(time);
        return sb;
    }

    /**
     * 比较发表时间和现在时间，若超过一天，则显示为日期格式的形式
     * @param oldTime
     * @param time
     * @return
     */
    public static String compareTimeWithNow(String oldTime, long time) {
        String ret = null;
        long l = (time - Long.parseLong(oldTime)) / 60000;
        ret = l + "分钟前";
        if (l > 60) {
            l = l / 60;
            ret = l + "小时前";
            if (l > 24) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = format.format(oldTime);
                return date;
            }
        }
        return ret;
    }

}
