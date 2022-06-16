package com.lianzheng.notarization.master.enums;

public enum AmountMsgEnum {
    NotaryAmount("NotaryAmount", "公证费"),
    CopyAmount("CopyAmount", "副本费"),
    TranslationAmount("TranslationAmount", "翻译费"),
    LogisticsAmount("LogisticsAmount", "快递费"),
    ServiceAmount("ServiceAmount", "公证服务费"),
    ModeToSEF("ModeToSEF", "寄台湾海基会快递费"),
    DoubleCertificate("DoubleCertificate", "双证译文文本相符"),
    DoubleCertificateCopy("DoubleCertificateCopy", "双证译文文本相符副本费"),
    ;;
    private String code;

    private String msg;

    AmountMsgEnum(String code, String msg) {
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
        AmountMsgEnum[] enums = values();
        for (AmountMsgEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        AmountMsgEnum[] enums = values();
        for (AmountMsgEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
