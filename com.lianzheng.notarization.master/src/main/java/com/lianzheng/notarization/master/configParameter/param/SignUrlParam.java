package com.lianzheng.notarization.master.configParameter.param;

import com.lianzheng.notarization.master.configParameter.utils.GetYmlParamUtil;
import lombok.Data;

import java.util.Map;

@Data
public class SignUrlParam {
    private String url;
    public  SignUrlParam(){
        if(GetYmlParamUtil.getYmlParam().get("signUrl")!=null){this.url = (String) GetYmlParamUtil.getYmlParam().get("signUrl");}
    }

}
