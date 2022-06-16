package com.lianzheng.core.log.curd.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.core.log.curd.dao.LogMapper;
import com.lianzheng.core.log.curd.entity.LogEntity;
import com.lianzheng.core.log.curd.service.LogService;
import org.springframework.stereotype.Service;

@DS("log")
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogEntity>  implements LogService {

}
