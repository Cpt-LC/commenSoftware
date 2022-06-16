package com.lianzheng.h5.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.h5.entity.SysNotarialOfficeEntity;
import com.lianzheng.h5.mapper.SysNotarialOfficeMapper;
import com.lianzheng.h5.service.SysNotarialOfficeService;
import org.springframework.stereotype.Service;



/**
 * 公证处管理
 *
 * @author lxk
 **/
@Service
@DS("mgmt")
public class SysNotarialOfficeServiceImpl extends ServiceImpl<SysNotarialOfficeMapper, SysNotarialOfficeEntity> implements SysNotarialOfficeService {



}
