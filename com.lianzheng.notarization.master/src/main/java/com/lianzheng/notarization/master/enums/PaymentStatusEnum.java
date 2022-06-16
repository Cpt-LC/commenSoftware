package com.lianzheng.notarization.master.enums;

public enum PaymentStatusEnum {
    NotPaid("NotPaid", "待支付"),
    Paid("Paid", "已支付"),
    Canceled("Canceled", "已取消"),
    Refunded("Refunded","已退款")

    ;
    private String code;

    private String msg;

    PaymentStatusEnum(String code, String msg) {
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
        PaymentStatusEnum[] enums = values();
        for (PaymentStatusEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        PaymentStatusEnum[] enums = values();
        for (PaymentStatusEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
