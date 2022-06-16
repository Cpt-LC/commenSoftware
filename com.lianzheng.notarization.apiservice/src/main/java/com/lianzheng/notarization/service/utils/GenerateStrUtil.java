package com.lianzheng.notarization.service.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class GenerateStrUtil {
    /**
     * 输入前缀
     */
    public static String getProcessNo() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDate localDate = localDateTime.toLocalDate();
        String date = "" + localDate.getYear() +
                (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() :
                        localDate.getMonthValue()) +
                (localDate.getDayOfMonth() < 10 ? "0" + localDate.getDayOfMonth() :
                        localDate.getDayOfMonth());

        String sources = "0123456789"; // 加上一些字母，就可以生成pc站的验证码了
        Random rand = new Random();
        StringBuilder randNum = new StringBuilder();
        for (int j = 0; j < 6; j++) {
            randNum.append(sources.charAt(rand.nextInt(9)));
        }
        return "32050101" + date + randNum.toString();
    }

    /**
     * 输入前缀
     */
    public static String getOrderNo() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDate localDate = localDateTime.toLocalDate();
        String date = "" + localDate.getYear() +
                (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() :
                        localDate.getMonthValue()) +
                (localDate.getDayOfMonth() < 10 ? "0" + localDate.getDayOfMonth() :
                        localDate.getDayOfMonth());

        String sjc = System.currentTimeMillis() + "";
        sjc = sjc.substring(sjc.length() - 6);

        String sources = "0123456789"; // 加上一些字母，就可以生成pc站的验证码了
        Random rand = new Random();
        StringBuilder randNum = new StringBuilder();
        for (int j = 0; j < 6; j++) {
            randNum.append(sources.charAt(rand.nextInt(9)));
        }
        return "WX" + date + sjc + randNum.toString();
    }
}
