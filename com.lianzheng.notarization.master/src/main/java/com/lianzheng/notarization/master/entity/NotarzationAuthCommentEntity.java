package com.lianzheng.notarization.master.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("notarzation_auth_comment")
public class NotarzationAuthCommentEntity {
   @TableId
   private String  id;
    //公证id
   private String notarzationId;

   private String tableName;

   private String referrerId;
   //字段名
   private String  fieldName;
   //标注内容
   private String comment;
   //标注状态 0有错未修改；1有错已修改
   private int  status;
   private Date createdTime;

   private Date updatedTime;
   private String  createdBy;

   private String updatedBy;
}
