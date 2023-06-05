package com.isetrip.jbotify.logs;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Log {

    public static void info(String message) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
        Date date = calendar.getTime();
        String log = "[" + date + " INFO] " + message;
        Logger.log(log);
        System.out.println(log);
    }

    public static void warn(String message) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
        Date date = calendar.getTime();
        String log = "[" + date + " WARN] " + message;
        Logger.log(log);
        System.out.println(log);
    }

    public static void error(String message) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
        Date date = calendar.getTime();
        String log = "[" + date + " ERROR] " + message;
        Logger.log(log);
        System.out.println(log);
    }

}
