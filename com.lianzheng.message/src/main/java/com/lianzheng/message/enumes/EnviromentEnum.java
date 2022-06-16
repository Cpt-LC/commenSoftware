package com.lianzheng.message.enumes;

/**
 * @author kk
 * @date 2021/12/21 11:54
 * @describe
 * @remark
 */
public enum EnviromentEnum {

    SIT("sit","测试环境"),
    PROD("prod","正式环境");

    public String name;

    public String descirbe;

    EnviromentEnum(String name,String descirbe){
        this.name = name;
        this.descirbe = descirbe;
    }

}
