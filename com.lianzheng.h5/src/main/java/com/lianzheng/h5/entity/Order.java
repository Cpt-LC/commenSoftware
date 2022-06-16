package com.lianzheng.h5.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("`order`")
@ApiModel(value="Order对象", description="订单表")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty(value = "用户id")
    @TableField("userId")
    private String userId;

    @ApiModelProperty(value = "订单编号：<前缀两位>+年月日8位+时间戳后6位+随机数6位")
    @TableField("orderNo")
    private String orderNo;

    @ApiModelProperty(value = "公证状态:待审核、待确认、待支付、出证中、待领取、已完成、已驳回、已取消")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "支付方式，线上支付或线下支付")
    @TableField("paymentMode")
    private String paymentMode;

    @ApiModelProperty(value = "支付类型")
    @TableField("paymentType")
    private String paymentType;

    @ApiModelProperty(value = "支付状态")
    @TableField("paymentStatus")
    private String paymentStatus;

    @ApiModelProperty(value = "实际金额")
    @TableField("realAmount")
    private BigDecimal realAmount;

    @ApiModelProperty(value = "已付金额")
    @TableField("paidAmount")
    private BigDecimal paidAmount;

    @ApiModelProperty(value = "公证费")
    @TableField("notaryAmount")
    private BigDecimal notaryAmount;

    @ApiModelProperty(value = "副本费")
    @TableField("copyAmount")
    private BigDecimal copyAmount;

    @ApiModelProperty(value = "翻译费")
    @TableField("translationAmount")
    private BigDecimal translationAmount;

    @ApiModelProperty(value = "快递费")
    @TableField("logisticsAmount")
    private BigDecimal logisticsAmount;

    @ApiModelProperty(value = "公证服务费")
    @TableField("serviceAmount")
    private BigDecimal serviceAmount;

    @ApiModelProperty(value = "商户订单号")
    @TableField("outtradeNo")
    private String outtradeNo;

    @TableField("paymentTime")
    private LocalDateTime paymentTime;

    @ApiModelProperty(value = "是否有效；如果取消支付，或是其他原因")
    @TableField("isDeleted")
    private Integer isDeleted;

    @ApiModelProperty(value = "开票名称")
    @TableField("billingName")
    private String billingName;

    @ApiModelProperty(value = "开票税号")
    @TableField("invoiceTaxNo")
    private String invoiceTaxNo;

    @ApiModelProperty(value = "物流公司")
    @TableField("logisticsCompany")
    private String logisticsCompany;

    @ApiModelProperty(value = "物流单号")
    @TableField("logisticsNumber")
    private String logisticsNumber;

    @ApiModelProperty(value = "是否邮寄")
    @TableField("isSend")
    private Integer isSend;

    @ApiModelProperty(value = "邮寄省")
    @TableField("sentToProvince")
    private String sentToProvince;

    @ApiModelProperty(value = "邮寄城市")
    @TableField("sentToCity")
    private String sentToCity;

    @ApiModelProperty(value = "邮寄区域")
    @TableField("sentToArea")
    private String sentToArea;

    @ApiModelProperty(value = "邮寄地址")
    @TableField("sentToAddress")
    private String sentToAddress;

    @ApiModelProperty(value = "邮寄电话号")
    @TableField("sentToPhone")
    private String sentToPhone;

    @ApiModelProperty(value = "收件人")
    @TableField("sendtToName")
    private String sendtToName;

    @TableField("createdTime")
    private LocalDateTime createdTime;

    @TableField("updatedTime")
    private LocalDateTime updatedTime;

    @TableField("createdBy")
    private String createdBy;

    @TableField("updatedBy")
    private String updatedBy;


}
