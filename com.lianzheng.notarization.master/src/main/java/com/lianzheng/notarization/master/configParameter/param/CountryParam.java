package com.lianzheng.notarization.master.configParameter.param;

import com.lianzheng.notarization.master.configParameter.utils.GetYmlParamUtil;
import lombok.Data;

import java.util.Map;

@Data
public class CountryParam {
    //不显示翻译问题的国家
    private String translateLanguage;
    //需要双证的国家
    private String certCountry;
    public CountryParam(){
        Map<String,Object> param= (Map) GetYmlParamUtil.getYmlParam().get("country");
        if(param.get("translateLanguage")!=null){this.translateLanguage =(String)param.get("translateLanguage");}
        if(param.get("certCountry")!=null){this.certCountry =(String)param.get("certCountry");}
    }

}
