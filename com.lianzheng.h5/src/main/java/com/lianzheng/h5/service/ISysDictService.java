package com.lianzheng.h5.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.h5.entity.SysDict;

import java.util.List;

/**
 * <p>
 * 系统字典 服务类
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */
public interface ISysDictService extends IService<SysDict> {


    /**
     * 根据type获取字典集合
     *
     * @param type
     * @return
     */
    List<SysDict> getDictListByType(String type);

}
