package com.lianzheng.notarization.master.enums;

/**
 * 不可以切换的公证
 */
public enum NotChangeNotarizationTypeEnum {
    WTMFB01("WTMFB01","委托买房"),
    WTMFS01("WTMFS01","委托卖房（直系亲属之间）"),
    WTQT("WTQT","委托（其他）"),;

    private String code;

    private String msg;

    NotChangeNotarizationTypeEnum(String code, String msg) {
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
        NotChangeNotarizationTypeEnum[] enums = values();
        for (NotChangeNotarizationTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        NotChangeNotarizationTypeEnum[] enums = values();
        for (NotChangeNotarizationTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
