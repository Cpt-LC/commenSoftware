package com.lianzheng.notarization.master.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
@TableName("`order`")
public class OrderEntity {

   @TableId
   private String id ;
    //用户id
   private String userId;
   //订单编号：<前缀两位>+年月日8位+时间戳后6位+随机数6位
   private String orderNo;
   //公证状态:待审核、待确认、待支付、出证中、待领取、已完成、已驳回、已取消',
   private String status;
    //支付方式，线上支付或线下支付
   private String paymentMode;
   //支付类型
    private String paymentType;
    //支付状态
    private String  paymentStatus;
    //实际金额
    private  BigDecimal realAmount;
    //已付金额
    private BigDecimal  paidAmount;
    //公证费
    private BigDecimal notaryAmount;
    //副本费
    private BigDecimal copyAmount;
    //翻译费
    private BigDecimal translationAmount;
    //快递费
    private BigDecimal logisticsAmount;
    //公证服务费
    private BigDecimal serviceAmount;
    //商户订单号
    private String  outtradeNo;
    //支付时间
     private Date  paymentTime;
     //是否有效
     private int  isDeleted;
    //开票名称
    private String billingName;
    //开票税号
    private String invoiceTaxNo;
     //物流公司
     private String  logisticsCompany;
     //物流单号
     private String  logisticsNumber;
     //是否邮寄
     private int isSend;
     //邮寄省
     private String sentToProvince;
     //邮寄城市
     private String sentToCity;
     //邮寄区域
     private String sentToArea;
     //邮寄地址
     private String sentToAddress;
     //邮寄电话号
     private String sentToPhone;
     //收件人
     private String sendtToName;
     //创建时间
     private Date createdTime;
     //更新时间
    private Date  updatedTime;
    //创建者
     private  String  createdBy;
    //更新者
    private  String updatedBy;

    public void update(Map<String, Object> param){
      if(param.containsKey("translationAmount")){ this.translationAmount = NumberUtils.createBigDecimal(param.get("translationAmount").toString());}
      if(param.containsKey("logisticsAmount")){ this.logisticsAmount =NumberUtils.createBigDecimal(param.get("logisticsAmount").toString());}
      if(param.containsKey("serviceAmount")){ this.serviceAmount =NumberUtils.createBigDecimal(param.get("serviceAmount").toString());}
      if(param.containsKey("paymentMode")){ this.paymentMode =(String) param.get("paymentMode");}
      if(param.containsKey("logisticsCompany")){ this.logisticsCompany =(String) param.get("logisticsCompany");}
      if(param.containsKey("logisticsNumber")){ this.logisticsNumber =(String) param.get("logisticsNumber");}
      if(param.containsKey("isSend")){ this.isSend =(int) param.get("isSend");}
     if(param.containsKey("sentToProvince")){ this.sentToProvince =(String) param.get("sentToProvince");}
     if(param.containsKey("sentToCity")){ this.sentToCity =(String) param.get("sentToCity");}
     if(param.containsKey("sentToArea")){ this.sentToArea =(String) param.get("sentToArea");}
     if(param.containsKey("sentToAddress")){ this.sentToAddress =(String) param.get("sentToAddress");}
     if(param.containsKey("sentToPhone")){ this.sentToPhone =(String) param.get("sentToPhone");}
     if(param.containsKey("sendtToName")){ this.sendtToName =(String) param.get("sendtToName");}
     if(param.containsKey("billingName")){ this.billingName =(String) param.get("billingName");}
     if(param.containsKey("invoiceTaxNo")){ this.invoiceTaxNo =(String) param.get("invoiceTaxNo");}
    }

}
