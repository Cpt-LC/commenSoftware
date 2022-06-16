package com.lianzheng.core.log.curd.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.core.log.curd.dao.SysSpecificLogMapper;
import com.lianzheng.core.log.curd.entity.SysSpecificLogEntity;
import com.lianzheng.core.log.curd.service.SysSpecificLogService;
import org.springframework.stereotype.Service;

@DS("log")
@Service
public class SysSpecificLogServiceImpl  extends ServiceImpl<SysSpecificLogMapper, SysSpecificLogEntity> implements SysSpecificLogService{
}
