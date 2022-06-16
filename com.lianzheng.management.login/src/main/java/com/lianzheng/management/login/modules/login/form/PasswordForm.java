package com.lianzheng.management.login.modules.login.form;

import lombok.Data;

/**
 * 密码表单
 *
 * @author gang.shen@kedata.com
 */
@Data
public class PasswordForm {
    /**
     * 原密码
     */
    private String password;
    /**
     * 新密码
     */
    private String newPassword;

}
