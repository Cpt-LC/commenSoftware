package com.lianzheng.notarization.master.enums;

public enum NotarizationTypeOneEnum {
    TAX("TAX", "纳税证明"),
    AC("AC","学历证明"),
    DC("DC","学位证明"),
    NC("NC","无犯罪记录证明"),
    MC("MC","结婚证明")	,
    DMC("DMC","离婚证明"),
    UMC("UMC","未婚证明"),
    COD("COD","存款证明"),
    DL("DL","驾驶证证明"),

    ;
    private String code;

    private String msg;

    NotarizationTypeOneEnum(String code, String msg) {
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
        NotarizationTypeOneEnum[] enums = values();
        for (NotarizationTypeOneEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        NotarizationTypeOneEnum[] enums = values();
        for (NotarizationTypeOneEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}