package com.lnwazg.kit.testframework.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义执行次数的性能测试
 * @author nan.li
 * @version 2017年2月24日
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Benchmark
{
    /**
     * 性能测试的循环次数<br>
     * 必须定义
     * @author nan.li
     * @return
     */
    int value();
}
