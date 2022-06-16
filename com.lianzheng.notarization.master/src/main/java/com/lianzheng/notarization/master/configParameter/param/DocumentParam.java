package com.lianzheng.notarization.master.configParameter.param;


import com.lianzheng.notarization.master.configParameter.utils.GetYmlParamUtil;
import lombok.Data;

import java.util.Map;

@Data
public class DocumentParam{
    private String environment;
    //详情页所需的模板
    private String csvTemplate;
    //需展示用户上传的身份证明材料类型
    private String[] userDocumentFilter;

    //需展示用户上传的证明材料类型
    private String[] materialDocumentFilter;

    private String generateTemplates;

    public DocumentParam(){
        //会在静态代码块中调用这里做特殊处理
        Object object = GetYmlParamUtil.getYmlParam().get("document");
        if(object==null){
            return ;
        }
        Map<String,Object> param= (Map)object;
        if(param.get("environment")!=null){ this.environment =(String)param.get("environment");}
        if(param.get("userDocumentFilter")!=null){this.userDocumentFilter =param.get("userDocumentFilter").toString().split(",");}
        if(param.get("materialDocumentFilter")!=null){this.materialDocumentFilter =param.get("materialDocumentFilter").toString().split(",");}
        if(param.get("csvTemplate")!=null){ this.csvTemplate =(String)param.get("csvTemplate");}
        if(param.get("generateTemplates")!=null){this.generateTemplates =(String)param.get("generateTemplates");}
    }
}
