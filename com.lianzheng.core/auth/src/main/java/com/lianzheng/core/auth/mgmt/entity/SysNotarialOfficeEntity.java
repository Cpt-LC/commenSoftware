package com.lianzheng.core.auth.mgmt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;

/**
 * 公证处管理
 *
 * @author lixinke
 **/
@Data
@TableName("sys_notarial_office")
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class SysNotarialOfficeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公证处名称
     */
    private String notaryOfficeName;

    /**
     * 公章地址
     */
    private String sealUrl;

    /**
     * 公证处url
     */
    private String baseUrl;

    /**
     * 内网服务器地址
     */
    private String insideIp;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 公证处求真编号
     */
    private String notaryOfficeNum;

    /**
     * 自取接受地址
     */
    private String receiveAddress;

    /**
     * 状态
     */
    private Integer flag;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 创建者
     */
    private Long createdBy;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    /**
     * 更新者
     */
    private Long updatedBy;

    /**
     * 页码
     */
    @TableField(exist = false)
    private Integer pageNo;

    /**
     * 每页条数
     */
    @TableField(exist = false)
    private Integer pageSize;

    /**
     * 上传的文件
     */
    @TableField(exist = false)
    private MultipartFile officialSeal;

}
