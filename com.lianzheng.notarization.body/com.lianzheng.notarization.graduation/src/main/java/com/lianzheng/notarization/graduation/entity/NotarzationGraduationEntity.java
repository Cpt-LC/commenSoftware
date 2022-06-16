package com.lianzheng.notarization.graduation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@TableName("notarzation_graduation")
public class NotarzationGraduationEntity {
    @TableId
    private String id;
    //毕业于哪所学校
    private String graduatedFrom;
    //毕业时间
    private Date  graduatedDate;
    //是否有效；如果取消支付，或是其他原因
    private int  isDeleted;

    private Date  createdTime;

    private Date updatedTime;
    private String createdBy;
    private String updatedBy;

    public void update(Map<String, Object> param){
        if(param.containsKey("graduatedFrom")){ this.graduatedFrom =(String)param.get("graduatedFrom");}
        if(param.containsKey("graduatedDate")){
            String date = param.get("graduatedDate")==null? "" : param.get("graduatedDate").toString();
            if(date.equals("")){
                return;
            }
            this.graduatedDate = DateUtils.stringToDate(date, "yyyy-MM-dd");
        }
    }
}
