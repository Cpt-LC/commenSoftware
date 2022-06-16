package com.lianzheng.notarization.master.enums;

/**
 * @Description: 文档的分类code
 * @author: 何江雁
 * @date: 2021年12月13日 18:16
 */
public enum DocumentCategoryCode {

    HEAD("HEAD","头像"),
    ID_BACK("ID-BACK","身份证人像面"),
    ID_FRONT("ID-FRONT","身份证国徽面"),
    ID_BACK_AGENT("ID-BACK-AGENT","代理人身份证人像面"),
    ID_FRONT_AGENT("ID-FRONT-AGENT","代理人身份证国徽面"),
    ID_OTHER_CER("ID-OTHER-CER","身份证明材料"),
    ID_OTHER_CER_AGENT("ID-OTHER-CER-AGENT","代理人身份证明材料"),
    HOUSEHOLD("HOUSEHOLD","户口本本人"),
    HOUSEHOLD_MAIN("HOUSEHOLD-MAIN","户口本户主"),
    ATTORNEY("ATTORNEY","委托书相关材料"),
    DECLARATION("DECLARATION","声明书"),
    BUSINESS_LICENSE("BUSINESS-LICENSE","营业执照"),
    MATERIAL_OT("MATERIAL-OT","证明材料"),
    MATERIAL_OT2("MATERIAL-OT2","其他证明材料"),
    NOTARY_MATERIAL("NOTARY-MATERIAL","公证员证明材料"),
    SIGN("SIGN", "签名"),
    SIGN_RECEIPT("SIGN-RECEIPT","送达回执签名"),

    //建行的额外的证明材料
    ACADEMIC("ACADEMIC","学历证明"),
    DEGREE("DEGREE","学位证明"),
    TAXOT("TAXOT","纳税证明"),
    MARRIAGE("MARRIAGE","结婚证"),
    DIVORCE("DIVORCE","离婚证"),
    MARRIAGEOT("MARRIAGEOT","婚姻证明凭证"),
    POFEEDBACK("POFEEDBACK","派出所反馈"),
    DEPOSIT("DEPOSIT","存款证明"),
    BANKCARD("BANKCARD","银行卡"),
    DL("DL","驾驶证"),

    PDF_NOTICE("PDF-NOTICE","告知书"),
    PDF_NOTICE_ENTRUSTED("PDF-NOTICE-ENTRUSTED","办理委托公证告知书"),
    PDF_NOTICE_HANDLE("PDF-NOTICE-HANDLE","办理放弃、继承告知书"),
    PDF_NOTICE_DECLARATION("PDF-NOTICE-DECLARATION","声明告知书"),
    PDF_RECEIPT("PDF-RECEIPT","送达回执"),
    PDF_PAY_NOTICE("PDF-PAY-NOTICE","缴费通知书"),
    PDF_DRAFT("PDF-DRAFT","拟稿纸"),
    PDF_DRAFT_CERT("PDF-DRAFT-CERT","译文与正本相符拟稿纸"),
    PDF_APPLICATION("PDF-APPLICATION","申请表"),
    PDF_AC_NOTICE("PDF-AC-NOTICE","受理表"),
    PDF_NOTARIZATION("PDF-NOTARIZATION","公证书"),
    PDF_NOTARIZATION_CERT("PDF-NOTARIZATION-CERT","译文与正本相符公证书"),
    PDF_QUESTION("PDF-QUESTION","询问笔录"),
    PDF_FO_NOTICE("PDF-FO-NOTICE","涉外、涉港澳台告知书"),
    PDF_HOME_ATTORNEY("PDF-HOME-ATTORNEY","房产委托书");





    private String code;

    private String msg;

    DocumentCategoryCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static String getEnumCode(String value) {
        DocumentCategoryCode[] enums = values();
        for (DocumentCategoryCode item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        DocumentCategoryCode[] enums = values();
        for (DocumentCategoryCode item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
