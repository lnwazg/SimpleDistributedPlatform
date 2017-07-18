package com.lnwazg.kit.controllerpattern;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由表
 * @author nan.li
 * @version 2017年4月21日
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping
{
    String value();
}
