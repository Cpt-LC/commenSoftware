package com.lianzheng.h5.service.impl;

import com.lianzheng.h5.entity.User;
import com.lianzheng.h5.mapper.UserMapper;
import com.lianzheng.h5.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表。可兼容多个项目用户数据 服务实现类
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
