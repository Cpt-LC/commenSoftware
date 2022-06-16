package com.lianzheng.h5.util;

public class RandomCharsUtil {
    static String[] charsArray = new String[]{
            "a", "b", "c", "d", "e", "f", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "V", "W", "X", "Y", "Z",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
    };

    public static String create(int length) {
        StringBuilder meChars = new StringBuilder();
        for (int i = 0; i < length; i++) {
            double _randomNum = Math.random();
            String _itemVal;
            try {
                _itemVal = charsArray[(int) (_randomNum * charsArray.length - 1)];
            } catch (Exception e) {
                _itemVal = "A";
            }
            meChars.append(_itemVal);
        }
        return meChars.toString();
    }
}
