package com.lianzheng.notarization.master.configParameter.param;

import com.lianzheng.notarization.master.configParameter.utils.GetYmlParamUtil;
import lombok.Data;

import java.util.Map;

@Data
public class TruthParam {

    private  String BaseUrl;
    private  String requestClientCode;
    private  String responseClientCode;
    private  String Public_Key;

    public TruthParam(String truthCase){
        Map<String,Object> params= (Map) GetYmlParamUtil.getYmlParam().get("truth");
        Map<String,Object> param = (Map)params.get(truthCase);
        if(param.get("BaseUrl")!=null){this.BaseUrl =(String)param.get("BaseUrl");}
        if(param.get("requestClientCode")!=null){this.requestClientCode =(String)param.get("requestClientCode");}
        if(param.get("responseClientCode")!=null){this.responseClientCode =(String)param.get("responseClientCode");}
        if(param.get("Public_Key")!=null){this.Public_Key =(String)param.get("Public_Key");}
    }
}
