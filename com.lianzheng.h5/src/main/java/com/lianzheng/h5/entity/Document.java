package com.lianzheng.h5.entity;

import java.math.BigDecimal;
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
 * 上传文件
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("document")
@ApiModel(value="Document对象", description="上传文件")
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty(value = "相关表名")
    @TableField("refererTableName")
    private String refererTableName;

    @ApiModelProperty(value = "相关表的主键")
    @TableField("refererId")
    private String refererId;

    @ApiModelProperty(value = "分类code，来自于代码中的值，格式：公证事项/文件分类，比如，GR-HEAD")
    @TableField("categoryCode")
    private String categoryCode;

    @TableField("fileName")
    private String fileName;

    @ApiModelProperty(value = "文件大小，单位字节")
    @TableField("fileSize")
    private Long fileSize;

    @ApiModelProperty(value = "文件大小，单位兆（M），便于阅读")
    @TableField("fileSizeM")
    private BigDecimal fileSizeM;

    @TableField("fileExt")
    private String fileExt;

    @ApiModelProperty(value = "上传地址，相对路径")
    @TableField("uploadedRelativePath")
    private String uploadedRelativePath;

    @ApiModelProperty(value = "上传地址，绝对路径")
    @TableField("uploadedAbsolutePath")
    private String uploadedAbsolutePath;

    @ApiModelProperty(value = "文件内容哈希")
    @TableField("fileHash")
    private String fileHash;

    @TableField("createdTime")
    private LocalDateTime createdTime;

    @TableField("isDeleted")
    private Integer isDeleted;

    @TableField("deletedTime")
    private LocalDateTime deletedTime;

    @ApiModelProperty(value = "存储方式：local：本地")
    @TableField("storageType")
    private String storageType;

    @ApiModelProperty(value = "链的哈希")
    @TableField("chainHash")
    private String chainHash;

    @TableField("storePath")
    private String storePath;

    @TableField("storeGroup")
    private String storeGroup;


}
