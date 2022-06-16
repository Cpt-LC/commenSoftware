package com.lianzheng.job.core;

import com.lianzheng.core.exceptionhandling.ExceptionFormatter;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description: Job的基类
 * @author: 何江雁
 * @date: 2021年10月14日 15:03
 */
@Log4j2
@Component
public abstract class JobBase extends QuartzJobBean {
    @Override
    protected final void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDetail detail = context.getJobDetail();
        Long startTime = System.currentTimeMillis();
        JobKey key = detail.getKey();

        log.info(String.format("'%s'运行", key));
        try {
            executeInside(context);
        }
        catch (Exception ex){
            String exMessage = ExceptionFormatter.Format(ex);
            log.error(String.format("'%s'遇到错误:%s", key, exMessage));
            ex.printStackTrace();
        }
        finally {
            Long duration = System.currentTimeMillis()- startTime;
            BigDecimal diff = new BigDecimal(duration).divide(new BigDecimal(1000));
            log.info(String.format("'%s'已完成，耗时%ss", key, diff));
        }
    }

    protected abstract void executeInside(JobExecutionContext context) throws Exception;
}
