package com.lianzheng.notarization.driverLicense.service.Impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.config.ShiroUtils;
import com.lianzheng.notarization.driverLicense.dao.UserNotarizationDriverLicenseDao;
import com.lianzheng.notarization.driverLicense.entity.NotarizationDriverLicenseEntity;
import com.lianzheng.notarization.master.dao.UserNotarzationMasterDao;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.form.MapCommonForm;
import com.lianzheng.notarization.master.service.MasterService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.NotSupportedException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("DL")
@DS("h5")
public class DriverLicenseServiceImpl  implements MasterService {
    @Autowired
    private UserNotarizationDriverLicenseDao userNotarizationDriverLicenseDao;
    @Autowired
    private UserNotarzationMasterDao userNotarzationMasterDao;
    @Autowired
    private UserOrderDao userOrderDao;
    @Autowired
    private UserNotarzationMasterService userNotarzationMasterService;


    @Override
    public List<Map<String,Object>> getCertificateInfo(Map<String, Object> param) throws NotSupportedException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Map<String, Object> map  =  userNotarizationDriverLicenseDao.getCertificateInfo(param.get("id").toString());
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterDao.selectById(param.get("id").toString());
        Map<String, Object> mapOrder = userOrderDao.getOrderInfo(notarzationMasterEntity.getOrderId());
        map.putAll(mapOrder);
        List<Map<String,Object>> maplist = userNotarzationMasterService.editFrom(map);

        for (Map<String, Object> group :
                maplist) {
            if(!group.get("group").toString().equals("公证信息")){
                continue;
            }

            for (Map<String, Object> row :
                    (List<Map<String, Object>>) (group.get("rows"))) {

                for (MapCommonForm col :
                        (List<MapCommonForm>) (row.get("columns"))) {
                    //非该公证的字段  跳过
                    if(!col.getTableName().equals("notarization_driver_license")){
                        continue;
                    }

                    String key = col.getKey();
                    switch (key){
                        case "issuingTime":
                        case "issuingAuthority":
                            col.setVisible(true);
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
    @Transactional(rollbackFor = Exception.class)
    public void editCertificateInfo(Map<String, Object> param){
        //更新订单表和主表
        userNotarzationMasterService.editMasterAndOrder(param);
        //更新子表
        NotarizationDriverLicenseEntity notarizationDriverLicenseEntity =  userNotarizationDriverLicenseDao.selectOne(
                new QueryWrapper<NotarizationDriverLicenseEntity>().eq("id",param.get("id").toString()).eq("isDeleted",0)
        );

        //如果不存在新增
        if(notarizationDriverLicenseEntity == null){
            notarizationDriverLicenseEntity =new NotarizationDriverLicenseEntity();
            notarizationDriverLicenseEntity.setId(param.get("id").toString());
            notarizationDriverLicenseEntity.setUpdatedBy(ShiroUtils.getUserId().toString());
            notarizationDriverLicenseEntity.setCreatedBy(ShiroUtils.getUserId().toString());
            notarizationDriverLicenseEntity.update(param);
            notarizationDriverLicenseEntity.setCreatedTime(new Date());
            notarizationDriverLicenseEntity.setUpdatedTime(new Date());
            userNotarizationDriverLicenseDao.insert(notarizationDriverLicenseEntity);
            return;
        }

        notarizationDriverLicenseEntity.update(param);
        notarizationDriverLicenseEntity.setUpdatedTime(new Date());
        notarizationDriverLicenseEntity.setUpdatedBy(ShiroUtils.getUserId().toString());
        userNotarizationDriverLicenseDao.updateById(notarizationDriverLicenseEntity);


    }

    @Override
    public void generatePdf(String id) throws Exception {
        //公证的信息
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(id);
        userNotarzationMasterService.pdfGenerate(notarzationMasterEntity);
    }


    @Override
    public void generatePreparePaper(NotarzationMasterEntity notarzationMasterEntity) throws Exception {
        // todo 生成公证书的双证公证书  后续 做成动态
    }

}
