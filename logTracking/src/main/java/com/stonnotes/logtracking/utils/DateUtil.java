package com.stonnotes.logtracking.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: javan
 * @Date: 2019/6/19
 */
public class DateUtil {

    public static Date stringToDate(String strDate) {
        return stringToDate(strDate, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    public static Date stringToDate(String strDate, String f) {
        SimpleDateFormat format = new SimpleDateFormat(f);
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateFormat(long time) {
        Date currentTime = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

}
