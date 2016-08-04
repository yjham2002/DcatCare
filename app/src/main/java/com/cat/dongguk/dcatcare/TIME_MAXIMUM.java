package com.cat.dongguk.dcatcare;

import java.util.Calendar;
import java.util.Date;

public class TIME_MAXIMUM {

    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;

    public static String getDate(){
        Calendar cal = Calendar.getInstance();
        String dateSet = Integer.toString(cal.get(Calendar.YEAR))+"-"+Integer.toString(cal.get(Calendar.MONTH)+1)+"-"+
                Integer.toString(cal.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(cal.get(Calendar.HOUR_OF_DAY))+"-"+Integer.toString(cal.get(Calendar.MINUTE))+"-"+Integer.toString(cal.get(Calendar.SECOND));
        return dateSet;
    }

    public static String calculateTime(Date date) {

        long curTime = System.currentTimeMillis();
        long regTime = date.getTime();
        long diffTime = (curTime - regTime) / 1000;

        String msg;

        if (diffTime < TIME_MAXIMUM.SEC) {
            // sec
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            // min
            System.out.println(diffTime);

            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            // hour
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            // day
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            // day
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}