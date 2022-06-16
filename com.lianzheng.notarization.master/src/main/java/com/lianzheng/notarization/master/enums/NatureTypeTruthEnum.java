package com.lianzheng.notarization.master.enums;


public enum NatureTypeTruthEnum {
    P("P", "1"),//个人  自然人
    E("E", "2"),//企业  法人

    ;
    private String code;

    private String msg;

    NatureTypeTruthEnum(String code, String msg) {
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
        NatureTypeTruthEnum[] enums = values();
        for (NatureTypeTruthEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        NatureTypeTruthEnum[] enums = values();
        for (NatureTypeTruthEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}