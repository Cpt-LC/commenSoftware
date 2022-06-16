package com.lianzheng.core.log;

import java.lang.annotation.*;
@Documented
@Target({ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpecificLog {
    String message() default "系统日志";
}