package com.lianzheng.message.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lianzheng.core.auth.mgmt.exception.RRException;
import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.message.entity.EnvironmentParam;
import com.lianzheng.message.entity.MessageReqEntity;
import com.lianzheng.message.entity.TemplateParam;
import com.lianzheng.message.entity.TencentParam;
import com.lianzheng.message.enumes.EnviromentEnum;
import com.lianzheng.message.enumes.TencentErrorCodeEnum;
import com.lianzheng.message.service.MessageService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author kk
 * @date 2021/12/13 12:01
 * @describe
 * @remark
 */
@Service
public class TencentMessage implements MessageService {

    @Value("${message.tencent.secretId}")
    private String secretId;

    @Value("${message.tencent.secretKey}")
    private String secretKey;

    @Value("${message.enviroment}")
    private String enviroment;

    @Autowired
    private TemplateParam templateParam;

    @Autowired
    private EnvironmentParam environmentParam;

    public static TencentMessage tencentMessage = new TencentMessage();

    public static TemplateParam templateStaticParam = new TemplateParam();

    public static EnvironmentParam environmentParams = new EnvironmentParam();

    @PostConstruct
    public void init() {
        tencentMessage = this;
        tencentMessage.secretId = this.secretId;
        tencentMessage.secretKey = this.secretKey;
        templateStaticParam = this.templateParam;
        environmentParams = this.environmentParam;
    }


    @Override
    public ResponseBase sendMessage(MessageReqEntity bean) throws RRException {
        try {
            String[] templateParam = orderAndTransParams(bean.getTemplate(), bean.getTemplateParamMap());

            List<TencentParam> tencentParams = templateStaticParam.getParamList();
            Map<String, TencentParam> maps = tencentParams.stream().collect(Collectors.toMap(TencentParam::getTemplateName, Function.identity()));
            String templateId = maps.get(bean.getTemplate()).getTemplateId();
            // 环境判断，如果是sit环境，签名全部使用苏州联证智能
            String signName = EnviromentEnum.SIT.name.equals(enviroment) ? "苏州联证智能" : bean.getSignName();
            List<EnvironmentParam.AppList> paramList = environmentParams.getParamList();
            String sdkAppId = null;
            for (EnvironmentParam.AppList appList : paramList) {
                if (signName.equals(appList.getName())) {
                    sdkAppId = appList.getAppId();
                    break;
                }
            }
            Credential cred = new Credential(tencentMessage.secretId, tencentMessage.secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(cred, "ap-nanjing", clientProfile);
            SendSmsRequest req = new SendSmsRequest();
            req.setTemplateId(templateId);
            req.setPhoneNumberSet(bean.getPhoneNumberSet());
            req.setTemplateParamSet(templateParam);
            req.setSmsSdkAppId(sdkAppId);
            req.setSignName(bean.getSignName());
            SendSmsResponse resp = client.SendSms(req);
            /**
             * {
             * 	"RequestId": "0a0c0e33-6214-4485-80d2-074f44a797aa",
             * 	"SendStatusSet": [{
             * 		"SerialNo": "",
             * 		"IsoCode": "CN",
             * 		"Message": "the number of the same sms messages sent from a single mobile number exceeds the upper limit",
             * 		"Fee": 0,
             * 		"PhoneNumber": "+8617826378129",
             * 		"SessionContext": "",
             * 		"Code": "LimitExceeded.PhoneNumberSameContentDailyLimit"
             *        }]
             * }
             */
            JSONObject resultJson = JSONObject.parseObject(SendSmsResponse.toJsonString(resp));
            /**
             * [{
             * 	"SerialNo": "",
             * 	"IsoCode": "CN",
             * 	"Message": "the number of the same sms messages sent from a single mobile number exceeds the upper limit",
             * 	"Fee": 0,
             * 	"PhoneNumber": "+8617826378129",
             * 	"SessionContext": "",
             * 	"Code": "LimitExceeded.PhoneNumberSameContentDailyLimit"
             * }]
             */
            JSONArray jsonArray = JSONArray.parseArray(resultJson.getString("SendStatusSet"));

            int successNum = 0;
            int errorNum = 0;
            JSONArray resultArray = new JSONArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject itemObject = new JSONObject();
                String result = jsonArray.getJSONObject(i).getString("Code");
                if ("Ok".equals(result)) {
                    successNum += 1;
                } else {
                    errorNum += 1;
                    TencentErrorCodeEnum errorCodeEnum = TencentErrorCodeEnum.getContractTypeEnum(result);
                    if (errorCodeEnum == null) {
                        itemObject.clear();
                        itemObject.put("errorCode", "40001");
                        itemObject.put("explain", "错误：发送失败！");
                        itemObject.put("describe", "错误：发送失败！");
                        itemObject.put("originCode","错误：发送失败！");
                        itemObject.put("phoneNumber",jsonArray.getJSONObject(i).getString("PhoneNumber"));
                        resultArray.add(itemObject);
                        continue;
                    }
                    itemObject.clear();
                    itemObject.put("errorCode", errorCodeEnum.errorCode);
                    itemObject.put("explain", errorCodeEnum.explain);
                    itemObject.put("describe", errorCodeEnum.describe);
                    itemObject.put("originCode",errorCodeEnum.code);
                    itemObject.put("phoneNumber",jsonArray.getJSONObject(i).getString("PhoneNumber"));
                    resultArray.add(itemObject);
                }
            }
            if (successNum == bean.getPhoneNumberSet().length) {
                return ResponseBase.ok();
            } else {
                return ResponseBase.error("发送成功" + successNum + "条，发送失败" + errorNum + "条。").put("result",resultArray);
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    /**
     * {
     * "SendStatusSet": [{
     * "SerialNo": "2645:163951076316393957891416978",
     * "PhoneNumber": "+8617768369783",
     * "Fee": 1,
     * "SessionContext": "",
     * "Code": "Ok",
     * "Message": "send success",
     * "IsoCode": "CN"
     * }],
     * "RequestId": "a4217cfe-2cba-4478-b70a-bd67f11949ec"
     * }
     */

    public String[] orderAndTransParams(String template, Map<String, Object> paramsMap) {

        // 如果只有一个参数
        if (paramsMap.size() == 1) {
            String value = null;
            for (Object obj : paramsMap.values()) {
                value = String.valueOf(obj);
            }
            return new String[]{value};
        }

        // 多个参数的
        List<TencentParam> tencentParams = templateStaticParam.getParamList();
        Map<String, TencentParam> maps = tencentParams.stream().collect(Collectors.toMap(TencentParam::getTemplateName, Function.identity()));
//        String[] templateArray = TencentTemplateEnum.getTemplateArray(template);
        String[] templateArray = maps.get(template).getTemplateParam();
        List<String> list = new ArrayList<>();
        for (String str : templateArray) {
            list.add((String) paramsMap.get(str));
        }

        return list.toArray(new String[list.size()]);
    }


    static int getOrderNum(String[] templateArray, String str) {
        int result = 0;
        for (int i = 0; i < templateArray.length; i++) {
            if (templateArray[i].equals(str)) {
                result = i;
            }
        }
        return result;
    }

}
