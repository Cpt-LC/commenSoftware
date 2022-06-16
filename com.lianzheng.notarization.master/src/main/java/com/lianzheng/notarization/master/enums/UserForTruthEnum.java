package com.lianzheng.notarization.master.enums;


public enum UserForTruthEnum {
    Value1("探亲","01"),
    Value2("学习","03") ,
    Value3("继承","04"),
    Value4("就业","05"),
    Value5("结婚","06"),
    Value6("对外贸易","11"),
    Value7("诉讼","12"),
    Value8("招标投标","15"),
    Value9("签订合同","16"),
    Value10("减免税","17"),
    Value11("定居","02"),
    Value12("申请知识产权","14"),
    Value13("提供劳务","13"),
    Value14("扶养（抚养）亲属","09"),
    Value15("领取养老金","10"),
    Value16("其他","99")
    ;

    private String code;

    private String msg;

    UserForTruthEnum(String code, String msg) {
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
        UserForTruthEnum[] enums = values();
        for (UserForTruthEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        UserForTruthEnum[] enums = values();
        for (UserForTruthEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}