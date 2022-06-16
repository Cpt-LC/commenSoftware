package com.lianzheng.management.service.modules.notarization.quartz;

import com.lianzheng.management.service.modules.notarization.quartz.service.JobService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**

 **/
@Slf4j
@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Autowired
    private JobService jobService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("============spring boot项目启动执行程序==========");
        new Thread(() -> {
            try {
                jobService.generatePdfAfterPaidJob();
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                e.printStackTrace();
            }

        }).start();
    }
}
