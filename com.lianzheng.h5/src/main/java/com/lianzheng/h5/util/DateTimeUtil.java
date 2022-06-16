package com.lianzheng.h5.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static int getCurrentHHmmss() {
        LocalDateTime _nowTime = LocalDateTime.now();
        String currentHmsStr = doubleTime(_nowTime.getHour()) + doubleTime(_nowTime.getMinute()) + doubleTime(_nowTime.getSecond());
        return Integer.parseInt(currentHmsStr);
    }

    static String doubleTime(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }

   public static LocalDateTime ymdhmsToLocalTime(String yymmddhhmmss) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(yymmddhhmmss, dateTimeFormatter);
    }
}
