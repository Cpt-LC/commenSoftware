package com.lianzheng.job.sample;

import com.lianzheng.job.core.JobBase;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description: job示例
 * @author: 何江雁
 * @date: 2021年10月14日 14:16
 */
@Log4j2
@Component
public class SampleScheduleJob  extends JobBase {
    @Resource(name = "dbApp")
    private JdbcTemplate jdbcTemplate;

    @Override
    protected void executeInside(JobExecutionContext context) throws JobExecutionException {
        String sql = "select id, realName from user limit 5;";
        List<Map<String, Object>> result = jdbcTemplate
                .queryForList(sql);
        System.out.println(result);
    }
}
