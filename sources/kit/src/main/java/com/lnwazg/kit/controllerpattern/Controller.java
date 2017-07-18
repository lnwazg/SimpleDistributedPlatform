package com.lnwazg.kit.controllerpattern;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controller like 的framework的通用基础类
 * @author nan.li
 * @version 2016年10月27日
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller
{
    String value();
}
