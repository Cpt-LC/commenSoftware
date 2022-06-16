package com.lianzheng.notarization.master.enums;

public enum MaterialMsgOneEnum {
    //约定材料文字
    TAXMSG("TAXMSG", "申请人身份证明、纳税证明"),
    ACMSG("ACMSG","申请人身份证明、学历证明"),
    DCMSG("DCMSG","申请人身份证明、学位证明"),
    NCMSG("NCMSG","申请人身份证明、派出所反馈"),
    MCMSG("MCMSG","申请人身份证明、婚姻证明凭证"),
    DMCMSG("DMCMSG","申请人身份证明、婚姻证明凭证"),
    UMCMSG("UMCMSG","申请人身份证明、婚姻证明凭证"),
    CODMSG("CODMSG","申请人身份证明、存款证明"),
    DLMSG("DLMSG","申请人身份证明、驾驶证"),
    ;
    private String code;

    private String msg;

    MaterialMsgOneEnum(String code, String msg) {
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
        MaterialMsgOneEnum[] enums = values();
        for (MaterialMsgOneEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        MaterialMsgOneEnum[] enums = values();
        for (MaterialMsgOneEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
