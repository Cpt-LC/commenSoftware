package com.lianzheng.core.wechat.sessionstore;

/**
 * @Description: 企业微信会话接口异常类
 * @author: 何江雁
 * @date: 2021年10月19日 13:15
 */
public class WechatSessionStoreAPIException extends RuntimeException {
    private static final long serialVersionUID = 1837196141059742528L;

    private long code;

    public long getCode() {
        return this.code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    /*
    * @Description:
    * @author: 何江雁
    * @date: 2021/10/19 13:21
    * @param code:
//10000	参数错误，请求参数错误
//10001	网络错误，网络请求错误
//10002	数据解析失败
//10003	系统失败
//10004	密钥错误导致加密失败
//10005	fileid错误
//10006	解密失败
//10007 找不到消息加密版本的私钥，需要重新传入私钥对
//10008 解析encrypt_key出错
//10009 ip非法
//10010 数据过期
    * @Return:
    */
    public WechatSessionStoreAPIException(long code) {
        super(convertToMessage(code));
        this.code = code;
    }

    private static String convertToMessage(long code) {
        String message = "";
        if (code == 10000) {
            message = "参数错误，请求参数错误";
        } else if (code == 10001) {
            message = "网络错误，网络请求错误";
        } else if (code == 10002) {
            message = "数据解析失败";
        } else if (code == 10003) {
            message = "系统失败";
        } else if (code == 10004) {
            message = "密钥错误导致加密失败";
        } else if (code == 10005) {
            message = "fileid错误";
        } else if (code == 10006) {
            message = "解密失败";
        } else if (code == 10007) {
            message = "找不到消息加密版本的私钥，需要重新传入私钥对";
        } else if (code == 10008) {
            message = "解析encrypt_key出错";
        } else if (code == 10009) {
            message = "ip非法";
        } else if (code == 10010) {
            message = "数据过期";
        }
        return message;
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public WechatSessionStoreAPIException(long code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public WechatSessionStoreAPIException(long code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public WechatSessionStoreAPIException(long code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    /**
     * Constructs a new runtime exception with the specified detail
     * message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     *
     * @param message            the detail message.
     * @param cause              the cause.  (A {@code null} value is permitted,
     *                           and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression  whether or not suppression is enabled
     *                           or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
     * @since 1.7
     */
    protected WechatSessionStoreAPIException(long code, String message, Throwable cause,
                                             boolean enableSuppression,
                                             boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
}
