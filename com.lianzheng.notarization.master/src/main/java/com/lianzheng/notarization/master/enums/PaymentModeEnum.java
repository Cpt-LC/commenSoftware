package com.lianzheng.notarization.master.enums;

public enum PaymentModeEnum {

    Online("Online", "线上"),
    Offline("Offline", "线下"),
    ;

    private String code;

    private String msg;

    PaymentModeEnum(String code, String msg) {
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
        PaymentModeEnum[] enums = values();
        for (PaymentModeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        PaymentModeEnum[] enums = values();
        for (PaymentModeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
