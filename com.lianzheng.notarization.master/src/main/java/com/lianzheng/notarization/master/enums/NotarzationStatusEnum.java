package com.lianzheng.notarization.master.enums;

/**
 * 相关枚举值
 */
public enum NotarzationStatusEnum {

    //公证状态
    PENDINGAPPROVED("PendingApproved", "待审核"),
    PENDINGCONFIRMED("PendingConfirmed", "待确认"),
    PENDINGPAYMENT("PendingPayment", "待支付"),
    GENERATINGCERT("GeneratingCert", "出证中"),
    PENDINGPICKUP("PendingPickup", "待领取"),
    COMPLETED("Completed", "已完成"),
    REJECTED("Rejected", "已驳回"),
    CANCELED("Canceled", "已取消");
    private String code;

    private String msg;

    NotarzationStatusEnum(String code, String msg) {
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
        NotarzationStatusEnum[] enums = values();
        for (NotarzationStatusEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        NotarzationStatusEnum[] enums = values();
        for (NotarzationStatusEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
