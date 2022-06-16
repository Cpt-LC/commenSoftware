package com.lianzheng.notarization.degree.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianzheng.notarization.degree.entity.NotarizationDegreeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Mapper
@Repository
public interface UserNotarizationDegreeDao extends BaseMapper<NotarizationDegreeEntity> {

    Map<String, Object> getCertificateInfo(String id);
}
