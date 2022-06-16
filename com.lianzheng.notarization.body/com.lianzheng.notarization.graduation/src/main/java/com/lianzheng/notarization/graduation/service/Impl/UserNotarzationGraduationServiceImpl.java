package com.lianzheng.notarization.graduation.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.lianzheng.notarization.graduation.dao.UserNotarzationGraduationDao;
import com.lianzheng.notarization.graduation.entity.NotarzationGraduationEntity;
import com.lianzheng.notarization.graduation.service.UserNotarzationGraduationService;
import org.springframework.stereotype.Service;

@Service
@DS("h5")
public class UserNotarzationGraduationServiceImpl extends ServiceImpl<UserNotarzationGraduationDao,NotarzationGraduationEntity> implements UserNotarzationGraduationService {
}
