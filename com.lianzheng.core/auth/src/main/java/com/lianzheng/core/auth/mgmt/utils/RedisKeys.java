package com.lianzheng.core.auth.mgmt.utils;

/**
 * Redis所有Keys
 *
 * @author gang.shen@kedata.com
 */
public class RedisKeys {

    public static String getSysConfigKey(String key) {
        return "sys:config:" + key;
    }
}
