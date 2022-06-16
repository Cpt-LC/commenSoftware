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
////        method.invoke(o,"templates/昆山在线受理告知书.docx");
////        IGenerate iGenerate = SpringContextUtil.getBean(PdfNoticeOneGenerate.class);
////        PdfNoticeOneGenerate pdfNoticeGenerate=new PdfNoticeOneGenerate(notarzationMasterEntity);
////        pdfNoticeGenerate.Generatefile("templates/昆山在线受理告知书.docx");
////        NotarzationMasterEntity notarzationMasterEntity=userNotarzationMasterService.getById("02701580-fa99-4d83-bfdd-f2f26a56b37e");
////        PdfNoticeGenerate pdfNoticeGenerate=new PdfNoticeGenerate(notarzationMasterEntity);
////        pdfNoticeGenerate.Generatefile("templates/昆山在线受理告知书.docx");
//
////        DocumentParam documentParam =ConfigParameterUtil.getDocument();
////        for (String item:documentParam.getUserDocumentFilter() ){
////            System.out.println(item);
////            System.out.println(documentParam.getUserDocumentFilter().length);
////        }
//
////        generatePdf.notarizationPreview("templates/公证书_毕业证明.docx","notices/test.pdf");
    }

    //公证列表
    @PostMapping("/list")
    public ResponseBase list(@RequestBody Map<String, Object> params){
        if(params.get("page")==null||params.get("limit")==null){
            return ResponseBase.error("列表数据获取失败，请联系管理员");
        }
        //获取公证处id
        SysUserEntity userEntity = ShiroUtils.getUserEntity();
        Long notarialOfficeId = userEntity.getNotarialOfficeId();
        params.put("notarialOfficeId",notarialOfficeId);
        //判断登录用户所处的权限组
        Long userRoleId = sysUserService.getUserRoleId(userEntity.getId());
        //超级管理员全部可查看
        if (userRoleId == 1){
            params.remove("notarialOfficeId");
        }
        PagesUtils pagesList = userNotarzationMasterService.queryCertificateList(params);
        List<NotarzationMasterForm> notarzationMasterFormList = (List<NotarzationMasterForm>)pagesList.getList();
        List<SysUserEntity> sysUserEntityList =sysUserService.getUser("",null);
        //根据id转换成map,加入操作人
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

    @AuditLog("公证认领")
    @RequiresPermissions("sys:cert:claim")
    @PostMapping("/claim")
    public ResponseBase claim(@RequestBody Map<String,Object> param){
        if(param==null){
            return ResponseBase.error("认领失败，请联系管理员");
        }
        Boolean isCancel = (Boolean) param.get("isCancel");
        String id = param.get("id").toString();

        if(id==null||id.isEmpty()){
            return ResponseBase.error("认领失败，请联系管理员");
        }

        //验证是否可认领
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(id);
        if((isCancel==null || !isCancel) && checkItem!=null&&checkItem.getActionBy()!=null){
            return ResponseBase.error("该公证已认领");
        }
        else if(isCancel!=null && isCancel && checkItem==null&& (checkItem.getActionBy()==null || checkItem.getActionBy().toString().isEmpty())){
            return ResponseBase.error("该公证还未认领");
        }

        NotarzationMasterEntity entity = new NotarzationMasterEntity();
        entity.setId(id);
        if(isCancel==null || !isCancel){
            entity.setActionBy(ShiroUtils.getUserId());
        }
        else{
            entity.setActionBy(null);
        }
        //认领公证
       userNotarzationMasterService.claimCertificate(entity);
       return   ResponseBase.ok((isCancel==null || !isCancel) ? "认领成功" : "取消认领成功");
    }

    @RequiresPermissions("sys:cert:detail")
    @PostMapping("/info")
    public ResponseBase info(@RequestBody Map<String,Object> param) throws NotSupportedException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if(param==null||
                param.get("id")==null||
                param.get("id").equals("")||
                param.get("notarzationTypeCode")==null||
                param.get("notarzationTypeCode").equals("")){
            return ResponseBase.error("获取数据失败，请联系管理员");
        }
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterService.getById(param.get("id").toString());

        //获取基本信息
        MasterService masterService = MasterServiceBeanUtil.getMasterServiceBean(param.get("notarzationTypeCode").toString());
        List<Map<String,Object>> mapListFromMap  =masterService.getCertificateInfo(param);


        //获取公证员字段操作
        List<NotarzationAuthCommentEntity> notarzationAuthCommentEntityList = userNotarzationAuthCommentService.list(
                new QueryWrapper<NotarzationAuthCommentEntity>().eq("notarzationId",param.get("id"))
        );
        //替换代理的标注信息
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


    @AuditLog("公证员修改")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/edit")
    public ResponseBase edit(@RequestBody GreffierOperationForm param){
        if(param==null||param.getNotarzationForm()==null||param.getNotarzationAuthCommentEntityList()==null){
            return ResponseBase.error("修改失败，请联系管理员");
        }

        Map<String,Object> notarizationMap = param.getNotarzationForm();
        String inputStatus = notarizationMap.get("status").toString();
        Object authComment = notarizationMap.get("authComment");
        if(!inputStatus.equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode()) && (authComment==null||authComment.toString().trim().equals(""))){
            return ResponseBase.error("请填写审核意见");
        }

        Object idObj = notarizationMap.get("id");
        Object notarizationTypeCodeObj = notarizationMap.get("notarzationTypeCode");
        if (idObj==null ||
                idObj.equals("")||
                notarizationTypeCodeObj==null||
                notarizationTypeCodeObj.equals("")){
            return ResponseBase.error("修改失败，操作失败，请联系管理员");
        }
        String id = idObj.toString();
        String notarizationTypeCode = notarizationTypeCodeObj.toString();

        //验证是否可更新状态
        Long userId = ShiroUtils.getUserId();
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(id);
        String originalStatus = checkItem.getStatus();
        if(!checkItem.getStatus().equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())&&!checkItem.getStatus().equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())){
            return ResponseBase.error("状态已更改，请刷新");
        }
        if(checkItem.getActionBy()==null ||
                !checkItem.getActionBy().equals(userId) ||
                (checkItem.getRecordBy() != null && !checkItem.getRecordBy().equals(userId))
        ){
            return ResponseBase.error("无权操作");
        }

        //修改基本信息
        MasterService masterService = MasterServiceBeanUtil.getMasterServiceBean(notarizationTypeCode);
//        notarizationMap.put("status",NotarzationStatusEnum.PENDINGCONFIRMED.getCode());//无需更改，前端需要根据逻辑只做保存
        masterService.editCertificateInfo(notarizationMap);


        //删除后新增修改数据(标注)
        List<NotarzationAuthCommentEntity> notarzationAuthCommentEntityList = param.getNotarzationAuthCommentEntityList();
        userNotarzationAuthCommentService.remove(new QueryWrapper<NotarzationAuthCommentEntity>().eq("notarzationId",id));
        for (NotarzationAuthCommentEntity entity: notarzationAuthCommentEntityList ) {
            int status = entity.getStatus();
            String comment = status == 0 ? "信息有误，请重新填写" : (status == 1 ? "已修改，请确认" : "");
            entity.setComment(comment);
            entity.setCreatedBy(userId.toString());
            entity.setUpdatedBy(userId.toString());
            if(checkItem.getIsAgent()==1){  //是代理将标注内容替换
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
            //状态发生改变再通知用户
            this.sendMessage("edit",id);
        }
        return ResponseBase.ok("修改成功");
    }

    @AuditLog("审核通过")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/approve")
    public ResponseBase approve(@RequestBody Map<String,Object> param){
        if(param==null||param.get("id")==null||param.get("notarzationTypeCode")==null){
            return ResponseBase.error("审核失败，请联系管理员");
        }

        //验证是否可更新状态
        Long userId = ShiroUtils.getUserId();
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getStatus().equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())&&!checkItem.getStatus().equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())){
            return ResponseBase.error("状态已更改，请刷新");
        }
        if(checkItem.getActionBy()==null ||
                !checkItem.getActionBy().equals(userId) ||
                (checkItem.getRecordBy() != null && !checkItem.getRecordBy().equals(userId))
        ){
            return ResponseBase.error("无权操作");
        }
        String notarzationTypeCode = checkItem.getNotarzationTypeCode();

        SysUserEntity sysUserEntity = sysUserService.queryByUserId(checkItem.getActionBy());
        File file =new File(this.notarySigns+"/"+sysUserEntity.getRealName()+".png");
        if(!file.exists()){
            return ResponseBase.error(-1,"当前登录者没有配置公证员签名");
        }

        MasterService masterService = MasterServiceBeanUtil.getMasterServiceBean(notarzationTypeCode);
        //更新信息表和订单表
        param.put("status",NotarzationStatusEnum.PENDINGPAYMENT.getCode());
        masterService.editCertificateInfo(param);


        this.sendMessage("approve",param.get("id").toString());
        return ResponseBase.ok();
    }

    //发送短信方法
    //短信重发
    @PostMapping("/sendMessage")
    public void sendMessage(@RequestParam String type,@RequestParam String id){
        if(id==null){
            throw new RuntimeException("短信发送失败,请联系管理员");
        }
        MessagesParam messagesParam = ConfigParameterUtil.getMessages();//获取短信参数
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
                sendMap.put("action","已审核，需修改");
                sendMap.put("todo","及时修改");
                result= MessageUtils.doSendMessage("notice",signName,new String[]{phone},sendMap);
                break;
            case "approve":
                sendMap.put("action","已审核通过");
                sendMap.put("todo","及时缴费");
                result=MessageUtils.doSendMessage("notice",signName,new String[]{phone},sendMap);
                break;
            case "refuse":
                sendMap.put("action","已被退回");
                sendMap.put("todo","及时查看");
                result=MessageUtils.doSendMessage("notice",signName,new String[]{phone},sendMap);
                break;
            case "directorPass":
                sendMap.put("action","已出证");
                sendMap.put("todo","及时领取");
                result=MessageUtils.doSendMessage("notice",signName,new String[]{phone},sendMap);
                break;
            case "sendTo":
                OrderEntity orderEntity = userOrderService.getById(notarzationMasterEntity.getOrderId());
                sendMap.put("action","的公证书已寄出");
                sendMap.put("todo","注意查收，快递单号：${"+ orderEntity.getLogisticsNumber()+"}");
                result=MessageUtils.doSendMessage("notice",signName,new String[]{phone},sendMap);
                break;
        }
        if(!result.get("code").toString().equals("0")){
            List<LinkedHashMap> linkedHashMaps= (List<LinkedHashMap>)result.get("result");
            LinkedHashMap linkedHashMap = linkedHashMaps.get(0);
            throw new RuntimeException(linkedHashMap.get("explain").toString());
        }
    }








    @AuditLog("审核不通过")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/refuse")
    public ResponseBase refuse(@RequestBody Map<String,Object> param){
        if(param==null||param.get("id")==null){
            return ResponseBase.error("操作失败，请联系管理员");
        }

        Object authComment = param.get("authComment");
        if(authComment==null||authComment.toString().trim().equals("")){
            return ResponseBase.error("请在审核意见里填写退回原因");
        }

        //验证是否可更新状态
        Long userId = ShiroUtils.getUserId();
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getStatus().equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())){
            return ResponseBase.error("状态已更改，请刷新");
        }
        if(checkItem.getActionBy()==null ||
                !checkItem.getActionBy().equals(userId) ||
                (checkItem.getRecordBy() != null && !checkItem.getRecordBy().equals(userId))
        ){
            return ResponseBase.error("无权操作");
        }


        //修改状态
        userNotarzationMasterService.refuseCertificate(param);
        this.sendMessage("refuse",param.get("id").toString());
        return ResponseBase.ok("驳回成功");
    }


    //公证员提交审批
    @AuditLog("公证员提交审批")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/submit")
    public ResponseBase submit(@RequestBody Map<String,Object> param){
        if(param==null||param.get("id")==null){
            return ResponseBase.error("操作失败，请联系管理员");
        }

        //验证是否可更新状态
        Long userId = ShiroUtils.getUserId();
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getProcessStatus().equals(ProcessStatusEnum.DOING.getCode())){
            return ResponseBase.error("状态已更改，请刷新");
        }
        if(checkItem.getActionBy()==null ||
                !checkItem.getActionBy().equals(userId) ||
                (checkItem.getRecordBy() != null && !checkItem.getRecordBy().equals(userId))
        ){
            return ResponseBase.error("无权操作");
        }
        //修改状态
        userNotarzationMasterService.submitCertificate(param);
        return ResponseBase.ok("提交审批成功");
    }



    //主任驳回
    @AuditLog("主任驳回")
    @RequiresPermissions("sys:cert:detail:direct")
    @PostMapping("/directorRefuse")
    public ResponseBase directorRefuse(@RequestBody Map<String,Object> param){
        if(param==null||param.get("id")==null){
            return ResponseBase.error("操作失败，请联系管理员");
        }

        Object directorRejectedComment = param.get("directorRejectedComment");
        if(directorRejectedComment==null||directorRejectedComment.toString().trim().equals("")){
            return ResponseBase.error("请在审核意见里填写退回原因");
        }

        //验证是否可更新状态
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getProcessStatus().equals(ProcessStatusEnum.APPROVING.getCode())){
            return ResponseBase.error("状态已更改，请刷新");
        }


        //修改状态
        userNotarzationMasterService.directorRefuseCertificate(param);
        return ResponseBase.ok("驳回审批成功");
    }

    //主任预览
//    @RequiresPermissions("sys:cert:detail:direct")
    @PostMapping("/previewNotarization")
    public ResponseBase previewNotarization(@RequestBody Map<String,Object> param) throws Exception{
        if(param==null||param.get("id")==null){
            return ResponseBase.error("操作失败，请联系管理员");
        }
        //验证预览时的状态
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getProcessStatus().equals(ProcessStatusEnum.APPROVING.getCode())){
            return ResponseBase.error("状态已更改，请刷新");
        }

        //返回可以访问的文件地址
        return  ResponseBase.ok().put("preview",userNotarzationMasterService.previewNotarization(param.get("id").toString()));
    }


    //主任审批通过
    @AuditLog("主任审批通过")
//    @RequiresPermissions("sys:cert:detail:direct")
    @PostMapping("/directorPass")
    public ResponseBase directorPass(@RequestBody Map<String,Object> param) throws Exception{
        if(param==null||param.get("id")==null){
            return ResponseBase.error("操作失败，请联系管理员");
        }


        //验证是否可更新状态
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getProcessStatus().equals(ProcessStatusEnum.APPROVING.getCode())){
            return ResponseBase.error("状态已更改，请刷新");
        }


        //修改状态  插入公证书编号
        userNotarzationMasterService.directorPassCertificate(param);

        //生成最终的公证文件
        userNotarzationMasterService.generateNotarization(param.get("id").toString());
        this.sendMessage("directorPass",param.get("id").toString());
        return ResponseBase.ok("审批通过");
    }



    //用户取证完成
    @AuditLog("用户取证完成")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/pickup")
    public ResponseBase pickUp(@RequestBody Map<String,Object> param) throws Exception{
        if(param==null||param.get("id")==null){
            return ResponseBase.error("操作失败，请联系管理员");
        }

        //验证是否可更新状态
        NotarzationMasterEntity checkItem =  userNotarzationMasterService.getById(param.get("id").toString());
        if(!checkItem.getProcessStatus().equals(NotarzationStatusEnum.GENERATINGCERT.getCode())){
            return ResponseBase.error("状态已更改，请刷新");
        }

        //修改状态
        userNotarzationMasterService.pickUpCertificate(param);

        //如需邮寄发送短信已寄出
        OrderEntity orderEntity =userOrderService.getById(checkItem.getOrderId());
        if(orderEntity.getIsSend()==1){
            this.sendMessage("sendTo",param.get("id").toString());
        }

        return ResponseBase.ok("取证完成");
    }


    /**
     * 文件重新生成
     * @param param
     * @return
     * @throws Exception
     */
//    @AuditLog("重新生成文件")
    @PostMapping("/regeneratePdf")
    public ResponseBase regeneratePdf(@RequestBody Map<String,Object> param) throws Exception{
        if(param==null||param.get("id")==null){
            return ResponseBase.error("操作失败，请联系管理员");
        }

        String id = param.get("id").toString();
        //验证是否可更新状态
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterService.getById(id);
        param.put("notarialOfficeId",notarzationMasterEntity.getNotarialOfficeId());
        param.put("signedReceipt",notarzationMasterEntity.getSignedReceipt()); //parseDocuments需要该值

        SysUserEntity sysUserEntity = sysUserService.queryByUserId(notarzationMasterEntity.getActionBy());
        File file =new File(this.notarySigns+"/"+sysUserEntity.getRealName()+".png");
        if(!file.exists()){
            return ResponseBase.error(-1,"当前登录者没有配置公证员签名");
        }

        MasterService masterService = MasterServiceBeanUtil.getMasterServiceBean(notarzationMasterEntity.getNotarzationTypeCode());
        String status = null ;//前端需要提前生成文件做预览，不能用notarzationMasterEntity.getStatus(),但是如果没有传，则用数据库里的值;

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

        //检索该公证下的所有文件
        List<DocumentForm> documentFormList = userDocumentService.getlist(id);
        Map<String, List<MapCommonForm>> docCategories = userNotarzationMasterService.parseDocuments(documentFormList, param);
        docCategories.remove("证明材料");
        docCategories.remove("公证材料");
        for(MapCommonForm doc:docCategories.get("签字材料")){
            doc.setAbleToComment(Boolean.TRUE);
        }
        return ResponseBase.ok("文件生成完成").put("documents", docCategories);

    }


    /**
     * 供H5生成回执调用
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/generateReceipt")
    public ResponseBase generateReceipt(String id) throws Exception{
        if(id==null){
            return ResponseBase.error("操作失败，参数丢失");
        }
        //验证是否可更新状态
        log.info("回执的id="+id);
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterService.getById(id);
        if(notarzationMasterEntity==null){
            return ResponseBase.error("数据错误");
        }
        //生成送达回执
        userNotarzationMasterService.generateRecipt(id);
        return ResponseBase.ok("送达回执生成成功");
    }


    @AuditLog("已线下付款")
    @RequiresPermissions("sys:cert:detail:notary")
    @PostMapping("/updateOfflinePayment")
    public ResponseBase updateOfflinePayment(@RequestBody Map<String,Object> param) throws Exception{
        if(param==null){
            return ResponseBase.error("更新失败，请联系管理员");
        }
        String id = param.get("id").toString();

        if(id==null||id.isEmpty()){
            return ResponseBase.error("更新失败，请联系管理员");
        }

        //验证是否可认领
        OrderEntity mapOrder =  userOrderService.getOrderInfo(id);
        if(mapOrder==null || mapOrder.getPaymentMode()== null){
            return ResponseBase.error("该数据不存在");
        }
        if(!mapOrder.getPaymentMode().equals("Offline")){
            return ResponseBase.error("非线下支付数据");
        }

        //认领公证
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

        return   ResponseBase.ok("更新支付状态成功");
    }

    /**
     * 公证处查询列表
     * @param sysNotarialOfficeEntity
     * @return
     */
    @GetMapping("/findList")
    public ResponseBase findList(SysNotarialOfficeEntity sysNotarialOfficeEntity) {
        return ResponseBase.ok().put("data", sysNotarialOfficeService.findList(sysNotarialOfficeEntity));
    }

}
