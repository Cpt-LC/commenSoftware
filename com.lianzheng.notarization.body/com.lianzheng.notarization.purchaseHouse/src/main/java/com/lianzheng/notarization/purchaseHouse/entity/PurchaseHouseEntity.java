package com.lianzheng.notarization.purchaseHouse.entity;

import lombok.Data;

@Data
public class PurchaseHouseEntity {
    // "房屋类型：1-新房，2-二手房"
    private String roomType;

    // "受托人姓名"
    private String trusteeName;

    // "受托人性别"
    private String trusteeGender;

    // "受托人出生日期"
    private String trusteeBirthday;

    // "受托人身份证号"
    private String trusteeIdNum;

    // "房屋地址"
    private String roomAddress;

    // "委托人与受托人关系"
    private String trusteeRelation;

    // "不动产权证书编号/房屋所有权证"
    private String ownershipCertificate;

    // "国有土地使用证"
    private String landUseCertificate;
}
