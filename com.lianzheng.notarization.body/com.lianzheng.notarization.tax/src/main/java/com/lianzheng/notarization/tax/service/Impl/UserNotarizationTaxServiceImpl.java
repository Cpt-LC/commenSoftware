package com.lianzheng.notarization.tax.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.notarization.tax.dao.UserNotarizationTaxDao;
import com.lianzheng.notarization.tax.entity.NotarizationTaxEntity;
import com.lianzheng.notarization.tax.service.UserNotarizationTaxService;
import org.springframework.stereotype.Service;

@Service
@DS("h5")
public class UserNotarizationTaxServiceImpl  extends ServiceImpl<UserNotarizationTaxDao,NotarizationTaxEntity> implements UserNotarizationTaxService {
}
