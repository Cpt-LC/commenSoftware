package com.lianzheng.notarization.master.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.notarization.master.dao.UserNotarzationCertificateDao;
import com.lianzheng.notarization.master.entity.NotarzationCertificateEntity;
import com.lianzheng.notarization.master.service.UserNotarzationCertificateService;
import org.springframework.stereotype.Service;

@Service
@DS("h5")
public class UserNotarzationCertificateServiceImpl extends ServiceImpl<UserNotarzationCertificateDao,NotarzationCertificateEntity> implements UserNotarzationCertificateService {

}
