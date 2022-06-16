package com.lianzheng.notarization.master.enums;

public enum SignedEnum {
    UNSIGNED("0", "未签收"),
    SIGNED("1", "已签收"),

    ;
    private String code;

    private String msg;

    SignedEnum(String code, String msg) {
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
        SignedEnum[] enums = values();
        for (SignedEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        SignedEnum[] enums = values();
        for (SignedEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
