package com.lianzheng.notarization.master.enums;

public enum ProcessStatusEnum {
    //办理状态
    DOING("Doing", "办理中"),
    APPROVING("Approving", "待审批"),
    GENERATINGCERT("GeneratingCert", "出证中"),
    COMPLETED("Completed", "已完成"),

    ;
    private String code;

    private String msg;

    ProcessStatusEnum(String code, String msg) {
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
        ProcessStatusEnum[] enums = values();
        for (ProcessStatusEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        ProcessStatusEnum[] enums = values();
        for (ProcessStatusEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
