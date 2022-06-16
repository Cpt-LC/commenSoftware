package com.lianzheng.notarization.master.enums;

public enum  SexStringEnum {

    MAN("1", "男"),
    WOMEN("2", "女"),

            ;
    private String code;

    private String msg;

    SexStringEnum(String code, String msg) {
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
        SexStringEnum[] enums = values();
        for (SexStringEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        SexStringEnum[] enums = values();
        for (SexStringEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
