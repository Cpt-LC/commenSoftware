package com.lianzheng.notarization.master.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.lianzheng.core.auth.mgmt.config.ShiroUtils;
import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.notarization.master.dao.UserNotarzationMasterDao;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.OrderEntity;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.enums.NotarzationStatusEnum;
import com.lianzheng.notarization.master.enums.PaymentStatusEnum;
import com.lianzheng.notarization.master.enums.ProcessStatusEnum;
import com.lianzheng.notarization.master.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Service
@DS("h5")
public class UserOrderServiceImpl extends ServiceImpl<UserOrderDao,OrderEntity> implements UserOrderService {
    @Autowired
    private UserOrderDao userOrderDao;
    @Autowired
    private UserNotarzationMasterDao userNotarzationMasterDao;
    @Autowired
    private UserDocumentService userDocumentService;

    @Override
    public OrderEntity getOrderInfo(String orderId) {
        OrderEntity orderEntity = userOrderDao.selectOne(
                new QueryWrapper<OrderEntity>().eq("id", orderId).eq("isDeleted", 0)
        );
        return orderEntity;
    }

    @Override
    public void updateOfflinePayment(String id) throws Exception{
        //更新订单表
        OrderEntity orderEntity = userOrderDao.selectOne(
                new QueryWrapper<OrderEntity>().eq("id", id).eq("isDeleted", 0)
        );
        orderEntity.setPaymentStatus(PaymentStatusEnum.Paid.getCode());//需要更新方法里加
        orderEntity.setPaidAmount(orderEntity.getRealAmount());
        orderEntity.setUpdatedTime(new Date());
        orderEntity.setUpdatedBy(ShiroUtils.getUserId().toString());


        //更新受理状态为待审核
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterDao.selectOne(
                new QueryWrapper<NotarzationMasterEntity>().eq("orderId", id)
        );
        notarzationMasterEntity.setProcessStatus(ProcessStatusEnum.APPROVING.getCode());
        userNotarzationMasterDao.updateById(notarzationMasterEntity);
        userOrderDao.updateById(orderEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAfterPay(Map<String, Object> param) {
        String paymentStatus =param.get("paymentStatus").toString();
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(param.get("id").toString());
        if(!notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGPAYMENT.getCode()) && paymentStatus.equals(PaymentStatusEnum.Paid.getCode())){
            throw  new COREException("非可支付状态",10);
        }
        if(!notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGPAYMENT.getCode()) && paymentStatus.equals(PaymentStatusEnum.Canceled.getCode())){
            throw  new COREException("非可取消状态",10);
        }
        if(!notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.GENERATINGCERT.getCode()) && paymentStatus.equals(PaymentStatusEnum.Refunded.getCode())){
            throw  new COREException("非可退款状态",10);
        }

        OrderEntity orderEntity = userOrderDao.selectOne(
                new QueryWrapper<OrderEntity>().eq("id", notarzationMasterEntity.getOrderId()).eq("isDeleted", 0)
        );
        orderEntity.setPaymentStatus(paymentStatus);



        //支付完成生成签字文件
        if(paymentStatus.equals(PaymentStatusEnum.Paid.getCode())){
            if(param.get("signImg")==null){
                throw new COREException("参数有误,签名照不可为空",8);
            }
            Map<String, Object> signImg =(Map<String, Object>)param.get("signImg");
            userDocumentService.saveDocument((String)signImg.get("url"),(String)signImg.get("fileType"),(String)param.get("id"));//保存文件
            orderEntity.setPaidAmount(orderEntity.getRealAmount());//更新实付金额
            notarzationMasterEntity.setStatus(NotarzationStatusEnum.GENERATINGCERT.getCode());
            notarzationMasterEntity.setProcessStatus(ProcessStatusEnum.APPROVING.getCode());
        }else {
            notarzationMasterEntity.setStatus(NotarzationStatusEnum.CANCELED.getCode());
        }

        userOrderDao.updateById(orderEntity);//更新订单记录
        userNotarzationMasterDao.updateById(notarzationMasterEntity);//更新状态
    }
}
