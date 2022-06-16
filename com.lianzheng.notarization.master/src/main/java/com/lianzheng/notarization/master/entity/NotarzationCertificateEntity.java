package com.lianzheng.notarization.master.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigInteger;

@Data
@TableName("notarzation_certificate")
public class NotarzationCertificateEntity {
    @TableId(type = IdType.AUTO)
    private Integer certificateId;
    private String uuid;
    private String businessId;
    private String notarialCertificateNo; //该字段不可为空
    public NotarzationCertificateEntity(){

    }
    public NotarzationCertificateEntity(String uuid,String businessId,String notarialCertificateNo){
        this.uuid=uuid;
        this.businessId =businessId;
        this.notarialCertificateNo = notarialCertificateNo;
    }
}
