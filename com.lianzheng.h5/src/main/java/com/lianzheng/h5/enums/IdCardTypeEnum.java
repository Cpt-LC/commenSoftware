package com.lianzheng.h5.enums;

public enum IdCardTypeEnum {

    value1("1","身份证"),
    value2("2","外国护照"),
    value3("3","港澳居民来往内地通行证"),
    value4("4","台湾居民来往大陆通行证"),
    value5("5","其他证件"),


//    value1("身份证","01"),
//    value2("户口本","02"),
//    value3("中华人民共和国护照","03"),
//    value4("军官证","04"),
//    value5("士兵证","05"),
//    value6("港澳居民来往内地通行证","06"),
//    value7("台湾居民来往大陆通行证","07"),
//    value8("其他证件","08"),
//    value9("外国护照","09"),
    ;


    private String code;

    private String msg;

    IdCardTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static String getEnumCode(String value) {
        IdCardTypeEnum[] enums = values();
        for (IdCardTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        IdCardTypeEnum[] enums = values();
        for (IdCardTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
