package com.lianzheng.notarization.master.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.lianzheng.notarization.master.entity.UserEntity;
import com.lianzheng.notarization.master.dao.UserDao;
import com.lianzheng.notarization.master.service.UserService;
import org.springframework.stereotype.Service;

@Service
@DS("h5")
public class UserServiceImpl extends ServiceImpl<UserDao,UserEntity>  implements UserService {
}
