package com.lianzheng.notarization.service.utils;

import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.notarization.master.service.MasterService;
import org.springframework.stereotype.Component;

@Component
public class MasterServiceBeanUtil {

    public static MasterService getMasterServiceBean(String notarzationTypeCode){
        try{
            MasterService masterService = (MasterService) SpringContextUtil.getBean(notarzationTypeCode);
            return masterService;
        }catch (Exception e){
            return (MasterService) SpringContextUtil.getBean("masterNotarization");
        }
    }
}
