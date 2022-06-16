package com.lianzheng.notarization.master.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.core.auth.mgmt.utils.FileTokenUtils;
import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.notarization.master.dao.UserNotarzationMasterDao;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.OrderEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.form.DocumentForm;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@DS("h5")
public class UserNotarzationMasterApiServiceImpl extends ServiceImpl<UserNotarzationMasterDao, NotarzationMasterEntity> implements UserNotarzationMasterApiService {
    @Autowired
    private  UserNotarzationMasterDao userNotarzationMasterDao;
    @Autowired
    private UserOrderDao userOrderDao;
    @Autowired
    private UserDocumentService userDocumentService;
    @Autowired
    private FileTokenUtils fileTokenUtils;
    @Override
    public Map<String,Object> getMasterInfo(Map<String,Object> param){
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(param.get("id").toString());
        if(notarzationMasterEntity==null){
            throw new COREException("数据不存在",7);
        }
        OrderEntity orderEntity = userOrderDao.selectOne(
                new QueryWrapper<OrderEntity>().eq("id", notarzationMasterEntity.getOrderId()).eq("isDeleted", 0)
        );
        Map<String,Object> data = new HashMap<>();
        data.put("orderNo",notarzationMasterEntity.getOrderId());
        data.put("realAmount",orderEntity.getRealAmount());
        data.put("status",notarzationMasterEntity.getStatus());
        List<Map<String,Object>> files = new ArrayList<>();
        List<DocumentForm> documentFormList =userDocumentService.getlist(notarzationMasterEntity.getId());
        List<DocumentForm> documents =documentFormList.stream().filter(item->item.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTICE.getCode())//过滤需要的数据
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_PAY_NOTICE.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_QUESTION.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_AC_NOTICE.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_APPLICATION.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_FO_NOTICE.getCode())
        ).collect(Collectors.toList());
        for(DocumentForm document:documents){
            Map<String,Object> file = new HashMap<>();
            file.put("fileType",document.getCategoryCode());
            String token = fileTokenUtils.fileToken(document.getFileName());
            file.put("url",document.getUploadedAbsolutePath()+"?token="+token);
            files.add(file);
        }
        data.put("authComment",notarzationMasterEntity.getAuthComment());
        data.put("files",files);
        return data;
    }
}
