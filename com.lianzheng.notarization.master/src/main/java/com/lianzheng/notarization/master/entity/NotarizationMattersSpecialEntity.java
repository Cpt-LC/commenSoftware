package com.lianzheng.notarization.master.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("notarization_matters_special")
public class NotarizationMattersSpecialEntity {
    @TableId
    private String id;
    private String notarizationId;
    private String notarizationType;
    private String entryKey;
    private String entryValue;
    private String entryType;
    //创建时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    //更新时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  updatedTime;
    //创建者
    private String createdBy;
    //更新者
    private String updatedBy;
}
