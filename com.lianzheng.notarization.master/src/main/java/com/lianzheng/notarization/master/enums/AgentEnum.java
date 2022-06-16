package com.lianzheng.notarization.master.enums;

public enum AgentEnum {
    NoAgent("0", "是"),
    Agent("1", "否"),

    ;
    private String code;

    private String msg;

    AgentEnum(String code, String msg) {
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
        AgentEnum[] enums = values();
        for (AgentEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        AgentEnum[] enums = values();
        for (AgentEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
