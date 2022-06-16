/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.icredit.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.lianzheng.core.auth.mgmt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.core.auth.mgmt.dao.AuditLogDao;
import com.lianzheng.core.auth.mgmt.entity.AuditLogEntity;
import com.lianzheng.core.auth.mgmt.service.AuditLogService;
import com.lianzheng.core.auth.mgmt.utils.PageUtils;
import com.lianzheng.core.auth.mgmt.utils.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogDao, AuditLogEntity> implements AuditLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");

        IPage<AuditLogEntity> page = this.page(
                new Query<AuditLogEntity>().getPage(params),
                new QueryWrapper<AuditLogEntity>().like(StringUtils.isNotBlank(key), "username", key)
        );

        return new PageUtils(page);
    }
}
