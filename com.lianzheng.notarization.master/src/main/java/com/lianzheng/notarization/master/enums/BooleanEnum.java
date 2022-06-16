package com.lianzheng.notarization.master.enums;

public enum BooleanEnum {

    NoAgent("0", "否"),
    Agent("1", "是"),

    ;
    private String code;

    private String msg;

    BooleanEnum(String code, String msg) {
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
        BooleanEnum[] enums = values();
        for (BooleanEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        BooleanEnum[] enums = values();
        for (BooleanEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
