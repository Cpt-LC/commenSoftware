package com.lianzheng.notarization.master.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("document")
public class DocumentEntity {

   @TableId
    private String id;
    //相关表名
    private String refererTableName;
   //相关表的主键
    private String refererId;
    //分类code，来自于代码中的值，格式：公证事项/文件分类，比如，GR-HEAD
    private String categoryCode;
    //文件名
    private String fileName;
    //文件大小，单位字节
    private BigDecimal fileSize;
    //文件大小，单位兆（M），便于阅读
    private BigDecimal  fileSizeM;

    private String  fileExt;
    //上传地址，相对路径
    private String uploadedRelativePath;
    //上传地址，绝对路径
    private String uploadedAbsolutePath;
    //文件内容哈希
    private  String  fileHash;
    //创建时间
    private Date createdTime;
    //是否删除
    private int  isDeleted;
    //删除时间
    private Date deletedTime;
    //存储方式：local：本地
    private String storageType;
    //链的哈希
    private String  chainHash;

    private String storePath;

    private String storeGroup;

}
