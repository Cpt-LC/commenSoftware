package com.lianzheng.h5.util;

public class TextUtil {
    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static boolean isNoEmpty(String text) {
        return !isEmpty(text);
    }

}
