package com.lianzheng.job.core;

/**
 * @Description: 任务的配置类，从toml文件里读出
 * @author: 何江雁
 * @date: 2021年10月14日 14:54
 */

import com.lianzheng.core.config.TomlParser;
import com.moandjiezana.toml.Toml;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Configuration
public class JobsConfig {
	@Bean
	public List<JobConfig> jobs() throws IOException {
		Toml toml = TomlParser.rawParse("classpath:config.quartz.toml");
		List<Toml>  jobs = toml.getTables("jobs" );
		List<JobConfig> result = new ArrayList<>();
		jobs.forEach((t)->{
			JobConfig job = t.to(JobConfig.class);
			result.add(job);
		});
		return result;
	}
}
