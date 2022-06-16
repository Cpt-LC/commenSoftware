package com.lianzheng.h5.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.h5.dto.AddNotarizationDTO;
import com.lianzheng.h5.dto.UpdateNotarizationDTO;
import com.lianzheng.h5.entity.NotarizationMattersQuestion;
import com.lianzheng.h5.vo.AddNotarizationVO;

/**
 * <p>
 * 事项问题 服务类
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */
public interface INotarizationMattersQuestionService extends IService<NotarizationMattersQuestion> {


    /**
     * 新增认证
     *
     * @param dto
     * @return
     */
    AddNotarizationVO addRecord(AddNotarizationDTO dto);

    /**
     * 获取事项详情
     *
     * @param masterId
     * @param userId
     * @return
     */
    JSONObject getDetail(String masterId, String userId);

    /**
     * 更新认证
     *
     * @param dto
     * @return
     */
    Boolean updateRecord(UpdateNotarizationDTO dto);
}
