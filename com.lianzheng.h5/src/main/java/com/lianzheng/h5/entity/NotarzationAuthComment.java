package com.lianzheng.h5.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 公证申请修改内容表
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notarzation_auth_comment")
@ApiModel(value="NotarzationAuthComment对象", description="公证申请修改内容表")
public class NotarzationAuthComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty(value = "公证id")
    @TableField("notarzationId")
    private String notarzationId;

    @TableField("tableName")
    private String tableName;

    @ApiModelProperty(value = "相应的子表主键。比如，如果tableName是document，那么referrerId就是document表的主键")
    @TableField("referrerId")
    private String referrerId;

    @ApiModelProperty(value = "字段名")
    @TableField("fieldName")
    private String fieldName;

    @ApiModelProperty(value = "标注状态 0有错未修改；1有错已修改")
    @TableField("status")
    private Boolean status;

    @ApiModelProperty(value = "标注内容")
    @TableField("comment")
    private String comment;

    @TableField("createdTime")
    private LocalDateTime createdTime;

    @TableField("updatedTime")
    private LocalDateTime updatedTime;

    @TableField("createdBy")
    private String createdBy;

    @TableField("updatedBy")
    private String updatedBy;


}
