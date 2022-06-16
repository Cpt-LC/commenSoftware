package com.lianzheng.notarization.master.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;


import com.lianzheng.notarization.master.entity.NotarzationAuthCommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserNotarzationAuthCommentDao extends BaseMapper<NotarzationAuthCommentEntity>{
}
