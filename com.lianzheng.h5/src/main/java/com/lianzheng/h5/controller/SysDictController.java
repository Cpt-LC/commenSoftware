package com.lianzheng.h5.controller;

import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.service.ISysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 系统字典 前端控制器
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */

@Slf4j
@RestController
@RequestMapping("dict")
@Api(tags = "系统字典操作API")
public class SysDictController {

    @Autowired
    private ISysDictService dictService;


    @GetMapping(value = "getDictByType")
    @ApiOperation(value = "根据类型获取字典项")
    public ApiResult<?> detail(@RequestParam String type) {
        return ApiResult.success(dictService.getDictListByType(type));
    }
}
