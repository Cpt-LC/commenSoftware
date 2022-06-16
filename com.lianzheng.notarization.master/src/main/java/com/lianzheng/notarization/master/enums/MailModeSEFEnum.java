package com.lianzheng.notarization.master.enums;

public enum MailModeSEFEnum {

    expressModeToSEFP("P", " 普通挂号邮寄"),
    expressModeToSEFS("S", "特快专递"),

    ;
    private String code;

    private String msg;

    MailModeSEFEnum(String code, String msg) {
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
        MailModeSEFEnum[] enums = values();
        for (MailModeSEFEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        MailModeSEFEnum[] enums = values();
        for (MailModeSEFEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
