package com.lianzheng.h5.common;

public final class ApiResult<T> {

    /**
     * 请求成功
     */
    public static final int SUCCESS = 1000;
    /**
     * 请求失败
     */
    public static final int ERROR = 4000;
    /**
     * 普通的错误提醒
     */
    public static final int ERROR_TOAST = 3333;
    /**
     * 重要的错误提醒
     */
    public static final int ERROR_IMPORTANCE = 4444;

    /**
     * 需要登录
     */
    public static final int TOKEN_ERROR = 101;
    /**
     * 需要登录
     */
    public static final int TOKEN_DATED = 102;


    /**
     * 返回码
     */
    private int code;
    /**
     * 返回数据
     */
    private T data;
    /**
     * 错误消息提示
     */
    private String msg;

    private ApiResult(int code) {
        this(code, null, null);
    }

    private ApiResult(int code, T data) {
        this(code, data, null);
    }

    private ApiResult(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }


    public static <T> ApiResult<T> success() {
        return new ApiResult<T>(SUCCESS);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<T>(SUCCESS, data);
    }

    public static <T> ApiResult<T> successMsg(String msg) {
        return new ApiResult<T>(SUCCESS, null, msg);
    }

    public static <T> ApiResult<T> error() {
        return new ApiResult<T>(ERROR);
    }

    public static <T> ApiResult<T> error(int code) {
        return new ApiResult<T>(code);
    }

//    public static <T> ApiResult<T> error(T data) {
//        return new ApiResult<T>(ERROR, data);
//    }

    public static <T> ApiResult<T> error(String msg) {
        return new ApiResult<T>(ERROR, null, msg);
    }

    public static <T> ApiResult<T> errorData(T data) {
        return new ApiResult<T>(ERROR, data, "");
    }

    public static <T> ApiResult<T> errorMsg(int code, String msg) {
        return new ApiResult<T>(code, null, msg);
    }

    public static <T> ApiResult<T> errorData(int code, T data, String msg) {
        return new ApiResult<T>(code, data, msg);
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    @Override
    public String toString() {
        return "{\"code\":" + getCode() + ",\"data\":\"" + getData() + "\",\"msg\": \"" + getMsg() + "\"}";
    }
}
