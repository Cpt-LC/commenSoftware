package com.lianzheng.notarization.master.service;

import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;

import java.util.Map;

public interface TruthService {

   /**
    * 推送日志基础信息接口
    * @param notarzationMasterEntity
    * @throws Exception
    */
   void pushBaseData(NotarzationMasterEntity notarzationMasterEntity) throws Exception;

   /**
    * 上传文件并获得文件唯一标识
    * @param filePath
    * @param newFilePath
    * @return
    * @throws Exception
    */
   String pushFile(String filePath,String newFilePath) throws Exception;


   /**
    * 推送流程日志
    * @param notarzationMasterEntity
    * @throws Exception
    */
   void pushFlowLog(NotarzationMasterEntity notarzationMasterEntity) throws Exception;

   /**
    * 推送过程文件
    * @param notarzationMasterEntity
    * @throws Exception
    */
   void pushFlowFile(NotarzationMasterEntity notarzationMasterEntity) throws Exception;


   /**
    * 推送求真数据
    * @param notarzationMasterEntity
    */
   void pushToTruth(NotarzationMasterEntity notarzationMasterEntity)  throws Exception;
}
