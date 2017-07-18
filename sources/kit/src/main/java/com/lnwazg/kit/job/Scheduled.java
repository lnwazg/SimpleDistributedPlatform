package com.lnwazg.kit.job;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定时器表达式注解
 * @author nan.li
 * @version 2016年3月21日
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled
{
    String cron() default "";
}