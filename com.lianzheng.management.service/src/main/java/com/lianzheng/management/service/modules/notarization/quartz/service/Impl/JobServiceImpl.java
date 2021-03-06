package com.lianzheng.management.service.modules.notarization.quartz.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lianzheng.core.auth.mgmt.utils.RedisUtils;

import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.notarization.master.configParameter.param.DocumentParam;
import com.lianzheng.notarization.master.configParameter.param.MessagesParam;
import com.lianzheng.notarization.master.configParameter.param.OrderLogicalParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;

import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.management.service.modules.notarization.quartz.service.JobService;
import com.lianzheng.management.service.modules.notarization.utils.MasterServiceBeanUtil;
import com.lianzheng.notarization.master.dao.UserNotarzationMasterDao;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.OrderEntity;
import com.lianzheng.notarization.master.enums.PaymentModeEnum;
import com.lianzheng.notarization.master.enums.PaymentStatusEnum;
import com.lianzheng.notarization.master.enums.ProcessStatusEnum;
import com.lianzheng.notarization.master.service.MasterService;
import com.lianzheng.notarization.master.service.TruthService;
import com.lianzheng.notarization.master.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@DS("h5")
public class JobServiceImpl implements JobService {


    @Autowired
    private UserNotarzationMasterDao userNotarzationMasterDao;
    @Autowired
    private UserOrderDao userOrderDao;
    @Autowired
    private TruthService truthService;
    @Autowired
    private RedisUtils redisUtils;

    private ExecutorService threadPool;
    @Override
    public  void generatePdfAfterPaidJob() throws Exception {
        final Map<String,String> srcParam = new HashMap<>(); //???????????????????????????????????????redis??????
        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        switch (documentParam.getEnvironment()){
            case "zero":
                srcParam.put("src","PUSH_NOTARIZATIONID_AFTERPAY");
                srcParam.put("back","bk:PUSH_NOTARIZATIONID_AFTERPAY");break;
            case "one":
                srcParam.put("src","PUSH_NOTARIZATIONID_AFTERPAY_ONE");
                srcParam.put("back","bk:PUSH_NOTARIZATIONID_AFTERPAY_ONE");break;
            default:
                throw new RuntimeException("????????????????????????");
        }

        threadPool= Executors.newFixedThreadPool(10);
        String src = srcParam.get("src");
        String back = srcParam.get("back");
        while(true){
            List<Object> obj = redisUtils.blocking(src,back);//??????????????????
            if(obj!=null&&obj.size()>0){
                for(Object item:obj) {
                    if(item==null){
                        continue;
                    }
                    NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(item.toString());
                    OrderEntity orderEntity =  userOrderDao.selectById(notarzationMasterEntity.getOrderId());
                    if(orderEntity.getPaymentStatus().equals(PaymentStatusEnum.NotPaid.getCode())&&orderEntity.getPaymentMode().equals(PaymentModeEnum.Online.getCode())){ //????????????????????????????????????
                        log.info(item.toString()+"????????????????????????");
                        continue;
                    }
                    log.info(item.toString()+"?????????????????????????????????");
                    //??????????????????
                    threadPool.execute(new Thread(() -> {
                        try {
                            MasterService masterService = MasterServiceBeanUtil.getMasterServiceBean(notarzationMasterEntity.getNotarzationTypeCode());
                            masterService.generatePdf(notarzationMasterEntity.getId());

                            switch (documentParam.getEnvironment()){
                                case "zero":
                                    if(orderEntity.getPaymentMode().equals(PaymentModeEnum.Online.getCode())){
                                        truthService.pushToTruth(notarzationMasterEntity);
                                        notarzationMasterEntity.setProcessStatus(ProcessStatusEnum.APPROVING.getCode());
                                        userNotarzationMasterDao.updateById(notarzationMasterEntity);
                                    }
                                    break;
                                case "one":break;
                                default:
                                    masterService.generatePreparePaper(notarzationMasterEntity);
                                    break;//????????????????????????
                            }

                            redisUtils.lerm(back,1,item.toString());//?????????????????????
                        } catch (Exception e) {
                            log.error(e.getMessage(),e);
                            e.printStackTrace();
                        }
                    }));
                    //????????????????????????


                }
            }
        }
//        threadPool.shutdown();
//        List<NotarzationMasterEntity> notarzationMasterEntities =  userNotarzationMasterDao.getPaidNotarization();
    }

    @Override
    public void resetCertificateId() {
        userNotarzationMasterDao.truncateTable();
    }


    @Override
    public void cancelNotarization(){
        OrderLogicalParam orderLogicalParam = ConfigParameterUtil.getOrderLogical();//????????????
        MessagesParam messagesParam =  ConfigParameterUtil.getMessages();//????????????
        //???????????????????????????
        userNotarzationMasterDao.cancelNotarization(orderLogicalParam.getNotarizationExpireTime());
        int time = orderLogicalParam.getNotarizationExpireTime()-orderLogicalParam.getNotarizationRestTime();
        //??????????????????????????????????????????time+1????????????????????????????????????????????????????????????????????????????????????????????????
        List<NotarzationMasterEntity> notarzationMasterEntities = userNotarzationMasterDao.getCancelNotarizationRestTime(time+1,time);
        for(NotarzationMasterEntity item:notarzationMasterEntities){
            String realName = item.getRealName();
            String phone  = item.getPhone();
            String processNo =item.getProcessNo();
            Map<String,Object> sendMap =new HashMap<>();
            ResponseBase result = null;
            sendMap.put("name",realName);
            sendMap.put("processNo",processNo);
            sendMap.put("action","?????????????????????");
            sendMap.put("todo","????????????");
            result= MessageUtils.doSendMessage("notice",messagesParam.getSignName(),new String[]{phone},sendMap);
            log.info(item.getProcessNo()+result.toString());
        }
    }



}
