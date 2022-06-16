package com.lianzheng.h5.mapper;

import com.alibaba.fastjson.JSONObject;
import com.lianzheng.h5.entity.NotarzationGraduation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 毕业公证申请详情表 Mapper 接口
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */
public interface NotarzationGraduationMapper extends BaseMapper<NotarzationGraduation> {

    @Select("SELECT * FROM notarzation_auth_comment  WHERE notarzationId = #{id}")
    List<JSONObject> commentList(@Param("id") String id);
}
