package com.lianzheng.core.auth.mgmt.service;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.core.auth.mgmt.entity.AuditLogEntity;
import com.lianzheng.core.auth.mgmt.utils.PageUtils;

import java.util.Map;


/**
 * 系统日志
 *
 * @author gang.shen@kedata.com
 */
@DS("mgmt")
public interface AuditLogService extends IService<AuditLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

}
