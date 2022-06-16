package com.lianzheng.h5.common;

public class GlobalException extends RuntimeException {

    /**
     * 错误码，和http状态码一致
     **/
    private Integer code;

    /**
     * 错误消息
     **/
    private String msg;

    public GlobalException(String message, Integer code) {
        this.code = code;
        this.msg = message;
    }

    public GlobalException(String message) {
        this.code = 4000;
        this.msg = message;
    }

    public GlobalException(Integer code) {
        this.code = code;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }
}