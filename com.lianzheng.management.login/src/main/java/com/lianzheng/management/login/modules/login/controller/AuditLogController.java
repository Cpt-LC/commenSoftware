package com.lianzheng.management.login.modules.login.controller;

import com.lianzheng.core.auth.mgmt.utils.PageUtils;
import com.lianzheng.core.server.ResponseBase;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 系统日志
 *
 * @author gang.shen@kedata.com
 */
@RestController
@RequestMapping("/sys/log")
public class AuditLogController {
	@Autowired
	private com.lianzheng.core.auth.mgmt.service.AuditLogService auditLogService;

	/**
	 * 列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:log:list")
	public ResponseBase list(@RequestParam Map<String, Object> params){
		PageUtils page = auditLogService.queryPage(params);

		return ResponseBase.ok().put("page", page);
	}
	
}
