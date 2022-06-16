package com.lianzheng.notarization.graduation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;


import com.lianzheng.notarization.graduation.entity.NotarzationGraduationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface UserNotarzationGraduationDao extends BaseMapper<NotarzationGraduationEntity> {
    Map<String, Object> getCertificateInfo(String id);
}
