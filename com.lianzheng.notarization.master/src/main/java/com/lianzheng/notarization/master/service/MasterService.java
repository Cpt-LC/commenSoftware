package com.lianzheng.notarization.master.service;

import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.form.MapCommonForm;

import javax.transaction.NotSupportedException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface MasterService {

    List<Map<String,Object>> getCertificateInfo(Map<String,Object> param) throws NotSupportedException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    void editCertificateInfo(Map<String,Object> param);

    /**
     *
     * @param id 公证id
     */
    void generatePdf(String id) throws Exception;
    /**
     * 生成公证书（没有公证编号和盖章签名）
     * @param notarzationMasterEntity
     */
    void generatePreparePaper(NotarzationMasterEntity notarzationMasterEntity) throws Exception;



}
