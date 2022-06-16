package com.lianzheng.management.service.modules.notarization.quartz;


import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.auth.mgmt.utils.RedisUtils;
import com.lianzheng.management.service.modules.notarization.quartz.service.JobService;
import lombok.extern.java.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Log
@Component
@ConditionalOnProperty(prefix = "scheduling", name = "enabled")
public class generatePdfAfterPaidJob {

    @Autowired
    private JobService jobService;



    /*
        轮询订单获取订单生成文件
     */
//    @Scheduled(cron ="0 */1 * * * ?")
    public void pdf() throws Exception{
        jobService.generatePdfAfterPaidJob();
    }


    /**
     * 每年定时重置公正编号表
     * @throws Exception
     */
    @Scheduled(cron ="0 0 0 1 1 ?")
    public void resetCertificateId() throws Exception{
        log.info(String.format(" 每年定时重置公正编号表 时间:" + DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss")));
        jobService.resetCertificateId();
    }


    /**
     * 每两周删除一次文件
     * @throws Exception
     */
//    @Scheduled(cron ="0 0 0 1 1 ? *")
//    public void de() throws Exception{
//        log.info(String.format(" 每年定时重置公正编号表 时间:" + DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss")));

//    }

    /**
     *定时取消订单
     * @throws Exception
     *
     */
    @Scheduled(cron ="0 0 */1 * * ?")
    public void cancelNotarization() throws Exception{
        log.info(String.format(" 取消长时间未支付的订单 时间:" + DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss")));
        jobService.cancelNotarization();

    }

}
