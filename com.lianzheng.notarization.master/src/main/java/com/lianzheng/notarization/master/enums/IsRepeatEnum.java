package com.lianzheng.notarization.master.enums;

public enum IsRepeatEnum {

    NoAgent("0", "否"),
    Agent("1", "是"),

    ;
    private String code;

    private String msg;

    IsRepeatEnum(String code, String msg) {
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
        IsRepeatEnum[] enums = values();
        for (IsRepeatEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        IsRepeatEnum[] enums = values();
        for (IsRepeatEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
