package com.lianzheng.notarization.driverLicense.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.notarization.driverLicense.dao.UserNotarizationDriverLicenseDao;
import com.lianzheng.notarization.driverLicense.entity.NotarizationDriverLicenseEntity;
import com.lianzheng.notarization.driverLicense.service.UserNotarizationDriverLicenseService;
import org.springframework.stereotype.Service;

@Service
@DS("h5")
public class UserNotarizationDriverLicenseServiceImpl extends ServiceImpl<UserNotarizationDriverLicenseDao,NotarizationDriverLicenseEntity> implements UserNotarizationDriverLicenseService {
}
