package com.lianzheng.job.core;

/**
 * @Description: 任务的配置类，从toml文件里读出
 * @author: 何江雁
 * @date: 2021年10月14日 14:54
 */

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class JobConfig {
	private String name;
	public String getName(){
		return this.name;
	}

	private String id;
	public String getID(){
		return this.id;
	}

	private String group;
	public String getGroup(){
		return this.group;
	}

	private String cron;
	public String getCron(){
		return this.cron;
	}

	private String jobClass;
	public String getJobClass(){
		return this.jobClass;
	}

	private String triggerName;
	public String getTriggerName(){
		return this.triggerName;
	}

	private String triggerGroupName;
	public String getTriggerGroupName(){
		return this.triggerGroupName;
	}

	private HashMap<String, Object> data;
	public HashMap<String, Object> getData(){
		return this.data;
	}
}
