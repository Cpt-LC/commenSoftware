package com.lianzheng.notarization.master.configParameter.param;

import com.lianzheng.notarization.master.configParameter.utils.GetYmlParamUtil;
import lombok.Data;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Map;

@Data
public class OrderLogicalParam {
    //订单失效截至时间单位H
    private int notarizationExpireTime;
    //订单失效前剩余时间（提前H发送提醒）
    private int notarizationRestTime;
    public OrderLogicalParam(){
        Map<String,Object> param= (Map) GetYmlParamUtil.getYmlParam().get("orderLogical");
        if(param.get("notarizationExpireTime")!=null){this.notarizationExpireTime = NumberUtils.createInteger(param.get("notarizationExpireTime").toString());}
        if(param.get("notarizationRestTime")!=null){this.notarizationRestTime = NumberUtils.createInteger(param.get("notarizationRestTime").toString());}
    }
}
