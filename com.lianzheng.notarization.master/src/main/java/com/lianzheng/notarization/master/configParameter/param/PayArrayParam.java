package com.lianzheng.notarization.master.configParameter.param;

import com.lianzheng.notarization.master.configParameter.utils.GetYmlParamUtil;
import lombok.Data;
import org.apache.commons.lang.math.NumberUtils;


import java.math.BigDecimal;
import java.util.Map;

@Data
public class PayArrayParam {

    //公证费个人
    private BigDecimal NotaryAmountP;
    //公证费个人
    private BigDecimal NotaryAmountPAdd;
    //公证费企业
    private BigDecimal NotaryAmountE;
    //单位副本费
    private BigDecimal CopyAmount;
    //双证译文文本相符
    private BigDecimal DoubleCertificate;
    //寄台湾海基会 普通快递
    private BigDecimal ModeToSEFP;
    //特快
    private  BigDecimal ModeToSEFS;

    public PayArrayParam() {
        Map<String,Object> param= (Map) GetYmlParamUtil.getYmlParam().get("payArray");
        if(param.get("NotaryAmountP")!=null){this.NotaryAmountP = NumberUtils.createBigDecimal(param.get("NotaryAmountP").toString());}
        if(param.get("NotaryAmountPAdd")!=null){this.NotaryAmountPAdd = NumberUtils.createBigDecimal(param.get("NotaryAmountPAdd").toString());}
        if(param.get("NotaryAmountE")!=null){this.NotaryAmountE = NumberUtils.createBigDecimal(param.get("NotaryAmountE").toString());}
        if(param.get("CopyAmount")!=null){this.CopyAmount = NumberUtils.createBigDecimal(param.get("CopyAmount").toString());}
        if(param.get("DoubleCertificate")!=null){this.DoubleCertificate = NumberUtils.createBigDecimal(param.get("DoubleCertificate").toString());}
        if(param.get("ModeToSEFP")!=null){this.ModeToSEFP = NumberUtils.createBigDecimal(param.get("ModeToSEFP").toString());}
        if(param.get("ModeToSEFS")!=null){this.ModeToSEFS = NumberUtils.createBigDecimal(param.get("ModeToSEFS").toString());}
    }
}
