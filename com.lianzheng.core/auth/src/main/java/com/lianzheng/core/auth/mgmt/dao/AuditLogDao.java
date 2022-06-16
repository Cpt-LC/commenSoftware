package com.lianzheng.core.auth.mgmt.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianzheng.core.auth.mgmt.entity.AuditLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 系统日志
 *
 * @author gang.shen@kedata.com
 */
@Mapper
@Repository
public interface AuditLogDao extends BaseMapper<AuditLogEntity> {

}
