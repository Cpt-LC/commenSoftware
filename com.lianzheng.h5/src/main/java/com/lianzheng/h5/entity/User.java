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
 * 用户信息表。可兼容多个项目用户数据
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
@ApiModel(value="User对象", description="用户信息表。可兼容多个项目用户数据")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty(value = "用户名")
    @TableField("realName")
    private String realName;

    @ApiModelProperty(value = "加密后的密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "头像")
    @TableField("avatarUrl")
    private String avatarUrl;

    @TableField("phone")
    private String phone;

    @TableField("lastLoginTime")
    private LocalDateTime lastLoginTime;

    @TableField("email")
    private String email;

    @ApiModelProperty(value = "性别：1：男，2：女")
    @TableField("gender")
    private Integer gender;

    @TableField("birth")
    private LocalDate birth;

    @ApiModelProperty(value = "证件类型")
    @TableField("idCardType")
    private String idCardType;

    @ApiModelProperty(value = "证件号码")
    @TableField("idCardNo")
    private String idCardNo;

    @ApiModelProperty(value = "证件地址")
    @TableField("idCardAddress")
    private String idCardAddress;

    @ApiModelProperty(value = "国籍")
    @TableField("nationality")
    private String nationality;

    @ApiModelProperty(value = "项目索引号")
    @TableField("appidIndex")
    private Integer appidIndex;

    @ApiModelProperty(value = "登录失败次数")
    @TableField("loginFailedCount")
    private Integer loginFailedCount;

    @TableField("createdTime")
    private LocalDateTime createdTime;

    @TableField("updatedTime")
    private LocalDateTime updatedTime;


}
