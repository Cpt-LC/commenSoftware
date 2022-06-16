package com.lianzheng.notarization.service.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.auth.mgmt.utils.RedisUtils;
import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.core.log.SpecificLog;
import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.core.sign.Utils.SignUtils;
import com.lianzheng.notarization.master.configParameter.param.CountryParam;
import com.lianzheng.notarization.master.configParameter.param.MessagesParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.OrderEntity;
import com.lianzheng.notarization.master.entity.UserEntity;
import com.lianzheng.notarization.master.enums.*;
import com.lianzheng.notarization.master.service.*;
import com.lianzheng.notarization.master.utils.MessageUtils;
import com.lianzheng.notarization.service.from.SignFrom;
import com.lianzheng.notarization.service.utils.GenerateStrUtil;
import com.lianzheng.notarization.service.utils.PhoneFormatCheckUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("online_accept")
public class UserCertificateController {

    @Autowired
    private SignUtils signUtils;
    @Autowired
    private UserOrderService userOrderService;
    @Autowired
    private UserNotarzationMasterApiService userNotarzationMasterApiService;
    @Autowired
    private UserDocumentService userDocumentService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private  RedisUtils redisUtils;

    private final Path root = Paths.get("uploads");

    @Value("${sendtoNotaryName}")
    private String sendtoNotaryName;
    @Value("${sendtoNotaryPhone}")
    private String sendtoNotaryPhone;
    /*
    文件上传
     */
    @PostMapping("/document/upload")
    public ResponseBase uploads(MultipartFile file, SignFrom signFrom) throws Exception{
        Map<String,Object> param = JSON.parseObject(JSON.toJSONString(signFrom),Map.class);
        signUtils.checkSign(param);
        if(file==null){
            return ResponseBase.error(8,"参数有误");
        }
        String url =  userDocumentService.saveDocument(file);
        return new ResponseBase().ok().put("url",url);
    }


    /*
    推送支付信息接口
     */
    @SpecificLog(message = "推送支付信息")
    @PostMapping("/notarization-master/afterPay")
    public ResponseBase afterPay(@RequestBody Map<String,Object> param) throws Exception{
        signUtils.checkSign(param);
        this.payDateCheck(param);//校验参数


        userOrderService.saveAfterPay(param);//更新状态


        //如果支付成功生成签字文件
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterApiService.getById(param.get("id").toString());
        String paymentStatus =param.get("paymentStatus").toString();
        if(paymentStatus.equals(PaymentStatusEnum.Paid.getCode())){

            redisUtils.putList("PUSH_NOTARIZATIONID_AFTERPAY_ONE",param.get("id").toString());
        }

        try{
            this.sendMessage("afterPay",param.get("id").toString());//支付发送给公证员
        }catch (Exception e){
             e.printStackTrace();
        }
        return new ResponseBase().ok();
    }


    /*
    查询审核结果接口
     */
    @PostMapping("/notarization-master/info")
    public ResponseBase info(@RequestBody Map<String,Object> param) throws Exception{
        signUtils.checkSign(param);
        if(param.get("id")==null){
            return ResponseBase.error(8,"参数有误");
        }
        Map<String,Object> data = userNotarzationMasterApiService.getMasterInfo(param);
        return new ResponseBase().ok().put("data",data);
    }


    /*
    提交公证申请接口
     */
    @PostMapping("/notarization-master/createRecord")
    @Transactional(rollbackFor = Exception.class)
    public ResponseBase apply(@RequestBody Map<String,Object> param) throws Exception{
        signUtils.checkSign(param);
        log.info(param.toString());
        Map<String,Object> data = (Map<String,Object>)param.get("data");
        this.dataCheck(data);//校验数据


        UserEntity userEntity = userService.getOne(
                new QueryWrapper<UserEntity>().eq("idCardNo",data.get("idCardNo").toString())
        );
        //默认新建一个账户
        if(userEntity==null){
            userEntity =new UserEntity();
            userEntity.setId(UUID.randomUUID().toString());
            userEntity.setPhone((String)data.get("phone"));
            userEntity.setRealName((String)data.get("realName"));
            userEntity.setPassword("md5(md5(123456))");
            userEntity.setEmail((String)data.get("email"));
            Object gender =data.get("gender");
            userEntity.setGender(NumberUtils.createInteger(gender!=null?gender.toString():"1"));
            Object birth = data.get("birth");
            userEntity.setBirth(birth!=null?DateUtils.stringToDate(birth.toString(),"yyyy-MM-dd"):null);
            userEntity.setIdCardType((String)data.get("idCardType"));
            userEntity.setIdCardNo((String)data.get("idCardNo"));
            Object idCardAddress = data.get("idCardAddress");
            userEntity.setIdCardAddress(idCardAddress!=null?idCardAddress.toString():"");
            userService.save(userEntity);
        }



        //订单数据
        String orderNo = GenerateStrUtil.getOrderNo();
        OrderEntity order = new OrderEntity();
        order.setId(UUID.randomUUID().toString());
        order.setOrderNo(orderNo);
        order.setUserId(userEntity.getId());
        order.setOuttradeNo("");
        Object billingName = data.get("billingName");
        order.setBillingName(billingName!=null?billingName.toString():"");
        Object invoiceTaxNo = data.get("invoiceTaxNo");
        order.setInvoiceTaxNo(invoiceTaxNo!=null?invoiceTaxNo.toString():"");
        Integer youJiType = NumberUtils.createInteger(data.get("isSend").toString());
        if (youJiType != null && youJiType == 1) {
            order.setIsSend(1);
            order.setSendtToName((String)data.get("sentToName"));
            order.setSentToPhone((String)data.get("sentToPhone"));
            order.setSentToProvince((String)data.get("sentToProvince"));
            order.setSentToCity((String)data.get("sentToCity"));
            order.setSentToArea((String)data.get("sentToArea"));
            order.setSentToAddress((String)data.get("sentToAddress"));
        } else {
            order.setIsSend(0);
        }

        //主表数据
        NotarzationMasterEntity notarzationMaster =new NotarzationMasterEntity();
        //更新数据，并添加数据库中所需的默认值
        notarzationMaster.update(data);
        String id =UUID.randomUUID().toString();
        notarzationMaster.setId(id);
        notarzationMaster.setUserId(userEntity.getId());
        notarzationMaster.setOrderId(order.getId());
        notarzationMaster.setNotarzationTypeCode((String)data.get("notarizationTypeCode"));
        notarzationMaster.setCreatedBy(userEntity.getId());
        notarzationMaster.setProcessNo(GenerateStrUtil.getProcessNo());
        notarzationMaster.setUpdatedBy(userEntity.getId());
        //因为不允许为空,所以给一些默认数据
        notarzationMaster.setApplicantParty(ApplicationPartyEnum.P.getCode());
        notarzationMaster.setStatus(NotarzationStatusEnum.PENDINGAPPROVED.getCode());
        notarzationMaster.setProcessStatus(ProcessStatusEnum.DOING.getCode());
        notarzationMaster.setUsedToProvince("");
        notarzationMaster.setUsedToCity("");

        CountryParam countryParam = ConfigParameterUtil.getCountry();
        if(countryParam.getCertCountry().contains(notarzationMaster.getUsedToCountry())){//双证国家
            notarzationMaster.setHasMoreCert("1");
        }

        //保存数据
        userOrderService.save(order);
        userNotarzationMasterApiService.save(notarzationMaster);


        List<Map<String,Object>> materialUrlList = (List<Map<String,Object>>)data.get("materialUrlList");
        for(Map<String,Object> item : materialUrlList){
            userDocumentService.saveDocument((String)item.get("url"),(String)item.get("fileType"),id);
        }

        try {
            this.sendMessage("toUser",id);//发送给用户
            this.sendMessage("toNotary",id);//发送给公证员
        }catch (Exception e){
            e.printStackTrace();
        }


        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("processNo",notarzationMaster.getProcessNo());
        return new ResponseBase().ok().put("data",map);
    }


    //发送短信方法
    @PostMapping("/sendMessage")
    public void sendMessage(@RequestParam String type, @RequestParam String id){
        if(id==null){
            throw new RuntimeException("短信发送失败,请联系管理员");
        }
        MessagesParam messagesParam = ConfigParameterUtil.getMessages();//获取短信参数
        String signName = messagesParam.getSignName();
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterApiService.getById(id);
        String realName = notarzationMasterEntity.getRealName();
        String phone  = notarzationMasterEntity.getPhone();
        String processNo =notarzationMasterEntity.getProcessNo();
        Map<String,Object> sendMap =new HashMap<>();

        ResponseBase result = null;
        sendMap.put("name",realName);
        sendMap.put("processNo",processNo);
        switch (type){
            case "toUser":
                result= MessageUtils.doSendMessage("toUser",signName,new String[]{phone},sendMap);
                break;
            case "toNotary":
                sendMap.put("name",sendtoNotaryName);
                result= MessageUtils.doSendMessage("toNotary",signName,new String[]{sendtoNotaryPhone},sendMap);
                break;
            case "afterPay":
                SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());
                sendMap.put("name",sysUserEntity.getRealName());
                result= MessageUtils.doSendMessage("sendAfterPay",signName,new String[]{sysUserEntity.getMobile()},sendMap);
                break;
        }
        if(!result.get("code").toString().equals("0")){
            List<LinkedHashMap> linkedHashMaps= (List<LinkedHashMap>)result.get("result");
            LinkedHashMap linkedHashMap = linkedHashMaps.get(0);
            throw new RuntimeException(linkedHashMap.get("explain").toString());
        }
    }


    public void dataCheck(Map<String,Object> data){
        if(data==null){
            throw new COREException("参数有误",8);
        }
        if(data.get("realName")==null||StringUtils.isEmpty(data.get("realName").toString())){
            throw new COREException("参数有误,真实姓名不可为空",8);
        }
        if(data.get("gender")==null||StringUtils.isEmpty(data.get("gender").toString())){
            throw new COREException("参数有误,性别不可为空",8);
        }
        if(SexEnum.getEnumCode(data.get("gender").toString())==null){
            throw new COREException("参数有误,性别类型不存在",8);
        }


        if(data.get("idCardNo")==null||StringUtils.isEmpty(data.get("idCardNo").toString())){
            throw new COREException("参数有误,证件号不可为空",8);
        }
        if(data.get("idCardType")==null||StringUtils.isEmpty(data.get("idCardType").toString())){
            throw new COREException("参数有误,证件类型不可为空",8);
        }
        if(data.get("phone")==null||StringUtils.isEmpty(data.get("phone").toString())){
            throw new COREException("参数有误,手机号不可为空",8);
        }
        if(!PhoneFormatCheckUtils.isPhoneLegal(data.get("phone").toString())){
            throw new COREException("参数有误,手机号格式错误",8);
        }

        if(data.get("notarizationTypeCode")==null||StringUtils.isEmpty(data.get("notarizationTypeCode").toString())){
            throw new COREException("参数有误,公证类型不可为空",8);
        }
        if(NotarizationTypeOneEnum.getEnumCode(data.get("notarizationTypeCode").toString())==null){
            throw new COREException("参数有误,公证类型不存在",8);
        }
        if(data.get("usedToCountry")==null||StringUtils.isEmpty(data.get("usedToCountry").toString())){
            throw new COREException("参数有误,使用地不可为空",8);
        }
        if(UseToCountryOneEnum.getEnumCode(data.get("usedToCountry").toString())==null){
            throw new COREException("参数有误,使用地不存在",8);
        }

        if(data.get("copyNumber")==null||StringUtils.isEmpty(data.get("copyNumber").toString())){
            throw new COREException("参数有误,公证份数不可为空",8);
        }
        if(NumberUtils.createInteger(data.get("copyNumber").toString())<1){
            throw new COREException("参数有误,公证份数错误",8);
        }
        if(data.get("usedFor")==null||StringUtils.isEmpty(data.get("usedFor").toString())){
            throw new COREException("参数有误,用途不可为空",8);
        }
        if(UsedForEnum.getEnumCode(data.get("usedFor").toString())==null){
            throw new COREException("参数有误,用途不存在",8);
        }
        if(data.get("materialUrlList")==null){
            throw new COREException("参数有误,材料不可为空",8);
        }
        List<Map<String,Object>> materialUrlList = (List<Map<String,Object>>)data.get("materialUrlList");
        if(materialUrlList.size()<=0){
            throw new COREException("参数有误,材料不可为空",8);
        }else{
            //校验url是否有效  ，材料是否齐全
            boolean headFlag = false;
            boolean idFontFlag = false;
            boolean idBackflag = false;
            for(Map<String,Object> item : materialUrlList){
                if(item.get("fileType")==null||StringUtils.isEmpty(item.get("fileType").toString())){
                    throw new COREException("参数有误,文件类型不可为空",8);
                }
                String fileType = item.get("fileType").toString();
                if(DocumentCategoryCode.getEnumCode(fileType)==null){
                    throw new COREException("参数有误,文件类型不存在",8);
                }
                if(fileType.equals("HEAD")){
                    headFlag =true;
                }
                if(fileType.equals("ID-BACK")){
                    idBackflag =true;
                }
                if(fileType.equals("ID-FRONT")){
                    idFontFlag =true;
                }
                fileUrlCheck((String)item.get("url"));
            }
            if(!(headFlag&&idFontFlag&&idBackflag)){
                throw new COREException("参数有误,缺少头像或身份证",8);
            }
        }

        if(data.get("isSend")==null||StringUtils.isEmpty(data.get("isSend").toString())){
            throw new COREException("参数有误,是否邮寄不可为空",8);
        }else if(NumberUtils.createInteger(data.get("isSend").toString())==1){//如需邮寄校验邮寄信息
            if(data.get("sentToName")==null||StringUtils.isEmpty(data.get("sentToName").toString())||
               data.get("sentToPhone")==null||StringUtils.isEmpty(data.get("sentToPhone").toString())||
               data.get("sentToProvince")==null||StringUtils.isEmpty(data.get("sentToProvince").toString())||
               data.get("sentToCity")==null||StringUtils.isEmpty(data.get("sentToCity").toString())||
               data.get("sentToArea")==null||StringUtils.isEmpty(data.get("sentToArea").toString())||
               data.get("sentToAddress")==null||StringUtils.isEmpty(data.get("sentToAddress").toString())){
               throw new COREException("参数有误,邮寄信息不可为空",8);
            }
            if(!PhoneFormatCheckUtils.isPhoneLegal(data.get("sentToPhone").toString())){
                throw new COREException("参数有误,邮寄手机号格式错误",8);
            }
        }

        if(data.get("email")==null){
            throw new COREException("参数有误,邮箱不可为空",8);
        }

        if(data.get("email").toString().equals("")&&NumberUtils.createInteger(data.get("isSend").toString())==0){
            throw new COREException("参数有误，请选择邮寄或电子邮件",8);
        }
    }

    public void fileUrlCheck(String url){
        String fileNameAndSuffix = url.substring(url.lastIndexOf("/") + 1);
        String dirPath = this.root + "/" + fileNameAndSuffix;
        File localFile = new File(dirPath);
        if(!localFile.exists()){
            throw new COREException("参数有误,文件url无效",8);
        }
    }

    public void payDateCheck(Map<String,Object> data){
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterApiService.getById(data.get("id").toString());

        if(data==null){
            throw new COREException("参数有误",8);
        }
        if(data.get("id")==null||StringUtils.isEmpty(data.get("id").toString())){
            throw new COREException("参数有误,公证id不可为空",8);
        }
        if(data.get("paymentStatus")==null||StringUtils.isEmpty(data.get("paymentStatus").toString())){
            throw new COREException("参数有误,支付状态不可为空",8);
        }
        if(PaymentStatusEnum.getEnumCode(data.get("paymentStatus").toString())==null){
            throw new COREException("参数有误,支付状态不存在",8);
        }
        if(notarzationMasterEntity==null){
            throw new COREException("数据不存在",7);
        }
    }

    public static void main(String args[]) throws Exception{
//        RestTemplate restTemplate = new RestTemplate();
//        MultiValueMap map = new LinkedMultiValueMap<>();
//        map.add("appId","wx84f8307ecb859d3e");
//        map.add("nonce","asdasd");
//        map.add("token","ZDM5ZDU2YmE4OTQwNDI5ZDkyMDJkNWNhZDc3YjFmNmQ=");
//        map.add("businessId","businessId");
//        FileSystemResource fileSystemResource = new FileSystemResource("notices/222.docx");
//        map.add("file", fileSystemResource);
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("timestamp", "12390139089");
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        HttpEntity<MultiValueMap> httpEntity = new HttpEntity<>(map,headers);
//        System.out.println( restTemplate.postForObject("http://127.0.0.1:9104/document/upload",httpEntity,ResponseBase.class));


    }

}
