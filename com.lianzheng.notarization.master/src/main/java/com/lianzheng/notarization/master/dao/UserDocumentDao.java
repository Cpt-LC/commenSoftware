package com.lianzheng.notarization.master.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDocumentDao extends BaseMapper<DocumentEntity> {
    void deleteFileByCategoryCode(@Param("id") String id, @Param("categoryCode") String categoryCode);





}
