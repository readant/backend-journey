package com.readant.cms.common;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {

    String module();

    String action();
}
