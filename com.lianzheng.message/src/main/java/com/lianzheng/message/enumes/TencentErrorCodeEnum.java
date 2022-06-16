package com.lianzheng.message.enumes;

import com.lianzheng.core.auth.mgmt.exception.RRException;

/**
 * 腾讯云错误码 枚举
 *
 * @author kk
 * @date 2022/1/14 18:18
 * @describe
 *     errorCode  转换后的 code
 *     code       腾讯云错误码
 *     describe   腾讯云错误描述
 *     explain    转换过的描述
 * @remark
 */
public enum TencentErrorCodeEnum {

    TENCENT_ERROR_CODE_ENUM_1("ContainSensitiveWord", "FailedOperation.ContainSensitiveWord",
            "短信内容中含有敏感词，请联系腾讯云短信小助手。",
            "短信内容中含有敏感词"),
    TENCENT_ERROR_CODE_ENUM_2("FailResolvePacket", "FailedOperation.FailResolvePacket",
            "请求包解析失败，通常情况下是由于没有遵守API接口说明规范导致的，请参考 请求包体解析1004错误详解。",
            "请求包解析失败"),
    TENCENT_ERROR_CODE_ENUM_3("InsufficientBalanceInSmsPackage", "FailedOperation.InsufficientBalanceInSmsPackage",
            "套餐包余量不足，请购买套餐包。",
            "处理错误。"),
    TENCENT_ERROR_CODE_ENUM_4("JsonParseFail", "FailedOperation.JsonParseFail",
            "解析请求包体时候失败。",
            "解析请求包体时候失败"),
    TENCENT_ERROR_CODE_ENUM_5("MarketingSendTimeConstraint", "FailedOperation.MarketingSendTimeConstraint",
            "营销短信发送时间限制，为避免骚扰用户，营销短信只允许在8点到22点发送。",
            "营销短信发送时间限制，为避免骚扰用户，营销短信只允许在8点到22点发送。"),
    TENCENT_ERROR_CODE_ENUM_6("PhoneNumberInBlacklist", "FailedOperation.PhoneNumberInBlacklist",
            "手机号在黑名单库中，通常是用户退订或者命中运营商黑名单导致的，可联系 腾讯云短信小助手 解决。",
            "手机号在黑名单库中"),
    TENCENT_ERROR_CODE_ENUM_7("SignatureIncorrectOrUnapproved", "FailedOperation.SignatureIncorrectOrUnapproved",
            "签名未审批或格式错误。（1）可登陆 短信控制台，核查签名是否已审批并且审批通过；（2）核查是否符合格式规范，签名只能由中英文、数字组成，要求2 - 12个字，若存在疑问可联系 腾讯云短信小助手。",
            "签名未审批或格式错误"),
    TENCENT_ERROR_CODE_ENUM_8("TemplateIncorrectOrUnapproved", "FailedOperation.TemplateIncorrectOrUnapproved",
            "模板未审批或内容不匹配。（1）可登陆 短信控制台，核查模板是否已审批并审批通过；（2）核查是否符合 格式规范，若存在疑问可联系 腾讯云短信小助手。",
            "模板未审批或内容不匹配"),
    TENCENT_ERROR_CODE_ENUM_9("OtherError", "InternalError.OtherError",
            "其他错误，请联系 腾讯云短信小助手 并提供失败手机号。",
            "其他错误"),
    TENCENT_ERROR_CODE_ENUM_10("RequestTimeException", "InternalError.RequestTimeException",
            "请求发起时间不正常，通常是由于您的服务器时间与腾讯云服务器时间差异超过10分钟导致的，请核对服务器时间及 API 接口中的时间字段是否正常。",
            "请求发起时间不正常"),
    TENCENT_ERROR_CODE_ENUM_11("RestApiInterfaceNotExist", "InternalError.RestApiInterfaceNotExist",
            "不存在该 RESTAPI 接口，请核查 REST API 接口说明。",
            "不存在该 RESTAPI 接口"),
    TENCENT_ERROR_CODE_ENUM_12("SendAndRecvFail", "InternalError.SendAndRecvFail",
            "接口超时或短信收发包超时，请检查您的网络是否有波动，或联系 腾讯云短信小助手 解决。",
            "接口超时或短信收发包超时"),
    TENCENT_ERROR_CODE_ENUM_13("SigFieldMissing", "InternalError.SigFieldMissing",
            "后端包体中请求包体没有 Sig 字段或 Sig 为空。",
            "后端包体中请求包体没有 Sig"),
    TENCENT_ERROR_CODE_ENUM_14("SigVerificationFail", "InternalError.SigVerificationFail",
            "后端校验 Sig 失败。",
            "后端校验 Sig 失败"),
    TENCENT_ERROR_CODE_ENUM_15("Timeout", "InternalError.Timeout",
            "请求下发短信超时，请参考 60008错误详解。",
            "请求下发短信超时"),
    TENCENT_ERROR_CODE_ENUM_16("UnknownError", "InternalError.UnknownError",
            "未知错误类型。",
            "未知错误类型"),
    TENCENT_ERROR_CODE_ENUM_17("ContentLengthLimit", "InvalidParameterValue.ContentLengthLimit",
            "请求的短信内容太长，短信长度规则请参考 国内短信内容长度计算规则。",
            "请求的短信内容太长"),
    TENCENT_ERROR_CODE_ENUM_18("IncorrectPhoneNumber", "InvalidParameterValue.IncorrectPhoneNumber",
            "手机号格式错误，请参考 1016错误详解。",
            "手机号格式错误"),
    TENCENT_ERROR_CODE_ENUM_19("ProhibitedUseUrlInTemplateParameter", "InvalidParameterValue.ProhibitedUseUrlInTemplateParameter",
            "禁止在模板变量中使用 URL。",
            "禁止在模板变量中使用 URL"),
    TENCENT_ERROR_CODE_ENUM_20("SdkAppIdNotExist", "InvalidParameterValue.SdkAppIdNotExist",
            "SdkAppId 不存在。",
            "SdkAppId 不存在"),
    TENCENT_ERROR_CODE_ENUM_21("TemplateParameterFormatError", "InvalidParameterValue.TemplateParameterFormatError",
            "验证码模板参数格式错误，验证码类模板，模板变量只能传入0 - 6位（包括6位）纯数字。",
            "验证码模板参数格式错误"),
    TENCENT_ERROR_CODE_ENUM_22("TemplateParameterLengthLimit", "InvalidParameterValue.TemplateParameterLengthLimit",
            "单个模板变量字符数超过12个，企业认证用户不限制单个变量值字数，您可以 变更实名认证模式，变更为企业认证用户后，该限制变更约1小时左右生效。",
            "单个模板变量字符数超过12个"),
    TENCENT_ERROR_CODE_ENUM_23("AppCountryOrRegionDailyLimit", "LimitExceeded.AppCountryOrRegionDailyLimit",
            "业务短信国家/地区日下发条数超过设定的上限，可自行到控制台调整短信频率限制策略。",
            "业务短信国家/地区日下发条数超过设定的上限"),
    TENCENT_ERROR_CODE_ENUM_24("AppCountryOrRegionInBlacklist", "LimitExceeded.AppCountryOrRegionInBlacklist",
            "业务短信国家/地区在黑名单中，可自行到控制台调整短信限制策略。",
            "业务短信国家/地区在黑名单中"),
    TENCENT_ERROR_CODE_ENUM_25("AppDailyLimit", "LimitExceeded.AppDailyLimit",
            "业务短信日下发条数超过设定的上限 ，可自行到控制台调整短信频率限制策略。",
            "业务短信日下发条数超过设定的上限"),
    TENCENT_ERROR_CODE_ENUM_26("AppGlobalDailyLimit", "LimitExceeded.AppGlobalDailyLimit",
            "业务短信国际/港澳台日下发条数超过设定的上限，可自行到控制台调整短信频率限制策略。",
            "业务短信国际/港澳台日下发条数超过设定的上限"),
    TENCENT_ERROR_CODE_ENUM_27("AppMainlandChinaDailyLimit", "LimitExceeded.AppMainlandChinaDailyLimit",
            "业务短信中国大陆日下发条数超过设定的上限，可自行到控制台调整短信频率限制策略。",
            "业务短信中国大陆日下发条数超过设定的上限"),
    TENCENT_ERROR_CODE_ENUM_28("DailyLimit", "LimitExceeded.DailyLimit",
            "短信日下发条数超过设定的上限 (国际/港澳台)，如需调整限制，可联系 腾讯云短信小助手。",
            "短信日下发条数超过设定的上限 (国际/港澳台)"),
    TENCENT_ERROR_CODE_ENUM_29("DeliveryFrequencyLimit", "LimitExceeded.DeliveryFrequencyLimit",
            "下发短信命中了频率限制策略，可自行到控制台调整短信频率限制策略，如有其他需求请联系 腾讯云短信小助手。",
            "下发短信命中了频率限制策略"),
    TENCENT_ERROR_CODE_ENUM_30("PhoneNumberCountLimit", "LimitExceeded.PhoneNumberCountLimit",
            "调用接口单次提交的手机号个数超过200个，请遵守 API 接口输入参数 PhoneNumberSet 描述。",
            "调用接口单次提交的手机号个数超过200个"),
    TENCENT_ERROR_CODE_ENUM_31("PhoneNumberDailyLimit", "LimitExceeded.PhoneNumberDailyLimit",
            "单个手机号日下发短信条数超过设定的上限，可自行到控制台调整短信频率限制策略。",
            "单个手机号日下发短信条数超过设定的上限"),
    TENCENT_ERROR_CODE_ENUM_32("PhoneNumberOneHourLimit", "LimitExceeded.PhoneNumberOneHourLimit",
            "单个手机号1小时内下发短信条数超过设定的上限，可自行到控制台调整短信频率限制策略。",
            "单个手机号1小时内下发短信条数超过设定的上限"),
    TENCENT_ERROR_CODE_ENUM_33("PhoneNumberSameContentDailyLimit", "LimitExceeded.PhoneNumberSameContentDailyLimit",
            "单个手机号下发相同内容超过设定的上限，可自行到控制台调整短信频率限制策略。",
            "单个手机号下发相同内容超过设定的上限"),
    TENCENT_ERROR_CODE_ENUM_34("PhoneNumberThirtySecondLimit", "LimitExceeded.PhoneNumberThirtySecondLimit",
            "单个手机号30秒内下发短信条数超过设定的上限，可自行到控制台调整短信频率限制策略。",
            "单个手机号30秒内下发短信条数超过设定的上限"),
    TENCENT_ERROR_CODE_ENUM_35("EmptyPhoneNumberSet", "MissingParameter.EmptyPhoneNumberSet",
            "传入的号码列表为空，请确认您的参数中是否传入号码。",
            "传入的号码列表为空"),
    TENCENT_ERROR_CODE_ENUM_36("IndividualUserMarketingSmsPermissionDeny", "UnauthorizedOperation.IndividualUserMarketingSmsPermissionDeny",
            "个人用户没有发营销短信的权限，请参考 权益区别。",
            "个人用户没有发营销短信的权限"),
    TENCENT_ERROR_CODE_ENUM_37("RequestIpNotInWhitelist", "UnauthorizedOperation.RequestIpNotInWhitelist",
            "请求 IP 不在白名单中，您配置了校验请求来源 IP，但是检测到当前请求 IP 不在配置列表中，如有需要请联系 腾讯云短信小助手。",
            "请求 IP 不在白名单中"),
    TENCENT_ERROR_CODE_ENUM_38("RequestPermissionDeny", "UnauthorizedOperation.RequestPermissionDeny",
            "请求没有权限，请联系 腾讯云短信小助手。",
            "请求没有权限"),
    TENCENT_ERROR_CODE_ENUM_39("SdkAppIdIsDisabled", "UnauthorizedOperation.SdkAppIdIsDisabled",
            "此 SdkAppId 禁止提供服务，如有需要请联系 腾讯云短信小助手。",
            "此 SdkAppId 禁止提供服务"),
    TENCENT_ERROR_CODE_ENUM_40("SerivceSuspendDueToArrears", "UnauthorizedOperation.SerivceSuspendDueToArrears",
            "欠费被停止服务，可自行登录腾讯云充值来缴清欠款。",
            "处理错误。"),
    TENCENT_ERROR_CODE_ENUM_41("SmsSdkAppIdVerifyFail", "UnauthorizedOperation.SmsSdkAppIdVerifyFail",
            "SmsSdkAppId校验失败，请检查 SmsSdkAppId 是否属于云API密钥的关联账户。",
            "SmsSdkAppId校验失败"),
    TENCENT_ERROR_CODE_ENUM_42("UnsupportedOperation", "UnsupportedOperation.",
            "不支持该请求。",
            "不支持该请求。"),
    TENCENT_ERROR_CODE_ENUM_43("ContainDomesticAndInternationalPhoneNumber", "UnsupportedOperation.ContainDomesticAndInternationalPhoneNumber",
            "群发请求里既有国内手机号也有国际手机号。请排查是否存在（1）使用国内签名或模板却发送短信到国际手机号；（2）使用国际签名或模板却发送短信到国内手机号；",
            "群发请求里既有国内手机号也有国际手机号。"),
    TENCENT_ERROR_CODE_ENUM_44("UnsuportedRegion", "UnsupportedOperation.UnsuportedRegion",
            "不支持该地区短信下发。",
            "不支持该地区短信下发。");

    // 转换后的 code
    public String errorCode;
    // 腾讯云 code
    public String code;
    // 腾讯云 describe
    public String describe;
    // 转换过的描述
    public String explain;

    TencentErrorCodeEnum(String errorCode, String code, String describe, String explain) {
        this.errorCode = errorCode;
        this.code = code;
        this.describe = describe;
        this.explain = explain;
    }

    /**
     * 根据code 匹配对应的enum
     * @param code
     * @return
     */
    public static TencentErrorCodeEnum getContractTypeEnum(String code){
        for(TencentErrorCodeEnum tencentErrorCodeEnum: TencentErrorCodeEnum.values()) {
            if(tencentErrorCodeEnum.code.equals(code)) {
                return tencentErrorCodeEnum;
            }
        }
        return null;
    }

}
