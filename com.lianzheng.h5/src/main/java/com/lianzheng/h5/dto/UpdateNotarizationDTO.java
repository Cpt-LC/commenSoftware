package com.lianzheng.h5.dto;

import lombok.Data;

/**
 * <p>
 * 事项新增
 * </p>
 *
 * @author JP
 * @since 2022-04-08
 */
@Data
public class UpdateNotarizationDTO extends AddNotarizationDTO {


    // 审批状态
    private String pendingApproved;

}
