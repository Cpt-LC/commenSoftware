package com.lianzheng.core.server;


import java.util.HashMap;
import java.util.Map;

/**
   *   返回数据
 *
 * @author gang.shen@kedata.com
 */
public class ResponseBase extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public ResponseBase() {
		put("code", 0);
		put("msg", "success");
	}
	
	public static ResponseBase error() {
		return error(ResponseEnum.SERVER_FAIL.getCode(), "未知异常，请联系管理员");
	}
	
	public static ResponseBase error(String msg) {
		return error(ResponseEnum.SERVER_FAIL.getCode(), msg);
	}
	
	public static ResponseBase error(int code, String msg) {
		ResponseBase r = new ResponseBase();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static ResponseBase ok(String msg) {
		ResponseBase r = new ResponseBase();
		r.put("msg", msg);
		return r;
	}
	
	public static ResponseBase ok(Map<String, Object> map) {
		ResponseBase r = new ResponseBase();
		r.putAll(map);
		return r;
	}
	
	public static ResponseBase ok() {
		return new ResponseBase();
	}

	@Override
	public ResponseBase put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
