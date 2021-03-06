package com.lianzheng.notarization.purchaseHouse.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.config.ShiroUtils;
import com.lianzheng.notarization.master.dao.NotarizationMattersSpecialDao;
import com.lianzheng.notarization.master.dao.UserNotarzationMasterDao;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.entity.NotarizationMattersSpecialEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.enums.RoomTypeEnum;
import com.lianzheng.notarization.master.form.MapCommonForm;
import com.lianzheng.notarization.master.service.MasterService;
import com.lianzheng.notarization.master.service.NotarizationMattersSpecialService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;
import com.lianzheng.notarization.purchaseHouse.entity.PurchaseHouseEntity;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.NotSupportedException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service("WTMFB01")
@DS("h5")
public class UserNotarizationPurchaseHouseServiceImpl implements MasterService {
    @Autowired
    private UserNotarzationMasterDao userNotarzationMasterDao;
    @Autowired
    private UserOrderDao userOrderDao;
    @Autowired
    private UserNotarzationMasterService userNotarzationMasterService;
    @Autowired
    private NotarizationMattersSpecialDao notarizationMattersSpecialDao;
    @Autowired
    private NotarizationMattersSpecialService notarizationMattersSpecialService;

    @Override
    public List<Map<String, Object>> getCertificateInfo(Map<String, Object> param) throws NotSupportedException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Map<String, Object> map = notarizationMattersSpecialService.getInfoMapParam(param.get("id").toString());
        List<Map<String,Object>> maplist = userNotarzationMasterService.editFrom(map);

        String roomType = (String)map.get("roomType");
        for (Map<String, Object> group :
                maplist) {
            if(!group.get("group").toString().equals("????????????")){
                continue;
            }

            for (Map<String, Object> row :
                    (List<Map<String, Object>>) (group.get("rows"))) {

                for (MapCommonForm col :
                        (List<MapCommonForm>) (row.get("columns"))) {
                    //?????????????????????  ??????
                    if(!col.getTableName().equals("notarization_matters_special")){
                        continue;
                    }

                    String key = col.getKey();
                    switch (key){
                        case "roomType":
                        case "trusteeName":
                        case "trusteeGender":
                        case "trusteeBirthday":
                        case "trusteeIdNum":
                        case "roomAddress":
                        case "trusteeRelation":
                            col.setVisible(true);
                            break;
                        case "ownershipCertificate":
                        case "landUseCertificate":
                            if(roomType.equals(RoomTypeEnum.OLD.getCode())){
                                col.setVisible(true);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        return maplist;
    }

    @Override
    @SneakyThrows
    public void editCertificateInfo(Map<String, Object> param) {
        //????????????????????????
        userNotarzationMasterService.editMasterAndOrder(param);

        //??????????????????
        String notarizationId = param.get("id").toString();
        String notarizationType = param.get("notarzationTypeCode").toString();
        String userId = ShiroUtils.getUserId().toString();
        PurchaseHouseEntity purchaseHouseEntity = JSONObject.parseObject(JSONObject.toJSONString(param),PurchaseHouseEntity.class);
        System.out.println(purchaseHouseEntity.toString());
        List<NotarizationMattersSpecialEntity> mattersSpecialList = new ArrayList<>();
        for (Field field : purchaseHouseEntity.getClass().getDeclaredFields()){
            String uuid =UUID.randomUUID().toString();
            field.setAccessible(true);
            Object value = field.get(purchaseHouseEntity);
            if (Objects.nonNull(value)){
                Date date =new Date();
                NotarizationMattersSpecialEntity mattersSpecialEntity = new NotarizationMattersSpecialEntity(uuid,notarizationId,notarizationType,field.getName(),value.toString(),"string",date,date,userId,userId);
                mattersSpecialList.add(mattersSpecialEntity);
            }
        }
        notarizationMattersSpecialService.remove(
                new QueryWrapper<NotarizationMattersSpecialEntity>().eq("notarizationId",notarizationId)
        );
        if(CollectionUtil.isNotEmpty(mattersSpecialList)){
            notarizationMattersSpecialService.saveBatch(mattersSpecialList);
        }



    }

    @Override
    public void generatePdf(String id) throws Exception {
        //???????????????
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(id);
        userNotarzationMasterService.pdfGenerate(notarzationMasterEntity);
    }

    @Override
    public void generatePreparePaper(NotarzationMasterEntity notarzationMasterEntity) throws Exception {
        // todo ?????????????????????????????????  ?????? ????????????
    }
}
