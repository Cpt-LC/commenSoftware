package com.lianzheng.h5.service.impl;

import com.lianzheng.h5.entity.Order;
import com.lianzheng.h5.mapper.OrderMapper;
import com.lianzheng.h5.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-19
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
