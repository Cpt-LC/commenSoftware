package com.lianzheng.h5.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * <p>
 * 公证申请主体表
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notarzation_master")
@ApiModel(value="NotarzationMaster对象", description="公证申请主体表")
public class NotarzationMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty(value = "订单id")
    @TableField("orderId")
    private String orderId;

    @ApiModelProperty(value = "用户id")
    @TableField("userId")
    private String userId;

    @ApiModelProperty(value = "是否是代办")
    @TableField("isAgent")
    private Integer isAgent;

    @ApiModelProperty(value = "申请主体: P - 个人，E - 企业")
    @TableField("applicantParty")
    private String applicantParty;

    @ApiModelProperty(value = "公证事项类型,毕业证 - GR,TAX")
    @TableField("notarzationTypeCode")
    private String notarzationTypeCode;

    @ApiModelProperty(value = "真实姓名")
    @TableField("realName")
    private String realName;

    @ApiModelProperty(value = "性别：1：男，2：女")
    @TableField("gender")
    private Integer gender;

    @TableField("birth")
    private LocalDate birth;

    @ApiModelProperty(value = "证件类型")
    @TableField("idCardType")
    private String idCardType;

    @ApiModelProperty(value = "证件号码")
    @TableField("idCardNo")
    private String idCardNo;

    @ApiModelProperty(value = "身份证地址。如果证件类型是身份证的时候，该值不为空")
    @TableField("idCardAddress")
    private String idCardAddress;

    @ApiModelProperty(value = "公证处id")
    @TableField("notarialOfficeId")
    private Integer notarialOfficeId;

    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "使用地国家")
    @TableField("usedToCountry")
    private String usedToCountry;

    @ApiModelProperty(value = "使用地省份")
    @TableField("usedToProvince")
    private String usedToProvince;

    @ApiModelProperty(value = "使用地城市")
    @TableField("usedToCity")
    private String usedToCity;

    @ApiModelProperty(value = "所需份数")
    @TableField("copyNumber")
    private Integer copyNumber;

    @ApiModelProperty(value = "翻译成哪种语言")
    @TableField("translateTo")
    private String translateTo;

    @ApiModelProperty(value = "是否需要外事认证")
    @TableField("requiredVerification")
    private Integer requiredVerification;

    @ApiModelProperty(value = "国籍")
    @TableField("nationality")
    private String nationality;

    @ApiModelProperty(value = "使用用途：探亲 学习 继承 就业 结婚 对外贸易 诉讼 招标投标 签订合同 减免税 定居 申请知识产权 提供劳务 扶养（抚养）亲属 领取养老金 其他")
    @TableField("usedFor")
    private String usedFor;

    @ApiModelProperty(value = "是否需要办理公证证明译文与原件相符")
    @TableField("hasMoreCert")
    private Integer hasMoreCert;

//    @ApiModelProperty(value = "是否需要做外事认证")
//    @TableField("hasForeign")
//    private Integer hasForeign;

    @ApiModelProperty(value = "需要寄台湾海基会的副本份数")
    @TableField("sentToStraitsExchangeFoundation")
    private Integer sentToStraitsExchangeFoundation;

    @ApiModelProperty(value = "公证书邮寄到台湾海基会的邮寄方式: P: 普通挂号邮寄;  S:特快专递")
    @TableField("expressModeToSEF")
    private String expressModeToSEF;

    @ApiModelProperty(value = "链的哈希")
    @TableField("hash")
    private String hash;

    @ApiModelProperty(value = "是否已签收送达回执")
    @TableField("signedReceipt")
    private int signedReceipt;

    @ApiModelProperty(value = "受理单号")
    @TableField("processNo")
    private String processNo;

    @ApiModelProperty(value = "公证状态:待审核、待确认、待支付、出证中、待领取、已完成、已驳回、已取消")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "办理中、待审批、出证中、已完成")
    @TableField("processStatus")
    private String processStatus;

    @ApiModelProperty(value = "操作者，办证公证员")
    @TableField("actionBy")
    private String actionBy;

    @ApiModelProperty(value = "笔录者，笔录员")
    @TableField("recordBy")
    private String recordBy;

    @ApiModelProperty(value = "客户备注")
    @TableField("userRemark")
    private String userRemark;

    @ApiModelProperty(value = "审核意见")
    @TableField("authComment")
    private String authComment;

    @ApiModelProperty(value = "公证员备注")
    @TableField("staffRemark")
    private String staffRemark;

    @ApiModelProperty(value = "是否有效；如果取消支付，或是其他原因")
    @TableField("isDeleted")
    private Integer isDeleted;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @TableField("createdTime")
    private LocalDateTime createdTime;

    @TableField("updatedTime")
    private LocalDateTime updatedTime;

    @TableField("createdBy")
    private String createdBy;

    @TableField("updatedBy")
    private String updatedBy;

    @ApiModelProperty(value = "邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "真实姓名拼音")
    @TableField("pinyin")
    private String pinyin;

    // 0否  1是
    @ApiModelProperty(value = "重复提交")
    @TableField("isRepeat")
    private Integer isRepeat;

    /**
     * 2.0版修改字段
     */
    /* ************************************************************* */
    @ApiModelProperty(value = "企业名称")
    @TableField("companyName")
    private String companyName;

    @ApiModelProperty(value = "社会信用代码")
    @TableField("socialCreditCode")
    private String socialCreditCode;

    @ApiModelProperty(value = "企业法人代表")
    @TableField("legalRepresentative")
    private String legalRepresentative;

    @ApiModelProperty(value = "企业地址")
    @TableField("companyAddress")
    private String companyAddress;

    @ApiModelProperty(value = "办理人是否是法人代表：0-不是，1-是")
    @TableField("legalStatus")
    private Integer legalStatus;

    @ApiModelProperty(value = "企业法人代表")
    @TableField("companyType")
    private String companyType;

    @ApiModelProperty(value = "成立日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    @TableField("registerDate")
    private LocalDate registerDate;

    @ApiModelProperty(value = "使用地说明")
    @TableField("usedToRemark")
    private String usedToRemark;

    @ApiModelProperty(value = "用途说明")
    @TableField("usedForRemark")
    private String usedForRemark;
    /* ***************************************************************** */



    public void  updateFromAgent(JSONObject object){
        this.realName = object.getString("realNameAgent");
        this.birth = object.getDate("birthAgent").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        this.idCardType = object.getString("idCardTypeAgent");
        this.idCardNo= object.getString("idCardNoAgent");
        this.idCardAddress = object.getString("idCardAddressAgent");
//        this.phone = object.getString("phoneAgent");
        this.gender = object.getInteger("genderAgent");

    }
}
