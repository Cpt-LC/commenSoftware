package com.lianzheng.management.service.modules.notarization.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.annotation.AuditLog;
import com.lianzheng.core.auth.mgmt.config.ShiroUtils;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.service.SysNotarialOfficeService;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.core.server.PagesUtils;
import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.management.service.modules.notarization.form.GreffierOperationForm;
import com.lianzheng.management.service.modules.notarization.utils.MasterServiceBeanUtil;
import com.lianzheng.notarization.master.configParameter.param.MessagesParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.notarization.master.entity.NotarzationAuthCommentEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.OrderEntity;
import com.lianzheng.notarization.master.enums.ApplicationPartyEnum;
import com.lianzheng.notarization.master.enums.NotarzationStatusEnum;
import com.lianzheng.notarization.master.enums.ProcessStatusEnum;
import com.lianzheng.notarization.master.form.DocumentForm;
import com.lianzheng.notarization.master.form.MapCommonForm;
import com.lianzheng.notarization.master.form.NotarzationMasterForm;
import com.lianzheng.notarization.master.service.*;
import com.lianzheng.notarization.master.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.NotSupportedException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping("sys/certificate")
public class SysCertificateController extends AbstractController {
    @Autowired
    private UserNotarzationMasterService userNotarzationMasterService;
    @Autowired
    private UserOrderService userOrderService;
    @Autowired
    private UserDocumentService userDocumentService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserNotarzationAuthCommentService userNotarzationAuthCommentService;
    @Autowired
    private SysUserService sysUserService;
    private ExecutorService threadPool ;

    @Autowired
    private TruthService truthService;
    @Autowired
    private GeneratePdf generatePdf;

    @Resource
    private SysNotarialOfficeService sysNotarialOfficeService;

    Path notarySigns = Paths.get("notarySigns");

//    @PostMapping("/test2")
//    public void test2(MultipartFile file)throws Exception{
//
//        System.out.println(file.getOriginalFilename());
//        Files.copy(file.getInputStream(), this.notarySigns.resolve("test.pdf"));
//    }

    @PostMapping("/test3")
    public void test(String a)throws Exception{

//           truthService.postest("http://localhost:9001/notarization/sys/certificate/test2","notices/PDF_APPLICATION_3205010120220111130118.pdf");
//            generatePdf.generateCertificate("notices/PDF_NOTARIZATION_3205010120220111130118.docx","notices/PDF_NOTARIZATION_3205010120220111130118.pdf",null);
//        generatePdf.addPdf(new String[]{"notices/PDF_APPLICATION_3205010120220208242268.pdf","notices/PDF_NOTARIZATION_CERT_3205010120220111130118.pdf"},"notices/PDF_NOTARIZATION_PREVIEW_3205010120220111130118.pdf");
//        FileSdk.decrypt(a,"notices/test.pdf");
//        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterService.getById("f739d886-56a3-11ec-9e34-b8cb299dab4c");
////        truthService.pushBaseData(notarzationMasterEntity);
//        truthService.pushToTruth(notarzationMasterEntity);
//        truthService.pushFile("notices/PDF_QUESTION_3205010120220208242268.pdf","notices/PDF_QUESTION_3205010120220208242268_2.pdf");
////
////        NotarzationMasterEntity notarzationMasterEntity=userNotarzationMasterService.getById("02701580-fa99-4d83-bfdd-f2f26a56b37e");
////
////        Class<?> c = Class.forName("com.lianzheng.notarization.master.generate.PdfNoticeOneGenerate");
////        Constructor<?> con = c.getConstructor(NotarzationMasterEntity.class);
////        Object o = con.newInstance(notarzationMasterEntity);
////        Method method=c.getMethod("Generatefile",String.class);
////        method.invoke(o,"templates/???????????????????????????.docx");
////        IGenerate iGenerate = SpringContextUtil.getBean(PdfNoticeOneGenerate.class);
////        PdfNoticeOneGenerate pdfNoticeGenerate=new PdfNoticeOneGenerate(notarzationMasterEntity);
////        pdfNoticeGenerate.Generatefile("templates/???????????????????????????.docx");
////        NotarzationMasterEntity notarzationMasterEntity=userNotarzationMasterService.getById("02701580-fa99-4d83-bfdd-f2f26a56b37e");
////        PdfNoticeGenerate pdfNoticeGenerate=new PdfNoticeGenerate(notarzationMasterEntity);
////        pdfNoticeGenerate.Generatefile("templates/???????????????????????????.docx");
//
////        DocumentParam documentParam =ConfigParameterUtil.getDocument();
////        for (String item:documentParam.getUserDocumentFilter() ){
////            System.out.println(item);
////            System.out.println(documentParam.getUserDocumentFilter().length);
////        }
//
////        generatePdf.notarizationPreview("templates/?????????_????????????.docx","notices/test.pdf");
    }

    //????????????
    @PostMapping("/list")
    public ResponseBase list(@RequestBody Map<String, Object> params){
        if(params.get("page")==null||params.get("limit")==null){
            return ResponseBase.error("?????????????????????????????????????????????");
        }
        //???????????????id
        SysUserEntity userEntity = ShiroUtils.getUserEntity();
        Long notarialOfficeId = userEntity.getNotarialOfficeId();
        params.put("notarialOfficeId",notarialOfficeId);
        //????????????????????????????????????
        Long userRoleId = sysUserService.getUserRoleId(userEntity.getId());
        //??????????????????????????????
        if (userRoleId == 1){
            params.remove("notarialOfficeId");
        }
        PagesUtils pagesList = userNotarzationMasterService.queryCertificateList(params);
        List<NotarzationMasterForm> notarzationMasterFormList = (List<NotarzationMasterForm>)pagesList.getList();
        List<SysUserEntity> sysUserEntityList =sysUserService.getUser("",null);
        //??????id?????????map,???????????????
        Map<Long, SysUserEntity> maps = sysUserEntityList.stream().collect(Collectors.toMap(SysUserEntity::getId, Function.identity()));
        for(NotarzationMasterForm item :notarzationMasterFormList){
            Long actionBy =item.getActionBy();
            Long recordBy =item.getRecordBy();
            if(actionBy!=null && maps.get(actionBy)!=null){
                item.setActionName(maps.get(actionBy).getRealName());
            }
            if(recordBy!=null && maps.get(recordBy)!=null){
                item.setRecordName(maps.get(recordBy).getRealName());
            }
            if(item.getApplicantParty().equals(ApplicationPartyEnum.E.getCode())){
                item.setIdCardNo(item.getSocialCreditCode());
            }
        }
        return ResponseBase.ok().put("pagesList",pagesList);
    }

    @AuditLog("????????????")
    @RequiresPermissions("sys:cert:claim")
    @PostMapping("/claim")
    public ResponseBase claim(@RequestBody Map<String,Object> param){
        if(param==null){
            return ResponseBase.error("?????????????????????????????????");
        }
        Boolean isCancel = (Boolean) param.get("isCancel");
        String id = param.get("id").toString();

        if(id==null||id.isEmpty()){
            return ResponseBase.error("?????????????????????????????????");
        }

        //?????????????????????
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(id);
        if((isCancel==null || !isCancel) && checkItem!=null&&checkItem.getActionBy()!=null){
            return ResponseBase.error("??????????????????");
        }
        else if(isCancel!=null && isCancel && checkItem==null&& (checkItem.getActionBy()==null || checkItem.getActionBy().toString().isEmpty())){
            return ResponseBase.error("?????????????????????");
        }

        NotarzationMasterEntity entity = new NotarzationMasterEntity();
        entity.setId(id);
        if(isCancel==null || !isCancel){
            entity.setActionBy(ShiroUtils.getUserId());
        }
        else{
            entity.setActionBy(null);
        }
        //????????????
       userNotarzationMasterService.claimCertificate(entity);
       return   ResponseBase.ok((isCancel==null || !isCancel) ? "????????????" : "??????????????????");
    }

    @RequiresPermissions("sys:cert:detail")
    @PostMapping("/info")
    public ResponseBase info(@RequestBody Map<String,Object> param) throws NotSupportedException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if(param==null||
                param.get("id")==null||
                param.get("id").equals("")||
                param.get("notarzationTypeCode")==null||
                param.get("notarzationTypeCode").equals("")){
            return ResponseBase.error("???????????????????????????????????????");
        }
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterService.getById(param.get("id").toString());

        //??????????????????
        MasterService masterService = MasterServiceBeanUtil.getMasterServiceBean(param.get("notarzationTypeCode").toString());
        List<Map<String,Object>> mapListFromMap  =masterService.getCertificateInfo(param);


        //???????????????????????????
        List<NotarzationAuthCommentEntity> notarzationAuthCommentEntityList = userNotarzationAuthCommentService.list(
                new QueryWrapper<NotarzationAuthCommentEntity>().eq("notarzationId",param.get("id"))
        );
        //???????????????????????????
        for (NotarzationAuthCommentEntity entity:notarzationAuthCommentEntityList){
            if(notarzationMasterEntity.getIsAgent()==1){
                String fieldName =entity.getFieldName();
                if (StringUtils.isEmpty(fieldName)){
                    continue;
                }
                switch(fieldName){
                    case "phoneAgent":
                    case "realNameAgent":
                    case "idCardTypeAgent":
                    case "genderAgent":
                    case "idCardNoAgent":
                    case "birthAgent":
                    case "idCardAddressAgent":
                        entity.setFieldName(fieldName.replace("Agent",""));
                        entity.setTableName("notarzation_master");
                        break;
                    default:
                }
            }
        }


        return ResponseBase.ok().put("info",mapListFromMap).put("fieldNameEdit",notarzationAuthCommentEntityList);
    }


    @PostMapping("/getPaidDetail")
    public ResponseBase getPaidDetail(@RequestBody Map<String,Object> param){
        List<Map<String,Object>> paidDetail=userNotarzationMasterService.getPaidDetail(param);
        return ResponseBase.ok().put("detail",paidDetail);
    }


    @AuditLog("???????????????")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/edit")
    public ResponseBase edit(@RequestBody GreffierOperationForm param){
        if(param==null||param.getNotarzationForm()==null||param.getNotarzationAuthCommentEntityList()==null){
            return ResponseBase.error("?????????????????????????????????");
        }

        Map<String,Object> notarizationMap = param.getNotarzationForm();
        String inputStatus = notarizationMap.get("status").toString();
        Object authComment = notarizationMap.get("authComment");
        if(!inputStatus.equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode()) && (authComment==null||authComment.toString().trim().equals(""))){
            return ResponseBase.error("?????????????????????");
        }

        Object idObj = notarizationMap.get("id");
        Object notarizationTypeCodeObj = notarizationMap.get("notarzationTypeCode");
        if (idObj==null ||
                idObj.equals("")||
                notarizationTypeCodeObj==null||
                notarizationTypeCodeObj.equals("")){
            return ResponseBase.error("????????????????????????????????????????????????");
        }
        String id = idObj.toString();
        String notarizationTypeCode = notarizationTypeCodeObj.toString();

        //???????????????????????????
        Long userId = ShiroUtils.getUserId();
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(id);
        String originalStatus = checkItem.getStatus();
        if(!checkItem.getStatus().equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())&&!checkItem.getStatus().equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())){
            return ResponseBase.error("???????????????????????????");
        }
        if(checkItem.getActionBy()==null ||
                !checkItem.getActionBy().equals(userId) ||
                (checkItem.getRecordBy() != null && !checkItem.getRecordBy().equals(userId))
        ){
            return ResponseBase.error("????????????");
        }

        //??????????????????
        MasterService masterService = MasterServiceBeanUtil.getMasterServiceBean(notarizationTypeCode);
//        notarizationMap.put("status",NotarzationStatusEnum.PENDINGCONFIRMED.getCode());//???????????????????????????????????????????????????
        masterService.editCertificateInfo(notarizationMap);


        //???????????????????????????(??????)
        List<NotarzationAuthCommentEntity> notarzationAuthCommentEntityList = param.getNotarzationAuthCommentEntityList();
        userNotarzationAuthCommentService.remove(new QueryWrapper<NotarzationAuthCommentEntity>().eq("notarzationId",id));
        for (NotarzationAuthCommentEntity entity: notarzationAuthCommentEntityList ) {
            int status = entity.getStatus();
            String comment = status == 0 ? "??????????????????????????????" : (status == 1 ? "?????????????????????" : "");
            entity.setComment(comment);
            entity.setCreatedBy(userId.toString());
            entity.setUpdatedBy(userId.toString());
            if(checkItem.getIsAgent()==1){  //??????????????????????????????
                String fieldName =entity.getFieldName();
                if (StringUtils.isEmpty(fieldName)){
                    continue;
                }
                switch(fieldName){
                    case "phone":
                    case "realName":
                    case "idCardType":
                    case "gender":
                    case "idCardNo":
                    case "birth":
                    case "idCardAddress":
                        entity.setFieldName(fieldName+"Agent");
                        entity.setTableName("user");
                        break;
                    default:
                }
            }
        }
        userNotarzationAuthCommentService.saveBatch(notarzationAuthCommentEntityList);

        if(!originalStatus.equals(inputStatus))
        {
            //?????????????????????????????????
            this.sendMessage("edit",id);
        }
        return ResponseBase.ok("????????????");
    }

    @AuditLog("????????????")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/approve")
    public ResponseBase approve(@RequestBody Map<String,Object> param){
        if(param==null||param.get("id")==null||param.get("notarzationTypeCode")==null){
            return ResponseBase.error("?????????????????????????????????");
        }

        //???????????????????????????
        Long userId = ShiroUtils.getUserId();
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getStatus().equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())&&!checkItem.getStatus().equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())){
            return ResponseBase.error("???????????????????????????");
        }
        if(checkItem.getActionBy()==null ||
                !checkItem.getActionBy().equals(userId) ||
                (checkItem.getRecordBy() != null && !checkItem.getRecordBy().equals(userId))
        ){
            return ResponseBase.error("????????????");
        }
        String notarzationTypeCode = checkItem.getNotarzationTypeCode();

        SysUserEntity sysUserEntity = sysUserService.queryByUserId(checkItem.getActionBy());
        File file =new File(this.notarySigns+"/"+sysUserEntity.getRealName()+".png");
        if(!file.exists()){
            return ResponseBase.error(-1,"??????????????????????????????????????????");
        }

        MasterService masterService = MasterServiceBeanUtil.getMasterServiceBean(notarzationTypeCode);
        //???????????????????????????
        param.put("status",NotarzationStatusEnum.PENDINGPAYMENT.getCode());
        masterService.editCertificateInfo(param);


        this.sendMessage("approve",param.get("id").toString());
        return ResponseBase.ok();
    }

    //??????????????????
    //????????????
    @PostMapping("/sendMessage")
    public void sendMessage(@RequestParam String type,@RequestParam String id){
        if(id==null){
            throw new RuntimeException("??????????????????,??????????????????");
        }
        MessagesParam messagesParam = ConfigParameterUtil.getMessages();//??????????????????
        String signName = messagesParam.getSignName();
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterService.getById(id);
        String realName = notarzationMasterEntity.getRealName();
        String phone  = notarzationMasterEntity.getPhone();
        String processNo =notarzationMasterEntity.getProcessNo();
        Map<String,Object> sendMap =new HashMap<>();

        ResponseBase result = null;
        sendMap.put("name",realName);
        sendMap.put("processNo",processNo);
        switch (type){
            case "edit":
                sendMap.put("action","?????????????????????");
                sendMap.put("todo","????????????");
                result= MessageUtils.doSendMessage("notice",signName,new String[]{phone},sendMap);
                break;
            case "approve":
                sendMap.put("action","???????????????");
                sendMap.put("todo","????????????");
                result=MessageUtils.doSendMessage("notice",signName,new String[]{phone},sendMap);
                break;
            case "refuse":
                sendMap.put("action","????????????");
                sendMap.put("todo","????????????");
                result=MessageUtils.doSendMessage("notice",signName,new String[]{phone},sendMap);
                break;
            case "directorPass":
                sendMap.put("action","?????????");
                sendMap.put("todo","????????????");
                result=MessageUtils.doSendMessage("notice",signName,new String[]{phone},sendMap);
                break;
            case "sendTo":
                OrderEntity orderEntity = userOrderService.getById(notarzationMasterEntity.getOrderId());
                sendMap.put("action","?????????????????????");
                sendMap.put("todo","??????????????????????????????${"+ orderEntity.getLogisticsNumber()+"}");
                result=MessageUtils.doSendMessage("notice",signName,new String[]{phone},sendMap);
                break;
        }
        if(!result.get("code").toString().equals("0")){
            List<LinkedHashMap> linkedHashMaps= (List<LinkedHashMap>)result.get("result");
            LinkedHashMap linkedHashMap = linkedHashMaps.get(0);
            throw new RuntimeException(linkedHashMap.get("explain").toString());
        }
    }








    @AuditLog("???????????????")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/refuse")
    public ResponseBase refuse(@RequestBody Map<String,Object> param){
        if(param==null||param.get("id")==null){
            return ResponseBase.error("?????????????????????????????????");
        }

        Object authComment = param.get("authComment");
        if(authComment==null||authComment.toString().trim().equals("")){
            return ResponseBase.error("???????????????????????????????????????");
        }

        //???????????????????????????
        Long userId = ShiroUtils.getUserId();
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getStatus().equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())){
            return ResponseBase.error("???????????????????????????");
        }
        if(checkItem.getActionBy()==null ||
                !checkItem.getActionBy().equals(userId) ||
                (checkItem.getRecordBy() != null && !checkItem.getRecordBy().equals(userId))
        ){
            return ResponseBase.error("????????????");
        }


        //????????????
        userNotarzationMasterService.refuseCertificate(param);
        this.sendMessage("refuse",param.get("id").toString());
        return ResponseBase.ok("????????????");
    }


    //?????????????????????
    @AuditLog("?????????????????????")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/submit")
    public ResponseBase submit(@RequestBody Map<String,Object> param){
        if(param==null||param.get("id")==null){
            return ResponseBase.error("?????????????????????????????????");
        }

        //???????????????????????????
        Long userId = ShiroUtils.getUserId();
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getProcessStatus().equals(ProcessStatusEnum.DOING.getCode())){
            return ResponseBase.error("???????????????????????????");
        }
        if(checkItem.getActionBy()==null ||
                !checkItem.getActionBy().equals(userId) ||
                (checkItem.getRecordBy() != null && !checkItem.getRecordBy().equals(userId))
        ){
            return ResponseBase.error("????????????");
        }
        //????????????
        userNotarzationMasterService.submitCertificate(param);
        return ResponseBase.ok("??????????????????");
    }



    //????????????
    @AuditLog("????????????")
    @RequiresPermissions("sys:cert:detail:direct")
    @PostMapping("/directorRefuse")
    public ResponseBase directorRefuse(@RequestBody Map<String,Object> param){
        if(param==null||param.get("id")==null){
            return ResponseBase.error("?????????????????????????????????");
        }

        Object directorRejectedComment = param.get("directorRejectedComment");
        if(directorRejectedComment==null||directorRejectedComment.toString().trim().equals("")){
            return ResponseBase.error("???????????????????????????????????????");
        }

        //???????????????????????????
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getProcessStatus().equals(ProcessStatusEnum.APPROVING.getCode())){
            return ResponseBase.error("???????????????????????????");
        }


        //????????????
        userNotarzationMasterService.directorRefuseCertificate(param);
        return ResponseBase.ok("??????????????????");
    }

    //????????????
//    @RequiresPermissions("sys:cert:detail:direct")
    @PostMapping("/previewNotarization")
    public ResponseBase previewNotarization(@RequestBody Map<String,Object> param) throws Exception{
        if(param==null||param.get("id")==null){
            return ResponseBase.error("?????????????????????????????????");
        }
        //????????????????????????
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getProcessStatus().equals(ProcessStatusEnum.APPROVING.getCode())){
            return ResponseBase.error("???????????????????????????");
        }

        //?????????????????????????????????
        return  ResponseBase.ok().put("preview",userNotarzationMasterService.previewNotarization(param.get("id").toString()));
    }


    //??????????????????
    @AuditLog("??????????????????")
//    @RequiresPermissions("sys:cert:detail:direct")
    @PostMapping("/directorPass")
    public ResponseBase directorPass(@RequestBody Map<String,Object> param) throws Exception{
        if(param==null||param.get("id")==null){
            return ResponseBase.error("?????????????????????????????????");
        }


        //???????????????????????????
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getProcessStatus().equals(ProcessStatusEnum.APPROVING.getCode())){
            return ResponseBase.error("???????????????????????????");
        }


        //????????????  ?????????????????????
        userNotarzationMasterService.directorPassCertificate(param);

        //???????????????????????????
        userNotarzationMasterService.generateNotarization(param.get("id").toString());
        this.sendMessage("directorPass",param.get("id").toString());
        return ResponseBase.ok("????????????");
    }



    //??????????????????
    @AuditLog("??????????????????")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/pickup")
    public ResponseBase pickUp(@RequestBody Map<String,Object> param) throws Exception{
        if(param==null||param.get("id")==null){
            return ResponseBase.error("?????????????????????????????????");
        }

        //???????????????????????????
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getProcessStatus().equals(NotarzationStatusEnum.GENERATINGCERT.getCode())){
            return ResponseBase.error("???????????????????????????");
        }

        //????????????
        userNotarzationMasterService.pickUpCertificate(param);

        //?????????????????????????????????
        OrderEntity orderEntity =userOrderService.getById(checkItem.getOrderId());
        if(orderEntity.getIsSend()==1){
            this.sendMessage("sendTo",param.get("id").toString());
        }

        return ResponseBase.ok("????????????");
    }


    /**
     * ??????????????????
     * @param param
     * @return
     * @throws Exception
     */
//    @AuditLog("??????????????????")
    @PostMapping("/regeneratePdf")
    public ResponseBase regeneratePdf(@RequestBody Map<String,Object> param) throws Exception{
        if(param==null||param.get("id")==null){
            return ResponseBase.error("?????????????????????????????????");
        }

        String id = param.get("id").toString();
        //???????????????????????????
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterService.getById(id);
        param.put("notarialOfficeId",notarzationMasterEntity.getNotarialOfficeId());
        param.put("signedReceipt",notarzationMasterEntity.getSignedReceipt()); //parseDocuments????????????

        SysUserEntity sysUserEntity = sysUserService.queryByUserId(notarzationMasterEntity.getActionBy());
        File file =new File(this.notarySigns+"/"+sysUserEntity.getRealName()+".png");
        if(!file.exists()){
            return ResponseBase.error(-1,"??????????????????????????????????????????");
        }

        MasterService masterService = MasterServiceBeanUtil.getMasterServiceBean(notarzationMasterEntity.getNotarzationTypeCode());
        String status = null ;//???????????????????????????????????????????????????notarzationMasterEntity.getStatus(),????????????????????????????????????????????????;

        if(param.get("status")==null||param.get("status").equals("")){
            status = notarzationMasterEntity.getStatus();
        }else {
            status = param.get("status").toString();
        }
        String processStatus = notarzationMasterEntity.getProcessStatus();
        if(status.equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())
            || status.equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())
            || status.equals(NotarzationStatusEnum.PENDINGPAYMENT.getCode())){
            masterService.generatePdf(id);
        }
        if(status.equals(NotarzationStatusEnum.GENERATINGCERT.getCode())&&processStatus.equals(ProcessStatusEnum.DOING.getCode())){
            masterService.generatePdf(id);
            masterService.generatePreparePaper(notarzationMasterEntity);
        }
        if((status.equals(NotarzationStatusEnum.GENERATINGCERT.getCode())&&processStatus.equals(ProcessStatusEnum.APPROVING.getCode()))
        || (processStatus.equals(ProcessStatusEnum.GENERATINGCERT.getCode()))
        ){
            userNotarzationMasterService.generateNotarization(id);
        }

        //?????????????????????????????????
        List<DocumentForm> documentFormList = userDocumentService.getlist(id);
        Map<String, List<MapCommonForm>> docCategories = userNotarzationMasterService.parseDocuments(documentFormList, param);
        docCategories.remove("????????????");
        docCategories.remove("????????????");
        for(MapCommonForm doc:docCategories.get("????????????")){
            doc.setAbleToComment(Boolean.TRUE);
        }
        return ResponseBase.ok("??????????????????").put("documents", docCategories);

    }


    /**
     * ???H5??????????????????
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/generateReceipt")
    public ResponseBase generateReceipt(String id) throws Exception{
        if(id==null){
            return ResponseBase.error("???????????????????????????");
        }
        //???????????????????????????
        log.info("?????????id="+id);
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterService.getById(id);
        if(notarzationMasterEntity==null){
            return ResponseBase.error("????????????");
        }
        //??????????????????
        userNotarzationMasterService.generateRecipt(id);
        return ResponseBase.ok("????????????????????????");
    }


    @AuditLog("???????????????")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/updateOfflinePayment")
    public ResponseBase updateOfflinePayment(@RequestBody Map<String,Object> param) throws Exception{
        if(param==null){
            return ResponseBase.error("?????????????????????????????????");
        }
        String id = param.get("id").toString();

        if(id==null||id.isEmpty()){
            return ResponseBase.error("?????????????????????????????????");
        }

        //?????????????????????
        OrderEntity mapOrder =  userOrderService.getOrderInfo(id);
        if(mapOrder==null || mapOrder.getPaymentMode()== null){
            return ResponseBase.error("??????????????????");
        }
        if(!mapOrder.getPaymentMode().equals("Offline")){
            return ResponseBase.error("?????????????????????");
        }

        //????????????
        userOrderService.updateOfflinePayment(id);
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterService.getOne(
                new QueryWrapper<NotarzationMasterEntity>().eq("orderId", id)
        );

        new Thread(() -> {
            try {
               truthService.pushToTruth(notarzationMasterEntity);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                e.printStackTrace();
            }

        }).start();

        return   ResponseBase.ok("????????????????????????");
    }

    /**
     * ?????????????????????
     * @param sysNotarialOfficeEntity
     * @return
     */
    @GetMapping("/findList")
    public ResponseBase findList(SysNotarialOfficeEntity sysNotarialOfficeEntity) {
        return ResponseBase.ok().put("data", sysNotarialOfficeService.findList(sysNotarialOfficeEntity));
    }

}
