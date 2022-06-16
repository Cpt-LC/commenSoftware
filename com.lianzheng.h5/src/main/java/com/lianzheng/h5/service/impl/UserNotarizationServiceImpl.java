package com.lianzheng.h5.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.h5.entity.UserNotarization;
import com.lianzheng.h5.mapper.UserNotarizationMapper;
import com.lianzheng.h5.service.IUserNotarizationService;
import org.springframework.stereotype.Service;

@Service
public class UserNotarizationServiceImpl extends ServiceImpl<UserNotarizationMapper,UserNotarization> implements IUserNotarizationService {
}
