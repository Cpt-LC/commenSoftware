package com.lianzheng.notarization.master.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.lianzheng.notarization.master.entity.NotarzationAuthCommentEntity;
import com.lianzheng.notarization.master.dao.UserNotarzationAuthCommentDao;
import com.lianzheng.notarization.master.service.UserNotarzationAuthCommentService;
import org.springframework.stereotype.Service;

@Service
@DS("h5")
public class UserNotarzationAuthCommentServiceImpl extends ServiceImpl<UserNotarzationAuthCommentDao,NotarzationAuthCommentEntity> implements UserNotarzationAuthCommentService {

}
