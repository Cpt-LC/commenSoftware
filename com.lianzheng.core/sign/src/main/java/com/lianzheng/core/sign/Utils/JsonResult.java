package com.lianzheng.core.sign.Utils;

import lombok.Data;

@Data
public class JsonResult {
    /**
     * 错误码
     */
    private Integer code = 0;

    /**
     * 提示语
     */
    private String msg = "操作成功";

    /**
     * 返回对象
     */
    private Object data;
}