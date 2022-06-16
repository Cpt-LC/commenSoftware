package com.lianzheng.h5.enums;

import lombok.Getter;

/**
 * 状态枚举
 *
 * @author JP
 * @date 2022-04-08
 */
@Getter
public enum StatusEnum {


    /**
     * 0 - 失效
     */
    INVALID(0, "失效"),
    /**
     * 1 - 有效
     */
    VALID(1, "有效"),;

    private final int code;
    private final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
