package com.lianzheng.h5.mapper;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianzheng.h5.entity.Document;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 上传文件 Mapper 接口
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */
public interface DocumentMapper extends BaseMapper<Document> {

    @Select("SELECT  doc.categoryCode,doc.uploadedAbsolutePath FROM document doc  WHERE doc.refererId = #{refererId}")
    List<JSONObject> associationDocument(@Param("refererId") String refererId);

    @Select("<script>" +
            "SELECT doc.categoryCode,doc.uploadedAbsolutePath,doc.fileName,notar.createdTime,notar.processNo,notar.id " +
            "FROM document doc LEFT JOIN notarzation_master notar ON doc.refererId = notar.id " +
            "WHERE doc.refererTableName = 'notarzation_master'  AND doc.refererId = #{refererId} AND doc.isDeleted = 0" +
            " <if test=\"categoryCodes != null and categoryCodes != '' \">" +
            " AND categoryCode in (${categoryCodes}) " +
            " </if>" +
            " </script>")
    List<JSONObject> recordDocument(@Param("refererId") String refererId,
                                    @Param("categoryCodes") String categoryCodes);

    @Delete(" <script>" +
            " DELETE FROM document doc WHERE doc.uploadedAbsolutePath in " +
            " <foreach collection='list' item='item' open='(' separator=',' close=')'>" +
            " #{item} " +
            " </foreach>" +
            " </script>")
    void deleteOldDoc(@Param("list") List<String> list);
}
