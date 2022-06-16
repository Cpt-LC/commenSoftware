package com.lianzheng.h5.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.entity.Order;
import com.lianzheng.h5.entity.User;
import com.lianzheng.h5.service.impl.OrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-19
 */
@RestController
@RequestMapping("/order")
@Api(tags = "订单信息表")
public class OrderController {
    @Autowired
    OrderServiceImpl orderService;

    @ResponseBody
    @RequestMapping(value = "/emsInfo", method = RequestMethod.POST)
    @ApiOperation(value = "快递信息")
    public ApiResult<?> homeList(@RequestParam("orderId") String orderId) {
        Order order = orderService.getOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getId, orderId), false);

        return ApiResult.success(order);
    }
}
