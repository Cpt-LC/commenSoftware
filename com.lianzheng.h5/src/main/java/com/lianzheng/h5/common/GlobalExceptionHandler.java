package com.lianzheng.h5.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//全局异常处理器
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResult<?> handleException(Exception exception) {
        log.error("出现错误, 捕获: >> "+exception.toString());
        exception.printStackTrace();
        return ApiResult.errorMsg(500, exception.getMessage());
    }
}
