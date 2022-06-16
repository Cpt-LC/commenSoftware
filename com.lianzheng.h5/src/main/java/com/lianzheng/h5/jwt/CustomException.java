package com.lianzheng.h5.jwt;

import lombok.Data;

//这里可继承你需要定义的错误
@Data
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    //可以用来接受我们方法中传的参数
    private int code=401;
    private String msg;

    public CustomException(String msg) {
        super(msg);
        this.msg=msg;

    }
}
