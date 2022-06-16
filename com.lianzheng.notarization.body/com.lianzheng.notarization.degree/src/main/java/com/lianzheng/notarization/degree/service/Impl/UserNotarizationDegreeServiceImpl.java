package com.lianzheng.notarization.degree.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.notarization.degree.dao.UserNotarizationDegreeDao;
import com.lianzheng.notarization.degree.entity.NotarizationDegreeEntity;
import com.lianzheng.notarization.degree.service.UserNotarizationDegreeService;
import org.springframework.stereotype.Service;

@Service
@DS("h5")
public class UserNotarizationDegreeServiceImpl extends ServiceImpl<UserNotarizationDegreeDao,NotarizationDegreeEntity> implements UserNotarizationDegreeService {
}
