package com.lianzheng.notarization.master.enums;

public enum SexEnum {
    MAN("1", "男"),
    WOMEN("2", "女"),

    ;
    private String code;

    private String msg;

    SexEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static String getEnumCode(String value) {
        SexEnum[] enums = values();
        for (SexEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        SexEnum[] enums = values();
        for (SexEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
