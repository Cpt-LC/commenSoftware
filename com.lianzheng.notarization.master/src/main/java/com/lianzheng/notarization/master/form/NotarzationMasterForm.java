package com.lianzheng.notarization.master.form;

import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import lombok.Data;

import java.math.BigDecimal;
/*
列表查询类
 */
@Data
public class NotarzationMasterForm extends NotarzationMasterEntity {
    //支付方式，线上支付或线下支付
    private String paymentMode;
    //支付状态
    private String  paymentStatus;
    //实际金额
    private BigDecimal realAmount;
    //已付金额
    private BigDecimal  paidAmount;
    //操作者，办证公证员
    private String actionName;
    //笔录者，笔录员
    private String recordName;
}
