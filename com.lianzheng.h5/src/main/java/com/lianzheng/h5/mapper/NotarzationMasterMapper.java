package com.lianzheng.h5.mapper;

import com.alibaba.fastjson.JSONObject;
import com.lianzheng.h5.entity.NotarzationMaster;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 公证申请主体表 Mapper 接口
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-15
 */
public interface NotarzationMasterMapper extends BaseMapper<NotarzationMaster> {

//    @Select("SELECT notar.status,notar.realName,notar.processNo,notar.createdTime,notar.notarzationTypeCode,notar.id,notar.orderId,notar.signedReceipt,o.realAmount,o.paymentMode FROM notarzation_master notar LEFT JOIN `order` o ON notar.orderId = o.id WHERE notar.userId = #{userId} ORDER BY createdTime DESC")
//    List<JSONObject> homeList(@Param("userId") String userId);

    IPage<Map<String, Object>> homeList(IPage<Map<String, Object>> page, @Param("userId") String userId,@Param("index") Integer index);

    List<NotarzationMaster> getIsRepeat(@Param("userId")String userId ,@Param("code")String code ,@Param("isAgent")int isAgent);
}
