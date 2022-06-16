package com.lianzheng.h5.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.wxpay.sdk.WXPayUtil;
import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.common.RedisHelper;
import com.lianzheng.h5.entity.NotarzationMaster;
import com.lianzheng.h5.entity.Order;
import com.lianzheng.h5.jwt.IgnoreAuth;
import com.lianzheng.h5.pay.WxPayHelper;
import com.lianzheng.h5.service.impl.NotarzationMasterServiceImpl;
import com.lianzheng.h5.service.impl.OrderServiceImpl;
import com.lianzheng.h5.util.Md5Util;
import com.lianzheng.core.payment.Result.ResultPay;
import com.lianzheng.core.payment.config.WxPayProperties;
import com.lianzheng.core.payment.config.WxPaysConfig;
import com.lianzheng.core.payment.payFactory.PayFactory;
import com.lianzheng.core.payment.servive.Pay;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Description: TODO
 * @author: 何江雁
 * @date: 2021年12月14日 10:21
 */
@RestController
@RequestMapping("/payment-wx")
@Api(tags = "微信支付")
public class PaymentController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisHelper redisHelper;

    @Autowired
    private WxPaysConfig wxPaysConfig;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    NotarzationMasterServiceImpl masterServiceImpl;

    @Value("${ks.debug}")
    boolean isDebugMode;

    @Value("${ks.payCallPageUrl}")
    String payCallPageUrl;

    //获取支付参数对象
    private WxPayProperties getConfig() throws IOException {
        List<WxPayProperties> list = wxPaysConfig.pays();//可以从返回的list中选择所需的参数对象
        Assert.isTrue(list.stream().count() > 0, "Wx payment setting is not correct");

        WxPayProperties wxPayProperties = list.get(0);
        return wxPayProperties;
    }

    @ResponseBody
    @RequestMapping(value = "/h5PayUrl", method = RequestMethod.POST)
    @ApiOperation(value = "登录后的回调")
    public ApiResult<?> prepay(@RequestParam("orderId") String orderId,String billingName ,String invoiceTaxNo) throws Exception {

        Order order = orderService.getOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getId, orderId), false);

        if (order == null) {
            return ApiResult.error("订单信息不存在");
        }
        //更新开票信息  和税号
        order.setBillingName(billingName);
        order.setInvoiceTaxNo(invoiceTaxNo);

        WxPayProperties wxPayProperties = getConfig();
        String outtradeNo = Md5Util.encryption(UUID.randomUUID().toString());
        order.setOuttradeNo(outtradeNo);
        orderService.saveOrUpdate(order);

        int totalCentPenny;
        if (isDebugMode) {
            totalCentPenny = 1;
        } else {
            totalCentPenny = order.getRealAmount().toBigInteger().intValue();//总价/单位：分、
        }

        if (totalCentPenny == 0) {
            return ApiResult.error("金额字段未指定");
        }
        String totalIntCentPenny = totalCentPenny + "";
        //调用统一支付接口
        Pay wxpay = new PayFactory().getPayType("WX");
//        wxpay.createdPay
        ResultPay<Map<String, String>> resultPay = WxPayHelper.createdPay(request,
                outtradeNo, totalIntCentPenny,
                "行政服务", "MWEB",
                wxPayProperties);
        String mweb_url = resultPay.getResult().get("mweb_url");

        //支付
        String url = payCallPageUrl + totalIntCentPenny + "&id=" + orderId;
        String urlString = URLEncoder.encode(url, "UTF-8");
        mweb_url = mweb_url + "&redirect_url=" + urlString;

        JSONObject result = new JSONObject();
        result.put("payUrl", mweb_url);
        result.put("outtradeNo", outtradeNo);

        //模拟刷新
//        mockUpdatePayRecord(orderNo, outtradeNo);

        return ApiResult.success(result);
    }

    @PostMapping(value = "/notify")
    @IgnoreAuth
    public String payNotify() throws Exception {
        WxPayProperties wxPayProperties = getConfig();

        StringBuilder sb = new StringBuilder();
        //获取返回的参数
        try (InputStream is = request.getInputStream()) {
            // 将InputStream转换成String
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        System.out.println("----------request----------------");
        String inputContent = sb.toString();
        System.out.println(inputContent);
        Map<String, String[]> requestMap = request.getParameterMap();
        // 转换成map
        Map<String, String> resultMap = WXPayUtil.xmlToMap(inputContent);
        Pay wxpay = new PayFactory().getPayType("WX");
        ResultPay<Map<String, String>> resultPay = wxpay.checkSign(resultMap, wxPayProperties);//支付参数根据上述所说获取
        Map<String, String> returnMap = resultPay.getResult();
        String returnCode = returnMap.get("return_code");  //状态
        if (returnCode.equals("SUCCESS")) {
            //这里写业务逻辑代码需要相关的订单信息可从resultMap中获取

            Order order = orderService.getOne(Wrappers.<Order>lambdaQuery().eq(Order::getOuttradeNo, resultMap.get("out_trade_no")), false);
            if (order != null && "NotPaid".equals(order.getPaymentStatus())) {
                order.setPaidAmount(order.getRealAmount());
                order.setPaymentStatus("Paid");
                order.setPaymentTime(LocalDateTime.now());
                orderService.saveOrUpdate(order);

                NotarzationMaster notarzationMaster = masterServiceImpl.getOne(Wrappers.<NotarzationMaster>lambdaQuery()
                        .eq(NotarzationMaster::getOrderId, order.getId()), false);
                notarzationMaster.setStatus("GeneratingCert");
                masterServiceImpl.saveOrUpdate(notarzationMaster);

                redisHelper.putList("PUSH_NOTARIZATIONID_AFTERPAY", notarzationMaster.getId());
            }
        }
        return returnMap.get("xmlBack");
    }


    @ResponseBody
    @RequestMapping(value = "/offlinePayments", method = RequestMethod.POST)
    @ApiOperation(value = "线下支付")
    public ApiResult<?> offlinePayments(@RequestParam("masterId") String masterId,
                                        @RequestParam("orderId") String orderId,String billingName ,String invoiceTaxNo) {
        //查询此订单是否是线下支付的订单
        Order order = orderService.getOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getId, orderId), false);

        if (order == null || !"Offline".equals(order.getPaymentMode())) {
            return ApiResult.error("订单不存在或者非线下支付！");
        }

        order.setInvoiceTaxNo(invoiceTaxNo);
        order.setBillingName(billingName);

        //修改订单类型为生成证书
        NotarzationMaster notarzationMaster = masterServiceImpl.getOne(Wrappers.<NotarzationMaster>lambdaQuery()
                .eq(NotarzationMaster::getOrderId, order.getId()), false);
        notarzationMaster.setStatus("GeneratingCert");
        masterServiceImpl.saveOrUpdate(notarzationMaster);
        orderService.saveOrUpdate(order);

        redisHelper.putList("PUSH_NOTARIZATIONID_AFTERPAY", masterId);

        return ApiResult.success("成功加入到列表");
    }

    @ResponseBody
    @RequestMapping(value = "/checkPay", method = RequestMethod.POST)
    @ApiOperation(value = "验证支付")
    public ApiResult<?> offlinePayments(@RequestParam("orderId") String orderId) {

        Order order = orderService.getOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getId, orderId), false);
        return ApiResult.success(order);
    }
}
