package com.lianzheng.notarization.degree.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@TableName("notarization_degree")
public class NotarizationDegreeEntity {
    @TableId
    private String id;
    //授予时间
    private Date grantTime;
    //授予机构
    private String issuingAuthority;
    //学位名称
    private String degreeName;

    //是否有效；如果取消支付，或是其他原因
    private int  isDeleted;
    private Date  createdTime;

    private Date updatedTime;
    private String createdBy;
    private String updatedBy;


    public void update(Map<String, Object> param){
        if(param.containsKey("issuingAuthority")&&param.get("issuingAuthority")!=null){ this.issuingAuthority =(String)param.get("issuingAuthority");}
        if(param.containsKey("degreeName")&&param.get("degreeName")!=null){ this.degreeName =(String)param.get("degreeName");}
        if(param.containsKey("grantTime")&&param.get("grantTime")!=null){ this.grantTime = DateUtils.stringToDate(param.get("grantTime").toString(), "yyyy-MM-dd"); }
    }

}
