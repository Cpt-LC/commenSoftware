package com.lianzheng.notarization.tax.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianzheng.notarization.tax.entity.NotarizationTaxEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface UserNotarizationTaxDao extends BaseMapper<NotarizationTaxEntity> {

    Map<String, Object> getCertificateInfo(String id);
}
