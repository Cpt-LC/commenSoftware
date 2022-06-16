package com.lianzheng.h5.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.h5.entity.SysDict;
import com.lianzheng.h5.mapper.SysDictMapper;
import com.lianzheng.h5.service.ISysDictService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统字典 服务实现类
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {


    @Override
    public List<SysDict> getDictListByType(String type) {
        return this.lambdaQuery()
                .eq(SysDict::getType, type)
                .eq(SysDict::getDelFlag, false)
                .orderByAsc(SysDict::getSort)
                .list();
    }
}
