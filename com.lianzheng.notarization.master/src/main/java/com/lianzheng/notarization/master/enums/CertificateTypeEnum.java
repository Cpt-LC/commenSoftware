package com.lianzheng.notarization.master.enums;

public enum CertificateTypeEnum {
    IDCard("身份证", "身份证"),
    IDCard2("港澳居民通行证", "港澳居民通行证"),
    IDCard3("台湾居民通行证", "台湾居民通行证"),
    value2("户口本","户口本"),
    value3("中华人民共和国护照","中华人民共和国护照"),
    value4("军官证","军官证"),
    value5("士兵证","士兵证"),
    value6("港澳居民来往内地通行证","港澳居民来往内地通行证"),
    value7("台湾居民来往大陆通行证","台湾居民来往大陆通行证"),
    value8("其他证件","其他证件"),
    value9("国外护照","国外护照"),
    ;
    private String code;

    private String msg;

    CertificateTypeEnum(String code, String msg) {
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
        CertificateTypeEnum[] enums = values();
        for (CertificateTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        CertificateTypeEnum[] enums = values();
        for (CertificateTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
