package com.lianzheng.h5.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
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
 * 毕业公证申请详情表
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notarzation_graduation")
@ApiModel(value="NotarzationGraduation对象", description="毕业公证申请详情表")
public class NotarzationGraduation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty(value = "毕业于哪所学校")
    @TableField("graduatedFrom")
    private String graduatedFrom;

    @ApiModelProperty(value = "毕业时间")
    @TableField("graduatedDate")
    private LocalDate graduatedDate;

    @ApiModelProperty(value = "是否有效；如果取消支付，或是其他原因")
    @TableField("isDeleted")
    private Integer isDeleted;

    @TableField("createdTime")
    private LocalDateTime createdTime;

    @TableField("updatedTime")
    private LocalDateTime updatedTime;

    @TableField("createdBy")
    private String createdBy;

    @TableField("updatedBy")
    private String updatedBy;


}
