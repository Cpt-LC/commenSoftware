package com.lianzheng.h5.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("notarization_degree")
public class NotarizationDegree {
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

}
