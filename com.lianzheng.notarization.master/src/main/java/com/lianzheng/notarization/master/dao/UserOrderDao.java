package com.lianzheng.notarization.master.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;


import com.lianzheng.notarization.master.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface UserOrderDao extends BaseMapper<OrderEntity> {
    Map<String, Object> getOrderInfo(@Param("orderId") String orderId);
}
