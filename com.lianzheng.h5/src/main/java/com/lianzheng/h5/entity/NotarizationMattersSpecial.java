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
@TableName("notarization_matters_special")
@ApiModel(value="notarization_matters_special表对象", description="公证事项特殊项汇总表")
public class NotarizationMattersSpecial implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty("公证主表id")
    @TableField("notarizationId")
    private String notarizationId;

    @ApiModelProperty("事项类型")
    @TableField("notarizationType")
    private String notarizationType;

    @ApiModelProperty("输入项key值")
    @TableField("entryKey")
    private String entryKey;

    @ApiModelProperty("输入项value值")
    @TableField("entryValue")
    private String entryValue;

    @ApiModelProperty("输入项类型：string，date等")
    @TableField("entryType")
    private String entryType;

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
