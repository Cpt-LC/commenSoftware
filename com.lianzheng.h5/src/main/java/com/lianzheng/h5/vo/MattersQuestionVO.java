package com.lianzheng.h5.vo;

import lombok.Data;

/**
 * 标注的问题
 *
 * @author JP
 * @date 2022-04-08
 */
@Data
public class MattersQuestionVO {
    /**
     * 问答的id
     */
    private String id;
    /**
     * 标注的错误内容
     */
    private String comment;
}
