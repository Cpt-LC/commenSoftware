package com.lianzheng.notarization.master.configParameter.param;

import com.lianzheng.notarization.master.configParameter.utils.GetYmlParamUtil;
import lombok.Data;

import java.util.Map;

@Data
public class MessagesParam {
    private String signName;
    public MessagesParam(){
        Map<String,Object> param= (Map) GetYmlParamUtil.getYmlParam().get("messages");
        if(param.get("signName")!=null){this.signName =(String)param.get("signName");}
    }
}
