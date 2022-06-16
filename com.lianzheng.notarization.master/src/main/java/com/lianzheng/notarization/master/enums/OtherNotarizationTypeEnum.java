package com.lianzheng.notarization.master.enums;

/**
 * 其他公证事项，用于判定是否可编辑公证类型
 */
public enum  OtherNotarizationTypeEnum {

//    FDJC("FDJC","法定继承"),
//    SHC("SHC","生存"),
//    GJ("GJ","国籍"),
//    JH("JH","监护"),
//    HJZHX("HJZHX","户籍注销"),
//    ZHSD("ZHSD","住所地（居住地）"),
//    JL("JL","经历"),
//    ZHWU("ZHWU","职务（职称）"),
//    ZG("ZG","资格"),
//    HY("HY","婚姻状况"),
//    SHY("SHY","收养关系"),
//    GQ("GQ","股权"),
//    ZHSHCHQ("ZHSHCHQ","知识产权权"),
//    CK("CK","存款"),
//    BDCHWQ("BDCHWQ","不动产物权"),
//    DCHWQ("DCHWQ","动产物权"),
//    ZHQ("ZHQ","债权"),
//    XNCHQL("XNCHQL","虚拟财产权利"),
//    CCHQT("CCHQT","其他财产权"),
//    SHR("SHR","收入状况"),
//    PJJJ("PJJJ","票据拒绝"),
//    XP("XP","选票"),
//    ZHWEN("ZHWEN","指纹"),
//    YJSHY("YJSHY","印鉴式样"),
//    QMSHY("QMSHY","签名式样"),
//    BKKL("BKKL","不可抗力"),
//    YWSHJ("YWSHJ","意外事件"),
//    CHWDAJZ("CHWDAJZ","查无档案记载"),
    ZBXZ("ZBXZ","暂不选择"),
    ;
    private String code;

    private String msg;

    OtherNotarizationTypeEnum(String code, String msg) {
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
        OtherNotarizationTypeEnum[] enums = values();
        for (OtherNotarizationTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        OtherNotarizationTypeEnum[] enums = values();
        for (OtherNotarizationTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
