package com.lianzheng.notarization.master.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;

import com.lianzheng.notarization.master.form.NotarzationMasterForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
@DS("h5")
public interface UserNotarzationMasterDao extends BaseMapper<NotarzationMasterEntity>{

    List<NotarzationMasterForm> queryCertificateList(@Param("param1") Map<String, Object> params, @Param("page") Integer page, @Param("limit") Integer limit);
    int countCertificateList(@Param("param1") Map<String, Object> params);


    //info操作
    Map<String, Object> getCertificateInfo(String id);


    /*
    查询所有公证待支付，订单状态待支付的公证(已作废)1月25日
     */
    List<NotarzationMasterEntity> getPaidNotarization();

    void truncateTable();


    /*
    查询所有需要取消的订单（超时time小时）,状态为待支付和待确认
     */
    void cancelNotarization(int expireTime);


    /*
    查询所有需要取消的订单（离超时time小时，剩余restTime小时）,状态为待支付和待确认
     */
    List<NotarzationMasterEntity> getCancelNotarizationRestTime(@Param("expireTime") int expireTime,@Param("restTime")int restTime);

}
