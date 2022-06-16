package com.lianzheng.core.auth.mgmt.service;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.core.auth.mgmt.entity.SysCaptchaEntity;

import java.awt.image.BufferedImage;

/**
 * 验证码
 *
 * @author gang.shen@kedata.com
 */
@DS("mgmt")
public interface SysCaptchaService extends IService<SysCaptchaEntity> {

    /**
     * 获取图片验证码
     */
    BufferedImage getCaptcha(String uuid);

    /**
     * 验证码效验
     *
     * @param uuid uuid
     * @param code 验证码
     * @return true：成功  false：失败
     */
    boolean validate(String uuid, String code);
}
