package com.lianzheng.h5.enums;

import lombok.Getter;

/**
 * 事项状态枚举
 *
 * @author
 */
@Getter
public enum NotarizationStatusEnum {

    //公证状态
    PENDINGAPPROVED("PendingApproved", "待审核"),
    PENDINGCONFIRMED("PendingConfirmed", "待确认"),
    PENDINGPAYMENT("PendingPayment", "待支付"),
    GENERATINGCERT("GeneratingCert", "出证中"),
    PENDINGPICKUP("PendingPickup", "待取证"),
    COMPLETED("Completed", "已完成"),
    REJECTED("Rejected", "已驳回"),
    CANCELED("Canceled", "已取消");

    private final String code;

    private final String msg;

    NotarizationStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
