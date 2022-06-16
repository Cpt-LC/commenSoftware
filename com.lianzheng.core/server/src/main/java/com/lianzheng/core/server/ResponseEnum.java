package com.lianzheng.core.server;

/**
   *   返回code定义
 *
 * @author gang.shen@kedata.com
 */
public enum ResponseEnum {

	SUCCESS(0, "操作成功"), FAIL(-1, "系统繁忙，请稍后尝试"),
	
	/** 30x 表单错误 **/
	PARAM_FORMAT_ERR(300, "参数格式不正确"),
	
	
	/** 40x 鉴权异常  **/
	AUTH_FAIL(401, "没有权限"),
	TOKEN_FAIL(401, "鉴权失败"),
	
	
	/** 50x 服务异常  **/
	SERVER_FAIL(500, "服务内部错误"),
	SIGN_FAIL(510, "签名认证失败"), DECRYPT_FAIL(511, "解密失败");
	
	
	private int code;
	
	private String msg;
	
	ResponseEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
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
	
}
