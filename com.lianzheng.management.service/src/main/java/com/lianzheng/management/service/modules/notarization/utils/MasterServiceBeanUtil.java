package com.lianzheng.management.service.modules.notarization.utils;

import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.notarization.master.configParameter.param.DocumentParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.notarization.master.service.MasterService;
import org.springframework.stereotype.Component;

@Component
public class MasterServiceBeanUtil {

    public static MasterService getMasterServiceBean(String notarzationTypeCode){
        try{
            // todo 临时解决建行反射bean与昆山同名
            DocumentParam documentParam = ConfigParameterUtil.getDocument();
            if (documentParam.getEnvironment().equals("one")){
                return (MasterService) SpringContextUtil.getBean("masterNotarization");
            }
            MasterService masterService = (MasterService) SpringContextUtil.getBean(notarzationTypeCode);
            return masterService;
        }catch (Exception e){
            return (MasterService) SpringContextUtil.getBean("masterNotarization");
        }
    }
}
