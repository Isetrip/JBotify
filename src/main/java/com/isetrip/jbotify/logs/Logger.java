package com.isetrip.jbotify.logs;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

class Logger {
    public static void log(final String message) {
        try {
            File dataFolder = new File("logs");
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            File saveTo = new File("logs", getDate() + ".log");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }
            //FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(saveTo, true), StandardCharsets.UTF_8));//new PrintWriter(fw);
            pw.println(message);
            pw.flush();
            pw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
        Date date = calendar.getTime();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = formatter.format(date);
        return time;
    }

}
