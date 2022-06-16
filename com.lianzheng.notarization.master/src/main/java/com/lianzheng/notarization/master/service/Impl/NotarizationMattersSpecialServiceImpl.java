package com.lianzheng.notarization.master.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.notarization.master.dao.NotarizationMattersSpecialDao;
import com.lianzheng.notarization.master.dao.UserNotarzationMasterDao;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.entity.NotarizationMattersSpecialEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.service.NotarizationMattersSpecialService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotarizationMattersSpecialServiceImpl extends ServiceImpl<NotarizationMattersSpecialDao,NotarizationMattersSpecialEntity> implements NotarizationMattersSpecialService {

    @Autowired
    private UserNotarzationMasterDao userNotarzationMasterDao;
    @Autowired
    private UserOrderDao userOrderDao;
    @Autowired
    private UserNotarzationMasterService userNotarzationMasterService;
    @Autowired
    private NotarizationMattersSpecialDao notarizationMattersSpecialDao;

    @Override
    public Map<String, Object> getInfoMapParam(String masterId) {
        Map<String,Object> map= userNotarzationMasterDao.getCertificateInfo(masterId);
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterDao.selectById(masterId);
        Map<String, Object> mapOrder = userOrderDao.getOrderInfo(notarzationMasterEntity.getOrderId());
        map.putAll(mapOrder);
        List<NotarizationMattersSpecialEntity> notarizationMattersSpecialEntityList =  notarizationMattersSpecialDao.selectList(
                new QueryWrapper<NotarizationMattersSpecialEntity>().eq("notarizationId",masterId)
        );
        Map<String, Object> mapMattersSpecial = new HashMap<>();
        for(NotarizationMattersSpecialEntity item : notarizationMattersSpecialEntityList){
            String key = item.getEntryKey();
            String value = item.getEntryValue();
            String type = item.getEntryType();
            switch (type){
                case "string":
                    mapMattersSpecial.put(key,value);break;
                default:
                    throw new COREException("未定义该类型",1);
            }
        }
        map.putAll(mapMattersSpecial);
        return map;
    }

}
