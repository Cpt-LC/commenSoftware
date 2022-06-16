package com.lianzheng.h5.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("notarization_driver_license")
public class NotarizationDriverLicense {
    @TableId
    private String id;
    //颁发时间
    private Date issuingTime;
    //颁发机构
    private String issuingAuthority;
    //是否有效；如果取消支付，或是其他原因
    private int  isDeleted;

    private Date createdTime;
    private Date updatedTime;
    private String createdBy;
    private String updatedBy;

}
