package com.lianzheng.core.auth.mgmt.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianzheng.core.auth.mgmt.entity.SysCaptchaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 验证码
 *
 * @author gang.shen@kedata.com
 */
@Mapper
@Repository
public interface SysCaptchaDao extends BaseMapper<SysCaptchaEntity> {

}
