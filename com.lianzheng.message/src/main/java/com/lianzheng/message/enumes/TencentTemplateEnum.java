package com.lianzheng.message.enumes;

/**
 * @author kk
 * @date 2021/12/13 15:17
 * @describe 腾讯短信模板的枚举
 * @remark
 */
public enum TencentTemplateEnum {

    //TENCENT_TEMPLATE_1(1,"captcha","1101393"),
    TENCENT_TEMPLATE_1(1, "captcha", "1237840"),
    TENCENT_TEMPLATE_2(2, "notice", "1237845"),
    TENCENT_TEMPLATE_3(3, "sendAfterPay", "1342337"),
    TENCENT_TEMPLATE_4(4, "toUser", "1342332"),
    TENCENT_TEMPLATE_5(5, "toNotary", "1342312"),
    TENCENT_TEMPLATE_6(6, "tradeRe", "1396634"),
    TENCENT_TEMPLATE_7(7, "tradeRefuse", "1396663"),
    TENCENT_TEMPLATE_8(8, "tradeCert", "1397171"),;

    private Integer code;
    private String name;
    private String templateId;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    TencentTemplateEnum(int code, String name, String templateId) {
        this.code = code;
        this.name = name;
        this.templateId = templateId;
    }

    public static TencentTemplateEnum getEnumByCode(Integer code) {
        for (TencentTemplateEnum tencentTemplateEnum : TencentTemplateEnum.values()) {
            if (tencentTemplateEnum.code.equals(code)) {
                return tencentTemplateEnum;
            }
        }
        return null;
    }

    public static TencentTemplateEnum getEnumByName(String name) {
        for (TencentTemplateEnum tencentTemplateEnum : TencentTemplateEnum.values()) {
            if (tencentTemplateEnum.name.equals(name)) {
                return tencentTemplateEnum;
            }
        }
        return null;
    }

    public static String[] getTemplateArray(String name) {
        if (TENCENT_TEMPLATE_1.name.equals(name)) {
            return new String[]{"captcha"};
        } else if (TENCENT_TEMPLATE_2.name.equals(name)) {
            return new String[]{"name","processNo","action","todo"};
        }else if(TENCENT_TEMPLATE_3.name.equals(name)){
            return new String[]{"name","processNo"};
        }else if(TENCENT_TEMPLATE_4.name.equals(name)){
            return new String[]{"name","processNo"};
        }else if(TENCENT_TEMPLATE_5.name.equals(name)){
            return new String[]{"name","processNo"};
        }
        return null;
    }

}
