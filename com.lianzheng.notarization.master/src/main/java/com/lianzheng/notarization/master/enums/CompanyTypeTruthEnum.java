package com.lianzheng.notarization.master.enums;

/**
 * 求真  企业类型枚举转化
 */
public enum CompanyTypeTruthEnum {

    // 1 法人机构 0 非法人机构
    jg("jg","1","01"),
    shydw("shydw","1","02"),
    shhtt("shhtt","1","03"),
    jjh("jjh","1","04"),
    shhjjfw("shhjjfw","1","05"),
    yxzrgs("yxzrgs","1","06"),
    gfyxgs("gfyxgs","1","07"),
    qtqy("qtqy","1","08"),
    ncjt("ncjt","1","09"),
    ncjtjjzzh("ncjtjjzzh","1","10"),
    chzhnchzzzh("chzhnchzzzh","1","11"),
    jcqzhxzzhzzh("jcqzhxzzhzzh","1","12"),


    dzqy("dzqy","0","01"),
    hhqy("hhqy","0","02"),
    ffrqyfwjg("ffrqyfwjg","0","03"),
    qtzzh("qtzzh","0","04")
    ;
    private String code;

    private String msg;

    private String truth;


    CompanyTypeTruthEnum(String code, String msg,String truth) {
        this.code = code;
        this.msg = msg;
        this.truth = truth;
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

    public String getTruth() {
        return truth;
    }

    public void setTruth(String truth) {
        this.truth = truth;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static String getEnumCode(String value) {
        CompanyTypeTruthEnum[] enums = values();
        for (CompanyTypeTruthEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        CompanyTypeTruthEnum[] enums = values();
        for (CompanyTypeTruthEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }

    public static String getEnumTruth(String value) {
        CompanyTypeTruthEnum[] enums = values();
        for (CompanyTypeTruthEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getTruth();
            }
        }
        return null;
    }
}
