package com.lianzheng.notarization.driverLicense.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianzheng.notarization.driverLicense.entity.NotarizationDriverLicenseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Mapper
@Repository
public interface UserNotarizationDriverLicenseDao  extends BaseMapper<NotarizationDriverLicenseEntity> {
    Map<String, Object> getCertificateInfo(String id);
}
