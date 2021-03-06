package com.lianzheng.notarization.master.service.Impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.lianzheng.core.auth.mgmt.dao.SysNotarialOfficeDao;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.notarization.master.configParameter.param.TruthParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.core.resource.FileContentUtil;
import com.lianzheng.core.sign.Utils.AesSdk;
import com.lianzheng.core.sign.Utils.FileSdk;
import com.lianzheng.core.sign.Utils.JsonResult;
import com.lianzheng.core.sign.Utils.RsaSdk;
import com.lianzheng.notarization.master.dao.UserDao;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.OrderEntity;
import com.lianzheng.notarization.master.entity.UserEntity;
import com.lianzheng.notarization.master.enums.*;
import com.lianzheng.notarization.master.form.DocumentForm;
import com.lianzheng.notarization.master.form.NotaryInfoFrom;
import com.lianzheng.notarization.master.service.TruthService;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;
import lombok.extern.java.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Service
@DS("h5")
public class TruthServiceImpl implements TruthService{
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private UserOrderServiceImpl userOrderService;
    @Autowired
    private UserNotarzationMasterService userNotarzationMasterService;
    @Autowired
    private UserDocumentService userDocumentService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SysNotarialOfficeDao sysNotarialOfficeDao;

    /**
     * ????????????
     * @param notarzationMasterEntity
     */
    @Override
    public void pushToTruth(NotarzationMasterEntity notarzationMasterEntity) throws Exception{
        pushBaseData(notarzationMasterEntity);//??????????????????
        pushFlowLog(notarzationMasterEntity);//????????????????????????
        pushFlowFile(notarzationMasterEntity);//??????????????????
    }


    /*
    ??????????????????
     */
    @Override
    public void pushBaseData(NotarzationMasterEntity notarzationMasterEntity) throws Exception{
        //????????????  ????????????
        Map<String,Object> param = new HashMap<>();
        //????????? ??????
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());
        NotaryInfoFrom notaryInfoFrom = this.getNotaryInfo(sysUserEntity);

        //????????????
        OrderEntity orderEntity =userOrderService.getById(notarzationMasterEntity.getOrderId());


        //????????????  ????????????????????????????????????
        param.put("zyzbh",notaryInfoFrom.getNotaryId());
        param.put("gzyId",sysUserEntity.getId());
        param.put("gzyxm",sysUserEntity.getRealName());
        param.put("jgmc",notaryInfoFrom.getNotaryOfficeName());
        param.put("jgbm",notaryInfoFrom.getNotaryOfficeId());
        param.put("ywlsh",notarzationMasterEntity.getId());
        param.put("sqsj", DateUtils.format(notarzationMasterEntity.getCreatedTime(),"yyyy-MM-dd HH:mm:ss"));
        param.put("slsj", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        switch (notarzationMasterEntity.getUsedToCountry()){
            case "??????":
                param.put("itemName","????????????");
                param.put("itemCode","01");
                break;
            case "????????????":
                param.put("itemName","????????????");
                param.put("itemCode","05");
                break;
            case "????????????":
                param.put("itemName","????????????");
                param.put("itemCode","06");
                break;
            case "????????????":
                param.put("itemName","????????????");
                param.put("itemCode","07");
                break;
            default:
                param.put("itemName","????????????");
                param.put("itemCode","03");
                break;
        }
        param.put("syly","1012");//????????????????????????
        param.put("jj","0");//????????????
        param.put("mj","0");//????????????
        param.put("syd",notarzationMasterEntity.getUsedToCountry());
        param.put("yt", UserForTruthEnum.getEnumMsg(notarzationMasterEntity.getUsedFor()));
        param.put("sfcjgzs","1");
        param.put("bgqx","01");//??????
        param.put("lczt","05");
        param.put("bzjd","0");
        param.put("gyflfwlx","10");
        param.put("sfyq","1");
        param.put("fwlx","5");

        //????????????????????????????????????
        if(notarzationMasterEntity.getIsAgent()==1){
            UserEntity userEntity = userDao.selectById(notarzationMasterEntity.getUserId());
            List<Map<String,Object>> agentPersons = new ArrayList<>();
            Map<String,Object> agentPerson = new HashMap<>();
            agentPerson.put("natureType","1");//?????????????????????
            agentPerson.put("dsrmc",userEntity.getRealName());
            agentPerson.put("zjlx",IdCardTypeTruthEnum.getEnumMsg(userEntity.getIdCardType()));
            agentPerson.put("zjhm",userEntity.getIdCardNo());
            agentPerson.put("gjdm","??????");
            agentPerson.put("sftz","11");//????????????
            agentPerson.put("lxdz",userEntity.getIdCardAddress());//????????????
            agentPerson.put("lxdh",userEntity.getPhone());
            agentPerson.put("csrq",DateUtils.format(userEntity.getBirth(),"yyyy-MM-dd"));
            agentPerson.put("dsrxb",userEntity.getGender()==1?"1":"2");
            agentPersons.add(agentPerson);
            param.put("agentPersons",agentPersons);
        }



        //???????????????
        List<Map<String,Object>> clientPersons = new ArrayList<>();
        Map<String,Object> clientPerson = new HashMap<>();
        clientPerson.put("natureType", NatureTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getApplicantParty()));
        clientPerson.put("dsrmc",notarzationMasterEntity.getRealName());
        if(notarzationMasterEntity.getApplicantParty().equals("E")){//??????
            clientPerson.put("zzlx",CompanyTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getCompanyType()));
            clientPerson.put("frlb",CompanyTypeTruthEnum.getEnumTruth(notarzationMasterEntity.getCompanyType()));
            clientPerson.put("fddbr",notarzationMasterEntity.getLegalRepresentative());
            clientPerson.put("zjlx","05");
            clientPerson.put("lxdz",notarzationMasterEntity.getCompanyAddress());
            clientPerson.put("csrq",DateUtils.format(notarzationMasterEntity.getRegisterDate(),"yyyy-MM-dd"));
        }
        if(notarzationMasterEntity.getApplicantParty().equals("P")){//??????
            clientPerson.put("dsrxb",notarzationMasterEntity.getGender());
            clientPerson.put("zjlx",IdCardTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getIdCardType()));
            clientPerson.put("gjdm","??????");
            clientPerson.put("sftz","11");//????????????
            clientPerson.put("lxdz",notarzationMasterEntity.getIdCardAddress()!=null?notarzationMasterEntity.getIdCardAddress():"");//????????????
            clientPerson.put("csrq",DateUtils.format(notarzationMasterEntity.getBirth(),"yyyy-MM-dd"));
        }
        clientPerson.put("lxdh",notarzationMasterEntity.getPhone());
        clientPerson.put("zjhm",notarzationMasterEntity.getIdCardNo());
        clientPersons.add(clientPerson);
        param.put("clientPersons",clientPersons);



        //????????????????????????
        List<Map<String,Object>> businessInfos = new ArrayList<>();
        Map<String,Object> businessInfo = new HashMap<>();
        businessInfo.put("notarizationType",NotarizationTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getNotarzationTypeCode()));
        businessInfo.put("itemCharge",(orderEntity.getPaidAmount().divide(new BigDecimal("100"))).toString());
        businessInfo.put("amountCharge",(orderEntity.getPaidAmount().divide(new BigDecimal("100"))).toString());
        businessInfo.put("copy",notarzationMasterEntity.getCopyNumber().toString());
        businessInfo.put("contractMoney","0");
        //????????????????????????????????????????????????
        List<Map<String,Object>> registerPersons = new ArrayList<>();
        Map<String,Object> registerPerson = new HashMap<>();
        registerPerson.put("personType",NatureTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getApplicantParty()));
        registerPerson.put("personName",notarzationMasterEntity.getRealName());
        registerPerson.put("certificateType",notarzationMasterEntity.getIdCardType());
        registerPerson.put("certificateNum",notarzationMasterEntity.getIdCardNo());
        //????????????  ????????????????????????????????????
        if(notarzationMasterEntity.getApplicantParty().equals("E")){
            registerPerson.put("certificateType","????????????????????????");
        }
        registerPersons.add(registerPerson);
        businessInfo.put("registerPersons",registerPersons);
        businessInfos.add(businessInfo);
        if(notarzationMasterEntity.getHasMoreCert().equals("1")){//??????  ?????????
            Map<String,Object> businessInfoCert = new HashMap<>();
            businessInfoCert.put("notarizationType",NotarizationTypeTruthEnum.getEnumMsg("CERT"));
            businessInfoCert.put("itemCharge","0");
            businessInfoCert.put("amountCharge","0");
            businessInfoCert.put("copy",notarzationMasterEntity.getCopyNumber().toString());
            businessInfoCert.put("contractMoney","0");
            //????????????????????????????????????????????????
            List<Map<String,Object>> registerPersonsCert = new ArrayList<>();
            Map<String,Object> registerPersonCert = new HashMap<>();
            registerPersonCert.put("personType",NatureTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getApplicantParty()));
            registerPersonCert.put("personName",notarzationMasterEntity.getRealName());
            registerPersonCert.put("certificateType",notarzationMasterEntity.getIdCardType());
            registerPersonCert.put("certificateNum",notarzationMasterEntity.getIdCardNo());
            //????????????  ????????????????????????????????????
            if(notarzationMasterEntity.getApplicantParty().equals("E")){
                registerPersonCert.put("certificateType","????????????????????????");
            }
            registerPersonsCert.add(registerPersonCert);
            businessInfoCert.put("registerPersons",registerPersonsCert);
            businessInfos.add(businessInfoCert);
        }
        param.put("businessInfos",businessInfos);
        log.info(param.toString());


        //??????????????????
        TruthParam truthParam = ConfigParameterUtil.getTruth("base");
        JsonResult jsonResult = postMessage(truthParam.getBaseUrl() + "/acceptBaseDataByShipin", truthParam.getRequestClientCode(), truthParam.getResponseClientCode(), param);
        if (jsonResult.getCode().equals(0)) {
            log.info("????????????????????????");
            //????????????????????????
            JSONObject jsonObject = JSON.parseObject(jsonResult.getData().toString());
        } else {
            log.info("????????????????????????");
            //??????????????????
            log.info(jsonResult.toString());
        }

    }


    /*
    ??????????????????
     */
    @Override
    public void pushFlowLog(NotarzationMasterEntity notarzationMasterEntity) throws Exception{
        //????????????
        Map<String,Object> param = new HashMap<>();
        //????????? ??????
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());
        NotaryInfoFrom notaryInfoFrom = this.getNotaryInfo(sysUserEntity);

        param.put("registerFileId",notarzationMasterEntity.getId());
        param.put("jgmc",notaryInfoFrom.getNotaryOfficeName());
        param.put("jgbm",notaryInfoFrom.getNotaryOfficeId());
        List<Map<String,Object>> logDetails = new ArrayList<>();//???????????????????????????
        Map<String,Object> logDetail = new HashMap<>();
        logDetail.put("status","01");
        logDetail.put("content","??????");
        logDetail.put("operateTime",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        logDetail.put("operator",notaryInfoFrom.getNotaryName());
        logDetail.put("zyzbh",notaryInfoFrom.getNotaryId());
        logDetails.add(logDetail);
        logDetail = new HashMap<>();
        logDetail.put("status","02");
        logDetail.put("content","??????");
        logDetail.put("operateTime",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        logDetail.put("operator",notaryInfoFrom.getNotaryName());
        logDetail.put("zyzbh",notaryInfoFrom.getNotaryId());
        logDetails.add(logDetail);
        logDetail = new HashMap<>();
        logDetail.put("status","03");
        logDetail.put("content","??????");
        logDetail.put("operateTime",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        logDetail.put("operator",notaryInfoFrom.getNotaryName());
        logDetail.put("zyzbh",notaryInfoFrom.getNotaryId());
        logDetails.add(logDetail);
        logDetail = new HashMap<>();
        logDetail.put("status","05");
        logDetail.put("content","??????");
        logDetail.put("operateTime",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        logDetail.put("operator",notaryInfoFrom.getNotaryName());
        logDetail.put("zyzbh",notaryInfoFrom.getNotaryId());
        logDetails.add(logDetail);
        param.put("logDetails",logDetails);

        log.info(param.toString());

        TruthParam truthParam = ConfigParameterUtil.getTruth("base");
        JsonResult jsonResult = postMessage(truthParam.getBaseUrl() + "/acceptFlowLogByShipin", truthParam.getRequestClientCode(), truthParam.getResponseClientCode(), param);
        if (jsonResult.getCode().equals(0)) {
            log.info("??????????????????????????????");
            //????????????????????????
            JSONObject jsonObject = JSON.parseObject(jsonResult.getData().toString());
        } else {
            log.info("??????????????????????????????");
            //??????????????????
            log.info(jsonResult.toString());
        }

    }

    @Override
    public void pushFlowFile(NotarzationMasterEntity notarzationMasterEntity) throws Exception{
        //????????????
        Map<String,Object> param = new HashMap<>();
        //????????? ??????
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());
        NotaryInfoFrom notaryInfoFrom = this.getNotaryInfo(sysUserEntity);
        param.put("registerFileId",notarzationMasterEntity.getId());
        param.put("jgmc",notaryInfoFrom.getNotaryOfficeName());
        param.put("jgbm",notaryInfoFrom.getNotaryOfficeId());
        List<Map<String,Object>> fileLists = new ArrayList<>();//??????????????????
        Map<String,Object> fileList = new HashMap<>();
        List<DocumentForm> documentFormList =userDocumentService.getlist(notarzationMasterEntity.getId());
        List<DocumentForm> documents =documentFormList.stream().filter(item->item.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTICE.getCode())//?????????????????????
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_PAY_NOTICE.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_QUESTION.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_AC_NOTICE.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_APPLICATION.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_FO_NOTICE.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTICE_ENTRUSTED.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTICE_HANDLE.getCode())
                ||item.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTICE_DECLARATION.getCode())
        ).collect(Collectors.toList());
        String fileId=null;
        //??????????????????
        for(DocumentForm document:documents){
            fileList=new HashMap<>();
            String fileUrl = userDocumentService.getUrl(notarzationMasterEntity.getNotarialOfficeId(),document.getUploadedAbsolutePath());
            fileId=pushFile(fileUrl,"notices/"+document.getFileName()+"_2"+document.getFileExt());
            fileList.put("fileId",fileId);
            fileList.put("status","05");
            fileList.put("fileType", DocumentCategoryTruthCode.getEnumMsg(document.getCategoryCode()));
            fileList.put("fileName",document.getFileName()+document.getFileExt());
            fileList.put("fileSize",document.getFileSize().longValue());
            fileList.put("fileTime",DateUtils.format(document.getCreatedTime(),"yyyy-MM-dd HH:mm:ss"));
            fileList.put("operator",notaryInfoFrom.getNotaryName());
            fileList.put("zyzbh",notaryInfoFrom.getNotaryId());
            fileList.put("fileUrl",fileId);
            fileLists.add(fileList);
        }
        param.put("fileList",fileLists);
        log.info(param.toString());

        //acceptFlowFileByShipinWithUrl
        TruthParam truthParam = ConfigParameterUtil.getTruth("base");
        JsonResult jsonResult = postMessage(truthParam.getBaseUrl() + "/acceptFlowFileByFuqiangWithUrl", truthParam.getRequestClientCode(), truthParam.getResponseClientCode(), param);
        if (jsonResult.getCode().equals(0)) {
            log.info("??????????????????????????????");
            //????????????????????????
            JSONObject jsonObject = JSON.parseObject(jsonResult.getData().toString());
        } else {
            log.info("????????????????????????");
            //??????????????????
            log.info(jsonResult.toString());
        }
    }



    /*
    ??????????????????
     */
    @Override
    public String pushFile(String filePath,String newFilePath) throws Exception{
        //????????????
        FileSdk.encrypt(filePath, newFilePath);

        //????????????
        Map<String, Object> params = new HashMap<>();
        File file = new File(newFilePath);
        params.put("fileName", file.getName());
        params.put("md5", FileSdk.md5(newFilePath));
        params.put("clientId","ks-2022");


        //??????????????????
        TruthParam truthParam = ConfigParameterUtil.getTruth("file");
        JsonResult jsonResult = postMessage(truthParam.getBaseUrl() + "/upload", truthParam.getRequestClientCode(),truthParam.getResponseClientCode(),params, newFilePath);
        String fileId = null;
        if (jsonResult.getCode().equals(0)) {
            log.info("??????????????????");
            //????????????????????????
            JSONObject jsonObject = JSON.parseObject(jsonResult.getData().toString());
            fileId = jsonObject.getString("fileId");
            log.info(fileId);
        } else {
            log.info("??????????????????");
            //??????????????????
            log.info(jsonResult.toString());
        }
        file.delete();
        return fileId;
    }


    /**
     * ??????????????????????????? ??????????????????????????????
     * @param sysUserEntity
     * @return
     */
    public  NotaryInfoFrom getNotaryInfo(SysUserEntity sysUserEntity) throws Exception{
        NotaryInfoFrom notaryInfoFrom = null;
        //?????????????????????????????????????????????
//        Reader reader = FileContentUtil.getStreamReader("/notarySigns/truth-notary-info.csv");
//        Reader reader = new InputStreamReader(new FileInputStream(new File("notarySigns/truth-notary-info.csv")));
//        Iterable<CSVRecord> records = new CSVParser(reader,
//                    CSVFormat.EXCEL.withDelimiter(',')
//                            .withHeader("notaryOfficeName", "notaryOfficeId", "notaryName", "notaryId"));
//        for(CSVRecord record:records){
//            if(record.get("notaryName").equals(sysUserEntity.getRealName())){
//                notaryInfoFrom =new NotaryInfoFrom(record.get("notaryOfficeName"),record.get("notaryOfficeId"),record.get("notaryName"),record.get("notaryId"));
//            }else {
//                continue;
//            }
//        }

        SysNotarialOfficeEntity sysNotarialOfficeEntity =  sysNotarialOfficeDao.selectById(sysUserEntity.getNotarialOfficeId());
        notaryInfoFrom = new NotaryInfoFrom(sysNotarialOfficeEntity.getNotaryOfficeName(),sysNotarialOfficeEntity.getNotaryOfficeNum(),sysUserEntity.getRealName(),sysUserEntity.getNotaryNum());


        return  notaryInfoFrom;
    }




    /**
     * ????????????
     *
     * @param url                ????????????
     * @param requestClientCode  ?????????ID
     * @param responseClientCode ?????????ID
     * @param object             ????????????
     * @param filePath           ????????????
     * @return ????????????????????????
     */
    public static JsonResult postMessage(String url, String requestClientCode, String responseClientCode, Object object, String filePath) {
        // ????????????AES??????
        String secret = AesSdk.generateKey();

        // ???????????????AES??????
        String strJson = JSON.toJSONString(object);
        String encryptBody = AesSdk.encrypt(strJson, secret);

        // ????????????RSA??????
        Map<String, String> title = new HashMap<>(5);
        title.put("requestClientCode", requestClientCode);
        title.put("responseClientCode", responseClientCode);
        title.put("secret", secret);
        TruthParam truthParam = ConfigParameterUtil.getTruth("file");
        String encryptTitle = RsaSdk.encrypt(JSON.toJSONString(title), truthParam.getPublic_Key());

        // ??????????????????????????????????????????json?????????
        Map<String, Object> message = new HashMap<>(5);
        message.put("title", encryptTitle);
        message.put("body", encryptBody);

        // ??????post??????
        String result = post(url, message, filePath);
        JsonResult jsonResult = JSON.parseObject(result, JsonResult.class);
        if (jsonResult.getCode().equals(0) && jsonResult.getData() != null) {
            String messageData = AesSdk.decrypt(jsonResult.getData().toString(), secret);
            jsonResult.setData(messageData);
        }
        return jsonResult;
    }


    public static JsonResult postMessage(String url, String requestClientCode, String responseClientCode, Object object) {
        // ????????????AES??????
        String secret = AesSdk.generateKey();

        // ???????????????AES??????
        String strJson = JSON.toJSONString(object);
        String encryptBody = AesSdk.encrypt(strJson, secret);

        // ????????????RSA??????
        Map<String, String> title = new HashMap<>(5);
        title.put("requestClientCode", requestClientCode);
        title.put("responseClientCode", responseClientCode);
        title.put("secret", secret);
        TruthParam truthParam = ConfigParameterUtil.getTruth("base");
        String encryptTitle = RsaSdk.encrypt(JSON.toJSONString(title), truthParam.getPublic_Key());

        // ??????????????????????????????????????????json?????????
        Map<String, Object> message = new HashMap<>(5);
        message.put("title", encryptTitle);
        message.put("body", encryptBody);
        String json = JSON.toJSONString(message);

        // ??????post??????
        String result = httpPostWithJson(url, json);
        JsonResult jsonResult = JSON.parseObject(result, JsonResult.class);
        if (jsonResult.getCode().equals(0) && jsonResult.getData() != null) {
            String messageData = AesSdk.decrypt(jsonResult.getData().toString(), secret);
            jsonResult.setData(messageData);
        }
        return jsonResult;
    }


    /**
     * HTTP ????????????
     *
     * @param url      ????????????
     * @param param    ???????????????
     * @param filePath ????????????
     * @return ????????????
     */
    public static String post(String url, Map<String, Object> param, String filePath) {
        String result = "";
        HttpResponse response;
        //try-with-resource????????? ???????????????????????????
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            //??????content-type  ?????????????????????????????????????????????????????????????????????
            multipartEntityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
            //???????????????
            multipartEntityBuilder.addBinaryBody("file", new File(filePath));
            multipartEntityBuilder.setCharset(StandardCharsets.UTF_8);

            if (param != null) {
                for (Map.Entry<String, Object> map : param.entrySet()) {
                    multipartEntityBuilder.addTextBody(map.getKey(), map.getValue().toString(), ContentType.APPLICATION_FORM_URLENCODED);
                }
            }

            HttpEntity entity = multipartEntityBuilder.build();
            HttpPost post = new HttpPost(url);
            post.setEntity(entity);
            response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * HTTP JSON??????
     *
     * @param url  ????????????
     * @param json json?????????
     * @return ????????????
     */
    public static String httpPostWithJson(String url, String json) {
        String result = "";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            BasicResponseHandler handler = new BasicResponseHandler();
            // ????????????????????????
            StringEntity entity = new StringEntity(json, "utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            result = httpClient.execute(httpPost, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
