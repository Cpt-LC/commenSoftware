package com.lianzheng.notarization.master.enums;

/**
 * 企业类型  非法人和法人
 */
public enum CompanyTypeEnum {
    jg("jg","机关"),
    shydw("shydw","事业单位"),
    shhtt("shhtt","社会团体"),
    jjh("jjh","基金会"),
    shhjjfw("shhjjfw","社会机构服务"),
    yxzrgs("yxzrgs","有限责任公司"),
    gfyxgs("gfyxgs","股份有限公司"),
    qtqy("qtqy","其它企业"),
    ncjt("ncjt","农村集体"),
    ncjtjjzzh("ncjtjjzzh","农村集体经济组织"),
    chzhnchzzzh("chzhnchzzzh","城镇农村合作组织"),
    jcqzhxzzhzzh("jcqzhxzzhzzh","基层群众性自治组织"),


    dzqy("dzqy","独资企业"),
    hhqy("hhqy","合伙企业"),
    ffrqyfwjg("ffrqyfwjg","非法人企业服务机构"),
    qtzzh("qtzzh","其他组织")
    ;
    private String code;

    private String msg;


    CompanyTypeEnum(String code, String msg) {
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
        CompanyTypeEnum[] enums = values();
        for (CompanyTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        CompanyTypeEnum[] enums = values();
        for (CompanyTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
