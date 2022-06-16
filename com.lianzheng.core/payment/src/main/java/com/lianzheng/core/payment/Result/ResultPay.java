package com.lianzheng.core.payment.Result;


public class ResultPay<T> {
    String msg;
    int code;
    Boolean success = true;
    T result;
    public ResultPay(){}
    public ResultPay(Boolean successful, String msg, int code, T result) {
        this.msg = msg;
        this.code = code;
        this.success = successful;
        this.result = result;
    }
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public void setResult(T result) {
        this.result = result;
    }
    public T getResult() {
        return result;
    }
}
