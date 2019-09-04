package com.avenir.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static Date formatDate(String date, Integer type) throws ParseException {
        SimpleDateFormat df;
        if(type == 1) {
            df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        } else if(type == 2) {
            df = new SimpleDateFormat("yyyy/MM/dd");
        } else {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return df.parse(date);
    }
    public static String formatDate1(Date date, Integer type) throws ParseException {
        SimpleDateFormat df;
        if(type == 1) {
            df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        } else if(type == 2) {
            df = new SimpleDateFormat("yyyy/MM/dd");
        } else {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return df.format(date);
    }
}
