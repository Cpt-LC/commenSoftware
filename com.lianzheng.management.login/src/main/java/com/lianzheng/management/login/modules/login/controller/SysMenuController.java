package com.lianzheng.management.login.modules.login.controller;


import com.lianzheng.core.auth.mgmt.annotation.AuditLog;
import com.lianzheng.core.auth.mgmt.entity.SysMenuEntity;
import com.lianzheng.core.auth.mgmt.service.ShiroService;
import com.lianzheng.core.auth.mgmt.service.SysMenuService;
import com.lianzheng.core.server.ResponseBase;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 系统菜单
 *
 * @author gang.shen@kedata.com
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends AbstractController {
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private ShiroService shiroService;

	/**
	 * 导航菜单
	 */
	@GetMapping("/nav")
	public ResponseBase nav(){
		List<SysMenuEntity> menuList = sysMenuService.getUserMenuList(getUserId());
		Set<String> permissions = shiroService.getUserPermissions(getUserId());
		return ResponseBase.ok().put("menuList", menuList).put("permissions", permissions);
	}
	
	/**
	 * 所有菜单列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:menu:list")
	public List<SysMenuEntity> list(){
		List<SysMenuEntity> menuList = sysMenuService.list();
		for(SysMenuEntity sysMenuEntity : menuList){
			SysMenuEntity parentMenuEntity = sysMenuService.getById(sysMenuEntity.getParentId());
			if(parentMenuEntity != null){
				sysMenuEntity.setParentName(parentMenuEntity.getName());
			}
		}

		return menuList;
	}
	
	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@GetMapping("/select")
	@RequiresPermissions("sys:menu:select")
	public ResponseBase select(){
		//查询列表数据
		List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList();
		
		//添加顶级菜单
		SysMenuEntity root = new SysMenuEntity();
		root.setId(0L);
		root.setName("一级菜单");
		root.setParentId(-1L);
		root.setOpen(true);
		menuList.add(root);
		
		return ResponseBase.ok().put("menuList", menuList);
	}
	
	/**
	 * 菜单信息
	 */
	@GetMapping("/info/{menuId}")
	@RequiresPermissions("sys:menu:info")
	public ResponseBase info(@PathVariable("menuId") Long menuId){
		SysMenuEntity menu = sysMenuService.getById(menuId);
		return ResponseBase.ok().put("menu", menu);
	}
	
	/**
	 * 保存
	 */
	@AuditLog("保存菜单")
	@PostMapping("/save")
	@RequiresPermissions("sys:menu:save")
	public ResponseBase save(@RequestBody SysMenuEntity menu){
		sysMenuService.save(menu);
		return ResponseBase.ok();
	}
	
	/**
	 * 修改
	 */
	@AuditLog("修改菜单")
	@PostMapping("/update")
	@RequiresPermissions("sys:menu:update")
	public ResponseBase update(@RequestBody SysMenuEntity menu){
				
		sysMenuService.updateById(menu);
		
		return ResponseBase.ok();
	}
	
	/**
	 * 删除
	 */
	@AuditLog("删除菜单")
	@PostMapping("/delete/{menuId}")
	@RequiresPermissions("sys:menu:delete")
	public ResponseBase delete(@PathVariable("menuId") long menuId){
		if(menuId <= 4){
			return ResponseBase.error("系统菜单，不能删除");
		}

		//判断是否有子菜单或按钮
		List<SysMenuEntity> menuList = sysMenuService.queryListParentId(menuId);
		if(menuList.size() > 0){
			return ResponseBase.error("请先删除子菜单或按钮");
		}

		sysMenuService.delete(menuId);

		return ResponseBase.ok();
	}

}
