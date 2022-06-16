package com.lianzheng.notarization.master.enums;

public enum DocumentCategoryTruthCode {

    value1("PDF-QUESTION","4"),
    value2("PDF-PAY-NOTICE","9"),
    value3("PDF-AC-NOTICE","2"),
    value4("PDF-APPLICATION","1"),
    value5("PDF-FO-NOTICE","3"),
    value6("PDF-NOTICE","3"),
    value7("PDF-HOME-ATTORNEY","9"),
    value8("PDF-NOTICE-ENTRUSTED","3"),
    value9("PDF-NOTICE-HANDLE","3"),
    value10("PDF-NOTICE-DECLARATION","3"),
    ;


    private String code;

    private String msg;

    DocumentCategoryTruthCode(String code, String msg) {
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
        DocumentCategoryTruthCode[] enums = values();
        for (DocumentCategoryTruthCode item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        DocumentCategoryTruthCode[] enums = values();
        for (DocumentCategoryTruthCode item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
