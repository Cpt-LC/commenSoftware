package com.lianzheng.h5.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class IdCardUtil {
    /**
     * 通过身份证号码获取出生日期(birthday)、年龄(age)、性别(sex)
     * @param idCardNo 身份证号码
     * @return 返回的出生日期格式：1993-05-07   性别格式：1:男，0:女
     */
    public static Map<String, String> getBirthdayAgeSex(String idCardNo) {
        String birthday = "";
        String age = "";
        String sexCode = "";

        int year = Calendar.getInstance().get(Calendar.YEAR);
        char[] number = idCardNo.toCharArray();
        boolean flag = true;
        if (number.length == 15) {
            for (int x = 0; x < number.length; x++) {
                if (!flag){
                    return new HashMap<String, String>();
                }
                flag = Character.isDigit(number[x]);
            }
        } else if (number.length == 18) {
            for (int x = 0; x < number.length - 1; x++) {
                if (!flag){
                    return new HashMap<String, String>();
                }
                flag = Character.isDigit(number[x]);
            }
        }
        if (flag && idCardNo.length() == 15) {
            birthday = "19" + idCardNo.substring(6, 8) + "-"
                    + idCardNo.substring(8, 10) + "-"
                    + idCardNo.substring(10, 12);
            sexCode = Integer.parseInt(idCardNo.substring(idCardNo.length() - 3, idCardNo.length())) % 2 == 0 ? "0" : "1";
            age = (year - Integer.parseInt("19" + idCardNo.substring(6, 8))) + "";
        } else if (flag && idCardNo.length() == 18) {
            birthday = idCardNo.substring(6, 10) + "-"
                    + idCardNo.substring(10, 12) + "-"
                    + idCardNo.substring(12, 14);
            sexCode = Integer.parseInt(idCardNo.substring(idCardNo.length() - 4, idCardNo.length() - 1)) % 2 == 0 ? "0" : "1";
            age = (year - Integer.parseInt(idCardNo.substring(6, 10))) + "";
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("birthday", birthday);
        map.put("age", age);
        map.put("sex", sexCode);
        return map;
    }
}
