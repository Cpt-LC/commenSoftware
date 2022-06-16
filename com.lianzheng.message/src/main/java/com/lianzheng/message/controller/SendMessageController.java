package com.lianzheng.message.controller;

import com.alibaba.fastjson.JSONObject;
import com.lianzheng.core.auth.mgmt.exception.RRException;
import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.message.entity.MessageReqEntity;
import com.lianzheng.message.entity.TemplateParam;
import com.lianzheng.message.entity.TencentParam;
import com.lianzheng.message.messageFactory.MessageFactory;
import com.lianzheng.message.service.MessageService;
import com.lianzheng.message.util.IPUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kk
 * @date 2021/12/13 11:57
 * @describe
 * @remark
 */
@RestController
@RequestMapping("message")
public class SendMessageController {

    @Value("${message.way}")
    private String way;


    /**
     * 发送短信
     *
     * @param request
     * @param messageReqEntity
     * @return
     */
    @PostMapping("send")
    public ResponseBase sendMessage(HttpServletRequest request, @RequestBody MessageReqEntity messageReqEntity) {
        try {
            String ip = IPUtils.getIpAddr(request);
            if (StringUtils.isBlank(ip)) {
                return ResponseBase.error("错误：未查询到IP地址！");

            }
            if (!"127.0.0.1".equals(ip)) {
                return ResponseBase.error("错误：无权访问！");
            }
            MessageFactory factory = new MessageFactory();
            MessageService service = factory.getMessageType(way);
            return service.sendMessage(checkMessage(messageReqEntity));
        } catch (RRException ex) {
            return ResponseBase.error(ex.getMsg());
        }
    }

    /**
     * 统一校验传递的请求参数不允许为空
     *
     * @param bean
     * @return
     */
    public static MessageReqEntity checkMessage(MessageReqEntity bean) {
        if (StringUtils.isBlank(bean.getTemplate())) {
            throw new RRException("错误：template为空！");
        }
        if (StringUtils.isBlank(bean.getSignName())) {
            throw new RRException("错误：signName为空！");
        }
        if (bean.getPhoneNumberSet() == null || bean.getPhoneNumberSet().length == 0) {
            throw new RRException("错误：phoneNumberSet为空！");
        }
        if (bean.getTemplateParamMap()==null ) {
            throw new RRException("错误：getTemplateParamMap为空！");
        }
        return bean;
    }


    public static void main(String[] args) {
        MessageReqEntity bean = new MessageReqEntity();
        bean.setTemplate("notice");
        bean.setSignName("昆山公证处");
        bean.setPhoneNumberSet(new String[]{"+8617768369783", "+8617826378129"});
        bean.setTemplateParamSet(new String[]{ "12312"});
        JSONObject json = (JSONObject)JSONObject.toJSON(bean);
        System.out.println(json.toJSONString());

        //System.out.println(doSendMessage("notice","苏州联证智能",new String[]{"17768369783"},new String[]{"1","2","3","4"}));
        System.out.println(doSendMessage("notice","苏州联证智能",new String[]{"17826378129"}));
    }


    /**
     * 发送短信（本地调用）
     *
     * @param template          短信模板： 表示验证码
     * @param signName         公证处全称：昆山公证处、吴江公证处
     * @param phoneNumberSet   手机号：支持多个手机号
     * @return
     */
    public static String doSendMessage(String template, String signName, String[] phoneNumberSet) {
        Map<String, Object> map = new HashMap<>();
        map.put("template", template);
        map.put("signName", signName);
        map.put("phoneNumberSet", phoneNumberSet);
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("todo","todo");
        paramsMap.put("action","action");
        paramsMap.put("processNo","processNo");
        paramsMap.put("name","name");
        map.put("templateParamMap", paramsMap);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject("http://127.0.0.1:9103/message/message/send", map, String.class);
    }

}
