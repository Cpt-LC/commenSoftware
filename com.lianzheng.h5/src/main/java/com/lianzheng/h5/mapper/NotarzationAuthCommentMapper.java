package com.lianzheng.h5.mapper;

import com.alibaba.fastjson.JSONObject;
import com.lianzheng.h5.entity.NotarzationAuthComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 公证申请修改内容表 Mapper 接口
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */
public interface NotarzationAuthCommentMapper extends BaseMapper<NotarzationAuthComment> {
    //`tableName`,`fieldName`,`comment`,`status`
    @Select("SELECT * FROM notarzation_auth_comment  WHERE notarzationId = #{id}")
    List<JSONObject> commentList(@Param("id") String id);
}
