package com.lianzheng.notarization.master.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.notarization.master.entity.OrderEntity;

import java.util.Map;


public interface UserOrderService extends IService<OrderEntity> {

    /*
   获取详情表所需要的订单表的信息
     */
    OrderEntity getOrderInfo(String orderId);

    void updateOfflinePayment(String id) throws Exception;


    /**
     * 建行推送支付后的信息，更新订单表
     * @param param
     */
    void saveAfterPay(Map<String,Object> param);
}
