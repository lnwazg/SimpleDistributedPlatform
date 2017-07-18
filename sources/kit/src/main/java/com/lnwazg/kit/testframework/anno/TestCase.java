package com.lnwazg.kit.testframework.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记该方法是否用于测试<br>
 * 想测试某个方法，就在该方法上面加上这个注解即可！
 * 
 * @author nan.li
 * @version 2016年3月28日
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestCase
{
}
