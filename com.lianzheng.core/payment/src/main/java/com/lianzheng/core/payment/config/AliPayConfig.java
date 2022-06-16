package com.lianzheng.core.payment.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Data
@Slf4j
public class AliPayConfig {

    /**
     * 支付宝gatewayUrl
     */
//    @Value("${alipay.gatewayUrl}")
    private String gatewayUrl;
    /**
     * 商户应用id
     */
//    @Value("${alipay.appId}")
    private String appid;
    /**
     * RSA私钥，用于对商户请求报文加签
     */
//    @Value("${alipay.merchant_private_key}")
    private String appPrivateKey;
    /**
     * 支付宝RSA公钥，用于验签支付宝应答
     */
//    @Value("${alipay.alipay_public_key}")
    private String alipayPublicKey;
    /**
     * 签名类型
     */
    private String signType = "RSA2";
    /**
     * 格式
     */
    private String formate = "json";
    /**
     * 编码
     */
    private String charset = "UTF-8";
    /*
    延迟支付时间
     */
//    @Value("${alipay.timeout_express}")
    private String timeout_express;
    /*
    延迟支付时间
     */
//    @Value("${alipay.time_expire}")
    private String time_expire;
    /**
     * 同步地址
     */
//    @Value("${alipay.return_url}")
    private String returnUrl;
    /**
     * 异步地址
     */
//    @Value("${alipay.notify_url}")
    private String notifyUrl;
    /**
     * 最大查询次数
     */
    private static int maxQueryRetry = 5;
    /**
     * 查询间隔（毫秒）
     */
    private static long queryDuration = 5000;
    /**
     * 最大撤销次数
     */
    private static int maxCancelRetry = 3;
    /**
     * 撤销间隔（毫秒）
     */
    private static long cancelDuration = 3000;

    @Bean
    public AlipayClient alipayClient(){
        return new DefaultAlipayClient(this.getGatewayUrl(),
                this.getAppid(),
                this.getAppPrivateKey(),
                this.getFormate(),
                this.getCharset(),
                this.getAlipayPublicKey(),
                this.getSignType());
    }
}
