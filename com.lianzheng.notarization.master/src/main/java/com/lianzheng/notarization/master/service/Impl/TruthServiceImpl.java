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
     * 推送数据
     * @param notarzationMasterEntity
     */
    @Override
    public void pushToTruth(NotarzationMasterEntity notarzationMasterEntity) throws Exception{
        pushBaseData(notarzationMasterEntity);//推送基础数据
        pushFlowLog(notarzationMasterEntity);//推送流程日志数据
        pushFlowFile(notarzationMasterEntity);//推送流程文件
    }


    /*
    推送基础数据
     */
    @Override
    public void pushBaseData(NotarzationMasterEntity notarzationMasterEntity) throws Exception{
        //调用参数  基本信息
        Map<String,Object> param = new HashMap<>();
        //公证员 信息
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());
        NotaryInfoFrom notaryInfoFrom = this.getNotaryInfo(sysUserEntity);

        //订单信息
        OrderEntity orderEntity =userOrderService.getById(notarzationMasterEntity.getOrderId());


        //封装参数  参考外部文件求真接口文件
        param.put("zyzbh",notaryInfoFrom.getNotaryId());
        param.put("gzyId",sysUserEntity.getId());
        param.put("gzyxm",sysUserEntity.getRealName());
        param.put("jgmc",notaryInfoFrom.getNotaryOfficeName());
        param.put("jgbm",notaryInfoFrom.getNotaryOfficeId());
        param.put("ywlsh",notarzationMasterEntity.getId());
        param.put("sqsj", DateUtils.format(notarzationMasterEntity.getCreatedTime(),"yyyy-MM-dd HH:mm:ss"));
        param.put("slsj", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        switch (notarzationMasterEntity.getUsedToCountry()){
            case "中国":
                param.put("itemName","国内民事");
                param.put("itemCode","01");
                break;
            case "中国香港":
                param.put("itemName","涉港公证");
                param.put("itemCode","05");
                break;
            case "中国澳门":
                param.put("itemName","涉澳公证");
                param.put("itemCode","06");
                break;
            case "中国台湾":
                param.put("itemName","涉台公证");
                param.put("itemCode","07");
                break;
            default:
                param.put("itemName","涉外民事");
                param.put("itemCode","03");
                break;
        }
        param.put("syly","1012");//领域默认上报其他
        param.put("jj","0");//默认值否
        param.put("mj","0");//默认值否
        param.put("syd",notarzationMasterEntity.getUsedToCountry());
        param.put("yt", UserForTruthEnum.getEnumMsg(notarzationMasterEntity.getUsedFor()));
        param.put("sfcjgzs","1");
        param.put("bgqx","01");//短期
        param.put("lczt","05");
        param.put("bzjd","0");
        param.put("gyflfwlx","10");
        param.put("sfyq","1");
        param.put("fwlx","5");

        //代理的公证添加代理人信息
        if(notarzationMasterEntity.getIsAgent()==1){
            UserEntity userEntity = userDao.selectById(notarzationMasterEntity.getUserId());
            List<Map<String,Object>> agentPersons = new ArrayList<>();
            Map<String,Object> agentPerson = new HashMap<>();
            agentPerson.put("natureType","1");//目前全是自然人
            agentPerson.put("dsrmc",userEntity.getRealName());
            agentPerson.put("zjlx",IdCardTypeTruthEnum.getEnumMsg(userEntity.getIdCardType()));
            agentPerson.put("zjhm",userEntity.getIdCardNo());
            agentPerson.put("gjdm","中国");
            agentPerson.put("sftz","11");//默认其他
            agentPerson.put("lxdz",userEntity.getIdCardAddress());//默认其他
            agentPerson.put("lxdh",userEntity.getPhone());
            agentPerson.put("csrq",DateUtils.format(userEntity.getBirth(),"yyyy-MM-dd"));
            agentPerson.put("dsrxb",userEntity.getGender()==1?"1":"2");
            agentPersons.add(agentPerson);
            param.put("agentPersons",agentPersons);
        }



        //当事人信息
        List<Map<String,Object>> clientPersons = new ArrayList<>();
        Map<String,Object> clientPerson = new HashMap<>();
        clientPerson.put("natureType", NatureTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getApplicantParty()));
        clientPerson.put("dsrmc",notarzationMasterEntity.getRealName());
        if(notarzationMasterEntity.getApplicantParty().equals("E")){//企业
            clientPerson.put("zzlx",CompanyTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getCompanyType()));
            clientPerson.put("frlb",CompanyTypeTruthEnum.getEnumTruth(notarzationMasterEntity.getCompanyType()));
            clientPerson.put("fddbr",notarzationMasterEntity.getLegalRepresentative());
            clientPerson.put("zjlx","05");
            clientPerson.put("lxdz",notarzationMasterEntity.getCompanyAddress());
            clientPerson.put("csrq",DateUtils.format(notarzationMasterEntity.getRegisterDate(),"yyyy-MM-dd"));
        }
        if(notarzationMasterEntity.getApplicantParty().equals("P")){//个人
            clientPerson.put("dsrxb",notarzationMasterEntity.getGender());
            clientPerson.put("zjlx",IdCardTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getIdCardType()));
            clientPerson.put("gjdm","中国");
            clientPerson.put("sftz","11");//默认其他
            clientPerson.put("lxdz",notarzationMasterEntity.getIdCardAddress()!=null?notarzationMasterEntity.getIdCardAddress():"");//默认其他
            clientPerson.put("csrq",DateUtils.format(notarzationMasterEntity.getBirth(),"yyyy-MM-dd"));
        }
        clientPerson.put("lxdh",notarzationMasterEntity.getPhone());
        clientPerson.put("zjhm",notarzationMasterEntity.getIdCardNo());
        clientPersons.add(clientPerson);
        param.put("clientPersons",clientPersons);



        //公证业务基本信息
        List<Map<String,Object>> businessInfos = new ArrayList<>();
        Map<String,Object> businessInfo = new HashMap<>();
        businessInfo.put("notarizationType",NotarizationTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getNotarzationTypeCode()));
        businessInfo.put("itemCharge",(orderEntity.getPaidAmount().divide(new BigDecimal("100"))).toString());
        businessInfo.put("amountCharge",(orderEntity.getPaidAmount().divide(new BigDecimal("100"))).toString());
        businessInfo.put("copy",notarzationMasterEntity.getCopyNumber().toString());
        businessInfo.put("contractMoney","0");
        //向公证业务基本信息加入当事人信息
        List<Map<String,Object>> registerPersons = new ArrayList<>();
        Map<String,Object> registerPerson = new HashMap<>();
        registerPerson.put("personType",NatureTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getApplicantParty()));
        registerPerson.put("personName",notarzationMasterEntity.getRealName());
        registerPerson.put("certificateType",notarzationMasterEntity.getIdCardType());
        registerPerson.put("certificateNum",notarzationMasterEntity.getIdCardNo());
        //如果企业  类型置为社会统一信用代码
        if(notarzationMasterEntity.getApplicantParty().equals("E")){
            registerPerson.put("certificateType","社会统一信用代码");
        }
        registerPersons.add(registerPerson);
        businessInfo.put("registerPersons",registerPersons);
        businessInfos.add(businessInfo);
        if(notarzationMasterEntity.getHasMoreCert().equals("1")){//双证  双业务
            Map<String,Object> businessInfoCert = new HashMap<>();
            businessInfoCert.put("notarizationType",NotarizationTypeTruthEnum.getEnumMsg("CERT"));
            businessInfoCert.put("itemCharge","0");
            businessInfoCert.put("amountCharge","0");
            businessInfoCert.put("copy",notarzationMasterEntity.getCopyNumber().toString());
            businessInfoCert.put("contractMoney","0");
            //向公证业务基本信息加入当事人信息
            List<Map<String,Object>> registerPersonsCert = new ArrayList<>();
            Map<String,Object> registerPersonCert = new HashMap<>();
            registerPersonCert.put("personType",NatureTypeTruthEnum.getEnumMsg(notarzationMasterEntity.getApplicantParty()));
            registerPersonCert.put("personName",notarzationMasterEntity.getRealName());
            registerPersonCert.put("certificateType",notarzationMasterEntity.getIdCardType());
            registerPersonCert.put("certificateNum",notarzationMasterEntity.getIdCardNo());
            //如果企业  类型置为社会统一信用代码
            if(notarzationMasterEntity.getApplicantParty().equals("E")){
                registerPersonCert.put("certificateType","社会统一信用代码");
            }
            registerPersonsCert.add(registerPersonCert);
            businessInfoCert.put("registerPersons",registerPersonsCert);
            businessInfos.add(businessInfoCert);
        }
        param.put("businessInfos",businessInfos);
        log.info(param.toString());


        //调用接口上传
        TruthParam truthParam = ConfigParameterUtil.getTruth("base");
        JsonResult jsonResult = postMessage(truthParam.getBaseUrl() + "/acceptBaseDataByShipin", truthParam.getRequestClientCode(), truthParam.getResponseClientCode(), param);
        if (jsonResult.getCode().equals(0)) {
            log.info("基础数据上传成功");
            //打印文件唯一标识
            JSONObject jsonObject = JSON.parseObject(jsonResult.getData().toString());
        } else {
            log.info("基础数据上传失败");
            //打印错误信息
            log.info(jsonResult.toString());
        }

    }


    /*
    推送操作日志
     */
    @Override
    public void pushFlowLog(NotarzationMasterEntity notarzationMasterEntity) throws Exception{
        //调用参数
        Map<String,Object> param = new HashMap<>();
        //公证员 信息
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());
        NotaryInfoFrom notaryInfoFrom = this.getNotaryInfo(sysUserEntity);

        param.put("registerFileId",notarzationMasterEntity.getId());
        param.put("jgmc",notaryInfoFrom.getNotaryOfficeName());
        param.put("jgbm",notaryInfoFrom.getNotaryOfficeId());
        List<Map<String,Object>> logDetails = new ArrayList<>();//加入四个状态的日志
        Map<String,Object> logDetail = new HashMap<>();
        logDetail.put("status","01");
        logDetail.put("content","申请");
        logDetail.put("operateTime",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        logDetail.put("operator",notaryInfoFrom.getNotaryName());
        logDetail.put("zyzbh",notaryInfoFrom.getNotaryId());
        logDetails.add(logDetail);
        logDetail = new HashMap<>();
        logDetail.put("status","02");
        logDetail.put("content","初核");
        logDetail.put("operateTime",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        logDetail.put("operator",notaryInfoFrom.getNotaryName());
        logDetail.put("zyzbh",notaryInfoFrom.getNotaryId());
        logDetails.add(logDetail);
        logDetail = new HashMap<>();
        logDetail.put("status","03");
        logDetail.put("content","受理");
        logDetail.put("operateTime",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        logDetail.put("operator",notaryInfoFrom.getNotaryName());
        logDetail.put("zyzbh",notaryInfoFrom.getNotaryId());
        logDetails.add(logDetail);
        logDetail = new HashMap<>();
        logDetail.put("status","05");
        logDetail.put("content","审查");
        logDetail.put("operateTime",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        logDetail.put("operator",notaryInfoFrom.getNotaryName());
        logDetail.put("zyzbh",notaryInfoFrom.getNotaryId());
        logDetails.add(logDetail);
        param.put("logDetails",logDetails);

        log.info(param.toString());

        TruthParam truthParam = ConfigParameterUtil.getTruth("base");
        JsonResult jsonResult = postMessage(truthParam.getBaseUrl() + "/acceptFlowLogByShipin", truthParam.getRequestClientCode(), truthParam.getResponseClientCode(), param);
        if (jsonResult.getCode().equals(0)) {
            log.info("流程日志数据上传成功");
            //打印文件唯一标识
            JSONObject jsonObject = JSON.parseObject(jsonResult.getData().toString());
        } else {
            log.info("流程日志数据上传失败");
            //打印错误信息
            log.info(jsonResult.toString());
        }

    }

    @Override
    public void pushFlowFile(NotarzationMasterEntity notarzationMasterEntity) throws Exception{
        //调用参数
        Map<String,Object> param = new HashMap<>();
        //公证员 信息
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());
        NotaryInfoFrom notaryInfoFrom = this.getNotaryInfo(sysUserEntity);
        param.put("registerFileId",notarzationMasterEntity.getId());
        param.put("jgmc",notaryInfoFrom.getNotaryOfficeName());
        param.put("jgbm",notaryInfoFrom.getNotaryOfficeId());
        List<Map<String,Object>> fileLists = new ArrayList<>();//加入过程文件
        Map<String,Object> fileList = new HashMap<>();
        List<DocumentForm> documentFormList =userDocumentService.getlist(notarzationMasterEntity.getId());
        List<DocumentForm> documents =documentFormList.stream().filter(item->item.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTICE.getCode())//过滤需要的数据
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
        //封装文件参数
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
            log.info("过程文件数据上传成功");
            //打印文件唯一标识
            JSONObject jsonObject = JSON.parseObject(jsonResult.getData().toString());
        } else {
            log.info("过程文件上传失败");
            //打印错误信息
            log.info(jsonResult.toString());
        }
    }



    /*
    推送过程文件
     */
    @Override
    public String pushFile(String filePath,String newFilePath) throws Exception{
        //文件加密
        FileSdk.encrypt(filePath, newFilePath);

        //参数封装
        Map<String, Object> params = new HashMap<>();
        File file = new File(newFilePath);
        params.put("fileName", file.getName());
        params.put("md5", FileSdk.md5(newFilePath));
        params.put("clientId","ks-2022");


        //调用接口上传
        TruthParam truthParam = ConfigParameterUtil.getTruth("file");
        JsonResult jsonResult = postMessage(truthParam.getBaseUrl() + "/upload", truthParam.getRequestClientCode(),truthParam.getResponseClientCode(),params, newFilePath);
        String fileId = null;
        if (jsonResult.getCode().equals(0)) {
            log.info("文件上传成功");
            //打印文件唯一标识
            JSONObject jsonObject = JSON.parseObject(jsonResult.getData().toString());
            fileId = jsonObject.getString("fileId");
            log.info(fileId);
        } else {
            log.info("文件上传失败");
            //打印错误信息
            log.info(jsonResult.toString());
        }
        file.delete();
        return fileId;
    }


    /**
     * 通过公证员信息获取 求真所需的公证处信息
     * @param sysUserEntity
     * @return
     */
    public  NotaryInfoFrom getNotaryInfo(SysUserEntity sysUserEntity) throws Exception{
        NotaryInfoFrom notaryInfoFrom = null;
        //读取表格文件从中选择对应的数据
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
     * 发送消息
     *
     * @param url                消息方法
     * @param requestClientCode  请求方ID
     * @param responseClientCode 接收方ID
     * @param object             消息参数
     * @param filePath           文件附件
     * @return 平台统一返回对象
     */
    public static JsonResult postMessage(String url, String requestClientCode, String responseClientCode, Object object, String filePath) {
        // 随机生成AES密钥
        String secret = AesSdk.generateKey();

        // 消息内容，AES加密
        String strJson = JSON.toJSONString(object);
        String encryptBody = AesSdk.encrypt(strJson, secret);

        // 消息头，RSA加密
        Map<String, String> title = new HashMap<>(5);
        title.put("requestClientCode", requestClientCode);
        title.put("responseClientCode", responseClientCode);
        title.put("secret", secret);
        TruthParam truthParam = ConfigParameterUtil.getTruth("file");
        String encryptTitle = RsaSdk.encrypt(JSON.toJSONString(title), truthParam.getPublic_Key());

        // 封装消息头和消息内容，转换位json字符串
        Map<String, Object> message = new HashMap<>(5);
        message.put("title", encryptTitle);
        message.put("body", encryptBody);

        // 调用post方法
        String result = post(url, message, filePath);
        JsonResult jsonResult = JSON.parseObject(result, JsonResult.class);
        if (jsonResult.getCode().equals(0) && jsonResult.getData() != null) {
            String messageData = AesSdk.decrypt(jsonResult.getData().toString(), secret);
            jsonResult.setData(messageData);
        }
        return jsonResult;
    }


    public static JsonResult postMessage(String url, String requestClientCode, String responseClientCode, Object object) {
        // 随机生成AES密钥
        String secret = AesSdk.generateKey();

        // 消息内容，AES加密
        String strJson = JSON.toJSONString(object);
        String encryptBody = AesSdk.encrypt(strJson, secret);

        // 消息头，RSA加密
        Map<String, String> title = new HashMap<>(5);
        title.put("requestClientCode", requestClientCode);
        title.put("responseClientCode", responseClientCode);
        title.put("secret", secret);
        TruthParam truthParam = ConfigParameterUtil.getTruth("base");
        String encryptTitle = RsaSdk.encrypt(JSON.toJSONString(title), truthParam.getPublic_Key());

        // 封装消息头和消息内容，转换位json字符串
        Map<String, Object> message = new HashMap<>(5);
        message.put("title", encryptTitle);
        message.put("body", encryptBody);
        String json = JSON.toJSONString(message);

        // 调用post方法
        String result = httpPostWithJson(url, json);
        JsonResult jsonResult = JSON.parseObject(result, JsonResult.class);
        if (jsonResult.getCode().equals(0) && jsonResult.getData() != null) {
            String messageData = AesSdk.decrypt(jsonResult.getData().toString(), secret);
            jsonResult.setData(messageData);
        }
        return jsonResult;
    }


    /**
     * HTTP 表单上传
     *
     * @param url      服务路径
     * @param param    参数键值对
     * @param filePath 文件路径
     * @return 上传结果
     */
    public static String post(String url, Map<String, Object> param, String filePath) {
        String result = "";
        HttpResponse response;
        //try-with-resource的形式 可以省略手动关资源
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            //设置content-type  因为没有设置，一直提示上传文件为空，踩了很多坑
            multipartEntityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
            //待上传文件
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
     * HTTP JSON上传
     *
     * @param url  服务路径
     * @param json json字符串
     * @return 上传结果
     */
    public static String httpPostWithJson(String url, String json) {
        String result = "";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            BasicResponseHandler handler = new BasicResponseHandler();
            // 解决中文乱码问题
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
