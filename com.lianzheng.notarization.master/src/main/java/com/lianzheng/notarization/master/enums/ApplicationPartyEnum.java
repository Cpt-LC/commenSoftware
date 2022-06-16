package com.lianzheng.notarization.master.enums;

public enum ApplicationPartyEnum {
    P("P", "个人"),
    E("E", "企业"),

    ;
    private String code;

    private String msg;

    ApplicationPartyEnum(String code, String msg) {
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
        ApplicationPartyEnum[] enums = values();
        for (ApplicationPartyEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        ApplicationPartyEnum[] enums = values();
        for (ApplicationPartyEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
