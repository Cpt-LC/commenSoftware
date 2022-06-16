package com.lianzheng.core.auth.mgmt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.core.auth.mgmt.config.ShiroUtils;
import com.lianzheng.core.auth.mgmt.dao.SysNotarialOfficeDao;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import com.lianzheng.core.auth.mgmt.service.SysNotarialOfficeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公证处管理
 *
 * @author lxk
 **/
@Service
public class SysNotarialOfficeServiceImpl extends ServiceImpl<SysNotarialOfficeDao, SysNotarialOfficeEntity> implements SysNotarialOfficeService {

    @Resource
    private SysNotarialOfficeDao sysNotarialOfficeDao;

    @Override
    public Map<String, Object> findList(SysNotarialOfficeEntity sysNotarialOfficeEntity) {
        Map<String, Object> map = new HashMap<>();
        if (sysNotarialOfficeEntity.getPageNo() != null && sysNotarialOfficeEntity.getPageSize() != null) {
            Integer pageNo = sysNotarialOfficeEntity.getPageNo();
            Integer pageSize = sysNotarialOfficeEntity.getPageSize();
            sysNotarialOfficeEntity.setPageNo((pageNo - 1) * pageSize);
            sysNotarialOfficeEntity.setPageSize(pageNo * pageSize);
        } else {
            sysNotarialOfficeEntity.setPageNo(0);
            sysNotarialOfficeEntity.setPageSize(10);
        }
        List<SysNotarialOfficeEntity> sysNotarialOfficeEntities = sysNotarialOfficeDao.selectByName(sysNotarialOfficeEntity);
        map.put("list", sysNotarialOfficeEntities);
        map.put("count", sysNotarialOfficeEntities.size());
        return map;
    }

    @Override
    public void insert(SysNotarialOfficeEntity sysNotarialOfficeEntity) {
        Long userId = ShiroUtils.getUserId();
        sysNotarialOfficeEntity.setCreatedBy(userId);
        sysNotarialOfficeEntity.setUpdatedBy(userId);
        sysNotarialOfficeDao.insert(sysNotarialOfficeEntity);
    }

    @Override
    public void update(SysNotarialOfficeEntity sysNotarialOfficeEntity) {
        Long userId = ShiroUtils.getUserId();
        sysNotarialOfficeEntity.setUpdatedBy(userId);
        sysNotarialOfficeEntity.setUpdatedTime(new Date());
        sysNotarialOfficeDao.updateById(sysNotarialOfficeEntity);
    }

}
