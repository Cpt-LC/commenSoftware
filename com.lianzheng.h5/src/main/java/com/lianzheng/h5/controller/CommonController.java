package com.lianzheng.h5.controller;

import com.lianzheng.h5.common.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/common")
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    HttpServletRequest request;


    @Value("${ks.payCallPageUrl}")
    String payCallPageUrl;

    @Value("${ks.fileUrl}")
    String ksFileUrl;


    @ResponseBody
    @RequestMapping(value = "/测试基本信息", method = RequestMethod.GET)
    @ApiOperation(value = "得到当前版本信息")
    public ApiResult<?> version() {
        String stringBuffer = "  payCallPageUrl :" +
                payCallPageUrl +
                "  ksFileUrl :" +
                ksFileUrl;
        return ApiResult.success(stringBuffer);
    }
}
