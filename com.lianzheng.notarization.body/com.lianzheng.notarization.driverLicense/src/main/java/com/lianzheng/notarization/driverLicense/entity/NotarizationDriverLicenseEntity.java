package com.lianzheng.notarization.driverLicense.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@TableName("notarization_driver_license")
public class NotarizationDriverLicenseEntity {
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
    public void update(Map<String, Object> param){
        if(param.containsKey("issuingAuthority")&&param.get("issuingAuthority")!=null){ this.issuingAuthority =(String)param.get("issuingAuthority");}
        if(param.containsKey("issuingTime")&&param.get("issuingTime")!=null){ this.issuingTime = DateUtils.stringToDate(param.get("issuingTime").toString(), "yyyy-MM-dd"); }
    }
}
