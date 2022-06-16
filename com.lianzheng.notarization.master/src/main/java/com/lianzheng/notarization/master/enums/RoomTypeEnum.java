package com.lianzheng.notarization.master.enums;

public enum RoomTypeEnum {

    NEW("1", "新房"),
    OLD("2", "二手房"),

    ;
    private String code;

    private String msg;

    RoomTypeEnum(String code, String msg) {
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
        RoomTypeEnum[] enums = values();
        for (RoomTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        RoomTypeEnum[] enums = values();
        for (RoomTypeEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
