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
        final Map<String,String> srcParam = new HashMap<>(); //根据环境配置读取支付信息的redis环境
        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        switch (documentParam.getEnvironment()){
            case "zero":
                srcParam.put("src","PUSH_NOTARIZATIONID_AFTERPAY");
                srcParam.put("back","bk:PUSH_NOTARIZATIONID_AFTERPAY");break;
            case "one":
                srcParam.put("src","PUSH_NOTARIZATIONID_AFTERPAY_ONE");
                srcParam.put("back","bk:PUSH_NOTARIZATIONID_AFTERPAY_ONE");break;
            default:
                throw new RuntimeException("配有配置项目环境");
        }

        threadPool= Executors.newFixedThreadPool(10);
        String src = srcParam.get("src");
        String back = srcParam.get("back");
        while(true){
            List<Object> obj = redisUtils.blocking(src,back);//获取消息列表
            if(obj!=null&&obj.size()>0){
                for(Object item:obj) {
                    if(item==null){
                        continue;
                    }
                    NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(item.toString());
                    OrderEntity orderEntity =  userOrderDao.selectById(notarzationMasterEntity.getOrderId());
                    if(orderEntity.getPaymentStatus().equals(PaymentStatusEnum.NotPaid.getCode())&&orderEntity.getPaymentMode().equals(PaymentModeEnum.Online.getCode())){ //如果线上支付，并且没支付
                        log.info(item.toString()+"未支付，数据异常");
                        continue;
                    }
                    log.info(item.toString()+"支付完成，开始生成文件");
                    //异步生成文件
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
                                    break;//非昆山生成公证书
                            }

                            redisUtils.lerm(back,1,item.toString());//删除完成的任务
                        } catch (Exception e) {
                            log.error(e.getMessage(),e);
                            e.printStackTrace();
                        }
                    }));
                    //线上修改办理状态


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
        OrderLogicalParam orderLogicalParam = ConfigParameterUtil.getOrderLogical();//订单参数
        MessagesParam messagesParam =  ConfigParameterUtil.getMessages();//短信参数
        //取消所有的超时公证
        userNotarzationMasterDao.cancelNotarization(orderLogicalParam.getNotarizationExpireTime());
        int time = orderLogicalParam.getNotarizationExpireTime()-orderLogicalParam.getNotarizationRestTime();
        //获取需要发送短信的公证，该处time+1必须与定时任务执行时隔相同，避免出现重复发送短信和漏发短信的情况
        List<NotarzationMasterEntity> notarzationMasterEntities = userNotarzationMasterDao.getCancelNotarizationRestTime(time+1,time);
        for(NotarzationMasterEntity item:notarzationMasterEntities){
            String realName = item.getRealName();
            String phone  = item.getPhone();
            String processNo =item.getProcessNo();
            Map<String,Object> sendMap =new HashMap<>();
            ResponseBase result = null;
            sendMap.put("name",realName);
            sendMap.put("processNo",processNo);
            sendMap.put("action","将于一天内失效");
            sendMap.put("todo","及时处理");
            result= MessageUtils.doSendMessage("notice",messagesParam.getSignName(),new String[]{phone},sendMap);
            log.info(item.getProcessNo()+result.toString());
        }
    }



}
