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
@TableName("notarization_matters_question")
public class NotarizationMattersQuestionEntity {

    @TableId
    private String id;

    private String notarizationId;

    private String question;

    private String answer;

    private String type;

    private Integer sort;
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
