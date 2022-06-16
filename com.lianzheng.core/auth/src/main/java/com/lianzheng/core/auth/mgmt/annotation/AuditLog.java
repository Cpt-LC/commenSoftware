package com.lianzheng.core.auth.mgmt.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @author gang.shen@kedata.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    String value() default "";
}
