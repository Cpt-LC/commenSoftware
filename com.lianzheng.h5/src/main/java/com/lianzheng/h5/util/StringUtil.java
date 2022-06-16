package com.lianzheng.h5.util;

import com.lianzheng.h5.jwt.util.JwtUtils;

public class StringUtil {
    //首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }


    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }


    public static String tokenImgUrl(String absPathUrl) {
        return tokenUrl(absPathUrl);
    }

    public static String tokenPdfUrl(String absPathUrl) {
        return tokenUrl(absPathUrl);
    }

    public static String tokenUrl(String absPathUrl) {
        absPathUrl = removeToken(absPathUrl);
        String fileName = absPathUrl.substring(absPathUrl.lastIndexOf("/") + 1);
        String token = FileStorageByNotaryUtil.generateToken(fileName, JwtUtils.getNotarialOfficeSecretKey());
        return absPathUrl + "?token=" + token;
    }

    public static String removeToken(String urlPath) {
        if (urlPath.contains("?token")) {
            return urlPath.substring(0, urlPath.indexOf("?token"));
        }
        return urlPath;
    }
}
