package com.lianzheng.notarization.master.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import lombok.Data;
import org.apache.commons.lang.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Data
@TableName("notarzation_master")
public class NotarzationMasterEntity {
    @TableId
    private String id;
    //订单id
    private String orderId;
    //用户id
    private String userId;
    //申请主体: P - 个人，E - 企业
    private String applicantParty;
    //是否是代办
    private int isAgent;
    //公证事项类型,毕业证 - GR,TAX
    private String notarzationTypeCode;
    //真实姓名
    private String realName;
    //公证处id
    private Long notarialOfficeId;
    //性别：1：男，2：女
    private String gender;
    //生日
    private Date birth;
    //省份证类型
    private String idCardType;
    //证件号码
    private String idCardNo;
    //证件地址
    private String idCardAddress;
    //手机号
    private  String phone;
    //使用地国家
    private String usedToCountry;
    //使用地省份
    private String usedToProvince;
    //使用地城市
    private String  usedToCity;
    //使用地备注
    private String usedToRemark;
    //所需份数
    private BigDecimal copyNumber;
    // 翻译成哪种语言
    private String translateTo;

    //是否需要外事认证
    private int requiredVerification;
    //国籍
    private String nationality;
    // 用途
    private String usedFor;

    //用途备注
    private String usedForRemark;

    //是否需要办理公证证明译文与原件相符
    private String hasMoreCert;
    //需要寄台湾海基会的副本份数
    private int sentToStraitsExchangeFoundation;
    //公证书邮寄到台湾海基会的邮寄方式: P: 普通挂号邮寄;  S:特快专递
    private String expressModeToSEF;
    //链的哈希
    private String hash;
    //受理单号
    private String processNo;
    //公证状态:待审核、待确认、待支付、出证中、待领取、已完成、已驳回、已取消
    private String status;
    //办理中、待审批、出证中、已完成
    private String processStatus;
    //送达回执时候签名
    private int signedReceipt;
    //操作者，办证公证员
    private Long actionBy;
    //笔录者，笔录员
    private Long recordBy;
    //客户备注
    private String userRemark;
    //审核意见
    private String authComment;
    //主任审核意见
    private String directorRejectedComment;
    //公证员备注
    private String staffRemark;
    //是否有效；如果取消支付，或是其他原因
    private int isDeleted;
    //创建时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  createdTime;
    //更新时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  updatedTime;
    //创建者
    private String createdBy;
    //更新者
    private String updatedBy;
    //邮箱
    private String email;
    //真实姓名拼音
    private String pinyin;
    //企业名称
    private String companyName;
    //企业信用代码
    private String socialCreditCode;
    //企业法人代表
    private String legalRepresentative;
    //企业地址
    private String companyAddress;
    //办理人是否法人代表 0不是 1是
    private Integer legalStatus;
    //企业类型
    private String companyType;
    //注册日期
    private Date  registerDate;
    //重复提交
    private int isRepeat;

    public void update(Map<String, Object> param){

        if(Objects.nonNull(param.get("notarzationTypeCode"))){this.notarzationTypeCode = (String) param.get("notarzationTypeCode");}
        if(Objects.nonNull(param.get("birth"))){ this.birth = DateUtils.stringToDate(param.get("birth").toString(),"yyyy-MM-dd");}
        if(Objects.nonNull(param.get("isAgent"))){ this.isAgent =(int)param.get("isAgent");}
        if(Objects.nonNull(param.get("realName"))){ this.realName =(String)param.get("realName");}
        if(Objects.nonNull(param.get("gender"))){ this.gender =param.get("gender").toString();}
        if(Objects.nonNull(param.get("idCardType"))){ this.idCardType =(String)param.get("idCardType");}
        if(Objects.nonNull(param.get("idCardNo"))){ this.idCardNo =(String)param.get("idCardNo");}
        if(Objects.nonNull(param.get("idCardAddress"))){ this.idCardAddress =(String)param.get("idCardAddress");}
        if(Objects.nonNull(param.get("phone"))){ this.phone =(String)param.get("phone");}
        if(Objects.nonNull(param.get("usedToCountry"))){ this.usedToCountry =(String)param.get("usedToCountry");}
        if(Objects.nonNull(param.get("usedToProvince"))){ this.usedToProvince =(String)param.get("usedToProvince");}
        if(Objects.nonNull(param.get("usedToCity"))){ this.usedToCity =(String)param.get("usedToCity");}
        if(Objects.nonNull(param.get("translateTo"))){ this.translateTo =(String)param.get("translateTo");}
        if(Objects.nonNull(param.get("nationality"))){ this.nationality =(String)param.get("nationality");}
        if(Objects.nonNull(param.get("usedFor"))){ this.usedFor =(String)param.get("usedFor");}
        if(Objects.nonNull(param.get("status"))){ this.status =(String)param.get("status");}
        if(Objects.nonNull(param.get("processStatus"))){ this.processStatus =(String)param.get("processStatus");}
        if(Objects.nonNull(param.get("actionBy"))){ this.actionBy =NumberUtils.createLong(param.get("actionBy").toString());}
        if(Objects.nonNull(param.get("recordBy")) && param.get("recordBy")!=null){ this.recordBy =NumberUtils.createLong(param.get("recordBy").toString());}
        if(Objects.nonNull(param.get("userRemark"))){ this.userRemark =(String)param.get("userRemark");}
        if(Objects.nonNull(param.get("authComment"))){ this.authComment =(String)param.get("authComment");}
        if(Objects.nonNull(param.get("staffRemark"))){ this.staffRemark =(String)param.get("staffRemark");}
        if(Objects.nonNull(param.get("directorRejectedComment"))){ this.directorRejectedComment =(String)param.get("directorRejectedComment");}
        if(Objects.nonNull(param.get("applicantParty"))){ this.applicantParty =(String)param.get("applicantParty");}
        if(Objects.nonNull(param.get("copyNumber"))){ this.copyNumber = NumberUtils.createBigDecimal(param.get("copyNumber").toString());}
        if(Objects.nonNull(param.get("hasMoreCert"))){ this.hasMoreCert =param.get("hasMoreCert").toString();}
        if(Objects.nonNull(param.get("requiredVerification"))){ this.requiredVerification =(int)param.get("requiredVerification");}
        if(Objects.nonNull(param.get("sentToStraitsExchangeFoundation"))){ this.sentToStraitsExchangeFoundation =(int) param.get("sentToStraitsExchangeFoundation");}
        if(Objects.nonNull(param.get("expressModeToSEF"))){ this.expressModeToSEF =(String) param.get("expressModeToSEF");}
        if(Objects.nonNull(param.get("email"))){ this.email =(String) param.get("email");}
        if(Objects.nonNull(param.get("pinyin"))){ this.pinyin = (String) param.get("pinyin");}
        if(Objects.nonNull(param.get("companyName"))){this.companyName = (String) param.get("companyName");}
        if(Objects.nonNull(param.get("socialCreditCode"))){ this.socialCreditCode = (String) param.get("socialCreditCode");}
        if(Objects.nonNull(param.get("legalRepresentative"))){ this.legalRepresentative = (String) param.get("legalRepresentative");}
        if(Objects.nonNull(param.get("companyAddress"))){ this.companyAddress = (String) param.get("companyAddress");}
        if(Objects.nonNull(param.get("legalStatus"))){ this.legalStatus =NumberUtils.createInteger(param.get("legalStatus").toString());}
        if(Objects.nonNull(param.get("usedToRemark"))){ this.usedToRemark = (String) param.get("usedToRemark");}
        if(Objects.nonNull(param.get("usedForRemark"))){ this.usedForRemark = (String) param.get("usedForRemark");}
        if(Objects.nonNull(param.get("companyType"))){ this.companyType = (String) param.get("companyType");}
        if(Objects.nonNull(param.get("registerDate"))){ this.registerDate = DateUtils.stringToDate(param.get("registerDate").toString(),"yyyy-MM-dd");;}
    }
}
