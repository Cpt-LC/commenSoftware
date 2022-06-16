package com.lianzheng.notarization.master.configParameter.utils;


import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.notarization.master.configParameter.param.DocumentParam;
import com.lianzheng.notarization.master.enums.MaterialMsgEnum;
import com.lianzheng.notarization.master.enums.MaterialMsgOneEnum;
import com.lianzheng.notarization.master.enums.NotarizationTypeEnum;
import com.lianzheng.notarization.master.enums.NotarizationTypeOneEnum;

/**
 * 根据项目环境获取公证类别信息
 */
public class GetNotarizationTypeUtil {

    public static String getNotarizationType(String code){
        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        switch (documentParam.getEnvironment()){
            case "zero":
                return NotarizationTypeEnum.getEnumMsg(code);
            case "one":
                return NotarizationTypeOneEnum.getEnumMsg(code);
        }
        throw new  COREException("未配置该公证",500);
    }

    public static String getMaterialMsg(String code){
        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        switch (documentParam.getEnvironment()){
            case "zero":
                return MaterialMsgEnum.getEnumMsg(code+"MSG");
            case "one":
                return MaterialMsgOneEnum.getEnumMsg(code+"MSG");
        }
        throw new  COREException("未配置该公证材料",500);
    }
}
