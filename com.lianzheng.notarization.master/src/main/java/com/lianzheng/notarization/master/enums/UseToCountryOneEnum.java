package com.lianzheng.notarization.master.enums;

public enum  UseToCountryOneEnum {
    Value1("中国","中国"),
    Value2("中国香港","中国香港") ,
    Value3("中国澳门","中国澳门"),
    Value4("加拿大","加拿大"),
    Value5("英国","英国"),
    Value6("新加坡","新加坡"),
    Value7("澳大利亚","澳大利亚"),
    Value8("新西兰","新西兰"),
    Value9("日本","日本"),
    Value10("法国","法国"),
    Value11("美国","美国"),
    Value12("韩国","韩国"),
    Value13("奥地利","奥地利"),
            ;

    private String code;

    private String msg;

    UseToCountryOneEnum(String code, String msg) {
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
        UseToCountryOneEnum[] enums = values();
        for (UseToCountryOneEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        UseToCountryOneEnum[] enums = values();
        for (UseToCountryOneEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
