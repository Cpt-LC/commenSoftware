package com.lianzheng.job.core;

/**
 * @Description: 容器监听器, 读取toml配置
 * @author: 何江雁
 * @date: 2021年10月14日 15:49
 */

import com.lianzheng.core.exceptionhandling.ExceptionFormatter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.List;

@Log4j2
@WebListener
public class QuartzJobListener implements ServletContextListener {

    @Override
    @SneakyThrows
    public void contextInitialized(ServletContextEvent arg0) {
        QuartzManager quartzManager = WebApplicationContextUtils.getWebApplicationContext(arg0.getServletContext()).getBean(QuartzManager.class);

        JobsConfig config = WebApplicationContextUtils.getWebApplicationContext(arg0.getServletContext()).getBean(JobsConfig.class);
        List<JobConfig> jobs = config.jobs();

        Boolean hasError = false;
        for (JobConfig job : jobs) {
            try {
                quartzManager.addJob(job);
            } catch (Exception e) {
                hasError = true;
                String exMessage = ExceptionFormatter.Format(e);
                log.fatal(String.format("'%s.%s' encountered the error: %s",
                        job.getGroup(), job.getName().isEmpty() ? job.getJobClass() : job.getName(),exMessage));
                e.printStackTrace();
            }
        }

        if(hasError){
            log.warn("QuartzJobListener 启动了，但是有job启动失败");
//            quartzManager.shutdownJobs();
        }
        else{
            log.info("QuartzJobListener 启动了");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

}
