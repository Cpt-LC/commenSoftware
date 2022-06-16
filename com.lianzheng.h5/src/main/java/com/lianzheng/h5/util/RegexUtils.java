package com.lianzheng.h5.util;

import java.util.regex.Pattern;

public class RegexUtils {

    /**
     * 正则：手机号（简单）, 1字头＋10位数字即可.
     */
    public static boolean validateMobilePhone(String in) {
        if (in == null) return false;
        Pattern pattern = Pattern.compile("^[1]\\d{10}$");
        return pattern.matcher(in).matches();
    }
}
