package com.lianzheng.management.login.modules.login.controller;


import com.lianzheng.core.auth.mgmt.annotation.AuditLog;
import com.lianzheng.core.auth.mgmt.entity.SysRoleEntity;
import com.lianzheng.core.auth.mgmt.service.SysRoleMenuService;
import com.lianzheng.core.auth.mgmt.service.SysRoleService;
import com.lianzheng.core.auth.mgmt.utils.PageUtils;
import com.lianzheng.core.auth.mgmt.validator.ValidatorUtils;
import com.lianzheng.core.server.ResponseBase;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 * @author gang.shen@kedata.com
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends AbstractController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 角色列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:role:list")
    public ResponseBase list(@RequestParam Map<String, Object> params) {

        PageUtils page = sysRoleService.queryPage(params);

        return ResponseBase.ok().put("page", page);
    }

    /**
     * 角色列表
     */
    @GetMapping("/select")
    @RequiresPermissions("sys:role:select")
    public ResponseBase select() {
        Map<String, Object> map = new HashMap<>();

        List<SysRoleEntity> list = (List<SysRoleEntity>) sysRoleService.listByMap(map);
        //移除超级管理员
        list.removeIf(item -> item.getId() == 1);

        return ResponseBase.ok().put("list", list);
    }

    /**
     * 角色信息
     */
    @GetMapping("/info/{roleId}")
    @RequiresPermissions("sys:role:info")
    public ResponseBase info(@PathVariable("roleId") Long roleId) {
        SysRoleEntity role = sysRoleService.getById(roleId);

        //查询角色对应的菜单
        List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
        role.setMenuIdList(menuIdList);

        return ResponseBase.ok().put("role", role);
    }

    /**
     * 保存角色
     */
    @AuditLog("保存角色")
    @PostMapping("/save")
    @RequiresPermissions("sys:role:save")
    public ResponseBase save(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);
        role.setCreatedBy(getUserId());
        sysRoleService.saveRole(role);

        return ResponseBase.ok();
    }

    /**
     * 修改角色
     */
    @AuditLog("修改角色")
    @PostMapping("/update")
    @RequiresPermissions("sys:role:update")
    public ResponseBase update(@RequestBody SysRoleEntity role) {
        System.out.println(role);
        ValidatorUtils.validateEntity(role);
        System.out.println(role);
        role.setCreatedBy(getUserId());
        sysRoleService.update(role);

        return ResponseBase.ok();
    }

    /**
     * 删除角色
     */
    @AuditLog("删除角色")
    @PostMapping("/delete")
    @RequiresPermissions("sys:role:delete")
    public ResponseBase delete(@RequestBody Long[] roleIds) {
        System.out.println(roleIds.toString());
        sysRoleService.deleteBatch(roleIds);

        return ResponseBase.ok();
    }
}
