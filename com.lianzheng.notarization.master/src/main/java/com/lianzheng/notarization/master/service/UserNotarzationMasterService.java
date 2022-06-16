package com.lianzheng.notarization.master.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.lianzheng.core.server.PagesUtils;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.form.DocumentForm;
import com.lianzheng.notarization.master.form.MapCommonForm;

import javax.transaction.NotSupportedException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public interface UserNotarzationMasterService extends IService<NotarzationMasterEntity> {
    PagesUtils queryCertificateList(Map<String, Object> params);
    void claimCertificate(NotarzationMasterEntity param);
    void refuseCertificate(Map<String, Object> param);
    void submitCertificate(Map<String, Object> param);
    void directorRefuseCertificate(Map<String, Object> param);
    void directorPassCertificate(Map<String, Object> param);
    void pickUpCertificate(Map<String, Object> param);
    Map<String, List<MapCommonForm>> parseDocuments(List<DocumentForm> documentFormList, Map<String, Object> param);

    /**
     * 生成详情页所需参数
     * @param param
     * @return
     */
    List<Map<String,Object>> editFrom(Map<String,Object> param) throws NotSupportedException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException;


    /**
     * 生成共有的文件
     * @param notarzationMasterEntity 公证信息
     * @throws Exception
     */
    void pdfGenerate(NotarzationMasterEntity notarzationMasterEntity) throws Exception;

    /**
     * 获取询问笔录的公共的参数
     * @param notarzationMasterEntity 公证信息
     * @return
     */
    ConcurrentHashMap<String, Map<String,Object>> getPublicParam(NotarzationMasterEntity notarzationMasterEntity);

    /**
     * 预览生成公证书
     * @param id
     * @throws Exception
     */
    List<MapCommonForm> previewNotarization(String id) throws Exception;

    /**
     * 生成公证书和送达回执
     * @param id
     * @throws Exception
     */
    void generateNotarization(String id) throws Exception;



    /**
     * 生成送达回执供h5调用
     * @param id
     * @throws Exception
     */
    void generateRecipt(String id)throws Exception;

    /**
     * 获取支付明细
     * @param param
     * @return
     */
    List<Map<String,Object>> getPaidDetail(Map<String,Object> param);

    /**
     * 公证员更新公共的信息
     * @param param
     */
    void editMasterAndOrder(Map<String,Object> param);


    /**
     * 生成译文相符
     * @param notarzationMasterEntity
     * @param hashMap
     * @throws Exception
     */
    void  generatePreparePaperCert(NotarzationMasterEntity notarzationMasterEntity,ConcurrentHashMap<String, Map<String,Object>> hashMap) throws Exception;

    /**
     * 是否国外
     * @return
     */
    boolean isForeign(String country);


    /**
     * 生成签字文件所需的一些公共参数
     * @param notarzationMasterEntity
     * @return
     */
    ConcurrentHashMap<String, Map<String, Object>> getCommenHashMap(NotarzationMasterEntity notarzationMasterEntity);

    /**
     * 生成签字文件所需签名   与公共参数分离
     * @param notarzationMasterEntity
     * @return
     */
    ConcurrentHashMap<String, Map<String, Object>> getSignHashMap(NotarzationMasterEntity notarzationMasterEntity);

    /**
     * 判断公证费用是否需要加价
     * @param notarzationMasterEntity
     * @return
     */
    Boolean isAgentP(NotarzationMasterEntity notarzationMasterEntity);

}
