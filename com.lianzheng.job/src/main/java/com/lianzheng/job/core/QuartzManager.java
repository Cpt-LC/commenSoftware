package com.lianzheng.job.core;

/**
 * @Description: job的配置管理类，负责job的添删改查、启动停止
 * @author: 何江雁
 * @date: 2021年10月15日 14:09
 */

import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Log4j2
@Service
public class QuartzManager {
    @Autowired
    private Scheduler scheduler;

    /**
     * 功能： 添加一个定时任务
     *
     * @param config job在classpath:config.quartz.toml的配置
     */
    public void addJob(JobConfig config) throws SchedulerException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String className = config.getJobClass();
        Class jobClass = (Class<? extends Job>) Class.forName(className).newInstance().getClass();

        String jobName = config.getName();
        String jobGroupName = config.getGroup();
        String triggerName = config.getTriggerName();
        String triggerGroupName = config.getTriggerGroupName();
        String cron = config.getCron();
        Map<String, Object> objects = config.getData();
        Boolean hasCron = cron != null && !cron.isEmpty();

        if (jobName == null || jobName.isEmpty()) {
            //设置默认值，取class名
            jobName = jobClass.getSimpleName();
        }
        if (jobGroupName == null || jobGroupName.isEmpty()) {
            //设置默认值，取模块名
            String packageName = jobClass.getPackage().getName();
            String[] packageNames = packageName.split("\\.");
            jobGroupName = packageNames[packageNames.length - 1];
        }

        if (triggerName == null || triggerName.isEmpty()) {
            //设置默认值
            triggerName = jobName + "_Trigger";
        }

        if (triggerGroupName == null || triggerGroupName.isEmpty()) {
            //设置默认值
            triggerGroupName = hasCron ? "cron" : "simple";
        }
        // 任务名，任务组，任务执行类
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
        log.info("jobDetail.getKey:" + jobDetail.getKey());
        // 触发器
        if (objects != null) {
            jobDetail.getJobDataMap().put("data", objects);
        }

        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        // 触发器名,触发器组
        triggerBuilder.withIdentity(triggerName, triggerGroupName);
        triggerBuilder.startNow();

        if (hasCron) {
            // 触发器时间设定
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
        }
        // 创建Trigger对象
        Trigger trigger = triggerBuilder.build();
        // 调度容器设置JobDetail和Trigger
        scheduler.scheduleJob(jobDetail, trigger);
        // 启动
        if (!scheduler.isShutdown()) {
            scheduler.start();
        }

    }

    /**
     * 功能：修改一个任务的触发时间
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param cron             时间设置，参考quartz说明文档
     */
    public void modifyJobTime(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
                              String cron) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                // 触发器
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名,触发器组
                triggerBuilder.withIdentity(triggerName, triggerGroupName);
                triggerBuilder.startNow();
                // 触发器时间设定
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                // 创建Trigger对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 方式一 ：修改一个任务的触发时间
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /*
     * @Description: 即时重跑指定的job，应用于反复跑的情形
     * @author: 何江雁
     * @date: 2021/10/22 13:19
     * @param null:
     * @Return:
     */
    public void rerunAt(Trigger trigger, long seconds) throws SchedulerException {
        TriggerBuilder<Trigger> triggerBuilder = (TriggerBuilder<Trigger>) trigger.getTriggerBuilder();

        // 触发器名,触发器组
        triggerBuilder.withIdentity(trigger.getKey());
        Date now = new Date();
        triggerBuilder.startAt(new Date(now.getTime() + seconds * 1000));

        // 创建Trigger对象
        trigger = triggerBuilder.build();
        System.out.println("rerun at "+trigger.getStartTime());
        // 调度容器设置JobDetail和Trigger
        scheduler.rescheduleJob(trigger.getKey(), trigger);
    }

    /**
     * 功能: 移除一个任务
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     */
    public void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
        try {

            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            // 移除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));

            System.out.println("removeJob:" + JobKey.jobKey(jobName));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 功能：启动所有定时任务
     */
    public void startJobs() {
        try {
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 功能：关闭所有定时任务
     */
    public void shutdownJobs() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
