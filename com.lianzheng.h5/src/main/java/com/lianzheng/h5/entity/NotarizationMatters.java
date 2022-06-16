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
 * 公证事项表
 * </p>
 *
 * @author JP
 * @since 2022-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notarization_matters")
@ApiModel(value="notarization_matters表对象", description="公证事项表")
public class NotarizationMatters implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty("事项code")
    @TableField("code")
    private String code;

    @ApiModelProperty("事项名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("状态：0-无效，1-有效")
    @TableField("status")
    private Integer status;

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
