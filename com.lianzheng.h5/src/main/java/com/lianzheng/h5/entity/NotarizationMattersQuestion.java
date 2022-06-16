package com.lianzheng.h5.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 公证事项问题汇总表
 * </p>
 *
 * @author JP
 * @since 2022-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notarization_matters_question")
@ApiModel(value="notarization_matters_question表对象", description="公证事项问题汇总表")
public class NotarizationMattersQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty("公证主表id")
    @TableField("notarizationId")
    private String notarizationId;

    @ApiModelProperty("问题")
    @TableField("question")
    private String question;

    @ApiModelProperty("回答")
    @TableField("answer")
    private String answer;

    @ApiModelProperty("事项类型，notarzation_matters表id")
    @TableField("type")
    private String type;

    @ApiModelProperty("问题排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("createdTime")
    @TableField("createdTime")
    private LocalDateTime createdTime;

    @ApiModelProperty("updatedTime")
    @TableField("updatedTime")
    private LocalDateTime updatedTime;

    @ApiModelProperty("createdBy")
    @TableField("createdBy")
    private String createdBy;

    @ApiModelProperty("updatedBy")
    @TableField("updatedBy")
    private String updatedBy;

}
