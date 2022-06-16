package com.lianzheng.management.service.modules.notarization.controller;


import com.lianzheng.core.auth.mgmt.annotation.AuditLog;
import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.service.SysUserRoleService;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.core.auth.mgmt.utils.PageUtils;
import com.lianzheng.core.auth.mgmt.validator.Assert;
import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.management.service.modules.notarization.form.PasswordForm;
import org.apache.commons.lang.ArrayUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统用户
 *
 * @author gang.shen@kedata.com
 */
@RestController
@RequestMapping("/sys/user")
public class  SysUserController extends AbstractController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserRoleService sysUserRoleService;


	/**
	 * 所有用户列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:user:list")
	public ResponseBase list(@RequestParam Map<String, Object> params){
		PageUtils page = sysUserService.queryPage(params);

		return ResponseBase.ok().put("page", page);
	}
	
	/**
	 * 获取登录的用户信息
	 */
	@GetMapping("/info")
	public ResponseBase info(){
		return ResponseBase.ok().put("user", getUser());
	}
	
	/**
	 * 修改登录用户密码
	 */
	@AuditLog("修改密码")
	@PostMapping("/password")
	public ResponseBase password(@RequestBody PasswordForm form){
		Assert.isBlank(form.getNewPassword(), "新密码不为能空");
		
		//sha256加密
		String password = new Sha256Hash(form.getPassword(), getUser().getSalt()).toHex();
		//sha256加密
		String newPassword = new Sha256Hash(form.getNewPassword(), getUser().getSalt()).toHex();
				
		//更新密码
		boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
		if(!flag){
			return ResponseBase.error("原密码不正确");
		}
		
		return ResponseBase.ok();
	}
	
	/**
	 * 用户信息
	 */
	@GetMapping("/info/{userId}")
	@RequiresPermissions("sys:user:info")
	public ResponseBase info(@PathVariable("userId") Long userId){
		SysUserEntity user = sysUserService.getById(userId);
		
		//获取用户所属的角色列表
		List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
		user.setRoleIdList(roleIdList);
		
		return ResponseBase.ok().put("user", user);
	}
	
	/**
	 * 保存用户
	 */
	@AuditLog("保存用户")
	@PostMapping("/save")
	@RequiresPermissions("sys:user:save")
	public ResponseBase save(@RequestBody SysUserEntity user){

//		ValidatorUtils.validateEntity(user);
		user.setCreatedBy(getUserId());
		sysUserService.saveUser(user);
		
		return ResponseBase.ok();
	}
	
	/**
	 * 修改用户
	 */
	@AuditLog("修改用户")
	@PostMapping("/update")
	@RequiresPermissions("sys:user:update")
	public ResponseBase update(@RequestBody SysUserEntity user){
//		ValidatorUtils.validateEntity(user, UpdateGroup.class);

		user.setCreatedBy(getUserId());
		sysUserService.update(user);
		
		return ResponseBase.ok();
	}
	
	/**
	 * 删除用户
	 */
	@AuditLog("删除用户")
	@PostMapping("/delete")
	@RequiresPermissions("sys:user:delete")
	public ResponseBase delete(@RequestBody Long[] userIds){
		if(ArrayUtils.contains(userIds, 1L)){
			return ResponseBase.error("系统管理员不能删除");
		}
		
		if(ArrayUtils.contains(userIds, getUserId())){
			return ResponseBase.error("当前用户不能删除");
		}
		
		sysUserService.deleteBatch(userIds);
		
		return ResponseBase.ok();
	}

	/**
	 * 获取公证员列表
	 * @return
	 */
	@PostMapping("/getAction")
	public ResponseBase getAction(String roleName){
		if(roleName==null||roleName.equals("")){
			return ResponseBase.error("参数错误");
		}
		List<SysUserEntity> sysUserEntityList =sysUserService.getUser(roleName,null);
		return ResponseBase.ok().put("Action",sysUserEntityList);
	}


}
