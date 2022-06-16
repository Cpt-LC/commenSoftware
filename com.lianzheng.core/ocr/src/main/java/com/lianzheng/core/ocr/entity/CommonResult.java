package com.lianzheng.core.ocr.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author kk
 * @date 2022/4/15 14:11
 * @describe
 * @remark
 */
@Data
public class CommonResult extends OcrResult {

    private JSONObject resultJSON;

}
