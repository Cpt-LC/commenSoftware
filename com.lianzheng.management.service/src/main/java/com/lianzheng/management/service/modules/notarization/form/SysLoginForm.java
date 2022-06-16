package com.lianzheng.management.service.modules.notarization.form;

import lombok.Data;

/**
 * 登录表单
 *
 * @author gang.shen@kedata.com
 */
@Data
public class SysLoginForm {
    private String username;
    private String password;
    private String captcha;
    private String uuid;


}
