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
 * sys_dict实体类
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dict")
@ApiModel(value="sys_dict对象", description="系统字典表")
public class SysDict implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty("code值")
    @TableField("code")
    private String value;

    @ApiModelProperty("标签名")
    @TableField("label")
    private String text;

    @ApiModelProperty("类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("父级编号")
    @TableField("parentId")
    private String parentId;

    @ApiModelProperty("描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("删除标记:0-未删除，1-已删除")
    @TableField("delFlag")
    private Boolean delFlag;

    @TableField("createdTime")
    private LocalDateTime createdTime;

    @TableField("updatedTime")
    private LocalDateTime updatedTime;

    @TableField("createdBy")
    private String createdBy;

    @TableField("updatedBy")
    private String updatedBy;

}
