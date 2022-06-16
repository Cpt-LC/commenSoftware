# core-payment的使用说明

## 配置
1. 在pom.xml里添加

        <dependency>
            <groupId>com.github.wxpay</groupId>
            <artifactId>wxpay-sdk</artifactId>
            <version>0.0.4</version>
            <scope>system</scope>
            <systemPath>${pom.basedir}/../lib/WXPay-SDK-Java-0.0.4.jar</systemPath>
        </dependency>
        <dependency>
            <groupId>com.github.yingzhuo</groupId>
            <artifactId>spring-boot-stater-env</artifactId>
            <version>2.0.2</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>com.lianzheng</groupId>
            <artifactId>core-payment</artifactId>
            <version>0.0.1</version>
            <scope>system</scope>
            <systemPath>${pom.basedir}/../lib/core-payment-0.0.1.jar</systemPath>
        </dependency>
2. copy resource 文件
   - 拷贝 config.pay.toml
   - 拷贝META-INF/spring.factories
注意:  如果pom依赖下载不下来微信的sdk     可以引入WXPay-SDK-Java-0.0.4.jar

## 代码
1. 在Application里，添加如下扫描路径
@SpringBootApplication(scanBasePackages = {
        "com.example.demo"
        ,"com.lianzheng.core.*"
        ,"com.github.*"
})

2. 在controller/serviceImpl里，支付前的调用
   @Autowired
   private WxPaysConfig wxPaysConfig;

   @PostMapping(path="/prepay")
   public Map<String,String> prepay(HttpServletRequest request,
                                    @RequestBody Map<String,Object> params) throws Exception {

       WxPayProperties wxPayProperties = getConfig();

       //todo,生成订单号
       String orderNo = "L1234567890";
       //todo，计算支付金额
       String totalString = "1";//总价/单位：分、

       //调用统一支付接口
       Pay wxpay = new PayFactory().getPayType("WX");
       /*参数分别为
       1.请求request 、
       2. 订单流水号（每次提起支付的流水号不可相同） 、
       3.总价/单位：分、
       4. 支付的标题 、
       5.支付类型：H5支付为MWEB 、
       6.支付参数对象*/
       ResultPay<Map<String,String>> resultPay =  wxpay.createdPay(request,orderNo,totalString,
               "行政服务","MWEB",wxPayProperties);
       Map<String,String> returnMap =  resultPay.getResult();
       //返回给前端 returnMap即可
       return returnMap;
   }
      
    

3. 回调验签
     支付成功后微信返回的格式
     <xml><appid><![CDATA[wx848307ecb859]]></appid>
     <bank_type><![CDATA[OTHERS]]></bank_type>
     <cash_fee><![CDATA[1]]></cash_fee>
     <fee_type><![CDATA[CNY]]></fee_type>
     <is_subscribe><![CDATA[N]]></is_subscribe>
     <mch_id><![CDATA[1615450398]]></mch_id>
     <nonce_str><![CDATA[2e13db45445d486c9f903d389dcd1c7e]]></nonce_str>
     <out_trade_no><![CDATA[LOL20211133123575578467782]]></out_trade_no>
     <result_code><![CDATA[SUCCESS]]></result_code>
     <return_code><![CDATA[SUCCESS]]></return_code>
     <sign><![CDATA[6CD7895BA12D1B54E96EC166BE13C7EF]]></sign>
     <time_end><![CDATA[20211128000000]]></time_end>
     <total_fee>1</total_fee>
     <trade_type><![CDATA[JSAPI]]></trade_type>
     <transaction_id><![CDATA[4200001227202111277346507844]]></transaction_id>
     </xml>

 
   // 回调验签接口编写
    @RequestMapping(value = "/notify")
    public String payNotify(HttpServletRequest request) throws Exception {
        WxPayProperties wxPayProperties = getConfig();
        tringBuilder sb = new StringBuilder();
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
        ResultPay<Map<String,String>> resultPay =  wxpay.checkSign(resultMap,wxPayProperties);//支付参数根据上述所说获取
        Map<String,String> returnMap =  resultPay.getResult();
        String returnCode = returnMap.get("return_code");  //状态
        if (returnCode.equals("SUCCESS")) {
            //这里写业务逻辑代码需要相关的订单信息可从resultMap中获取
        }
        return returnMap.get("xmlBack");
    } 

