package com.lianzheng.core.auth.mgmt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 系统配置信息
 *
 * @author gang.shen@kedata.com
 */
@Data
@TableName("sys_config")
public class SysConfigEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "参数名不能为空")
    private String paramKey;
    @NotBlank(message = "参数值不能为空")
    private String paramValue;
    private String remark;

}
