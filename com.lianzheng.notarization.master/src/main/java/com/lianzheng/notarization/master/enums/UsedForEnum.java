package com.lianzheng.notarization.master.enums;

/**
 * @Description: TODO
 * @author: 何江雁
 * @date: 2022年01月07日 8:23
 */
public enum UsedForEnum {
    Value1("探亲","探亲"),
    Value2("学习","学习") ,
    Value3("继承","继承"),
    Value4("就业","就业"),
    Value5("结婚","结婚"),
    Value6("对外贸易","对外贸易"),
    Value7("诉讼","诉讼"),
    Value8("招标投标","招标投标"),
    Value9("签订合同","签订合同"),
    Value10("减免税","减免税"),
    Value11("定居","定居"),
    Value12("申请知识产权","申请知识产权"),
    Value13("提供劳务","提供劳务"),
    Value14("扶养（抚养）亲属","扶养（抚养）亲属"),
    Value15("领取养老金","领取养老金"),
    Value16("其他","其他")
            ;

    private String code;

    private String msg;

    UsedForEnum(String code, String msg) {
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
        UsedForEnum[] enums = values();
        for (UsedForEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        UsedForEnum[] enums = values();
        for (UsedForEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
